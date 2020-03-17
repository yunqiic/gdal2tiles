package com.walkgis.tiles.util.geopackage;

public class Metadata_1_2 extends Metadata_1_1 {
    public Metadata_1_2(GeoPackage geoPackage) {
        super(geoPackage);
        this.VERSION = "1.2";
        this.OPTIONAL.put("attribution", "");
    }
}
