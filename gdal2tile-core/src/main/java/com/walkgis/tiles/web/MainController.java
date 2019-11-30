package com.walkgis.tiles.web;

import com.walkgis.tiles.MainApp;
import com.walkgis.tiles.control.CusPanel;
import com.walkgis.tiles.control.LoadingView;
import com.walkgis.tiles.control.MainView;
import com.walkgis.tiles.entity.FileItem;
import com.walkgis.tiles.util.GDAL2Tiles;
import com.walkgis.tiles.util.GeopackageUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


@FXMLController
public class MainController implements Initializable {
    @FXML
    private AnchorPane panelStandard, panelGoogle, panelRaster, panelAdvance;
    @FXML
    private CusPanel panelFileList, panelTileTypeSelect, panelTileSetting;
    @FXML
    private Button btnProve, btnNext, btnReview;
    @FXML
    private StackPane stackPanel;
    @FXML
    private ListView listViewFiles;
    @FXML
    private Label transform, projection;
    @Autowired
    private MainView mainView;

    public FileItem fileItem;

    @FXML
    private void showSelectDialog(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据");
        Stage selectFile = new Stage();
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialDirectory(new File("E:\\Data\\CAOBAO"));

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

    @FXML
    private void btnReviewClick(MouseEvent event) {
        MainApp.showView(LoadingView.class, Modality.WINDOW_MODAL);
    }

    public void generateTile() {
        String[] args = "-profile geodetic E:\\Data\\CAOBAO\\aaa.tif E:\\Data\\CAOBAO\\tiles\\java".split(" ");
        GDAL2Tiles gdal2tiles = new GDAL2Tiles(args);
        try {
            gdal2tiles.process(null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
        fileItem = (FileItem) newValue;
        if (fileItem.getFile().exists() && fileItem.getDataset() != null) {
            this.projection.setText(fileItem.getProjection());
            this.transform.setText(fileItem.getTransform());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.panelStandard.setOnMouseClicked(this::showSelectDialog);
        this.panelAdvance.setOnMouseClicked(this::showSelectDialog);
        this.panelRaster.setOnMouseClicked(this::showSelectDialog);
        this.panelGoogle.setOnMouseClicked(this::showSelectDialog);

        this.btnNext.setOnMouseClicked(this::btnNextClick);
        this.btnProve.setOnMouseClicked(this::btnProveClick);
        this.btnReview.setOnMouseClicked(this::btnReviewClick);

        this.listViewFiles.getSelectionModel().selectedItemProperty().addListener(this::noticeListItemChange);

        if (fileItem != null) {
            this.projection.textProperty().bindBidirectional(fileItem.projectionProperty());
            this.transform.textProperty().bindBidirectional(fileItem.transformProperty());
            this.projection.setText("");
            this.transform.setText("");
        }
    }

    @FXML
    public Object getSelectItem() {
        return this.listViewFiles.getSelectionModel().getSelectedItem();
    }

}
