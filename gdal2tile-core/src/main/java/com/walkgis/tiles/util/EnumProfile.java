package com.walkgis.tiles.util;

public enum EnumProfile {
    mercator("mercator"), geodetic("geodetic"), raster("raster");
    private String value;

    EnumProfile(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
