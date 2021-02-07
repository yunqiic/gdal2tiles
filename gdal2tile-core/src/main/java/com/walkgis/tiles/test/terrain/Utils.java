package com.walkgis.tiles.test.terrain;

import org.gdal.gdal.Band;
import org.gdal.gdalconst.gdalconstConstants;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

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

    public static byte[] readRaster() {
        return null;
    }

    public static BigDecimal bandToPixvals(Band band, int i, int k, int xsize, int ysize) throws Exception {
        if (band.getDataType() == gdalconstConstants.GDT_Byte) {
            byte[] pixel = new byte[xsize * ysize];//最多是八位，所以，取个最大值。
            band.ReadRaster(i, k, xsize, ysize, pixel);
            return new BigDecimal(ByteBuffer.wrap(pixel).get());
        } else if (band.getDataType() == gdalconstConstants.GDT_Int16) {
            short[] pixel = new short[xsize * ysize];
            band.ReadRaster(i, k, xsize, ysize, pixel);
            return new BigDecimal(ShortBuffer.wrap(pixel).get());
        } else throw new Exception("不支持的类型");
    }
}
