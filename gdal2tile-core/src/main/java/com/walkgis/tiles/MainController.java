package com.walkgis.tiles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainController{
    @FXML
    private Button myButton;

    @FXML
    private TextField myTextField;

    public void showDateTime(ActionEvent event) {
        System.out.println("Button Clicked!");

        Date now = new Date();

        DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        String dateTimeString = df.format(now);
        // Show in VIEW
        myTextField.setText(dateTimeString);
    }
}
