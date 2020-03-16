package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/*
 *
 * gen by beetlsql 2020-03-15
 */
@Table(name = ".sample_tile_pyramid")
public class SampleTilePyramid {

    // alias
    public static final String ALIAS_id = "id";
    public static final String ALIAS_tile_column = "tile_column";
    public static final String ALIAS_tile_row = "tile_row";
    public static final String ALIAS_zoom_level = "zoom_level";
    public static final String ALIAS_tile_data = "tile_data";

    private Integer id;
    private Integer tileColumn;
    private Integer tileRow;
    private Integer zoomLevel;
    private String tileData;

    public SampleTilePyramid() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTileColumn() {
        return tileColumn;
    }

    public void setTileColumn(Integer tileColumn) {
        this.tileColumn = tileColumn;
    }

    public Integer getTileRow() {
        return tileRow;
    }

    public void setTileRow(Integer tileRow) {
        this.tileRow = tileRow;
    }

    public Integer getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(Integer zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public String getTileData() {
        return tileData;
    }

    public void setTileData(String tileData) {
        this.tileData = tileData;
    }


}
