package com.walkgis.tiles.util.gdtypes;

import java.math.BigDecimal;

public class XY {
    public double x;
    public double y;

    public XY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public XY add(XY other) {
        return new XY(this.x + other.x, this.y + other.y);
    }

    public XY sub(XY other) {
        return new XY(this.x - other.x, this.y - other.y);
    }

    public XY mul(double other) {
        return new XY(this.x * other, this.y * other);
    }

    public XY truediv(double other) {
        return new XY(this.x / other, this.y / other);
    }

    public XY floor() {
        return new XY((int) this.x, (int) this.y);
    }

    public boolean almost_equal(XY other, Integer places, Double delta) throws Exception {
        if (this.x == other.x && this.y == other.y) {
            return true;
        }
        if (places != null && places != null) {
            throw new Exception("places != null && places != null");
        }
        if (delta != null) {
            return (Math.abs(this.x - other.x) <= delta && Math.abs(this.y - other.y) <= delta);
        }
        if (places == null) {
            places = 7;
        }
        BigDecimal bigDecimal = new BigDecimal(Math.abs(other.x - this.x));
        BigDecimal bigDecimal1 = new BigDecimal(Math.abs(other.y - this.y));
        bigDecimal.setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
        return (bigDecimal.setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue() == 0 &&
                bigDecimal1.setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue() == 0);
    }

}