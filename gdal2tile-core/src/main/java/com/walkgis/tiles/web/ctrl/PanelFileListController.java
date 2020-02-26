package com.walkgis.tiles.web.ctrl;

import com.walkgis.tiles.MainApp;
import com.walkgis.tiles.entity.FileItemList;
import com.walkgis.tiles.web.MainViewController;
import com.walkgis.tiles.web.sub.ReviewViewController;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PanelFileListController implements Initializable {
    @FXML
    private ListView listViewFiles;
    @FXML
    private Label projection, transform;
    @FXML
    private Button btnChangeRSR, btnChangeExtent, btnOpenFile, btnReview, btnRemove;

    public static FileItemList fileItemList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.btnReview.setOnMouseClicked(this::btnReviewClick);
        this.btnOpenFile.setOnMouseClicked(this::btnOpenFileClick);
        this.btnRemove.setOnMouseClicked(this::btnRemoveClick);
        this.btnChangeRSR.setOnMouseClicked(this::btnChangeRSRClick);
        this.btnChangeExtent.setOnMouseClicked(this::btnChangeExtentClick);

        this.fileItemList = new FileItemList(new ArrayList<>());
        this.listViewFiles.itemsProperty().bind(fileItemList);

        this.listViewFiles.getSelectionModel().selectedItemProperty().addListener(this::noticeListItemChange);

        if (this.fileItemList.size() > 0) {
            this.projection.textProperty().bindBidirectional(this.fileItemList.get(0).projectionProperty());
            this.transform.textProperty().bindBidirectional(this.fileItemList.get(0).transformProperty());
            this.projection.setText("");
            this.transform.setText("");
        }
    }

    @FXML
    private void btnReviewClick(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("review.fxml"));
        try {
            MainApp.primaryStage.setScene(new Scene(loader.load()));
            MainApp.primaryStage.initModality(Modality.NONE);
            MainApp.primaryStage.show();
            ((ReviewViewController) loader.getController()).showReview((FileItemList.FileItem) this.listViewFiles.getSelectionModel().getSelectedItem());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void btnOpenFileClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据");
        Stage selectFile = new Stage();

        if ((MainApp.defaultDir == null || "".equals(MainApp.defaultDir)))
            MainApp.defaultDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(MainApp.defaultDir));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("所有文件类型", "*.pdf", "*.tif"),
                new FileChooser.ExtensionFilter("GeoPDF", "*.pdf"),
                new FileChooser.ExtensionFilter("GeoTIFF", "*.tif")
        );
        File file = fileChooser.showOpenDialog(selectFile);
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
            FileItemList.FileItem item = new FileItemList.FileItem(file, 0);
            item.setDataset(dataset);
            fileItemList.add(item);
        }
    }

    @FXML
    private void btnRemoveClick(MouseEvent event) {
        ObservableList<FileItemList.FileItem> fileItemObservableList = listViewFiles.getSelectionModel().getSelectedItems();
        fileItemList.remove(fileItemObservableList.get(0));
    }

    @FXML
    private void btnChangeRSRClick(MouseEvent event) {

    }

    @FXML
    private void btnChangeExtentClick(MouseEvent event) {

    }

    /**
     * 行单击事件
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    @FXML
    private void noticeListItemChange(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        if (newValue == null) return;
        FileItemList.FileItem fileItem = (FileItemList.FileItem) newValue;
        if (fileItem.getFile().exists() && fileItem.getDataset() != null) {
            this.projection.setText(fileItem.getProjection());
            this.transform.setText(fileItem.getTransform());
        } else {
            this.projection.setText("");
            this.transform.setText("");
        }
    }
}