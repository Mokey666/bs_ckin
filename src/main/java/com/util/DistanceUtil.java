package com.util;

import org.junit.Test;

public class DistanceUtil {
    //地球半径
    private static double EARTH_RADIUS = 6378137.0; // 单位米

    public static double getDistance(double lon1, double lat1,double lon2, double lat2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s =2*Math.asin(Math.sqrt((Math.pow(Math.sin(a/2),2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b/2),2))));//距离计算公式
        s = s * EARTH_RADIUS;
        s = Math.round(s*10000d)/10000d;
        return s;
    }
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }//弧度转换

}
