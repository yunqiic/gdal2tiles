package com.walkgis.tiles.entities.gpkg;


import org.beetl.sql.core.annotatoin.Table;

/*
 *
 * gen by beetlsql 2020-03-15
 */
@Table(name = ".gpkg_tile_matrix_set")
public class GpkgTileMatrixSet {

    // alias
    public static final String ALIAS_table_name = "table_name";
    public static final String ALIAS_srs_id = "srs_id";
    public static final String ALIAS_max_x = "max_x";
    public static final String ALIAS_max_y = "max_y";
    public static final String ALIAS_min_x = "min_x";
    public static final String ALIAS_min_y = "min_y";

    private String tableName;
    private Integer srsId;
    private Float maxX;
    private Float maxY;
    private Float minX;
    private Float minY;

    public GpkgTileMatrixSet() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getSrsId() {
        return srsId;
    }

    public void setSrsId(Integer srsId) {
        this.srsId = srsId;
    }

    public Float getMaxX() {
        return maxX;
    }

    public void setMaxX(Float maxX) {
        this.maxX = maxX;
    }

    public Float getMaxY() {
        return maxY;
    }

    public void setMaxY(Float maxY) {
        this.maxY = maxY;
    }

    public Float getMinX() {
        return minX;
    }

    public void setMinX(Float minX) {
        this.minX = minX;
    }

    public Float getMinY() {
        return minY;
    }

    public void setMinY(Float minY) {
        this.minY = minY;
    }


}
