package com.walkgis.tiles.web.entity;

public enum ViewNameEnum {
    panelTileTypeSelect("panelTileTypeSelect"), panelTileSetting("panelTileSetting"), panelProgress("panelProgress"), panelFileList("panelFileList");
    private String value;

    ViewNameEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
