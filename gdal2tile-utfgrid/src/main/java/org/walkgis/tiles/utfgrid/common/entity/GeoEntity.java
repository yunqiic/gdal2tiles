package org.walkgis.tiles.utfgrid.common.entity;

public interface GeoEntity<ID> {
    ID getId();

    Object getShape();

    void setShape(Object var1);
}
