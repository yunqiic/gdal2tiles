package com.walkgis.tiles.web.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class ZoomComboboxModel {
    private ObservableList<Integer> zoomFrom;
    private ObservableList<Integer> zoomTo;
    private IntegerProperty zoomFromValue;
    private IntegerProperty zoomToValue;

    public ZoomComboboxModel() {
        this.zoomFrom = FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));
        this.zoomTo = FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));

        this.zoomFromValue = new SimpleIntegerProperty(this, "zoomFromValue", 1);
        this.zoomToValue = new SimpleIntegerProperty(this, "zoomToValue", 18);
    }

    public ObservableList<Integer> getZoomFrom() {
        return zoomFrom;
    }

    public void setZoomFrom(ObservableList<Integer> zoomFrom) {
        this.zoomFrom = zoomFrom;
    }

    public ObservableList<Integer> getZoomTo() {
        return zoomTo;
    }

    public void setZoomTo(ObservableList<Integer> zoomTo) {
        this.zoomTo = zoomTo;
    }

    public ObservableValue getZoomFromValue() {
        return zoomFromValue;
    }

    public void setZoomFromValue(Integer zoomFromValue) {
        this.zoomFromValue.setValue(zoomFromValue);
    }

    public ObservableValue getZoomToValue() {
        return zoomToValue;
    }

    public void setZoomToValue(Integer zoomToValue) {
        this.zoomToValue.setValue(zoomToValue);
    }

    public IntegerProperty zoomFromValueProperty() {
        return zoomFromValue;
    }

    public IntegerProperty zoomToValueProperty() {
        return zoomToValue;
    }
}
