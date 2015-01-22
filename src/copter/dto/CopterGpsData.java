/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter.dto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author default
 */
public class CopterGpsData {

    private double lng;
    private double lat;
    private double altitude;
    private double speed;
    private double climb;

    public CopterGpsData(double lng, double lat, double altitude, double speed, double climb) {
        this.lng = lng;
        this.lat = lat;
        this.altitude = altitude;
        this.speed = speed;
        this.climb = climb;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getClimb() {
        return climb;
    }

    public void setClimb(double climb) {
        this.climb = climb;
    }

    public LinkedHashMap toMap() {
        LinkedHashMap obj = new LinkedHashMap();
        obj.put("lng", lng);
        obj.put("lat", lat);
        obj.put("altitude", altitude);
        obj.put("climb", climb);
        obj.put("speed", speed);
        return (LinkedHashMap) obj;
    }

}
