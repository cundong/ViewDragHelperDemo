package com.cundong.viewdraghelper.util;

/**
 * Created by cundong on 2015/11/20.
 */
public class EvaluateUtils {

    /**
     * 数值线性变化 startValue->endValue
     *
     * @param fraction
     * @param startValue
     * @param endValue
     */
    public static Float evaluateFloat(float fraction, Float startValue, Float endValue) {

        float startFloat = startValue.floatValue();
        float endFloat = endValue.floatValue();

        return startFloat + fraction * (endFloat - startFloat);
    }

    /**
     * 颜色线性变化 startValue->endValue
     *
     * @param fraction
     * @param startValue
     * @param endValue
     */
    public static Integer evaluateColor(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;
        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;
        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
                | (int) ((startB + (int) (fraction * (endB - startB))));
    }
}