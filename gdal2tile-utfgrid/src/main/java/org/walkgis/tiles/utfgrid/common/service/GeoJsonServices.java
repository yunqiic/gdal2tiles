package org.walkgis.tiles.utfgrid.common.service;

import org.geojson.Feature;

public interface GeoJsonServices<T> {
    Feature toFeature(T var1);

    void toEntity(Feature var1, T var2);
}
