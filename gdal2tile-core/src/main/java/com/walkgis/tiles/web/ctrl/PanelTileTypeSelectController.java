package com.walkgis.tiles.web.ctrl;

import com.walkgis.tiles.MainApp;
import com.walkgis.tiles.entity.FileItem;
import com.walkgis.tiles.web.MainViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PanelTileTypeSelectController implements Initializable {
    @FXML
    private GridPane panelTileTypeSelect;
    @FXML
    private AnchorPane panelStandard, panelGoogle, panelRaster, panelAdvance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.panelStandard.setOnMouseClicked(this::showSelectDialog);
        this.panelAdvance.setOnMouseClicked(this::showSelectDialog);
        this.panelRaster.setOnMouseClicked(this::showSelectDialog);
        this.panelGoogle.setOnMouseClicked(this::showSelectDialog);
    }

    @FXML
    private void showSelectDialog(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据");
        if ((MainApp.defaultDir == null || "".equals(MainApp.defaultDir)))
            MainApp.defaultDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(MainApp.defaultDir));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("所有文件类型", "*.pdf", "*.tif"),
                new FileChooser.ExtensionFilter("GeoPDF", "*.pdf"),
                new FileChooser.ExtensionFilter("GeoTIFF", "*.tif")
        );
        File file = fileChooser.showOpenDialog(MainApp.primaryStage);
        if (file != null) {
            Dataset dataset = gdal.Open(file.getAbsolutePath(), gdalconst.GA_ReadOnly);
            if (dataset == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Look, an Error Dialog");
                alert.setContentText(gdal.GetLastErrorMsg());

                alert.showAndWait();
                return;
            }
            FileItem item = new FileItem(file, 0);
            item.setDataset(dataset);

            PanelFileListController.fileItems.add(item);

           MainViewController.nextView("panelFileList");
        }
    }
}
