package com.walkgis.tiles.util.mbtiles;

import java.util.HashMap;

public class Metadata_1_0 extends Metadata {

    public Metadata_1_0(MBTiles mbtiles) {
        super(mbtiles);
        this.VERSION = "1.0";
        this.MANDATORY = new HashMap<>();
        this.MANDATORY.put("name", "");
        this.MANDATORY.put("type", "");
        this.MANDATORY.put("version", "");
        this.MANDATORY.put("description", "");

        this.OPTIONAL = new HashMap<>();
    }

    public String _clean_type(String value) throws Exception {
        if (value.equalsIgnoreCase(TYPES.BASELAYER.toString()))
            return TYPES.BASELAYER.toString();
        else if (value.equalsIgnoreCase(TYPES.OVERLAY.toString()))
            return TYPES.OVERLAY.toString();
        else throw new Exception("type {value!r} must be one of: {types}");
    }
}
