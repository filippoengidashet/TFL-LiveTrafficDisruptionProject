package tae.co.uk.ltd.mvp.model.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import tae.co.uk.ltd.application.TrafficDisruptionApplication;
import tae.co.uk.ltd.mvp.model.constants.Constants;
import tae.co.uk.ltd.mvp.model.constants.XmlElements;
import tae.co.uk.ltd.mvp.model.exception.LiveTrafficDisruptionException;
import tae.co.uk.ltd.mvp.model.pojo.Disruption;
import tae.co.uk.ltd.mvp.model.pojo.Point;

public class XMLFeedDownloadService extends Service {

    private static final String TAG = XMLFeedDownloadService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new XMLDownloaderTask().execute(Constants.TIMS);

        return START_FLAG_REDELIVERY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class XMLDownloaderTask extends AsyncTask<String, Disruption, Boolean> {

        private final String TAG = XMLDownloaderTask.class.getSimpleName();

        public XMLDownloaderTask() {      }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getTrafficDisruptionApplication().notifyShowDialog(TrafficDisruptionApplication.FetchStatus.RUNNING);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String url = params[0];

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod(Constants.RequestMethod.GET);
                connection.setConnectTimeout(Constants.ConnectionUtil.TIMEOUT);
                connection.connect();

                int statusCode = connection.getResponseCode();
                if (statusCode != Constants.ResponseCode.SUCCESS) {
                    getTrafficDisruptionApplication().errorOccurred(new LiveTrafficDisruptionException("Wrong Status Code!!!"));
                    throw new LiveTrafficDisruptionException("Bad Response Code!!!");
                }

                InputStream inputStream = connection.getInputStream();
                try {
                    processStream(inputStream);
                } catch (XmlPullParserException e) {
                    getTrafficDisruptionApplication().errorOccurred(new LiveTrafficDisruptionException(e + " XmlPullParserException!!!"));
                    throw new LiveTrafficDisruptionException(e + " XmlPullParserException!!!");
                }

                inputStream.close();
            } catch (MalformedURLException e) {
                getTrafficDisruptionApplication().errorOccurred(new LiveTrafficDisruptionException(e + " MalformedURLException!!!"));
            } catch (IOException e) {
                getTrafficDisruptionApplication().errorOccurred(new LiveTrafficDisruptionException(e + " IOException!!!"));
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Disruption... values) {
            super.onProgressUpdate(values);
            Disruption disruption = values[0];
            notifyDisruption(disruption);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            getTrafficDisruptionApplication().notifyShowDialog(TrafficDisruptionApplication.FetchStatus.STOPPED);
            stopSelf();
        }

        private void notifyDisruption(Disruption disruption) {
            Intent disruptionIntent = new Intent();
            disruptionIntent.putExtra(Constants.DISRUPTION_EXTRA, disruption);
            disruptionIntent.setAction(Constants.DISRUPTION_FILTER);
            sendBroadcast(disruptionIntent);
        }

        private void processStream(InputStream inputStream) throws XmlPullParserException, IOException {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(inputStream, null);

            int eventType = xpp.getEventType();
            Disruption disruption = null;
            Point point;
            String text = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.END_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    if (XmlElements.DISRUPTION.equals(tagName)) {
                        disruption = new Disruption();
                        disruption.setId(xpp.getAttributeValue(null, XmlElements.Attribute.ID));
                    } else if (XmlElements.POINT.equals(tagName)) {
                        point = new Point();

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG) {
                            } else if (eventType == XmlPullParser.TEXT) {
                                text = xpp.getText();
                            } else if (eventType == XmlPullParser.END_TAG) {
                                String endTagName = xpp.getName();
                                if (XmlElements.COORDINATES_EN.equals(endTagName)) {
                                    point.setCoordinatesEN(text);
                                } else if (XmlElements.COORDINATES_LL.equals(endTagName)) {
                                    StringTokenizer tokenizer = new StringTokenizer(text, ",");
                                    double latitute = 0.0;
                                    double longitude = 0.0;
                                    int count = 0;
                                    while (tokenizer.hasMoreTokens()) {
                                        if (count == 0) {
                                            longitude = Double.parseDouble(tokenizer.nextToken());
                                        } else {
                                            latitute = Double.parseDouble(tokenizer.nextToken());
                                        }
                                        count++;
                                    }
                                    point.setCoordinatesLL(text);
                                    point.setLatitude(latitute);
                                    point.setLongitude(longitude);

                                } else if (XmlElements.POINT.equals(endTagName)) {
                                    disruption.setPoint(point);
                                    break;
                                }
                            }
                            eventType = xpp.next();
                        }
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    text = xpp.getText();
                } else if (eventType == XmlPullParser.END_TAG) {
                    String endTagName = xpp.getName();
                    if (XmlElements.DISRUPTION.equals(endTagName)) {
                        publishProgress(disruption);
                    } else if (XmlElements.STATUS.equals(endTagName)) {
                        disruption.setStatus(text);
                    } else if (XmlElements.SEVERITY.equals(endTagName)) {
                        disruption.setSeverity(text);
                    } else if (XmlElements.LEVEL_OF_INTEREST.equals(endTagName)) {
                        disruption.setLevelOfInterest(text);
                    } else if (XmlElements.CATEGORY.equals(endTagName)) {
                        disruption.setCategory(text);
                    } else if (XmlElements.SUB_CATEGORY.equals(endTagName)) {
                        disruption.setSubCategory(text);
                    } else if (XmlElements.START_TIME.equals(endTagName)) {
                        disruption.setStartTime(text);
                    } else if (XmlElements.LOCATION.equals(endTagName)) {
                        disruption.setLocation(text);
                    } else if (XmlElements.CORRIDOR.equals(endTagName)) {
                        disruption.setCorridor(text);
                    } else if (XmlElements.COMMENTS.equals(endTagName)) {
                        disruption.setComments(text);
                    } else if (XmlElements.CURRENT_UPDATE.equals(endTagName)) {
                        disruption.setCurrentUpdate(text);
                    } else if (XmlElements.REMARK_TIME.equals(endTagName)) {
                        disruption.setRemarkTime(text);
                    } else if (XmlElements.LAST_MODIFIED_TIME.equals(endTagName)) {
                        disruption.setLastModifiedTime(text);
                    }
                }
                eventType = xpp.next();
            }
        }
    }
    private TrafficDisruptionApplication getTrafficDisruptionApplication() {
        return (TrafficDisruptionApplication) getApplicationContext();
    }
}