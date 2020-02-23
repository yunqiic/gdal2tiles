module springboot.javafx {
    requires javafx.graphics;
    requires javafx.controls;
    requires slf4j.api;
    requires spring.boot;
    requires spring.context;
    requires javafx.fxml;
    requires spring.beans;
    requires spring.core;
    opens com.walkgis.bootfx to javafx.base,java.base;
    exports com.walkgis.bootfx;
}