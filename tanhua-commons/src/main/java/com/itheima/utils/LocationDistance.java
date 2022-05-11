/*
package com.itheima.utils;

import com.itheima.mongo.UserLocation;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

public class LocationDistance {

    public static String getDistance(UserLocation user1,UserLocation user2){
        GlobalCoordinates source = new GlobalCoordinates(user1.getLocation().getX(),user2.getLocation().getY());
        GlobalCoordinates target = new GlobalCoordinates(user1.getLocation().getX(),user2.getLocation().getY());
        double meter = getDistanceMeter(source, target, Ellipsoid.WGS84);
        long round = Math.round(meter);
        if (round < 1000 || round >= 0){
            return round + "米";
        }else {
            return Math.round(meter / 1000) + "千米";
        }
    }
    public static double getDistanceMeter(GlobalCoordinates gpsFrom,GlobalCoordinates gpsTo,Ellipsoid ellipsoid){
        GeodeticCurve geodeticCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid,gpsFrom,gpsTo);
        return geodeticCurve.getEllipsoidalDistance();
    }
}
*/
