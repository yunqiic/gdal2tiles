package com.walkgis.tiles.util.mbtiles;

public enum TYPES {
    OVERLAY("overlay"), BASELAYER("baselayer");
    private String value;

    TYPES(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
