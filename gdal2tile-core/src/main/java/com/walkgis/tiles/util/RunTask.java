package com.walkgis.tiles.util;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RunTask extends Task<Integer> {
    private final static Log logger = LogFactory.getLog(RunTask.class);
    private GDAL2Tiles gdal2TilesTemp;
    private RunTask runTask;
    private SimpleStringProperty rateProperty = new SimpleStringProperty();

    public RunTask(GDAL2Tiles gdal2TilesTemp) {
        this.gdal2TilesTemp = gdal2TilesTemp;
        this.runTask = this;
    }

    @Override
    protected void updateMessage(String message) {
        super.updateMessage(message);
    }

    @Override
    protected void updateTitle(String title) {
        super.updateTitle(title);
    }

    @Override
    protected void updateProgress(double workDone, double totalWork) {
        super.updateProgress(workDone, totalWork);
        if (!Platform.isFxApplicationThread())
            Platform.runLater(() -> rateProperty.setValue((int) ((workDone / totalWork) * 100) + "%"));
    }

    public final ReadOnlyStringProperty rateProperty() {
        return this.rateProperty;
    }

    @Override
    public Integer call() {
        try {
            if (gdal2TilesTemp.getOptions().nb_processes == 1) {
                CommonUtils.single_threaded_tiling(gdal2TilesTemp, runTask);
            } else {
                CommonUtils.multi_threaded_tiling(gdal2TilesTemp, runTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
