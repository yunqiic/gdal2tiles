package com.walkgis.tiles.web.entity;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ProgressModelProperty {
    private DoubleProperty value;

    public ProgressModelProperty() {
        value = new SimpleDoubleProperty();
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }
}
