package com.walkgis.tiles.web.ctrl;

import com.walkgis.tiles.MainApp;
import com.walkgis.tiles.entity.FileItemList;
import com.walkgis.tiles.util.RunTask;
import com.walkgis.tiles.web.sub.AdvanceSettingViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PanelTileSettingController implements Initializable {
    @FXML
    private GridPane panelTileSetting;
    @FXML
    private AnchorPane panelOutputDir, panelOutputMBTiles, panelOutputGeopackage;
    @FXML
    private ComboBox cmbZoomFrom, cmbZoomTo;
    @FXML
    private Button btnSettingAdvance;

    private FileItemList fileItemList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.panelOutputDir.setOnMouseClicked(this::panelOutputDirClick);
        this.panelOutputMBTiles.setOnMouseClicked(this::panelOutputMBTilesClick);
        this.panelOutputGeopackage.setOnMouseClicked(this::panelOutputGeopackageClick);

        this.btnSettingAdvance.setOnMouseClicked(this::btnSettingAdvanceClick);

        this.cmbZoomFrom.getItems().addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));
        this.cmbZoomTo.getItems().addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../panelFileList.fxml"));
        PanelFileListController control = loader.getController();
        fileItemList = control.fileItemList;
    }

    @FXML
    private void btnSettingAdvanceClick(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("advancesetting.fxml"));
        try {
            MainApp.primaryStage.setScene(new Scene(fxmlLoader.load()));
            MainApp.primaryStage.initModality(Modality.NONE);
            MainApp.primaryStage.show();
            AdvanceSettingViewController controller = fxmlLoader.getController();
            controller.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void panelOutputDirClick(MouseEvent event) {
        generateDirTiles();
    }

    @FXML
    private void panelOutputMBTilesClick(MouseEvent event) {

    }

    @FXML
    private void panelOutputGeopackageClick(MouseEvent event) {

    }

    private void generateDirTiles() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择保存的位置");
        Stage selectFile = new Stage();
        if ((MainApp.defaultDir == null || "".equals(MainApp.defaultDir)))
            MainApp.defaultDir = System.getProperty("user.home");
        directoryChooser.setInitialDirectory(new File(MainApp.defaultDir));

        File file = directoryChooser.showDialog(selectFile);
        if (file != null) {
            if (fileItemList.size() <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("没有输入文件");
                alert.setContentText("没有输入文件");

                alert.showAndWait();
                return;
            }

            FileItemList.FileItem fileItem = fileItemList.get(0);

//            mainViewController.nextView("panelProgress","panelProgress");

            RunTask task = new RunTask(fileItem.getFile().getAbsolutePath(), file.getAbsolutePath(), PanelProgressController.progressModel);

            task.call();
        }
    }
}
