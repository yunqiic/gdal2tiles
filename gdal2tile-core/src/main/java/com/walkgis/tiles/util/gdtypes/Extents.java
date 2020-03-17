package com.walkgis.tiles.util.gdtypes;

public class Extents {

    public XY lower_left;
    public XY upper_right;

    public Extents( XY lower_left,  XY upper_right) {
        this.lower_left = lower_left;
        this.upper_right = upper_right;
    }

    public boolean contains(Object other) throws Exception {
        if (!(other instanceof Extents)) {
            throw new Exception("!(other instanceof Extents)");
        } else if ((other instanceof  XY)) {
             XY xy = ( XY) other;
            return (lower_left.x <= xy.x && upper_right.x > xy.x) &&
                    (lower_left.y <= xy.y && upper_right.y > xy.y);
        }
        return false;
    }

    public boolean almost_equal(Extents other, Integer places, Double delta) throws Exception {
        return (this.lower_left.almost_equal(other.lower_left, places, delta) &&
                this.upper_right.almost_equal(other.upper_right, places, delta));
    }

    public  XY dimensions() {
        return this.upper_right.sub(this.lower_left);
    }
}