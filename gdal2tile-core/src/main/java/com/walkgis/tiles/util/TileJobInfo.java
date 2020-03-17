package com.walkgis.tiles.util;


import com.walkgis.tiles.util.storage.Storage;

import java.util.List;

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
    public OptionObj options = null;
    public boolean excludeTransparent = false;
    public TileSwne tileSwne;
    public Storage storage;


    public TileJobInfo(TileSwne tileSwne,Storage storage, String srcFile, Integer nbDataBands, String outputFilePath,
                       String tileExtension, String tileDriver, int tileSize,
                       boolean kml, List<int[]> tminmax, int tminz, int tmaxz,
                       String inSrsWkt, double[] outGeoTrans, double ominy,
                       boolean isEpsg4326, OptionObj options, boolean excludeTransparent) {
        this.tileSwne = tileSwne;
        this.storage = storage;
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
