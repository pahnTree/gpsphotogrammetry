package com.example.philip.mygpsapp;

/**
 * Created by Philip on 11/10/2015.
 */
public class Coordinates {
    public int x, y; // The pixel locations
    public float distanceX, distanceY; // The distances away in meters

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(float x, float y) {
        this.distanceX = x;
        this.distanceY = y;
    }

    public Coordinates(int x, int y, float distanceX, float distanceY) {
        this.x = x;
        this.y = y;
        this.distanceX = distanceX;
        this.distanceY = distanceY;
    }
}