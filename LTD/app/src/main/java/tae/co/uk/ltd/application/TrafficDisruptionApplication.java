package tae.co.uk.ltd.application;

/*
 * Copyright (c) 2015 Filippo Engidashet <filippo.eng@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

import tae.co.uk.ltd.mvp.model.constants.Constants;
import tae.co.uk.ltd.mvp.model.exception.LiveTrafficDisruptionException;
import tae.co.uk.ltd.mvp.model.pojo.Disruption;
import tae.co.uk.ltd.mvp.model.service.XMLFeedDownloadService;
import tae.co.uk.ltd.mvp.model.utilities.NetworkUtils;

/*
 * @author Filippo Engidashet
 * @version 1.0
 * @date 7/21/2015
 *
 * TrafficDisruptionApplication.java: This class is the base application for all views.
 *
 * It is responsible to start a fetching service of disruption feeds
 */
public class TrafficDisruptionApplication extends Application {

    private static final String TAG = TrafficDisruptionApplication.class.getSimpleName();

    /**
     * A {@link Disruption} which will be used in conjunction with the {@link List} above.
     */
    private List<Disruption> mDisruptionList = new ArrayList<>();

    /**
     * A {@link DisruptionListener} which will be used in conjunction with the {@link List} above.
     */
    private List<DisruptionListener> mListeners;

    @Override
    public void onCreate() {
        super.onCreate();
        mListeners = new ArrayList<>();
        registerReceiver(mReceiver, getIntentFilter());
        startFetching();
    }

    /**
     * This method is responsible for starting fetching the feed if network is available
     */
    private void startFetching() {
        if (NetworkUtils.isOnline(getApplicationContext())) {
            mDisruptionList.clear();
            startFetchingService();
        } else {
            for (DisruptionListener listener : mListeners) {
                listener.onFetchFailed(new LiveTrafficDisruptionException("Sorry you don't have network connection!"));
            }
        }
    }

    /**
     * This method is responsible for starting fetching the feed
     */
    private void startFetchingService() {
        Intent intent = new Intent(TrafficDisruptionApplication.this, XMLFeedDownloadService.class);
        startService(intent);
    }

    /**
     * This method is responsible for restarting fetching the feed if network is available
     */
    public void restartFetching() {
        stopFetching();
        startFetching();
    }

    /**
     * This method is responsible for stopping fetching the feed if network is available
     */
    private void stopFetching() {
        Intent intent = new Intent(TrafficDisruptionApplication.this, XMLFeedDownloadService.class);
        stopService(intent);
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.DISRUPTION_FILTER);
        return intentFilter;
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(mReceiver);
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public void addListener(DisruptionListener listener) {
        mListeners.add(listener);
    }

    public synchronized List<Disruption> getDisruptionList(String tabName) {
        if (Constants.CategoryType.ALL.equals(tabName)) {
            return mDisruptionList;
        }
        List<Disruption> disruptions = new ArrayList<>();
        for (Disruption disruption : mDisruptionList) {
            if (disruption.mCategory.equals(tabName)) {
                disruptions.add(disruption);
            }
        }
        return disruptions;
    }

    public void addDisruption(Disruption disruption) {
        mDisruptionList.add(disruption);
        notifyChange(disruption);
    }

    private void notifyChange(Disruption disruption) {
        for (DisruptionListener listener : mListeners) {
            listener.onDisruptionChanged(disruption);
        }
    }

    public void setDisruptionList(List<Disruption> disruptionList) {
        mDisruptionList = disruptionList;
    }

    public void errorOccurred(LiveTrafficDisruptionException exception) {
        for (DisruptionListener listener : mListeners) {
            listener.onFetchFailed(exception);
        }
    }

    public void notifyShowDialog(FetchStatus status) {
        for (DisruptionListener listener : mListeners) {
            listener.onStatusChanged(status);
        }
    }

    public interface DisruptionListener {

        void onDisruptionChanged(Disruption disruption);

        void onStatusChanged(FetchStatus status);

        void onFetchFailed(LiveTrafficDisruptionException exception);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Disruption disruption = (Disruption) intent.getSerializableExtra(Constants.DISRUPTION_EXTRA);
            addDisruption(disruption);
        }
    };

    public enum FetchStatus {

        RUNNING, STOPPED;
    }
}
