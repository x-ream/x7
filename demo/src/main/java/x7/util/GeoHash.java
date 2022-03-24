package x7.util;

import java.util.*;

/**
 * near by
 * @author Sim-wangyan
 * @creation 2020年7月13日 16:52:37
 * GeoHash geoHash = GeoHash.of5m();
 */
public final class GeoHash {

    public static final int HASH_PREFIX_LENGTH_3__ABOUT_600M = 3;// 距离精确600米左右
    public static final int HASH_PREFIX_LENGTH_6__ABOUT_20M = 4;// 距离精确20米左右
    public static final int HASH_PREFIX_LENGTH_8__ABOUT_5M = 5;// 距离精确5米左右
    public static final int BITS_PER_CHAR = 5;

    public static final double MIN_LAT = -90;
    public static final double MAX_LAT = 90;
    public static final double MIN_LNG = -180;
    public static final double MAX_LNG = 180;


    public static final char[] GEO_HASH_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };


    public final static Map<Character, Integer> charPosMap = new HashMap<>();

    static {
        int i = 0;
        for (char c : GEO_HASH_CHARS) {
            charPosMap.put(c, i++);
        }
    }

    private  int bitCount;
    private double minLat;
    private double minLng;

    private GeoHash(){}

    private static GeoHash init(int length) {
        GeoHash geoHash = new GeoHash();
        geoHash.bitCount = length  * BITS_PER_CHAR;
        geoHash.initMinLatLng();
        return geoHash;
    }

    public static GeoHash of5m(){
        return init(HASH_PREFIX_LENGTH_8__ABOUT_5M);
    }
    public static GeoHash of20m(){
        return init(HASH_PREFIX_LENGTH_6__ABOUT_20M);
    }
    public static GeoHash of600m(){
        return init(HASH_PREFIX_LENGTH_3__ABOUT_600M);
    }

    private void initMinLatLng() {
        minLat = MAX_LAT - MIN_LAT;
        for (int i = 0; i < bitCount; i++) {
            minLat /= 2.0;
        }
        minLng = MAX_LNG - MIN_LNG;
        for (int i = 0; i < bitCount; i++) {
            minLng /= 2.0;
        }
    }

    public String hash(double lng,double lat) {
        BitSet lngSet = getBits(lng, -180, 180);
        BitSet latSet = getBits(lat, -90, 90);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitCount; i++) {
            sb.append( (lngSet.get(i))?'1':'0');
            sb.append( (latSet.get(i))?'1':'0');
        }
        return base32(Long.parseLong(sb.toString(), 2));
    }

    public List<String> nearBy(double lng, double lat){

        List<String> list = new ArrayList<>();
        double uplat = lat + minLat;
        double downLat = lat - minLat;

        double leftlng = lng - minLng;
        double rightLng = lng + minLng;

        String leftUp = hash(leftlng,uplat);
        list.add(leftUp);

        String leftMid = hash(leftlng,lat);
        list.add(leftMid);

        String leftDown = hash(leftlng,downLat);
        list.add(leftDown);

        String midUp = hash(lng,uplat);
        list.add(midUp);

        String midMid = hash(lng,lat);
        list.add(midMid);

        String midDown = hash(lng,downLat);
        list.add(midDown);

        String rightUp = hash(rightLng,uplat);
        list.add(rightUp);

        String rightMid = hash(rightLng,lat);
        list.add(rightMid);

        String rightDown = hash(rightLng,downLat);
        list.add(rightDown);

        return list;
    }

    private BitSet getBits(double lat, double floor, double ceiling) {
        BitSet bitSet = new BitSet(bitCount);
        for (int i = 0; i < bitCount; i++) {
            double mid = (floor + ceiling) / 2;
            if (lat >= mid) {
                bitSet.set(i);
                floor = mid;
            } else {
                ceiling = mid;
            }
        }
        return bitSet;
    }

    private static String base32(long i) {
        char[] chars = new char[65];
        int charPos = 64;
        boolean negative = (i < 0);
        if (!negative){
            i = -i;
        }
        while (i <= -32) {
            chars[charPos--] = GEO_HASH_CHARS[(int) (-(i % 32))];
            i /= 32;
        }
        chars[charPos] = GEO_HASH_CHARS[(int) (-i)];
        if (negative){
            chars[--charPos] = '-';
        }
        return new String(chars, charPos, (65 - charPos));
    }

    private static double decode(BitSet bs, double floor, double ceiling) {
        double mid = 0;
        for (int i=0; i<bs.length(); i++) {
            mid = (floor + ceiling) / 2;
            if (bs.get(i))
                floor = mid;
            else
                ceiling = mid;
        }
        return mid;
    }


    public double[] geo(String geohash) {
        StringBuilder sb = new StringBuilder();
        for (char c : geohash.toCharArray()) {
            int i = charPosMap.get(c) + 32;
            sb.append( Integer.toString(i, 2).substring(1) );
        }

        BitSet lngSet = new BitSet();
        BitSet latSet = new BitSet();

        int j =0;
        for (int i=0; i< bitCount*2;i+=2) {
            boolean isSet = false;
            if ( i < sb.length() ) {
                isSet = sb.charAt(i) == '1';
            }
            lngSet.set(j++, isSet);
        }

        j=0;
        for (int i=1; i< bitCount*2;i+=2) {
            boolean isSet = false;
            if ( i < sb.length() ) {
                isSet = sb.charAt(i) == '1';
            }
            latSet.set(j++, isSet);
        }

        double lng = decode(lngSet, -180, 180);
        double lat = decode(latSet, -90, 90);

        return new double[] {lng, lat};
    }

}