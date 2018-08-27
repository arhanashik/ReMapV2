package com.bodytel.util.helper;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtil {
    private static DecimalFormat df = new DecimalFormat(".#");

    public static float floorToOneDecimalPoint(float input){
        if(input <= 0) return input;

        df.setRoundingMode(RoundingMode.DOWN);
        return Float.valueOf(df.format(input));
    }
}
