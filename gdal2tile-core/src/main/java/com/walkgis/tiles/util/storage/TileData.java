package com.walkgis.tiles.util.storage;

import java.sql.Blob;

public class TileData {
    private int zoom_level;
    private int tile_column;
    private int tile_row;
    private Blob tile_data;

    public TileData(int zoom_level, int tile_column, int tile_row, Blob tile_data) {
        this.zoom_level = zoom_level;
        this.tile_column = tile_column;
        this.tile_row = tile_row;
        this.tile_data = tile_data;
    }

    public int getZoom_level() {
        return zoom_level;
    }

    public void setZoom_level(int zoom_level) {
        this.zoom_level = zoom_level;
    }

    public int getTile_column() {
        return tile_column;
    }

    public void setTile_column(int tile_column) {
        this.tile_column = tile_column;
    }

    public int getTile_row() {
        return tile_row;
    }

    public void setTile_row(int tile_row) {
        this.tile_row = tile_row;
    }

    public Blob getTile_data() {
        return tile_data;
    }

    public void setTile_data(Blob tile_data) {
        this.tile_data = tile_data;
    }
}
