package com.walkgis.tiles;

import com.walkgis.tiles.util.GeopackageUtil;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;

public class MainController {
    private GeopackageUtil geopackageUtil;

    public MainController() {
        if (GDAL2Tiles.geopackage) {
            geopackageUtil = new GeopackageUtil();
            try {
                geopackageUtil.initGeopackage("E:\\date\\geopackage\\home.gpkg");
            } catch (FileAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateTile() {
        String[] args = "-profile geodetic E:\\Data\\CAOBAO\\aaa.tif E:\\Data\\CAOBAO\\tiles\\java".split(" ");
        GDAL2Tiles gdal2tiles = new GDAL2Tiles(args);
        try {
            gdal2tiles.process(geopackageUtil);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
