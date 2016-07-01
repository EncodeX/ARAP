package com.edu.neu.navigation.util;

import android.location.Location;

/**
 * Created by Administrator on 2016/6/28.
 */

public class DistanceCalculation {


    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results=new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }


}
