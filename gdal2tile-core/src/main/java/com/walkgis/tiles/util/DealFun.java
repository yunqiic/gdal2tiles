package com.walkgis.tiles.util;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;

import java.io.IOException;

@FunctionalInterface
interface DealFun {
    // 默认方法
    default void defaultMethod(){

    }

    // 静态方法
    static void staticMethod(){

    }

    void dealImage(String output, Driver out_drv, Dataset dstile, int tx, int ty, int tz, String tileext) throws IOException;
}
