package com.fMall.util;

import java.math.BigDecimal;

/**
 * Created by 高琮 on 2018/5/21.
 */
public class BigDecimalUtil {

    /**
     * 获得两个小数相加的值，维护精度
     * @param v1 第一个数字
     * @param v2 第二个数字
     * @return 相加得到的结果
     */
    public static BigDecimal add(double v1, double v2){
        BigDecimal b1=new BigDecimal(Double.toString(v1));
        BigDecimal b2=new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }


    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入,保留2位小数

        //除不尽的情况
    }
}
