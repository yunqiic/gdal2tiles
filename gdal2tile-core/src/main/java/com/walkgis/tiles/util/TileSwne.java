package com.walkgis.tiles.util;

import com.walkgis.tiles.util.storage.Storage;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;

import java.io.IOException;

@FunctionalInterface
interface TileSwne {
    // 默认方法
    default void defaultMethod(){

    }

    // 静态方法
    static void staticMethod(){

    }

    void dealImage(Storage storage, Driver out_drv, Dataset dstile, int tx, int ty, int tz, String tileext) throws IOException;
}
