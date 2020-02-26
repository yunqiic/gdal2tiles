package com.walkgis.tiles.util;

import com.walkgis.tiles.entity.ProgressModelProperty;
import javafx.concurrent.Task;
import org.gdal.gdal.gdal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunTask extends Task<Void> {
    private Logger logger = LoggerFactory.getLogger(RunTask.class);
    private String input_file;
    private String output_folder;
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private ProgressModelProperty modelProperty;

    public RunTask(String input_file, String output_folder, ProgressModelProperty modelProperty) {
        this.input_file = input_file;
        this.output_folder = output_folder;
        this.modelProperty = modelProperty;
    }

    private void single_threaded_tiling(String input_file, String output_folder, OptionObj options) throws Exception {
        List<TileDetail> tile_details = new ArrayList<>();
        TileJobInfo conf = CommonUtils.worker_tile_details(input_file, output_folder, options, tile_details);

        logger.debug("Tiles details calc complete.");

        ProgressBar progress_bar = new ProgressBar(tile_details.size(), modelProperty);
        progress_bar.start();

        for (TileDetail tileDetail : tile_details) {
            CommonUtils.create_base_tile(conf, tileDetail);
            progress_bar.log_progress();
        }

        CommonUtils.cachedDs = null;

        CommonUtils.create_overview_tiles(conf, output_folder, options, modelProperty);
    }

    private void multi_threaded_tiling(String input_file, String output_folder, OptionObj options) {
        int nb_processes = options.nb_processes == null ? 1 : options.nb_processes;
        gdal.SetConfigOption("GDAL_CACHEMAX", String.valueOf(gdal.GetCacheMax() / nb_processes));

        service = Executors.newFixedThreadPool(nb_processes, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        List<TileDetail> tile_details = new ArrayList<>();

        try {
            TileJobInfo conf = CommonUtils.worker_tile_details(input_file, output_folder, options, tile_details);

            ProgressBar progress_bar = new ProgressBar(tile_details.size(), modelProperty);
            progress_bar.start();
            for (int i = 0; i < tile_details.size(); i++) {
                service.execute(new CommonUtils.BaseTileTask(conf, tile_details.get(i)));
                progress_bar.log_progress();
            }

            service.shutdown();
            try {
                service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                CommonUtils.create_overview_tiles(conf, output_folder, options, modelProperty);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void call() {
        OptionObj options = new OptionObj();
        options.nb_processes = 8;
        try {
            if (options.nb_processes == 1) {
                single_threaded_tiling(input_file, output_folder, options);
            } else {
                multi_threaded_tiling(input_file, output_folder, options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
