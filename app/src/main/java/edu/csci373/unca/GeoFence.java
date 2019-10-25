package edu.csci373.unca;

public class GeoFence {

    private double mLat;
    private double mLon;
    private double mStretch;

    public GeoFence() {

    }

    public GeoFence(double mLat, double mLon) {
        this.mLat = mLat;
        this.mLon = mLon;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public double getStretch() {
        return mStretch;
    }

    public void setStretch(double stretch) {
        mStretch = stretch;
    }
}
