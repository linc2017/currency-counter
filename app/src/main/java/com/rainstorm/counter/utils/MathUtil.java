package com.rainstorm.counter.utils;

import java.math.BigDecimal;

/**
 * @description math util
 * @author liys
 */

public class MathUtil {

    /**
     * double add
     * 
     * @param v1 the first value
     * @param v2 the second value
     * @return v1 + v2
     */
    public static double add(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * float add
     *
     * @param v1 the first value
     * @param v2 the second value
     * @return v1 + v2
     */
    public static float add(float v1, float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.add(b2).floatValue();
    }

    /**
     * float sub
     * 
     * @param v1 the first value
     * @param v2 the second value
     * @return v1 - v2
     */
    public static float sub(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.subtract(b2).floatValue();
    }

    /**
     * float mul
     * 
     * @param v1
     * @param v2
     * @return v1 * v2
     */
    public static float mul(Float v1,Float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.multiply(b2).floatValue();
    }

}
