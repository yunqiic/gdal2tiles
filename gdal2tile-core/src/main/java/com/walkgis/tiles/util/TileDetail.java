package com.walkgis.tiles.util;

public class TileDetail {
    public Integer tx = 0;
	public Integer ty = 0;
	public Integer tz = 0;
	public Integer rx = 0;
	public Integer ry = 0;
	public Integer rxsize = 0;
	public Integer rysize = 0;
	public Integer wx = 0;
	public Integer wy = 0;
	public Integer wxsize = 0;
	public Integer wysize = 0;
	public Integer querysize = 0;

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
        return String.format("当前进行 %s-%s-%s 切片", this.tx, this.ty, this.tz);
    }

}
