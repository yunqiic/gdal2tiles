/**
 * @author JerFer
 * @date 2019/3/29---13:55
 */
package com.walkgis.tiles;

import com.walkgis.bootfx.AbstractJavaFxApplicationSupport;
import com.walkgis.tiles.view.DemoSplash;
import com.walkgis.tiles.view.MainView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class MainApp extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(MainApp.class, MainView.class, new DemoSplash(), args);
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.setTitle("地图切片");
    }

    public Collection<Image> loadDefaultIcons() {
        return Arrays.asList(
                new Image(this.getClass().getResource("/icons/gear_16x16.png").toExternalForm()),
                new Image(this.getClass().getResource("/icons/gear_24x24.png").toExternalForm()),
                new Image(this.getClass().getResource("/icons/gear_36x36.png").toExternalForm()),
                new Image(this.getClass().getResource("/icons/gear_42x42.png").toExternalForm()),
                new Image(this.getClass().getResource("/icons/gear_64x64.png").toExternalForm())
        );
    }
}
