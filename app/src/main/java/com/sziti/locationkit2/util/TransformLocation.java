package com.sziti.locationkit2.util;

public class TransformLocation {
//    double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
//    double PI = 3.1415926535897932384626;
//    double a = 6378245.0;
//    double ee = 0.00669342162296594323;
//
//    /**
//     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
//     * 即 百度 转 谷歌、高德
//     * @param bd_lon
//     * @param bd_lat
//     * @returns {*[]}
//     */
//    public double[] bd09togcj02(double bd_lon, double bd_lat){
//        double x = bd_lon - 0.0065;
//        double y = bd_lat - 0.006;
//        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
//        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
//        double gg_lng = z * Math.cos(theta);
//        double gg_lat = z * Math.sin(theta);
//        double[] point=new double[]{gg_lng, gg_lat};
//        return point;
//    }
//
//    /**
//     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
//     * 即谷歌、高德 转 百度
//     * @param lng
//     * @param lat
//     * @returns {*[]}
//     */
//    public double[] gcj02tobd09(double lng, double lat){
//        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
//        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
//        double bd_lng = z * Math.cos(theta) + 0.0065;
//        double bd_lat = z * Math.sin(theta) + 0.006;
//        double[] point=new double[]{bd_lng, bd_lat};
//        return point;
//    };
//
//    /**
//     * WGS84转GCj02
//     * @param lng
//     * @param lat
//     * @returns {*[]}
//     */
//    public double[] wgs84togcj02(double lng, double lat){
//        double dlat = transformlat(lng - 105.0, lat - 35.0);
//        double dlng = transformlng(lng - 105.0, lat - 35.0);
//        double radlat = lat / 180.0 * PI;
//        double magic = Math.sin(radlat);
//        magic = 1 - ee * magic * magic;
//        double sqrtmagic = Math.sqrt(magic);
//        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
//        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
//        double mglat = lat + dlat;
//        double mglng = lng + dlng;
//        double[] point=new double[]{mglng, mglat};
//        return point;
//    };
//
//    /**
//     * GCJ02 转换为 WGS84
//     * @param lng
//     * @param lat
//     * @returns {*[]}
//     */
//    public double[] gcj02towgs84(double lng, double lat){
//        double dlat = transformlat(lng - 105.0, lat - 35.0);
//        double dlng = transformlng(lng - 105.0, lat - 35.0);
//        double radlat = lat / 180.0 * PI;
//        double magic = Math.sin(radlat);
//        magic = 1 - ee * magic * magic;
//        double sqrtmagic = Math.sqrt(magic);
//        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
//        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
//        double mglat = lat + dlat;
//        double mglng = lng + dlng;
//        double[] point=new double[]{mglng, mglat};
//        return point;
//    };
//
//
//    private double transformlat(double lng,double lat){
//        double ret= -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
//        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
//        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
//        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
//        return ret;
//    }
//
//    private double transformlng(double lng,double lat){
//        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
//        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
//        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
//        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
//        return ret;
//    }
    private final static double a = 6378245.0; // 长半轴
    private final static double pi = 3.14159265358979324; // π
    private final static double ee = 0.00669342162296594323; // e²
    // GCJ-02 to WGS-84
    public static double[] toGPSPoint(double longitude, double latitude) {
        double[] dev = calDev(latitude, longitude);
        double retLat = latitude - dev[0];
        double retLon = longitude - dev[1];
        for (int i = 0; i < 1; i++) {
            dev = calDev(retLat, retLon);
            retLat = latitude - dev[0];
            retLon = longitude - dev[1];
        }
        return new double[]{retLon, retLat};
    }

    // 计算偏差
    private static double[] calDev(double wgLat, double wgLon) {
        if (isOutOfChina(wgLat, wgLon)) {
            return new double[]{0, 0};
        }
        double dLat = calLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = calLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        return new double[]{dLat, dLon};
    }

    // 判断坐标是否在国外
    private static boolean isOutOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    // 计算纬度
    private static double calLat(double x, double y) {
        double resultLat = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        resultLat += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        resultLat += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        resultLat += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return resultLat;
    }

    // 计算经度
    private static double calLon(double x, double y) {
        double resultLon = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        resultLon += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        resultLon += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        resultLon += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return resultLon;
    }
}
