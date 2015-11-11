package com.example.philip.mygpsapp;

/**
 * Created by Philip on 11/10/2015.
 */
public class Coordinates {
    public int x, y; // The pixel locations relative to origin
    public float distanceX, distanceY; // The distances away in meters relative to origin
    public double geoX, geoY; // The GPS coordinates at this point

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(float x, float y) {
        this.distanceX = x;
        this.distanceY = y;
    }

    public Coordinates(double geoX, double geoY) {
        this.geoX = geoX;
        this.geoY = geoY;
    }

    public Coordinates(int x, int y, float distanceX, float distanceY, double geoX, double geoY) {
        this.x = x;
        this.y = y;
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.geoX = geoX;
        this.geoY = geoY;
    }
}