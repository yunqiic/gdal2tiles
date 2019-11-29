/**
 * @author JerFer
 * @date 2019/3/29---13:55
 */
package com.walkgis.tiles;

import javafx.application.Application;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class MainApp extends Application {
    private static MainController mainController;

    public static void main(String[] args) {
        mainController = new MainController();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ComplexApplication.fxml"));

        primaryStage.setTitle("地图切片");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        VBox vBox = (VBox) root;
        vBox.getChildren().forEach(child -> {
            Node panelNode = child;
            if (panelNode instanceof Pane) {
                Pane pane = (Pane) panelNode;
                FilteredList<Node> filteredList = pane.getChildren().filtered(node -> node instanceof GridPane);
                if (filteredList.size() == 0) return;
                GridPane gridPane = (GridPane) filteredList.get(0);

                if (gridPane != null) {
                    gridPane.getChildren().forEach(node -> {
                        if (node instanceof AnchorPane) {
                            AnchorPane anchorPane = (AnchorPane) node;
                            anchorPane.setOnMouseClicked(event -> showSelectDialog(event));
                        }
                    });
                }

            }

        });
        primaryStage.show();
    }

    private void showSelectDialog(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择数据");
        Stage selectFile = new Stage();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GeoPDF", "*.pdf"),
                new FileChooser.ExtensionFilter("GeoTIFF", "*.tif")
        );
        File file = fileChooser.showOpenDialog(selectFile);
        if (file != null) {
            mainController.generateTile();
        }
    }
}
