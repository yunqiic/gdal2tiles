package com.walkgis.tiles.util.geopackage;

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
