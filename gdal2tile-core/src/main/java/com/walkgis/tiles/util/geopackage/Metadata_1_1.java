package com.walkgis.tiles.util.geopackage;


import java.math.BigDecimal;

public class Metadata_1_1 extends Metadata_1_0 {

    public Metadata_1_1(GeoPackage geoPackage) {
        super(geoPackage);
        this.VERSION = "1.1";
        this.MANDATORY.put("format", "");
        this.OPTIONAL.put("bounds", "");
    }

    public String _clean_format(String value) throws Exception {
        if (value.equalsIgnoreCase(FORMATS.PNG.toString()))
            return FORMATS.PNG.toString();
        else if (value.equalsIgnoreCase(FORMATS.JPG.toString()))
            return FORMATS.JPG.toString();
        else throw new Exception("type {value!r} must be one of: {types}");
    }

    public Double[] _clean_bounds(Double[] value, Integer places) throws Exception {
        if (places == null) places = 5;

        double left = new BigDecimal(value[0]).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
        double bootom = new BigDecimal(value[1]).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
        double right = new BigDecimal(value[2]).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
        double top = new BigDecimal(value[3]).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (left >= right || bootom >= top || left < -180.0 || right > 180.0 || bootom < -90.0 || top > 90.0) {
            throw new Exception("wrong extents");
        }
        return new Double[]{left, bootom, right, top};
    }
}
