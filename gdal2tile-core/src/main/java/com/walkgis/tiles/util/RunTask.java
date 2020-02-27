package com.walkgis.tiles.util;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTask extends Task<Integer> {
    private Logger logger = LoggerFactory.getLogger(RunTask.class);
    private GDAL2Tiles gdal2TilesTemp;
    private RunTask runTask;


    public RunTask(GDAL2Tiles gdal2TilesTemp) {
        this.gdal2TilesTemp = gdal2TilesTemp;
        this.runTask = this;
    }

    @Override
    protected void updateValue(Integer integer) {
        super.updateValue(integer);
    }

    @Override
    protected void updateMessage(String message) {
        super.updateMessage(message);
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

    public void reset(int size) {

    }
}
