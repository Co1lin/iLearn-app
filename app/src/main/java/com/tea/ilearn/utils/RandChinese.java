package com.tea.ilearn.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 *copy from https://blog.csdn.net/oQiHaoGongYuan/article/details/51417113
 */
public class RandChinese {
    static public char gen() {
        String str = "";
        int hightPos;
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }
}