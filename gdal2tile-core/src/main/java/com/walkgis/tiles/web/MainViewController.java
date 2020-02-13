package com.walkgis.tiles.web;

import com.walkgis.tiles.MainApp;
import com.walkgis.tiles.util.GDAL2TilesMulti;
import com.walkgis.tiles.view.AdvanceSettingView;
import com.walkgis.tiles.view.CusPanel;
import com.walkgis.tiles.entity.FileItem;
import com.walkgis.tiles.util.EnumProfile;
import com.walkgis.tiles.util.EnumResampling;
import com.walkgis.tiles.util.GDAL2Tiles;
import com.walkgis.tiles.view.ReviewView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.ogr.ogr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;


@FXMLController
public class MainViewController implements Initializable {
    @Value(value = "${defaultDir}")
    private String defaultDir;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ReviewViewController reviewViewController;
    @Autowired
    private AdvanceSettingViewController advanceSettingViewController;
    @FXML
    private AnchorPane panelStandard, panelGoogle, panelRaster, panelAdvance;
    @FXML
    private AnchorPane panelOutputDir, panelOutputMBTiles, panelOutputGeopackage;
    @FXML
    private CusPanel panelFileList, panelTileTypeSelect, panelTileSetting, panelProgress;
    @FXML
    private Button btnProve, btnNext, btnReview, btnOpenFile, btnRemove, btnChangeRSR, btnChangeExtent, btnSettingAdvance;
    @FXML
    private StackPane stackPanel;
    @FXML
    private ListView listViewFiles;
    @FXML
    private Label transform, projection;
    @FXML
    private ComboBox cmbZoomFrom, cmbZoomTo;

    public FileItem fileItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.panelStandard.setOnMouseClicked(this::showSelectDialog);
        this.panelAdvance.setOnMouseClicked(this::showSelectDialog);
        this.panelRaster.setOnMouseClicked(this::showSelectDialog);
        this.panelGoogle.setOnMouseClicked(this::showSelectDialog);

        this.panelOutputDir.setOnMouseClicked(this::panelOutputDirClick);
        this.panelOutputMBTiles.setOnMouseClicked(this::panelOutputMBTilesClick);
        this.panelOutputGeopackage.setOnMouseClicked(this::panelOutputGeopackageClick);

        this.btnNext.setOnMouseClicked(this::btnNextClick);
        this.btnProve.setOnMouseClicked(this::btnProveClick);
        this.btnReview.setOnMouseClicked(this::btnReviewClick);
        this.btnOpenFile.setOnMouseClicked(this::btnOpenFileClick);
        this.btnRemove.setOnMouseClicked(this::btnRemoveClick);
        this.btnChangeRSR.setOnMouseClicked(this::btnChangeRSRClick);
        this.btnChangeExtent.setOnMouseClicked(this::btnChangeExtentClick);
        this.btnSettingAdvance.setOnMouseClicked(this::btnSettingAdvanceClick);

        this.listViewFiles.getSelectionModel().selectedItemProperty().addListener(this::noticeListItemChange);

        this.cmbZoomFrom.getItems().addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));
        this.cmbZoomTo.getItems().addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));

        if (fileItem != null) {
            this.projection.textProperty().bindBidirectional(fileItem.projectionProperty());
            this.transform.textProperty().bindBidirectional(fileItem.transformProperty());
            this.projection.setText("");
            this.transform.setText("");
        }
    }

    @FXML
    private void panelOutputGeopackageClick(MouseEvent event) {

    }

    @FXML
    private void panelOutputMBTilesClick(MouseEvent event) {

    }

    @FXML
    private void panelOutputDirClick(MouseEvent event) {
        generateDirTiles();
        navicateToProgressPanel();
    }

    @FXML
    private void btnSettingAdvanceClick(MouseEvent event) {
        AbstractFxmlView view = applicationContext.getBean(AdvanceSettingView.class);
        Stage newStage = new Stage();
        Scene newScene;
        if (view.getView().getScene() != null) {
            newScene = view.getView().getScene();
        } else {
            newScene = new Scene(view.getView());
        }

        newStage.setScene(newScene);
        newStage.initModality(Modality.NONE);
        newStage.initOwner(MainApp.getStage());
        newStage.show();

        advanceSettingViewController.init();
    }

    @FXML
    private void showSelectDialog(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据");
        Stage selectFile = new Stage();
        if (StringUtils.isEmpty(defaultDir))
            defaultDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(defaultDir));

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
            FileItem item = new FileItem(file, 0);
            item.setDataset(dataset);
            listViewFiles.getItems().add(item);
            btnNextClick(null);
        }
    }

    @FXML
    private void btnOpenFileClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据");
        Stage selectFile = new Stage();
        if (StringUtils.isEmpty(defaultDir))
            defaultDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(defaultDir));

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
            FileItem item = new FileItem(file, 0);
            item.setDataset(dataset);
            listViewFiles.getItems().add(item);
        }
    }

    @FXML
    private void btnRemoveClick(MouseEvent event) {
        ObservableList<FileItem> fileItemObservableList = listViewFiles.getSelectionModel().getSelectedItems();
        fileItemObservableList.forEach(fileItem1 -> listViewFiles.getItems().remove(fileItem1));
    }

    @FXML
    private void btnChangeRSRClick(MouseEvent event) {

    }

    @FXML
    private void btnChangeExtentClick(MouseEvent event) {

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

        if (cusPanel.getId().equalsIgnoreCase("panelTileTypeSelect")) {

        } else if (cusPanel.getId().equalsIgnoreCase("panelFileList")) {
            if (listViewFiles.getItems().size() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("告警");
                alert.setHeaderText("没有输入文件");
                alert.setContentText("没有输入文件");

                alert.showAndWait();
                return;
            }

            if (listViewFiles.getItems().size() > 1) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("告警");
                alert.setHeaderText("告警");
                alert.setContentText("目前版本只支持一个影像");

                alert.showAndWait();
                return;
            }
        } else if (cusPanel.getId().equalsIgnoreCase("panelTileSetting")) {
            cmbZoomFrom.getSelectionModel().select(0);
            cmbZoomTo.getSelectionModel().select(0);
            generateDirTiles();
            navicateToProgressPanel();
        } else if (cusPanel.getId().equals("panelProgress")) {

        }

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

    @FXML
    private void btnReviewClick(MouseEvent event) {
        AbstractFxmlView view = applicationContext.getBean(ReviewView.class);
        Stage newStage = new Stage();
        Scene newScene;
        if (view.getView().getScene() != null) {
            newScene = view.getView().getScene();
        } else {
            newScene = new Scene(view.getView());
        }

        newStage.setScene(newScene);
        newStage.initModality(Modality.NONE);
        newStage.initOwner(MainApp.getStage());
        newStage.show();

        reviewViewController.showReview();
    }

    private void navicateToProgressPanel() {
        ObservableList<Node> children = stackPanel.getChildren();
        Integer finalCurrentIndex1 = 4;
        children.forEach(child -> {
            CusPanel panel = (CusPanel) child;
            if (panel.index.get() == finalCurrentIndex1)
                panel.setVisible(true);
            else panel.setVisible(false);
        });
    }

    private void generateDirTiles() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择保存的位置");
        Stage selectFile = new Stage();
        if (StringUtils.isEmpty(defaultDir))
            defaultDir = System.getProperty("user.home");
        directoryChooser.setInitialDirectory(new File(defaultDir));

        File file = directoryChooser.showDialog(selectFile);
        if (file != null) {
            if (listViewFiles.getItems().size() <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("没有输入文件");
                alert.setContentText("没有输入文件");

                alert.showAndWait();
                return;
            }
            FileItem fileItem = (FileItem) listViewFiles.getItems().get(0);
            generateTile(fileItem.getFile(), file);
        }
    }

    public void generateTile(File fileInput, File fileOutput) {
        String[] args = "-profile geodetic E:\\Data\\CAOBAO\\aaa.tif E:\\Data\\CAOBAO\\tiles\\java".split(" ");
        gdal.AllRegister();
        // 注册所有的驱动
        ogr.RegisterAll();
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");
        gdal.SetConfigOption("GDAL_DATA", "gdal-data");
        try {
//                GDAL2Tiles gdal2tiles = new GDAL2Tiles();
//                gdal2tiles.setInput(fileInput.getAbsolutePath());
//                gdal2tiles.setOutput(fileOutput.getAbsolutePath());
//                gdal2tiles.setResampling(EnumResampling.GRA_Average);
//                gdal2tiles.setProfile(EnumProfile.mercator);
//                gdal2tiles.process(null);

            GDAL2TilesMulti gdal2tiles = new GDAL2TilesMulti();
            gdal2tiles.setInput(fileInput.getAbsolutePath());
            gdal2tiles.setOutput(fileOutput.getAbsolutePath());
            gdal2tiles.setResampling(EnumResampling.GRA_Average);
            gdal2tiles.setProfile(EnumProfile.mercator);
            gdal2tiles.process(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        fileItem = (FileItem) newValue;
        if (fileItem.getFile().exists() && fileItem.getDataset() != null) {
            this.projection.setText(fileItem.getProjection());
            this.transform.setText(fileItem.getTransform());
        } else {
            this.projection.setText("");
            this.transform.setText("");
        }
    }

    @FXML
    public Object getSelectItem() {
        return this.listViewFiles.getSelectionModel().getSelectedItem();
    }

}
