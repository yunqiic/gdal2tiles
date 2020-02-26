package com.walkgis.tiles.web.ctrl;

import com.walkgis.tiles.entity.ProgressModelProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelProgressController implements Initializable {

    @FXML
    private Label lblProgressTop, lblProgressRate, lblProgressBottom;
    @FXML
    public ProgressBar probressBar;

    public static ProgressModelProperty progressModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        probressBar.progressProperty().bind(progressModel.valueProperty());
    }
}
