package tae.co.uk.ltd.mvp.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import tae.co.uk.ltd.R;
import tae.co.uk.ltd.application.TrafficDisruptionApplication;
import tae.co.uk.ltd.mvp.model.adapter.MakerWindowAdapter;
import tae.co.uk.ltd.mvp.model.constants.Constants;
import tae.co.uk.ltd.mvp.model.exception.LiveTrafficDisruptionException;
import tae.co.uk.ltd.mvp.model.pojo.Disruption;
import tae.co.uk.ltd.mvp.model.service.GPSTracker;

/**
 * Created by Filippo-TheAppExpert on 7/21/2015.
 */
public class MapActivity extends AppCompatActivity implements TrafficDisruptionApplication.DisruptionListener {

    private GoogleMap mMap;
    private List<Disruption> mDisruptionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDisruptionList = new ArrayList<>();
        mDisruptionList = ((TrafficDisruptionApplication) getApplicationContext()).getDisruptionList(Constants.CategoryType.ALL);
        setUpMapProperly();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapProperly();
    }

    private void setUpMapProperly() {

        try {

            FragmentManager fragmentManager = getSupportFragmentManager();
            SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
            mMap = supportMapFragment.getMap();

            if (mMap != null) {

                mMap.clear();

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);

                GPSTracker tracker = new GPSTracker(getApplicationContext());
                LatLng latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());

                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(10)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions()
                        .title("Address: " + " \n"
                                + "Postcode: ")
                        .anchor(0.0f, 1.0f)
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.setInfoWindowAdapter(new MakerWindowAdapter(getLayoutInflater()));

                addMarkers();
                addMakerEvent();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Unable to create Map!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ERROR MAP", e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMarkers() {
        for (Disruption disruption : mDisruptionList) {
            addMarker(disruption.mLocation, disruption.mComments, disruption.mPoint.mLatitude, disruption.mPoint.mLongitude);
        }
    }

    private void addMarker(String title, String comments, double latitude, double longitude) {
        mMap.addMarker(new MarkerOptions()
                .snippet(comments)
                .title(title)
                .anchor(0.0f, 1.0f)
                .position(new LatLng(latitude, longitude)));
    }

    private void addMakerEvent() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(),
                        "Get to that destination soon!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    @Override
    public void onDisruptionChanged(Disruption disruption) {
        mDisruptionList.add(disruption);
        addMarker(disruption.mLocation, disruption.mComments, disruption.mPoint.mLatitude, disruption.mPoint.mLongitude);
    }

    @Override
    public void onStatusChanged(TrafficDisruptionApplication.FetchStatus status) {

    }

    @Override
    public void onFetchFailed(LiveTrafficDisruptionException exception) {

    }
}
