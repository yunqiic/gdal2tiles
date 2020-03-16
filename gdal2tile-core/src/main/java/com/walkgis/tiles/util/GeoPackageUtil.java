package com.walkgis.tiles.util;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.SQLiteStyle;

import java.io.File;
import java.sql.SQLException;

public class GeoPackageUtil {
    private SQLManager sqlManager;

    private static class SingletonClassInstance {
        private static final GeoPackageUtil instance = new GeoPackageUtil();
    }

    private GeoPackageUtil() {
    }

    public static GeoPackageUtil getInstance() {
        return SingletonClassInstance.instance;
    }

    public void init(File file) {
        String url = "jdbc:sqlite:" + file.getAbsolutePath();

        ConnectionSource source = ConnectionSourceHelper.getSimple("org.sqlite.JDBC", url, "", "");
        DSTransactionManager.start();
        sqlManager = new SQLManager(new SQLiteStyle(), new ClasspathLoader("/sql"), source, new UnderlinedNameConversion(), new Interceptor[]{});
        sqlManager.executeUpdate(new SQLReady("PRAGMA application_id = 1196437808"));
        sqlManager.update("sqlite.createGpkgSpatialRefSys");
        sqlManager.update("sqlite.insertGpkgSpatialRefSys");
        sqlManager.update("sqlite.createViewStSpatialRefSys");
        sqlManager.update("sqlite.createViewSpatialRefSys");
        sqlManager.update("sqlite.createGpkgContents");
        sqlManager.update("sqlite.createGpkgGeometryColumns");
        sqlManager.update("sqlite.createViewStGeometryColumns");
        sqlManager.update("sqlite.createViewGeometryColumns");
        sqlManager.update("sqlite.createSimpleFeatureTable");
        sqlManager.update("sqlite.createGpkgTileMatrixSet");
        sqlManager.update("sqlite.createGpkgTileMatrix");
        sqlManager.update("sqlite.createGpkgExtensions");
        sqlManager.update("sqlite.createTrigger");
        try {
            DSTransactionManager.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTile(int tx, int ty, int tz, byte[] dataArrayR) {

    }
}