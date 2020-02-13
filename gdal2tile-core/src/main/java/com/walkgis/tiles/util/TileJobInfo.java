package com.walkgis.tiles.util;

import org.gdal.gdal.Driver;

import java.util.List;
import java.util.Map;

/**
 * Plain object to hold tile job configuration for a dataset
 **/
public class TileJobInfo {
    public String srcFile = "";
    public Integer nbDataBands = 0;
    public String outputFilePath = "";
    public String tileExtension = "";
    public Integer tileSize = 0;
    public String tileDriver = null;
    public Boolean kml = false;
    public List<int[]> tminmax;
    public Integer tminz = 0;
    public Integer tmaxz = 0;
    public String inSrsWkt = "";
    public double[] outGeoTrans;
    public double ominy;
    public Boolean isEpsg4326 = false;
    public Map<String, Object> options = null;
    public Boolean excludeTransparent = false;

    public TileJobInfo(String srcFile, Integer nbDataBands, String outputFilePath, String tileExtension, Integer tileSize, String tileDriver, Boolean kml, List<int[]> tminmax, Integer tminz, Integer tmaxz, String inSrsWkt, double[] outGeoTrans, Double ominy, Boolean isEpsg4326, Map<String, Object> options, Boolean excludeTransparent) {
        this.srcFile = srcFile;
        this.nbDataBands = nbDataBands;
        this.outputFilePath = outputFilePath;
        this.tileExtension = tileExtension;
        this.tileSize = tileSize;
        this.tileDriver = tileDriver;
        this.kml = kml;
        this.tminmax = tminmax;
        this.tminz = tminz;
        this.tmaxz = tmaxz;
        this.inSrsWkt = inSrsWkt;
        this.outGeoTrans = outGeoTrans;
        this.ominy = ominy;
        this.isEpsg4326 = isEpsg4326;
        this.options = options;
        this.excludeTransparent = excludeTransparent;
    }
}
