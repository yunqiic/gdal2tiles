package com.walkgis.tiles.test.terrain;

public class SrtmTileArgs {
    private String version = "1.0";
    private String profile = "mercator";
    private String resampling = "average";
    private String sSrs;
    private String zoom;
    private Boolean resume = false;
    private String srcnodata ;
    private Boolean verbose = false;
    private String title;
    /**
     * Join exist tiles and new generating tiles. Work if stored tile size == genereting tile size
     */
    private Boolean join = true;
    /**
     * Input srtm data. This mind what will be ganerated tiles without alpha channel
     */
    private Boolean srtm = true;
    /**
     * Will be generated terrain for Cesium from srtm data( --tile-driver=EHdr, --tile-data=Int16 )
     */
    private Boolean cesium = false;
    /**
     * Output tile size
     */
    private Integer tileSize = 256;

    private String tileDriver = "PNG";
    private String tileExt = "png";
    private String tileData = "Byte";
    /**
     * Raster which will be use for water mask on generate terrain tiles(use create_swbd_for_srtm.py for generate raster)
     */
    private Boolean waterMask = false;
    private String output;
    private String input;
    private String url;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getResampling() {
        return resampling;
    }

    public void setResampling(String resampling) {
        this.resampling = resampling;
    }

    public String getsSrs() {
        return sSrs;
    }

    public void setsSrs(String sSrs) {
        this.sSrs = sSrs;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    public Boolean getResume() {
        return resume;
    }

    public void setResume(Boolean resume) {
        this.resume = resume;
    }

    public String getSrcnodata() {
        return srcnodata;
    }

    public void setSrcnodata(String srcnodata) {
        this.srcnodata = srcnodata;
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }

    public Boolean getJoin() {
        return join;
    }

    public void setJoin(Boolean join) {
        this.join = join;
    }

    public Integer getTileSize() {
        return tileSize;
    }

    public void setTileSize(Integer tileSize) {
        this.tileSize = tileSize;
    }

    public String getTileDriver() {
        return tileDriver;
    }

    public void setTileDriver(String tileDriver) {
        this.tileDriver = tileDriver;
    }

    public String getTileExt() {
        return tileExt;
    }

    public void setTileExt(String tileExt) {
        this.tileExt = tileExt;
    }

    public String getTileData() {
        return tileData;
    }

    public void setTileData(String tileData) {
        this.tileData = tileData;
    }

    public Boolean getWaterMask() {
        return waterMask;
    }

    public void setWaterMask(Boolean waterMask) {
        this.waterMask = waterMask;
    }

    public Boolean getCesium() {
        return cesium;
    }

    public void setCesium(Boolean cesium) {
        this.cesium = cesium;
    }

    public Boolean getSrtm() {
        return srtm;
    }

    public void setSrtm(Boolean srtm) {
        this.srtm = srtm;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
