package com.walkgis.tiles.util;

public enum EnumResampling {
    GRA_NearestNeighbour(0), GRA_Bilinear(1), GRA_Cubic(2), GRA_CubicSpline(3), GRA_Lanczos(4), GRA_Average(5), Other(-1);
    private int value;

    EnumResampling(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
