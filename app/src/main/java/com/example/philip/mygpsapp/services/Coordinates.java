package com.example.philip.mygpsapp.services;

/**
 * Created by Philip on 11/10/2015.
 */
public class Coordinates {
    public float distanceX, distanceY; // The distances away in meters relative to origin
    public double geoX, geoY; // The GPS coordinates at this point

    public Coordinates() {
        // Do nothing
    }

    public Coordinates(float x, float y) {
        this.distanceX = x;
        this.distanceY = y;
    }

    public Coordinates(double geoX, double geoY) {
        this.geoX = geoX;
        this.geoY = geoY;
    }

    /**
     * Constructor for the distances from the origin
     * @param distanceX The distance in meters
     * @param distanceY The distance in meters
     * @param geoX The distance in Degrees Longitude
     * @param geoY The distance in Degrees Latitude
     */
    public Coordinates(float distanceX, float distanceY, double geoX, double geoY) {
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.geoX = geoX;
        this.geoY = geoY;
    }
}