package com.edu.neu.navigation.util;

/**
 * Created by Administrator on 2016/6/28.
 */

public class Point {
    private double latitude;
    private double longitude;
    private float x;
    private float y;
    private String message;

    public Point(double latitude, double longitude, String message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
    }
    //在图中坐标
    public Point(float x, float y, String message) {
        this.x = x;
        this.y = y;
        this.message = message;
    }

    public Point(double latitude, double longitude, float x, float y, String message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.x = x;
        this.y = y;
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
