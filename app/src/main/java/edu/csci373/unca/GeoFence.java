package edu.csci373.unca;

public class GeoFence {

    private double mLat;
    private double mLon;
    private double mRadius;

    public GeoFence() {

    }

    public GeoFence(double mLat, double mLon) {
        this.mLat = mLat;
        this.mLon = mLon;
        this.mRadius = 500;
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

    public double getRadius() {
        return mRadius;
    }

    public void setRadius(double radius) {
        mRadius = radius;
    }
}
