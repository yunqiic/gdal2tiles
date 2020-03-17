package com.walkgis.tiles.util.mbtiles;

public class Metadata_1_2 extends Metadata_1_1 {
    public Metadata_1_2(MBTiles mbtiles) {
        super(mbtiles);
        this.VERSION = "1.2";
        this.OPTIONAL.put("attribution", "");
    }
}
