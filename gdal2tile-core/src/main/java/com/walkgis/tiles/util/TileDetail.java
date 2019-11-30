package com.walkgis.tiles.util;

public class TileDetail {
    private Integer tx = 0;
    private Integer ty = 0;
    private Integer tz = 0;
    private Integer rx = 0;
    private Integer ry = 0;
    private Integer rxsize = 0;
    private Integer rysize = 0;
    private Integer wx = 0;
    private Integer wy = 0;
    private Integer wxsize = 0;
    private Integer wysize = 0;
    private Integer querysize = 0;

	public TileDetail(Integer tx, Integer ty, Integer tz, Integer rx, Integer ry, Integer rxsize, Integer rysize, Integer wx, Integer wy, Integer wxsize, Integer wysize, Integer querysize) {
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.rx = rx;
		this.ry = ry;
		this.rxsize = rxsize;
		this.rysize = rysize;
		this.wx = wx;
		this.wy = wy;
		this.wxsize = wxsize;
		this.wysize = wysize;
		this.querysize = querysize;
	}

	public String toString() {
        return String.format("TileDetail %s\n%s\n%s\n", this.tx, this.ty, this.tz);
    }

}
