/**
 *
 */
module gdal2tile.core {
    requires gdal;
    requires org.locationtech.jts;
    requires javafx.base;
    requires springboot.javafx.support;
    requires javafx.graphics;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires slf4j.api;
    requires spring.beans;
    requires spring.core;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires beetl;
    requires logback.core;
    requires java.desktop;
    requires javafx.swing;
    requires spring.jcl;
    requires dom4j;
    exports com.walkgis.tiles;
}
