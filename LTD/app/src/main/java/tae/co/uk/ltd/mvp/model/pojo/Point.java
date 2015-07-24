package tae.co.uk.ltd.mvp.model.pojo;

import java.io.Serializable;

/**
 * Created by Filippo-TheAppExpert on 7/20/2015.
 */
public class Point implements Serializable {

    public String mCoordinatesEN, mCoordinatesLL;
    public double mLatitude, mLongitude;

    private static final long serialVersionUID = 1L;

    public void setCoordinatesLL(String coordinatesLL) {
        mCoordinatesLL = coordinatesLL;
    }

    public void setCoordinatesEN(String coordinatesEN) {
        mCoordinatesEN = coordinatesEN;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @Override
    public String toString() {
        return mCoordinatesEN + " " + mCoordinatesLL;
    }
}
