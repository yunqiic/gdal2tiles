package com.walkgis.tiles.web.ctrl;

import com.walkgis.tiles.util.GDAL2Tiles;
import com.walkgis.tiles.util.RunTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelProgressController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(PanelProgressController.class);

    @FXML
    private Label lblProgressTop, lblProgressRate, lblProgressBottom;
    @FXML
    public ProgressBar probressBar;

    private double STEMP;
    private int total_items;
    private int nb_items_done;
    private int current_progress;

    public PanelProgressController() {
        this.total_items = total_items;
        this.nb_items_done = 0;
        this.current_progress = 0;
        this.STEMP = 2.5;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void init(GDAL2Tiles gdal2TilesTemp) {
        RunTask task = new RunTask(gdal2TilesTemp);
        probressBar.progressProperty().bind(task.progressProperty());
        lblProgressBottom.textProperty().bind(task.messageProperty());
        lblProgressTop.textProperty().bind(task.titleProperty());
        lblProgressRate.textProperty().bind(task.rateProperty());

//        task.workDoneProperty().addListener(((observableValue, oldValue, newValue) -> {
//            nb_items_done += 1;
//            double progress = (double) nb_items_done / total_items * 100.0;
//            if (progress >= (current_progress + STEMP)) {
//                boolean done = false;
//                while (!done) {
//                    if (current_progress + STEMP <= progress) {
//                        current_progress += STEMP;
//                        if (current_progress % 10 == 0) {
//
//                            probressBar.setProgress(current_progress);
//                            lblProgressRate.setText(current_progress + "%");
//
//                            if (this.current_progress == 100) {
//                                logger.debug("\n");
//                            }
//                        } else {
//                            logger.debug(".");
//                        }
//                    } else done = true;
//                }
//            }
//        }));
        new Thread(task).start();
    }
}
