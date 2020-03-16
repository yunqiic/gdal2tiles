package com.walkgis.tiles.util;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.SQLiteStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GeoPackageUtil {
    private SQLManager sqlManager;
    private Connection connection;

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
        connection = source.getMaster();
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
            createTileTable("tiles");
            DSTransactionManager.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertTile(String tableName, int tx, int ty, int tz, BufferedImage bufferedImage) throws SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        String sql = "INSERT INTO " + tableName + " VALUES (null,?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, tz);
        statement.setInt(2, tx);
        statement.setInt(3, ty);
        statement.setBytes(4, byteArrayOutputStream.toByteArray());

        statement.executeUpdate();
        statement.close();
//        sqlManager.executeUpdate(new SQLReady(sql, tz, tx, ty, new ByteArrayInputStream(dataArrayR)));
    }

    public void createTileTable(String tableName) throws SQLException {
        DSTransactionManager.start();
        String sql = "CREATE TABLE " + tableName + " ( " +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "  zoom_level INTEGER NOT NULL, " +
                "  tile_column INTEGER NOT NULL, " +
                "  tile_row INTEGER NOT NULL, " +
                "  tile_data BLOB NOT NULL, " +
                "  UNIQUE (zoom_level, tile_column, tile_row) " +
                ");" +
                "insert into gpkg_contents values('" + tableName + "',1,'','',datetime('now'),256,256,0,0,-1);" +
                "INSERT INTO gpkg_tile_matrix VALUES ('" + tableName + "',0,  1,  1,  512,  512,  2.0,  2.0);";
        sqlManager.executeUpdate(new SQLReady(sql));
        DSTransactionManager.commit();
    }
}