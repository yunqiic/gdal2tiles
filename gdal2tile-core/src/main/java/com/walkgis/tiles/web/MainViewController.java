package com.walkgis.tiles.web;

import com.walkgis.tiles.MainApp;
import com.walkgis.tiles.entity.ViewNameEnum;
import com.walkgis.tiles.web.ctrl.PanelFileListController;
import com.walkgis.tiles.web.ctrl.PanelTileSettingController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainViewController.class);
    @FXML
    private Button btnProve, btnNext;
    public static String currentView;

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
            nextView(ViewNameEnum.panelTileTypeSelect.name());
        } else if (currentView.equalsIgnoreCase(ViewNameEnum.panelTileSetting.name())) {
            nextView(ViewNameEnum.panelFileList.name());
        } else if (currentView.equals(ViewNameEnum.panelProgress.name())) {
            nextView(ViewNameEnum.panelTileSetting.name());
        }
    }

    @FXML
    private void btnNextClick(MouseEvent event) {
        if (currentView.equalsIgnoreCase(ViewNameEnum.panelTileTypeSelect.toString())) {
            nextView(ViewNameEnum.panelFileList.name());
        } else if (currentView.equalsIgnoreCase(ViewNameEnum.panelFileList.toString())) {
            if (PanelFileListController.fileItems.size() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("告警");
                alert.setHeaderText("没有输入文件");
                alert.setContentText("没有输入文件");

                alert.showAndWait();
                return;
            }

            if (PanelFileListController.fileItems.size() > 1) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("告警");
                alert.setHeaderText("告警");
                alert.setContentText("目前版本只支持一个影像");

                alert.showAndWait();
                return;
            }
            PanelTileSettingController controller = (PanelTileSettingController) nextView(ViewNameEnum.panelTileSetting.name());
            controller.init(PanelFileListController.fileItems.get(0).getFile());
        } else if (currentView.equalsIgnoreCase(ViewNameEnum.panelTileSetting.name())) {
            //这里读取文件，并且计算出最大最小比例尺
            nextView(ViewNameEnum.panelProgress.name());
        } else if (currentView.equals(ViewNameEnum.panelProgress.name())) {
        }
    }

    /**
     * 下一个页面
     *
     * @param fxmlName
     * @return
     */
    public static Object nextView(String fxmlName) {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlName + ".fxml"));
        try {
            Parent parent = loader.load();
            BorderPane borderPane = (BorderPane) MainApp.scene.getRoot();
            borderPane.setCenter(parent);
            currentView = fxmlName;
           return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
