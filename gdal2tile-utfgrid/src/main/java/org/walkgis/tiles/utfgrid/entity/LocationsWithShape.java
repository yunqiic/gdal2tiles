package org.walkgis.tiles.utfgrid.entity;

import org.walkgis.tiles.utfgrid.common.entity.GeoEntity;

public class LocationsWithShape implements GeoEntity<Integer> {

    private Integer id;
    private Object shape;

    public LocationsWithShape(Integer id) {
        this.id = id;
    }

    public LocationsWithShape(Integer id, Object shape) {
        this.id = id;
        this.shape = shape;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Object getShape() {
        return shape;
    }

    @Override
    public void setShape(Object shape) {
        this.shape = shape;
    }
}
