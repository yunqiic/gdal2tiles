/**
 * @author JerFer
 * @date 2019/3/29---13:55
 */
package com.walkgis.tiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.gdal.gdal.gdal;
import org.gdal.ogr.ogr;

import java.io.IOException;
import java.util.Properties;

public class MainApp extends Application {
    public static Scene scene;
    public static Stage primaryStage;
    public static String defaultDir;

    public MainApp() {
        Properties prop = new Properties();
        try {
            prop.load(this.getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultDir = prop.getProperty("defaultDir", "E:\\Data\\Raster");

        gdal.AllRegister();
        // 注册所有的驱动
        ogr.RegisterAll();
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");
        gdal.SetConfigOption("GDAL_DATA", prop.getProperty("gdalDataDir", "D:\\GDAL204\\gdal-data"));
    }

    @Override
    public void start(Stage _primaryStage) throws Exception {
        primaryStage = _primaryStage;
        BorderPane borderPane = (BorderPane) loadFXML("mainview");
        borderPane.setCenter(loadFXML("panelTileTypeSelect"));
        scene = new Scene(borderPane);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
