package com.walkgis.tiles.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.Pane;

public class CusPanel extends Pane {
    public SimpleIntegerProperty index = new SimpleIntegerProperty(this, "index");
    public SimpleIntegerProperty currentIndex = new SimpleIntegerProperty(this, "currentIndex");

    public int getIndex() {
        return index.get();
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public int getCurrentIndex() {
        return currentIndex.get();
    }

    public SimpleIntegerProperty currentIndexProperty() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex.set(currentIndex);
    }
}
