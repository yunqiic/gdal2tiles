package com.walkgis.tiles;

import com.walkgis.tiles.control.CusPanel;
import com.walkgis.tiles.entity.FileItem;
import com.walkgis.tiles.util.GeopackageUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import scala.Int;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class MainController {
    private GeopackageUtil geopackageUtil;
    @FXML
    private AnchorPane panelStandard;
    @FXML
    private AnchorPane panelGoogle;
    @FXML
    private AnchorPane panelRaster;
    @FXML
    private AnchorPane panelAdvance;
    @FXML
    private CusPanel panelFileList;
    @FXML
    private CusPanel panelTileTypeSelect;
    @FXML
    private CusPanel panelTileSetting;
    @FXML
    private Button btnProve;
    @FXML
    private Button btnNext;
    @FXML
    private StackPane stackPanel;
    @FXML
    private ListView listViewFiles;


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

    @FXML
    private void showSelectDialog(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据");
        Stage selectFile = new Stage();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GeoPDF", "*.pdf"),
                new FileChooser.ExtensionFilter("GeoTIFF", "*.tif")
        );
        File file = fileChooser.showOpenDialog(selectFile);
        if (file != null) {
            listViewFiles.getItems().add(new FileItem(file, 0));
            btnNextClick(null);
        }
    }

    @FXML
    private void btnProveClick(MouseEvent event) {
        ObservableList<Node> children = stackPanel.getChildren();

        if (children.size() == 0) return;
        CusPanel cusPanel = (CusPanel) children.filtered(child -> child.isVisible()).get(0);
        Integer currentIndex = cusPanel.index.get();
        currentIndex = currentIndex - 1;
        if (currentIndex == 0) {
            return;
        }

        Integer finalCurrentIndex1 = currentIndex;
        children.forEach(child -> {
            CusPanel panel = (CusPanel) child;
            if (panel.index.get() == finalCurrentIndex1)
                panel.setVisible(true);
            else panel.setVisible(false);
        });
    }

    @FXML
    private void btnNextClick(MouseEvent event) {
        ObservableList<Node> children = stackPanel.getChildren();
        if (children.size() == 0) return;
        CusPanel cusPanel = (CusPanel) children.filtered(child -> child.isVisible()).get(0);
        Integer currentIndex = cusPanel.index.get();
        currentIndex = currentIndex + 1;
        if (currentIndex > children.size()) {
            return;
        }

        Integer finalCurrentIndex1 = currentIndex;
        children.forEach(child -> {
            CusPanel panel = (CusPanel) child;
            if (panel.index.get() == finalCurrentIndex1)
                panel.setVisible(true);
            else panel.setVisible(false);
        });
    }
}
