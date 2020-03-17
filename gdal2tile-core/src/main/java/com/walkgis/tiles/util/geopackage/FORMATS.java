package com.walkgis.tiles.util.geopackage;

public enum FORMATS {
    PNG("png"), JPG("jpg");
    private String value;

    FORMATS(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
