package com.walkgis.tiles.test.terrain;

@FunctionalInterface
public interface TileSwneRasterInterface {
    int[] tileswneRaster(double lon, double lat, int zoom);
}
