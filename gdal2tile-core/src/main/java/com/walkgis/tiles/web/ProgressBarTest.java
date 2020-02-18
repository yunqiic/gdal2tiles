package com.walkgis.tiles.web;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ProgressBarTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProgressBar progressBar = new ProgressBar();

        Random rng = new Random();

        TestTask testTask = new TestTask(rng.nextInt(3000) + 2000, rng.nextInt(30) + 20);

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(testTask.progressProperty());


        BorderPane root = new BorderPane();
        root.setCenter(progressBar);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        ExecutorService executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        executor.execute(testTask);
    }

    public static void main(String[] args) {
        launch(args);
    }

    static class TestTask extends Task<Void> {

        private final int waitTime; // milliseconds
        private final int pauseTime; // milliseconds

        public static final int NUM_ITERATIONS = 100;

        TestTask(int waitTime, int pauseTime) {
            this.waitTime = waitTime;
            this.pauseTime = pauseTime;
        }

        @Override
        protected Void call() throws Exception {
            this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
            this.updateMessage("Waiting...");
            Thread.sleep(waitTime);
            this.updateMessage("Running...");
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                updateProgress((1.0 * i) / NUM_ITERATIONS, 1);
                Thread.sleep(pauseTime);
            }
            this.updateMessage("Done");
            this.updateProgress(1, 1);
            return null;
        }

    }
}
