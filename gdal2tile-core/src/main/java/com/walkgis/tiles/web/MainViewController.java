package com.walkgis.tiles.web;

import com.walkgis.tiles.MainApp;
import com.walkgis.tiles.entity.ViewNameEnum;
import com.walkgis.tiles.web.ctrl.PanelFileListController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);
    @FXML
    private Button btnProve, btnNext;
    private String currentView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentView = "panelTileTypeSelect";
//        currentView = nextView("panelFileList");
        this.btnNext.setOnMouseClicked(this::btnNextClick);
        this.btnProve.setOnMouseClicked(this::btnProveClick);
    }

    @FXML
    private void btnProveClick(MouseEvent event) {
        if (currentView.equalsIgnoreCase(ViewNameEnum.panelTileTypeSelect.toString())) {

        } else if (currentView.equalsIgnoreCase(ViewNameEnum.panelFileList.toString())) {
            currentView = nextView(ViewNameEnum.panelTileTypeSelect.name());
        } else if (currentView.equalsIgnoreCase(ViewNameEnum.panelTileSetting.name())) {
            currentView = nextView(ViewNameEnum.panelFileList.name());
        } else if (currentView.equals(ViewNameEnum.panelProgress.name())) {
            currentView = nextView(ViewNameEnum.panelTileSetting.name());
        }
    }

    @FXML
    private void btnNextClick(MouseEvent event) {
        if (currentView.equalsIgnoreCase(ViewNameEnum.panelTileTypeSelect.toString())) {
            currentView = nextView(ViewNameEnum.panelFileList.name());
        } else if (currentView.equalsIgnoreCase(ViewNameEnum.panelFileList.toString())) {
            if (PanelFileListController.fileItemList.size() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("告警");
                alert.setHeaderText("没有输入文件");
                alert.setContentText("没有输入文件");

                alert.showAndWait();
                return;
            }

            if (PanelFileListController.fileItemList.size() > 1) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("告警");
                alert.setHeaderText("告警");
                alert.setContentText("目前版本只支持一个影像");

                alert.showAndWait();
                return;
            }
            currentView = nextView(ViewNameEnum.panelTileSetting.name());
        } else if (currentView.equalsIgnoreCase(ViewNameEnum.panelTileSetting.name())) {
//            cmbZoomFrom.getSelectionModel().select(0);
//            cmbZoomTo.getSelectionModel().select(0);
//            generateDirTiles();
            currentView = nextView(ViewNameEnum.panelProgress.name());
        } else if (currentView.equals(ViewNameEnum.panelProgress.name())) {
        }
    }

    /**
     * 下一个页面
     *
     * @param fxmlName
     */
    public String nextView(String fxmlName) {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlName + ".fxml"));
        try {
            Parent parent = loader.load();
            BorderPane borderPane = (BorderPane) MainApp.scene.getRoot();
            borderPane.setCenter(parent);
            return parent.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
