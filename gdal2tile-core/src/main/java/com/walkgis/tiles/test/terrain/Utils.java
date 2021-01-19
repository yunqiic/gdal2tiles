package com.walkgis.tiles.test.terrain;

public class Utils {
    /**
     * 字符串转换为Ascii
     *
     * @param value
     * @return
     */
    public static String string2Ascii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    /**
     * Ascii转换为字符串
     *
     * @param value
     * @return
     */
    public static String ascii2String(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    public static Short bytes2Short(byte[] data2) {
        short retVal = 0;
        int len = data2.length < 2 ? data2.length : 2;
        for (int i = 0; i < len; i++) {
            retVal |= (data2[i] & 0xFF) << ((i & 0x03) << 1);
        }
        return retVal;
    }
}
