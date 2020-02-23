module gdal2tile.core {
    exports com.walkgis.tiles;
    requires javafx.graphics;
    requires springboot.javafx;
    requires javafx.fxml;
    requires javafx.swing;
    requires gdal;
    requires spring.beans;
    requires javafx.controls;
    requires slf4j.api;
    requires spring.core;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires java.sql;
    requires beetl;
    requires org.locationtech.jts;
    opens com.walkgis.tiles to javafx.base,java.base;
}