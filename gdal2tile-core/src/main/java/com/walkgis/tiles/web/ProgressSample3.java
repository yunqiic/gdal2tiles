package com.walkgis.tiles.web;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProgressSample3 extends Application {

    int num = 0;
    int num1 = 1 * 100;//这是是需要设置的时间，现在是1秒
    int num2 = 3 * 100;//下个循环的时间，现在为3秒
    boolean flag = true;

    Service<Integer> service = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {

            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    int i = 0;
                    num = num1;

                    while (flag) {
                        updateProgress(i++, num);
                        Thread.sleep(10);
                        if (i == num) {
                            if (num == num1) {
                                i = 0;
                                num = num2;
                            } else {
                                i = 0;
                                num = num1;
                            }
                        }
                    }
                    return null;
                }
            };
        }
    };

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 300, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("测试闹钟");
        Label label = new Label("进度");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0);
        progressBar.setPrefWidth(200);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setPrefHeight(60);
        hBox.getChildren().addAll(label, progressBar);

        Button button1 = new Button("开始");
        button1.setOnMouseClicked(event -> {
            progressBar.progressProperty().bind(service.progressProperty());
            service.start();
        });

        Button button2 = new Button("restart");
        button2.setOnMouseClicked(event -> {
            progressBar.progressProperty().bind(service.progressProperty());
            service.restart();
        });

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(hBox, button1, button2);
        scene.setRoot(vBox);
// primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
