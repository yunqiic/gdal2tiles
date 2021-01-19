package com.walkgis.tiles.test.terrain;

import com.walkgis.tiles.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Gdal2Tiles {
    private static final Log logger = LogFactory.getLog(Gdal2Tiles.class);
    private Integer tminz;
    private Integer tmaxz;
    private Integer resampling;
    private boolean overviewquery;
    private int querysize;
    private boolean scaledquery;
    private Class cesiumdata;
    private int tiledata;
    private String tiledriver;
    private String tileext;
    private boolean stoped = false;
    private String input;
    private String output;
    private Integer tilesize;
    private SrtmTileArgs options;
    private Driver outDrv;
    private Driver memDrv;
    private Dataset inDs;
    private List<Double> inNodata;
    private SpatialReference inSrs;
    private String inSrsWkt;
    private SpatialReference outSrs;
    private Dataset outDs;
    private Band alphaband;
    private int dataBandsCount;
    private boolean isepsg4326;
    private boolean kml;
    private double[] outGt;
    private double ominx;
    private double omaxx;
    private double omaxy;
    private double ominy;
    private GlobalMercator mercator;
    private LinkedList<int[]> tminmax;
    private GlobalGeodetic geodetic;
    private int nativezoom;
    private double[] tsize;
    private TileSwneInterface tileswne;
    private TileSwneRasterInterface tileswneRaster;
    private double[] swne;

    private String pt2fmt(Integer pt) {
        Map<Integer, String> fmttypes = new HashMap<>();

        fmttypes.put(gdalconstConstants.GDT_Byte, "B");
        fmttypes.put(gdalconstConstants.GDT_Int16, "h");
        fmttypes.put(gdalconstConstants.GDT_UInt16, "H");
        fmttypes.put(gdalconstConstants.GDT_Int32, "i");
        fmttypes.put(gdalconstConstants.GDT_UInt32, "I");
        fmttypes.put(gdalconstConstants.GDT_Float32, "f");
        fmttypes.put(gdalconstConstants.GDT_Float64, "f");
        if (fmttypes.containsKey(pt))
            return fmttypes.get(pt);
        else return "X";
    }

    public void process() throws Exception {
        this.openInput();
        this.generateMetadata();
        this.generateBaseTiles();
        this.generateOverviewTiles();
    }

    public void progressbar(Double complete) {
        System.out.println(complete);
    }

    private void stop() {
        this.stoped = true;
    }

    public Gdal2Tiles(SrtmTileArgs args) throws Exception {
        this.stoped = false;
        this.input = null;
        this.output = null;

        this.options = args;

        if (options.getCesium()) {
            this.tilesize = 65;
            this.tiledriver = "EHdr";
            this.tileext = "terrain";
            this.tiledata = gdalconstConstants.GDT_Int16;
            this.options.setSrtm(true);
            this.cesiumdata = Short.class;
        } else {
            this.tilesize = this.options.getTileSize();
            this.tileext = this.options.getTileExt();
            this.tiledriver = this.options.getTileDriver();

            if (this.options.getTileData().equalsIgnoreCase("Byte")) {
                this.tiledata = gdalconstConstants.GDT_Byte;
                this.cesiumdata = Byte.class;
            } else if (this.options.getTileData().equalsIgnoreCase("Int16")) {
                this.tiledata = gdalconstConstants.GDT_Int16;
                this.cesiumdata = Short.class;
            } else if (this.options.getTileData().equalsIgnoreCase("UInt16")) {
                this.tiledata = gdalconstConstants.GDT_UInt16;
                this.cesiumdata = Short.class;
            } else if (this.options.getTileData().equalsIgnoreCase("UInt32")) {
                this.tiledata = gdalconstConstants.GDT_UInt32;
                this.cesiumdata = Integer.class;
            } else if (this.options.getTileData().equalsIgnoreCase("Int32")) {
                this.tiledata = gdalconstConstants.GDT_Int32;
                this.cesiumdata = Integer.class;
            } else if (this.options.getTileData().equalsIgnoreCase("Float32")) {
                this.tiledata = gdalconstConstants.GDT_Float32;
                this.cesiumdata = Float.class;
            } else if (this.options.getTileData().equalsIgnoreCase("Float64")) {
                this.tiledata = gdalconstConstants.GDT_Float64;
                this.cesiumdata = Double.class;
            } else if (this.options.getTileData().equalsIgnoreCase("CInt16")) {
                this.tiledata = gdalconstConstants.GDT_CInt16;
                this.cesiumdata = null;
            } else if (this.options.getTileData().equalsIgnoreCase("CInt32")) {
                this.tiledata = gdalconstConstants.GDT_CInt32;
                this.cesiumdata = null;
            } else if (this.options.getTileData().equalsIgnoreCase("CFloat32")) {
                this.tiledata = gdalconstConstants.GDT_CFloat32;
                this.cesiumdata = null;
            } else if (this.options.getTileData().equalsIgnoreCase("CFloat64")) {
                this.tiledata = gdalconstConstants.GDT_CFloat64;
                this.cesiumdata = null;
            } else {
                this.tiledata = gdalconstConstants.GDT_Byte;
                this.cesiumdata = Byte.class;
            }
        }

        this.scaledquery = true;
        this.querysize = 4 * this.tilesize;

        this.overviewquery = false;

        if (args == null) throw new Exception("No input file specified", null);

        if (!(this.options.getVerbose() && this.options.getResampling().equalsIgnoreCase("near"))) {
            throw new Exception("This version of GDAL is not supported. Please upgrade to 1.6+.");
        }

        if (!StringUtils.isEmpty(args.getOutput()) && new File(args.getOutput()).exists()) {
            this.output = args.getOutput();
        }

        this.input = args.getInput();

        if (!new File(this.output).exists())
            this.output = new File(this.input).getParent();

        this.resampling = null;


        if (this.options.getResampling().equalsIgnoreCase("near")) {
            this.resampling = gdalconstConstants.GRA_NearestNeighbour;
            this.querysize = this.tilesize;
        } else if (this.options.getResampling().equalsIgnoreCase("bilinear")) {
            this.resampling = gdalconstConstants.GRA_NearestNeighbour;
            this.querysize = this.tilesize * 2;
        } else if (this.options.getResampling().equalsIgnoreCase("cubic")) {
            this.resampling = gdalconstConstants.GRA_Cubic;
        } else if (this.options.getResampling().equalsIgnoreCase("cubicspline")) {
            this.resampling = gdalconstConstants.GRA_CubicSpline;
        } else if (this.options.getResampling().equalsIgnoreCase("lanczos")) {
            this.resampling = gdalconstConstants.GRA_Lanczos;
        }

        this.tminz = null;
        this.tmaxz = null;

        if (!StringUtils.isEmpty(this.options.getZoom())) {
            String[] minmax = this.options.getZoom().split("-");

            if (minmax.length == 1)
                this.tminz = Integer.parseInt(minmax[0]);
            else if (minmax.length == 2) {
                this.tminz = Integer.parseInt(minmax[0]);
                this.tmaxz = Integer.parseInt(minmax[1]);
            }
        }

        if (this.options.getVerbose()) {
            System.out.println("Options:" + this.options);
            System.out.println("Input:" + this.input);
            System.out.println("Output:" + this.output);
            System.out.println(String.format("Cache: %s MB", (gdal.GetCacheMax() / 1024 / 1024)));
        }
    }

    private void openInput() throws Exception {
        this.outDrv = gdal.GetDriverByName(this.tiledriver);
        this.memDrv = gdal.GetDriverByName("MEM");

        if (this.outDrv == null)
            throw new Exception(String.format("The '%s' driver was not found, is it available in this GDAL build?", this.tiledriver));
        if (this.memDrv == null)
            throw new Exception("The 'MEM' driver was not found, is it available in this GDAL build?");

        if (!StringUtils.isEmpty(this.input))
            this.inDs = gdal.Open(this.input, gdalconstConstants.GA_ReadOnly);
        else throw new Exception("No input file was specified");

        if (this.inDs == null)
            throw new Exception(String.format("It is not possible to open the input file '%s'.", this.input));

        if (this.inDs.GetRasterCount() == 0)
            throw new Exception(String.format("Input file '%s' has no raster band", this.input));

        if (this.inDs.GetRasterBand(1).GetRasterColorTable() != null) {
            throw new Exception(String.format("Please convert this file to RGB/RGBA and run gdal2tiles on the result. From paletted file you can create RGBA file (temp.vrt) by: gdal_translate -of vrt -expand rgba %s temp.vrt then run: gdal2tiles temp.vrt", this.input));
        }

        if (this.options.getVerbose())
            System.out.println(String.format("Input file:( %sP x %sL - %s bands)", this.inDs.GetRasterXSize(), this.inDs.getRasterYSize(), this.inDs.getRasterCount()));

        this.inNodata = new ArrayList<>();
        for (int i = 1; i < this.inDs.GetRasterCount() + 1; i++) {
            Double[] tmp = new Double[1];
            this.inDs.GetRasterBand(i).GetNoDataValue(tmp);
            if (tmp.length > 0)
                this.inNodata.add(tmp[0]);
        }

        if (!StringUtils.isEmpty(this.options.getSrcnodata())) {
            List<Double> nds = Arrays.stream(this.options.getSrcnodata().split(",")).map(a -> Double.parseDouble(a)).collect(Collectors.toList());
            if (nds.size() < this.inDs.GetRasterCount())
//                this.inNodata = (nds.size() * this.inDs.GetRasterCount());
                this.inNodata = new ArrayList<>();
            else this.inNodata = nds;
        }

        if (this.options.getVerbose())
            System.out.println(String.format("NODATA: %s", this.inNodata));

        if (this.options.getVerbose())
            System.out.println(String.format("Preprocessed file:", "( %sP x %sL - %s bands)", this.inDs.GetRasterXSize(), this.inDs.getRasterYSize(), this.inDs.getRasterCount()));


        this.inSrs = null;
        if (!StringUtils.isEmpty(this.options.getsSrs())) {
            this.inSrs = new SpatialReference();
            this.inSrs.SetFromUserInput(this.options.getsSrs());
            this.inSrsWkt = this.inSrs.ExportToWkt();
        } else {
            this.inSrsWkt = this.inDs.GetProjection();
            if (!StringUtils.isEmpty(this.inSrsWkt) && this.inDs.GetGCPCount() != 0) {
                this.inSrsWkt = this.inDs.GetGCPProjection();
            }
            if (!StringUtils.isEmpty(this.inSrsWkt)) {
                this.inSrs = new SpatialReference();
                this.inSrs.ImportFromWkt(this.inSrsWkt);
            }
        }

        this.outSrs = new SpatialReference();

        if (this.options.getProfile().equalsIgnoreCase("mercator"))
            this.outSrs.ImportFromEPSG(900913);
        else if (this.options.getProfile().equalsIgnoreCase("geodetic"))
            this.outSrs.ImportFromEPSG(4326);
        else this.outSrs = this.inSrs;

        this.outDs = null;
        if (this.options.getProfile().equalsIgnoreCase("mercator") || this.options.getProfile().equalsIgnoreCase("geodetic")) {
            if (this.inDs.GetGeoTransform() == new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0}
                    && this.inDs.GetGCPCount() == 0) {
                throw new Exception("There is no georeference - neither affine transformation (worldfile) nor GCPs. You can generate only 'raster' profile tiles. Either gdal2tiles with parameter -p 'raster' or use another GIS software for georeference e.g. gdal_transform -gcp / -a_ullr / -a_srs");
            }

            if (this.inSrs != null) {
                if (!this.inSrs.GetAuthorityCode("GEOGCS").equalsIgnoreCase(this.outSrs.GetAuthorityCode("GEOGCS"))
                        || this.inDs.GetGCPCount() != 0) {

                    this.outDs = gdal.AutoCreateWarpedVRT(this.inDs, this.inSrsWkt, this.outSrs.ExportToWkt());
                    if (this.options.getVerbose()) {
                        System.out.println("Warping of the raster by AutoCreateWarpedVRT (result saved into 'tiles.vrt')");
                        this.outDs.GetDriver().CreateCopy("tiles.vrt", this.outDs);
                    }

                    if (this.inNodata.size() > 0) {
                        File tempfilename = File.createTempFile("-gdal2tiles", ".vrt");
                        this.outDs.GetDriver().CreateCopy(tempfilename.getAbsolutePath(), this.outDs);
                        String s = FileUtils.readFileToString(tempfilename, "UTF-8");

                        s = s.replace("<GDALWarpOptions>", "<GDALWarpOptions><Option name=\"INIT_DEST\">NO_DATA</Option><Option name=\"UNIFIED_SRC_NODATA\">YES</Option>");
                        for (int i = 0; i < this.inNodata.size(); i++) {
                            s = s.replace(
                                    String.format("<BandMapping src=\"%d\" dst=\"%d\"/>", i + 1, i + 1),
                                    String.format("<BandMapping src=\"%d\" dst=\"%d\"><SrcNoDataReal>%f</SrcNoDataReal><SrcNoDataImag>0</SrcNoDataImag><DstNoDataReal>%f</DstNoDataReal><DstNoDataImag>0</DstNoDataImag></BandMapping>", i + 1, i + 1, this.inNodata.get(i), this.inNodata.get(i))
                            );
                        }
                        FileUtils.writeStringToFile(tempfilename, s);
                        this.outDs = gdal.Open(tempfilename.getAbsolutePath());
                        tempfilename.delete();

                        if (this.inNodata.size() == 1) {
                            this.outDs.SetMetadataItem("NODATA_VALUES", String.format("%f", this.inNodata.get(0)));
                        } else
                            this.outDs.SetMetadataItem("NODATA_VALUES", String.format("%f %f %f", this.inNodata.get(0), this.inNodata.get(1), this.inNodata.get(2)));

                        if (this.options.getVerbose()) {
                            System.out.println("Modified warping result saved into 'tiles1.vrt'");
                            FileUtils.writeStringToFile(new File("tiles1.vrt"), s);
                        }
                    }

                    if (this.inNodata.size() == 0 && (this.outDs.GetRasterCount() == 1 || this.outDs.GetRasterCount() == 3)) {
                        File tempfilename = File.createTempFile("-gdal2tiles", ".vrt");
                        this.outDs.GetDriver().CreateCopy(tempfilename.getAbsolutePath(), this.outDs);
                        String s = FileUtils.readFileToString(tempfilename, "UTF-8");

                        s = s.replace("<BlockXSize>", String.format("<VRTRasterBand dataType=\"Byte\" band=\"%d\" subClass=\"VRTWarpedRasterBand\"><ColorInterp>Alpha</ColorInterp></VRTRasterBand><BlockXSize>", this.outDs.getRasterCount() + 1));
                        s = s.replace("</GDALWarpOptions>", String.format("<DstAlphaBand>%d</DstAlphaBand></GDALWarpOptions>", this.outDs.GetRasterCount() + 1));
                        s = s.replace("</WorkingDataType>", "</WorkingDataType><Option name=\"INIT_DEST\">0</Option>");

                        FileUtils.writeStringToFile(tempfilename, s);

                        this.outDs = gdal.Open(tempfilename.getAbsolutePath());
                        tempfilename.delete();

                        if (this.options.getVerbose()) {
                            System.out.println("Modified -dstalpha warping result saved into 'tiles1.vrt'");
                            FileUtils.writeStringToFile(new File("tiles1.vrt"), s);
                        }
                    }
                }
            } else {
                throw new Exception("Input file has unknown SRS.\nUse --s_srs ESPG:xyz (or similar) to provide source reference system.");
            }

            if (this.outDs != null && this.options.getVerbose()) {
                System.out.println(String.format("Projected file:\ntiles.vrt", "( %sP x %sL - %s bands)", this.outDs.getRasterXSize(), this.outDs.getRasterYSize(), this.outDs.getRasterCount()));
            }
        }

        if (this.outDs == null)
            this.outDs = this.inDs;

        this.alphaband = this.outDs.GetRasterBand(1).GetMaskBand();

        if ((this.alphaband.GetMaskFlags() == gdalconstConstants.GMF_ALPHA) ||
                this.outDs.GetRasterCount() == 4 ||
                this.outDs.GetRasterCount() == 2) {
            this.dataBandsCount = this.outDs.GetRasterCount() - 1;
        } else
            this.dataBandsCount = this.outDs.GetRasterCount();


        this.isepsg4326 = false;
        SpatialReference srs4326 = new SpatialReference();
        srs4326.ImportFromEPSG(4326);

        if (this.outSrs != null && srs4326.ExportToProj4().equalsIgnoreCase(this.outSrs.ExportToProj4())) {
            this.kml = true;
            this.isepsg4326 = true;
            if (this.options.getVerbose())
                System.out.println("KML autotest OK!");
        }

        this.outGt = this.outDs.GetGeoTransform();

        if (this.outGt[2] != 0 && this.outGt[4] != 0)
            throw new Exception("Georeference of the raster contains rotation or skew. Such raster is not supported. Please use gdalwarp first.");

        this.ominx = this.outGt[0];
        this.omaxx = this.outGt[0] + this.outDs.GetRasterXSize() * this.outGt[1];
        this.omaxy = this.outGt[3];
        this.ominy = this.outGt[3] - this.outDs.GetRasterYSize() * this.outGt[1];

        if (this.options.getVerbose())
            System.out.println(String.format("Bounds (output srs):[%f,%f,%f,%f]", this.ominx, this.ominy, this.omaxx, this.omaxy));


        if (this.options.getProfile().equalsIgnoreCase("mercator")) {
            this.mercator = new GlobalMercator(256);
            this.tileswne = (int tx, int ty, int zoom) -> {
                double[] bounds = mercator.tileBounds(tx, ty, zoom);
                double[] minLatLon = mercator.metersToLatLon(bounds[0], bounds[1]);
                double[] maxLatlon = mercator.metersToLatLon(bounds[2], bounds[3]);
                return new double[]{minLatLon[0], minLatLon[1], maxLatlon[0], maxLatlon[1]};
            };

            this.tminmax = new LinkedList<>();
            for (int tz = 0; tz < 32; tz++) {
                int[] tminxy = this.mercator.metersToTile(this.ominx, this.ominy, tz);
                int[] tmaxxy = this.mercator.metersToTile(this.omaxx, this.omaxy, tz);

                tminxy = new int[]{Math.max(0, tminxy[0]), Math.max(0, tminxy[1])};
                tmaxxy = new int[]{(int) Math.min(Math.pow(2, tz) - 1, tmaxxy[0]), (int) Math.min(Math.pow(2, tz) - 1, tmaxxy[1])};

                this.tminmax.add(tz, new int[]{tminxy[0], tminxy[1], tmaxxy[0], tmaxxy[1]});
            }

            if (this.tminz == 0) {
                this.tminz = this.mercator.zoomForPixelSize(this.outGt[1] * Math.max(this.outDs.getRasterXSize(), this.outDs.getRasterYSize()) / (float) (this.tilesize));
            }
            if (this.tmaxz == 0) {
                this.tmaxz = this.mercator.zoomForPixelSize(this.outGt[1]);
            }
            if (this.options.getVerbose()) {
                System.out.println(String.format("Bounds (latlong): " + this.mercator.metersToLatLon(this.ominx, this.ominy) + this.mercator.metersToLatLon(this.omaxx, this.omaxy)));
                System.out.println("MinZoomLevel:" + this.tminz);
                System.out.println("MaxZoomLevel:" + this.tmaxz + "(" + this.mercator.resolution(this.tmaxz) + ")");
            }
        }

        if (this.options.getProfile().equalsIgnoreCase("geodetic")) {
            this.geodetic = new GlobalGeodetic(null, 256);

            this.tileswneRaster = (double lon, double lat, int zoom) -> {
                double[] pxpy = geodetic.lonlatToPixels(lon, lat, zoom);
                return geodetic.pixelsToTile(pxpy[0], pxpy[1]);
            };
            this.tminmax = new LinkedList<>();
            for (int tz = 0; tz < 32; tz++) {
                int[] tminxy = this.geodetic.lonlatToTile(this.ominx, this.ominy, tz);
                int[] tmaxxy = this.geodetic.lonlatToTile(this.omaxx, this.omaxy, tz);

                tminxy = new int[]{Math.max(0, tminxy[0]), Math.max(0, tminxy[1])};
                tmaxxy = new int[]{Math.min((int) Math.pow(2, tz + 1) - 1, tmaxxy[0]),
                        (int) Math.min(Math.pow(2, tz) - 1, tmaxxy[1])};

                this.tminmax.add(tz, new int[]{tminxy[0], tminxy[1], tmaxxy[0], tmaxxy[1]});
            }

            if (this.tminz == null) {
                this.tminz = this.geodetic.zoomForPixelSize(this.outGt[1] *
                        Math.max(this.outDs.getRasterXSize(), this.outDs.getRasterYSize()) / (float) (this.tilesize));
            }
            if (this.tmaxz == null) {
                this.tmaxz = this.geodetic.zoomForPixelSize(this.outGt[1]);
            }

            if (this.options.getVerbose())
                System.out.println("Bounds (latlong):" + this.ominx + "," + this.ominy + "," + this.omaxx + "," + this.omaxy);
        }

        if (this.options.getProfile().equalsIgnoreCase("raster")) {
            this.nativezoom = (int) (Math.max(Math.ceil(log2(this.outDs.getRasterXSize() / (float) (this.tilesize))),
                    Math.ceil(log2(this.outDs.getRasterYSize() / (float) (this.tilesize)))));

            if (this.options.getVerbose())
                System.out.println("Native zoom of the raster:" + this.nativezoom);

            if (this.tminz == 0) {
                this.tminz = 0;
            }
            if (this.tmaxz == 0) {
                this.tmaxz = this.nativezoom;
            }
            this.tminmax = new LinkedList<>();
            this.tsize = new double[this.tmaxz + 1];

            for (int tz = 0; tz < this.tmaxz + 1; tz++) {
                double tsize = Math.pow(2.0, this.nativezoom - tz) * this.tilesize;

                int[] tminxy = new int[]{0, 0};
                int[] tmaxxy = new int[]{
                        ((int) (Math.ceil(this.outDs.getRasterXSize() / tsize))) - 1,
                        ((int) (Math.ceil(this.outDs.getRasterYSize() / tsize))) - 1
                };

                this.tsize[tz] = Math.ceil(tsize);
                this.tminmax.add(tz, new int[]{tminxy[0], tminxy[1], tmaxxy[0], tmaxxy[1]});
            }

            if (this.kml && !(this.inSrsWkt == null || "".equals(this.inSrsWkt))) {
                CoordinateTransformation ct = osr.CreateCoordinateTransformation(this.inSrs, srs4326);

                this.tileswne = (int x, int y, int z) -> {
                    double pixelsizex = Math.pow(2, (this.tmaxz - z) * this.outGt[1]); // X-pixel size in level
                    double pixelsizey = Math.pow(2, (this.tmaxz - z) * this.outGt[1]);
                    // Y-pixel size in level (usually -1*pixelsizex)
                    double west = this.outGt[0] + x * this.tilesize * pixelsizex;
                    double east = west + this.tilesize * pixelsizex;
                    double south = this.ominy + y * this.tilesize * pixelsizex;
                    double north = south + this.tilesize * pixelsizex;
                    if (!this.isepsg4326) {
                        // Transformation to EPSG:4326 (WGS84 datum)
                        double[] xx = ct.TransformPoint(west, south);
                        west = xx[0];
                        south = xx[1];
                        xx = ct.TransformPoint(east, north);
                        east = xx[0];
                        north = xx[1];
                    }

                    return new double[]{south, west, north, east};
                };
            } else {
                this.tileswne = (int x, int y, int z) -> new double[]{0, 0, 0, 0};
            }
        }
    }

    private void generateMetadata() {
        if (!new File(this.output).exists())
            new File(this.output).mkdirs();

        double[] southWest = new double[2];
        double[] northEast = new double[2];
        if (this.options.getProfile().equalsIgnoreCase("mercator")) {
            southWest = this.mercator.metersToLatLon(this.ominx, this.ominy);
            northEast = this.mercator.metersToLatLon(this.omaxx, this.omaxy);

            southWest = new double[]{Math.max(-85.05112878, southWest[0]), Math.max(-180.0, southWest[1])};
            northEast = new double[]{Math.min(85.05112878, northEast[0]), Math.min(180.0, northEast[1])};

            this.swne = new double[]{southWest[0], southWest[1], northEast[0], northEast[1]};

            if (this.options.getJoin())
                this.swne = new double[]{-90, -180, 90, 180};
        }
        if (this.options.getProfile().equalsIgnoreCase("geodetic")) {
            southWest = new double[]{this.ominy, this.ominx};
            northEast = new double[]{this.omaxy, this.omaxx};

            southWest = new double[]{Math.max(-90.0, southWest[0]), Math.max(-180.0, southWest[1])};
            northEast = new double[]{Math.min(90.0, northEast[0]), Math.min(180.0, northEast[1])};

            this.swne = new double[]{southWest[0], southWest[1], northEast[0], northEast[1]};
        }
        if (this.options.getProfile().equalsIgnoreCase("raster")) {

            southWest = new double[]{this.ominy, this.ominx};
            northEast = new double[]{this.omaxy, this.omaxx};

            this.swne = new double[]{southWest[0], southWest[1], northEast[0], northEast[1]};
        }
    }

    private void generateBaseTiles() {
        System.out.println("Generating Base Tiles:");
        if (this.options.getVerbose()) {
            System.out.println("");
            System.out.println("Tiles generated from the max zoom level:");
            System.out.println("----------------------------------------");
            System.out.println("");
        }

        int tminx = this.tminmax.get(this.tmaxz)[0];
        int tminy = this.tminmax.get(this.tmaxz)[1];
        int tmaxx = this.tminmax.get(this.tmaxz)[2];
        int tmaxy = this.tminmax.get(this.tmaxz)[3];

        Dataset ds = this.outDs;
        int tilebands;
        if (!this.options.getCesium() && !this.options.getSrtm())
            tilebands = this.dataBandsCount + 1;
        else
            tilebands = this.dataBandsCount;
        int querysize = this.querysize;

        if (this.options.getVerbose()) {
            System.out.println("dataBandsCount: " + this.dataBandsCount);
            System.out.println("tilebands: " + tilebands);
        }

        int tcount = (1 + Math.abs(tmaxx - tminx)) * (1 + Math.abs(tmaxy - tminy));

        long ti = 0;
        int tz = this.tmaxz;
        for (int ty = tmaxy; ty > tminy - 1; ty--) {
            for (int tx = tminx; tx < tmaxx + 1; tx++) {

                if (this.stoped)
                    break;

                ti += 1;

                File tilefilename = new File(this.output + File.separator + tz + File.separator + tx, String.format("%s.%s", ty, this.tileext));
                File waterfilename = new File(this.output + File.separator + tz + File.separator + tx, String.format("%s-water.%s", ty, this.tileext));

                if (this.options.getVerbose())
                    System.out.println(ti + "/" + tcount + tilefilename);

                boolean exists = tilefilename.exists();
                if (this.options.getResume() && tilefilename.exists()) {
                    if (this.options.getVerbose())
                        System.out.println("Tile generation skiped because of --resume");
                    else this.progressbar(ti / (double) tcount);
                    continue;
                }

                if (!tilefilename.getParentFile().exists())
                    tilefilename.getParentFile().mkdirs();


                double[] bound = null;
                if (this.options.getProfile().equalsIgnoreCase("mercator")) {
                    bound = this.mercator.tileBounds(tx, ty, tz);
                } else if (this.options.getProfile().equalsIgnoreCase("geodetic")) {
                    bound = this.geodetic.tileBounds(tx, ty, tz);
                }

                int rx = 0, ry = 0, rxsize = 0, rysize = 0, wx = 0, wy = 0, wxsize = 0, wysize = 0;

                if (this.options.getProfile().equalsIgnoreCase("mercator") ||
                        this.options.getProfile().equalsIgnoreCase("geodetic")) {
                    int[][] rbwb = this.geo_query(ds, bound[0], bound[3], bound[2], bound[1], 0);

                    int nativesize = rbwb[1][0] + rbwb[1][2];
                    if (this.options.getVerbose())
                        System.out.println("\tNative Extent (querysize" + nativesize + "): " + rbwb);

                    rbwb = this.geo_query(ds, bound[0], bound[3], bound[2], bound[1], querysize);

                    rx = rbwb[0][0];
                    ry = rbwb[0][1];
                    rxsize = rbwb[0][2];
                    rysize = rbwb[0][3];
                    wx = rbwb[1][0];
                    wy = rbwb[1][1];
                    wxsize = rbwb[1][2];
                    wysize = rbwb[1][3];
                } else {
                    int tsize = (int) this.tsize[tz];//tilesize in raster coordinates for actual zoom
                    int xsize = this.outDs.getRasterXSize();//size of the raster in pixels
                    int ysize = this.outDs.getRasterYSize();
                    if (tz >= this.nativezoom) {
                        querysize = this.tilesize;//int(2 * * (self.nativezoom - tz) * self.tilesize)
                    }

                    rx = (tx) * tsize;
                    rxsize = 0;
                    if (tx == tmaxx)
                        rxsize = xsize % tsize;
                    if (rxsize == 0)
                        rxsize = tsize;

                    rysize = 0;
                    if (ty == tmaxy)
                        rysize = ysize % tsize;
                    if (rysize == 0)
                        rysize = tsize;
                    ry = ysize - (ty * tsize) - rysize;

                    wx = 0;
                    wy = 0;
                    wxsize = (int) (rxsize / (float) (tsize) * this.tilesize);
                    wysize = (int) (rysize / (float) (tsize) * this.tilesize);

                    if (wysize != this.tilesize)
                        wy = this.tilesize - wysize;
                }

                if (this.options.getVerbose())
                    System.out.println("\tReadRaster Extent: (" + rx + "," + ry + "," + rxsize + "," + rysize + ") (" + wx + "," + wy + "," + wxsize + "," + wysize + ")");


                Dataset dsjoin = null;
                byte[] datajoin = null;
                if (this.options.getJoin() && exists) {
                    if (this.options.getVerbose())
                        System.out.println("Tile exists and join option set. Read exists tile.");
                    dsjoin = gdal.Open(tilefilename.getAbsolutePath());
                    datajoin = new byte[dsjoin.GetRasterXSize() * dsjoin.getRasterXSize() * dsjoin.getRasterCount()];
                    dsjoin.ReadRaster(0, 0, dsjoin.GetRasterXSize(), dsjoin.getRasterXSize(), dsjoin.GetRasterXSize(), dsjoin.getRasterYSize(), dsjoin.GetRasterBand(1).getDataType(), datajoin, getBandList(dsjoin.GetRasterCount()));
                }

                Dataset dstile = this.memDrv.Create("", this.tilesize, this.tilesize, tilebands, this.tiledata);

                byte[] data = new byte[rxsize * rysize * this.dataBandsCount];
                byte[] alpha = new byte[rxsize * rysize];
                byte[] datawater = new byte[rxsize * rysize];

                if (!this.options.getWaterMask()) {
                    ds.ReadRaster(rx, ry, rxsize, rysize, wxsize, wxsize, ds.GetRasterBand(1).getDataType(), data, getBandList(this.dataBandsCount));
                    if (!this.options.getCesium() && !this.options.getSrtm()) {
                        this.alphaband.ReadRaster(rx, ry, rxsize, rysize, wxsize, wysize, this.alphaband.getDataType(), alpha);
                    }

                    if (this.tilesize == querysize) {
                        //Use the ReadRaster result directly in tiles ('nearest neighbour' query)
                        dstile.WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, this.tiledata, data, getBandList(this.dataBandsCount));
                        if (!this.options.getCesium() && !this.options.getSrtm())
                            dstile.WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, this.tiledata, alpha, getBandList(tilebands));
                    } else {
                        Dataset dsquery = this.memDrv.Create("", querysize, querysize, tilebands, this.tiledata);
                        dsquery.WriteRaster(wx, wy, wxsize, wysize, this.tilesize, this.tilesize, 1, data, getBandList(tilebands));
                        if (!this.options.getCesium() && !this.options.getSrtm())
                            dsquery.WriteRaster(wx, wy, wxsize, wysize, this.tilesize, this.tilesize, 1, alpha, getBandList(tilebands));
                        this.scaleQueryToTile(dsquery, dstile, tilefilename.getAbsolutePath());
                        dsquery.delete();
                    }
                } else {
                    ds.GetRasterBand(1).ReadRaster(rx, ry, rxsize, rysize, wxsize, wysize, 1, data);
                    ds.GetRasterBand(2).ReadRaster(rx, ry, rxsize, rysize, wxsize, wysize, gdalconstConstants.GDT_Byte, datawater);
                    if (!this.options.getCesium() && !this.options.getSrtm()) {
                        this.alphaband.ReadRaster(rx, ry, rxsize, rysize, wxsize, wysize, this.alphaband.getDataType(), alpha);
                    }

                    if (this.tilesize == querysize) {
                        dstile.GetRasterBand(1).WriteRaster(wx, wy, wxsize, wysize, data);
                        if (!this.options.getCesium() && !this.options.getSrtm())
                            dstile.WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, this.tiledata, alpha, getBandList(tilebands));
                    } else {
                        Dataset dsquery = this.memDrv.Create("", querysize, querysize, tilebands, this.tiledata);
                        dsquery.GetRasterBand(1).WriteRaster(wx, wy, wxsize, wysize, data);
                        if (!this.options.getCesium() && !this.options.getSrtm())
                            dsquery.WriteRaster(wx, wy, wxsize, wysize, this.tilesize, this.tilesize, 1, alpha, getBandList(tilebands));
                        this.scaleQueryToTile(dsquery, dstile, tilefilename.getAbsolutePath());
                        dsquery.delete();

                        Dataset dswater = this.memDrv.Create("", 256, 256, gdalconstConstants.GDT_Byte);
                        dsquery = this.memDrv.Create("", querysize, querysize, tilebands, gdalconstConstants.GDT_Byte);

                        dsquery.GetRasterBand(1).WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, gdalconstConstants.GDT_Byte, datawater);//#, band_list=list(range(1,self.dataBandsCount)))
                        this.scaleQueryToTile(dsquery, dswater, tilefilename.getAbsolutePath());

                        this.outDrv.CreateCopy(waterfilename.getAbsolutePath(), dswater, 0);
                        dsquery.delete();
                        dswater.delete();

                    }
                }

                data = null;

                if (this.options.getJoin() && exists) {
                    if (this.options.getVerbose())
                        System.out.println("---Write join data.");
                    if (dsjoin.GetRasterCount() != dstile.GetRasterCount()) {
                        System.out.println(String.format("RasterCount of existing tile (%s) is not equal RasterCount of generating tile (%s)", dsjoin, dstile));
                        datajoin = null;
                        dsjoin.delete();
                        continue;
                    }

                    for (int i = 0; i < dstile.GetRasterYSize(); i++) {
                        for (int k = 0; i < dstile.getRasterXSize(); k++) {
                            List<Double> pixvals = new ArrayList<>();
                            for (int b = 1; b < dstile.getRasterCount() + 1; b++) {
                                Band band = dstile.GetRasterBand(b);
                                String fmt = pt2fmt(band.getDataType());
                                byte[] pixel = new byte[4];
                                band.ReadRaster(i, k, 1, 1, pixel);
                                Integer pixval = this.byte2Int(pixel);
                                pixvals.add(Double.valueOf(pixval));
                            }
                            int alphaIndex = dstile.GetRasterCount() - 1;
                            if (pixvals.get(alphaIndex) == 0 || (this.inNodata.size() > 0 && pixvals.get(alphaIndex) == this.inNodata.get(0))) {
                                for (int b = 1; b < dstile.GetRasterCount() + 1; b++) {
                                    Band band = dstile.GetRasterBand(b);
                                    String fmt = this.pt2fmt(band.getDataType());
                                    if (!this.tiledriver.equalsIgnoreCase("EHdr")) {
//                                        band.WriteRaster(i, k, 1, 1, pack(fmt, datajoin[b - 1][k][i]));
                                    } else {
//                                        band.WriteRaster(i, k, 1, 1, pack(fmt, datajoin[k][i]));
                                    }
                                }
                            } else {
                                for (int b = 1; b < dstile.GetRasterCount() + 1; b++) {
                                    Band band = dstile.GetRasterBand(b);
                                    String fmt = pt2fmt(band.getDataType());
                                    byte[] pixel = new byte[4];
                                    band.ReadRaster(i, k, 1, 1, pixel);
                                    Integer pixval = this.byte2Int(pixel);
                                    if (pixval >= -1000)
                                        pixval = (pixval + 1000) * 5;
                                    else {
                                        // Set NODATA to NULL
                                        pixval = pixval - pixval;
                                        pixval = (pixval + 1000) * 5;
                                    }
                                    byte[] xx = new byte[4];
                                    int2Bytes(pixval, xx, 0);
                                    band.WriteRaster(i, k, 1, 1, xx);
                                }
                            }
                        }
                    }
                }

                if (this.options.getCesium()) {
                    File previoustilefilename = new File(this.output, String.format("%s/%s/%s.%s", tz, tx - 1, ty, this.tileext));
                    if (previoustilefilename.exists()) {
                        for (int b = 1; b < dstile.GetRasterCount() + 1; b++) {
                            Band band = dstile.GetRasterBand(b);
                            for (int y = 0; y < dstile.GetRasterYSize(); y++) {
                                byte[] pixel = new byte[4];
                                band.ReadRaster(0, y, 1, 1, pixel);
                                String fmt = pt2fmt(band.getDataType());
                                Integer pixval = this.byte2Int(pixel);
                                if (this.options.getJoin() && !exists) {
                                    if (pixval >= -1000)
                                        pixval = (pixval + 1000) * 5;
                                    else {
                                        pixval = pixval - pixval;
                                        pixval = (pixval + 1000) * 5;
                                    }
                                    Dataset pdstile = gdal.Open(previoustilefilename.getAbsolutePath(), gdalconstConstants.GA_Update);
                                    byte[] xx = new byte[4];
                                    int2Bytes(pixval, xx, 0);
                                    pdstile.GetRasterBand(b).WriteRaster(dstile.GetRasterXSize() - 1, y, 1, 1, xx);
                                    pdstile.delete();
                                }
                                pixel = null;
                            }
                            previoustilefilename = new File(output, String.format("%s/%s/%s.%s", tz, tx, ty + 1, this.tileext));
                            if (!previoustilefilename.getParentFile().exists())
                                previoustilefilename.getParentFile().mkdirs();
                            if (previoustilefilename.exists()) {
                                for (int x = 0; x < dstile.getRasterXSize(); x++) {
                                    byte[] pixel = new byte[4];
                                    band.ReadRaster(x, 0, 1, 1, pixel);
                                    String fmt = pt2fmt(band.getDataType());
                                    Integer pixval = this.byte2Int(pixel);
                                    if (this.options.getJoin() && !exists) {
                                        if (pixval >= -1000)
                                            pixval = (pixval + 1000) * 5;
                                        else {
                                            pixval = pixval - pixval;
                                            pixval = (pixval + 1000) * 5;
                                        }
                                        Dataset pdstile = gdal.Open(previoustilefilename.getAbsolutePath(), gdalconstConstants.GA_Update);
                                        byte[] xx = new byte[4];
                                        int2Bytes(pixval, xx, 0);
                                        pdstile.GetRasterBand(b).WriteRaster(x, dstile.getRasterYSize() - 1, 1, 1, xx);
                                        pdstile.delete();
                                    }
                                    pixel = null;
                                }
                            }
                        }
                    }
                }
                if (!this.options.getResampling().equalsIgnoreCase("antialias")) {
                    Dataset tmp = this.outDrv.CreateCopy(tilefilename.getAbsolutePath(), dstile, 0);
                    if (tmp != null) tmp.delete();
                    if (this.options.getCesium() && !exists) {
                        try {
                            FileInputStream src = FileUtils.openInputStream(tilefilename);
                            FileOutputStream dst = FileUtils.openOutputStream(new File(tilefilename.getAbsoluteFile() + ".new"));
                            // TODO: 2021/1/19 0019 这里其实是把byte转成Ascii码
                            byte[] data2 = new byte[2];
                            while (src.read(data2) != -1) {
                                Short a = Utils.bytes2Short(data2);
                                if (a >= -1000)
                                    a = (short) ((a + (short) 1000) * 5);
                                else {
                                    //set NODATA TO NULL
                                    a = (short) (a - a);
                                    a = (short) ((a + (short) 1000) * 5);
                                }
                                dst.write(a);
                            }
                            src.close();
//                            dst.write(new char[]{'\x00', '\x00'});
                            data2 = null;
                            if (this.options.getWaterMask()) {
                                data2 = FileUtils.readFileToByteArray(waterfilename.getAbsoluteFile());
                                FileUtils.writeByteArrayToFile(new File(tilefilename + ".new"), data2);
                            }
                            dst.close();

                            if (tilefilename.exists())
                                FileUtils.forceDelete(tilefilename);
                            new File(tilefilename.getAbsoluteFile() + ".new").renameTo(tilefilename.getAbsoluteFile());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                dstile.delete();

                if (this.options.getJoin() && exists) {
                    datajoin = null;
                    dsjoin.delete();
                }

//                if (this.kml) {
//                    File kmlFileName = new File(this.output, String.format("%s/%s/%d.kml", tz, tx, ty));
//                    if (!this.options.getResume() || !kmlFileName.exists()) {
//
//                    }
//                }
                if (!this.options.getVerbose())
                    this.progressbar(ti / (double) tcount);
            }
        }

    }

    private void int2Bytes(int i, byte[] buf, int offset) {
        buf[offset] = (byte) i;
        i >>= 8;
        buf[offset + 1] = (byte) i;
        i >>= 8;
        buf[offset + 2] = (byte) i;
        i >>= 8;
        buf[offset + 3] = (byte) i;
    }

    private int byte2Int(byte[] bs) {
        int retVal = 0;
        int len = bs.length < 4 ? bs.length : 4;
        for (int i = 0; i < len; i++) {
            retVal |= (bs[i] & 0xFF) << ((i & 0x03) << 3);
        }
        return retVal;
        // 如果确定足4位，可直接返回值
        //return (bs[0]&0xFF) | ((bs[1] & 0xFF)<<8) | ((bs[2] & 0xFF)<<16) | ((bs[3] & 0xFF)<<24);
    }

    private void scaleQueryToTile(Dataset dsquery, Dataset dstile, String tilefilename) {
        int querysize = dsquery.GetRasterXSize();
        int tilesize = dstile.getRasterXSize();
        int tilebands = dstile.getRasterCount();

        if (this.options.getResampling().equalsIgnoreCase("average")) {
            for (int i = 1; i <= tilebands; i++) {
                int res = gdal.RegenerateOverview(dsquery.GetRasterBand(i), dstile.GetRasterBand(i), "average");
                if (res != 0)
                    System.out.println(String.format("RegenerateOverview() failed on %s, error %f", tilefilename, res));
            }
        } else if (options.getResampling().equalsIgnoreCase("antialias")) {//antialias
//            # Scaling by PIL (Python Imaging Library) - improved Lanczos
//            array = numpy.zeros((querysize, querysize, tilebands), numpy.uint8)
//            for i in range(tilebands):
//            array[:, :, i] = gdalarray.BandReadAsArray(dsquery.GetRasterBand(i + 1), 0, 0, querysize, querysize)
//            im = Image.fromarray(array, 'RGBA')  # Always four bands
//            im1 = im.resize((tilesize, tilesize), Image.ANTIALIAS)
//            if os.path.exists(tilefilename):
//            im0 = Image.open(tilefilename)
//            im1 = Image.composite(im1, im0, im1)
//            im1.save(tilefilename, self.tiledriver)
        } else {
            int gdal_resampling = -1;
            if (options.getResampling().equalsIgnoreCase("near"))
                gdal_resampling = gdalconst.GRA_NearestNeighbour;
            else if (options.getResampling().equalsIgnoreCase("bilinear"))
                gdal_resampling = gdalconst.GRA_Bilinear;
            else if (options.getResampling().equalsIgnoreCase("cubic"))
                gdal_resampling = gdalconst.GRA_Cubic;
            else if (options.getResampling().equalsIgnoreCase("cubicspline"))
                gdal_resampling = gdalconst.GRA_CubicSpline;
            else if (options.getResampling().equalsIgnoreCase("lanczos"))
                gdal_resampling = gdalconst.GRA_Lanczos;
            dsquery.SetGeoTransform(new double[]{0.0, tilesize / (double) querysize, 0.0, 0.0, 0.0, tilesize / (double) querysize});
            dstile.SetGeoTransform(new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0});
            int res = gdal.ReprojectImage(dsquery, dstile, null, null, gdal_resampling);
            if (res != 0)
                System.out.println(String.format("ReprojectImage() failed on %s, error %f", tilefilename, res));
        }
    }

    private int[][] geo_query(Dataset ds, double ulx, double uly, double lrx, double lry, int querysize) {
        double[] geotran = ds.GetGeoTransform();
        int rx = (int) ((ulx - geotran[0]) / geotran[1] + 0.001);
        int ry = (int) ((uly - geotran[3]) / geotran[5] + 0.001);
        int rxsize = (int) ((lrx - ulx) / geotran[1] + 0.5);
        int rysize = (int) ((lry - uly) / geotran[5] + 0.5);

        int wxsize, wysize;
        if (querysize == 0) {
            wxsize = rxsize;
            wysize = rysize;
        } else {
            wxsize = querysize;
            wysize = querysize;
        }

        int wx = 0;
        if (rx < 0) {
            int rxshift = Math.abs(rx);
            wx = (int) (wxsize * ((float) (rxshift) / rxsize));
            wxsize = wxsize - wx;
            rxsize = rxsize - (int) (rxsize * ((float) (rxshift) / rxsize));
            rx = 0;
        }
        if ((rx + rxsize) > ds.getRasterXSize()) {
            wxsize = (int) (wxsize * ((float) (ds.getRasterXSize() - rx) / rxsize));
            rxsize = ds.getRasterXSize() - rx;
        }

        int wy = 0;

        if (ry < 0) {
            int ryshift = Math.abs(ry);
            wy = (int) (wysize * ((float) (ryshift) / rysize));
            wysize = wysize - wy;
            rysize = rysize - (int) (rysize * ((float) (ryshift) / rysize));
            ry = 0;
        }

        if ((ry + rysize) > ds.getRasterYSize()) {
            wysize = (int) (wysize * ((float) (ds.getRasterYSize() - ry) / rysize));
            rysize = ds.getRasterYSize() - ry;
        }
        return new int[][]{new int[]{rx, ry, rxsize, rysize}, new int[]{wx, wy, wxsize, wysize}};
    }

    private void generateOverviewTiles() {
        System.out.println("Generating Overview Tiles:");
        int tilebands;
        if (!this.options.getCesium() && !this.options.getSrtm())
            tilebands = this.dataBandsCount + 1;
        else
            tilebands = this.dataBandsCount;

        int tcount = 0;
        for (int tz = this.tmaxz - 1; tz > this.tminz - 1; tz--) {
            int[] tminmaxxy = this.tminmax.get(tz);
            tcount += (1 + Math.abs(tminmaxxy[2] - tminmaxxy[0])) * (1 + Math.abs(tminmaxxy[3] - tminmaxxy[1]));
        }
        int ti = 0;
        for (int tz = this.tmaxz - 1; tz > this.tminz - 1; tz--) {
            int[] tminmaxxy = this.tminmax.get(tz);
            for (int ty = tminmaxxy[3]; ty > tminmaxxy[1] - 1; ty--) {
                for (int tx = tminmaxxy[0]; tx < tminmaxxy[2] + 1; tx++) {
                    if (this.stoped)
                        break;
                    ti += 1;
                    File tilefilename = new File(this.output, String.format("%d/%d/%d.%s", tz, tx, ty, this.tileext));
                    File waterfilename = new File(this.output, String.format("%d/%d/%d-water.%s", tz, tx, ty, this.tileext));

                    if (this.options.getVerbose()) {
                        logger.info(String.format("%d/%d---%s", ti, tcount, tilefilename.getAbsoluteFile()));
                    }
                    if (this.options.getResume() && tilefilename.exists()) {
                        if (this.options.getVerbose())
                            logger.info("Tile generation skiped because of --resume");
                        else
                            this.progressbar(ti / (double) tcount);
                        continue;
                    }
                    boolean exists = tilefilename.exists();
                    if (!tilefilename.getParentFile().exists())
                        tilefilename.getParentFile().mkdirs();

                    Dataset dsjoin = null;
                    byte[] datajoin = null;
                    if (this.options.getJoin() && exists) {
                        if (this.options.getVerbose())
                            logger.info("Tile exists and join option set. Read exists tile.");
                        dsjoin = gdal.Open(tilefilename.getAbsolutePath(), gdalconstConstants.GA_ReadOnly);
                        datajoin = new byte[dsjoin.getRasterXSize() * dsjoin.getRasterYSize() * dsjoin.getRasterCount()];
                        dsjoin.ReadRaster(0, 0, dsjoin.getRasterXSize(), dsjoin.getRasterYSize(), dsjoin.getRasterXSize(), dsjoin.getRasterYSize(), dsjoin.GetRasterBand(1).getDataType(), datajoin, getBandList(dsjoin.getRasterCount()));
                    }

                    Dataset dsquery = this.memDrv.Create("", 2 * this.tilesize, 2 * this.tilesize, tilebands, this.tiledata);
                    Dataset dstile = this.memDrv.Create("", this.tilesize, this.tilesize, tilebands, this.tiledata);

                    Dataset dswaterquery = null, dswater = null;
                    if (this.options.getWaterMask()) {
                        dswaterquery = this.memDrv.Create("", 2 * 256, 2 * 256, 1, gdalconstConstants.GDT_Byte);
                        dswater = this.memDrv.Create("", 256, 256, 1, gdalconstConstants.GDT_Byte);
                    }

                    List<int[]> children = new ArrayList<>();
                    for (int y = 2 * ty; y < (2 * ty + 2); y++) {
                        for (int x = 2 * tx; x < (2 * tx + 2); x++) {
                            int[] minmaxxy = this.tminmax.get(tz + 1);
                            int minx = minmaxxy[0], miny = minmaxxy[1], maxx = minmaxxy[2], maxy = minmaxxy[3];
                            if (x >= minx && x <= maxx && y >= miny && y <= maxy) {
                                Dataset dsquerytile = gdal.Open(new File(this.output, String.format("%d/%d/%d.%s", tz + 1, x, y, this.tileext)).getAbsolutePath(), gdalconstConstants.GA_ReadOnly);
                                Dataset dsquerywater = null;
                                if (this.options.getWaterMask())
                                    dsquerywater = gdal.Open(new File(this.output, String.format("%d/%d/%d-water.%s", tz + 1, x, y, this.tileext)).getAbsolutePath(), gdalconstConstants.GA_ReadOnly);

                                int tileposy, tileposx;
                                if ((ty == 0 && y == 1) || ty != 0 && (y % (2 * ty)) != 0)
                                    tileposy = 0;
                                else tileposy = this.tilesize;
                                if (tx != 0)
                                    tileposx = x % (2 * tx) * this.tilesize;
                                else if (tx == 0 && x == 1)
                                    tileposx = this.tilesize;
                                else tileposx = 0;

                                if (!this.options.getWaterMask()) {
                                    byte[] data = new byte[this.tilesize * this.tilesize * this.dataBandsCount];
                                    dsquerytile.ReadRaster(0, 0, this.tilesize, this.tilesize, this.tilesize, this.tilesize, this.tiledata, data, getBandList(tilebands));
                                    dsquery.WriteRaster(tileposx, tileposy, this.tilesize, this.tilesize, tilesize, tilesize, this.tiledata, data, getBandList(tilebands));
                                } else {
                                    byte[] data = new byte[this.tilesize * this.tilesize * this.dataBandsCount];
                                    dsquerytile.ReadRaster(0, 0, this.tilesize, this.tilesize, this.tilesize, this.tilesize, this.tiledata, data, getBandList(tilebands));
                                    dsquery.GetRasterBand(1).WriteRaster(tileposx, tileposy, this.tilesize, this.tilesize, data);
                                    byte[] data2 = new byte[256 * 256 * this.dataBandsCount];
                                    dsquerywater.ReadRaster(0, 0, 256, 256, 256, 256, this.tiledata, data2, getBandList(tilebands));
                                    dswaterquery.GetRasterBand(1).WriteRaster(tileposx, tileposy, 256, 256, data2);
                                }
                                children.add(new int[]{x, y, tz + 1});
                            }
                        }
                    }

                    this.scaleQueryToTile(dsquery, dstile, tilefilename.getAbsolutePath());
                    if (this.options.getWaterMask())
                        this.scaleQueryToTile(dswaterquery, dswater, waterfilename.getAbsolutePath());

                    if (this.options.getJoin() && exists) {
                        if (this.options.getVerbose())
                            logger.info("---Write join data.");
                        if (dsjoin.GetRasterCount() != dstile.GetRasterCount()) {
                            logger.info("RasterCount of existing tile (%s) is not equal RasterCount of generating tile (%s)");
                            datajoin = null;
                            dsjoin.delete();
                            continue;
                        }

                        for (int i = 0; i < dstile.getRasterYSize(); i++) {
                            for (int k = 0; k < dstile.getRasterXSize(); k++) {
                                List<Double> pixvals = new ArrayList<>();
                                for (int b = 1; b < dstile.GetRasterCount() + 1; b++) {
                                    Band band = dstile.GetRasterBand(b);
                                    byte[] xx = new byte[4];
                                    band.ReadRaster(i, k, 1, 1, xx);
                                    Integer pixval = byte2Int(xx);
                                    pixvals.add((double) pixval);
                                }
                                int alpha = dstile.GetRasterCount() - 1;
                                if (pixvals.get(alpha) == 0 || (!(this.inNodata.size() == 0) && pixvals.get(alpha) == this.inNodata.get(0))) {
                                    for (int b = 1; b < dstile.GetRasterCount() + 1; b++) {
                                        Band band = dstile.GetRasterBand(b);
                                        if (!this.tiledriver.equalsIgnoreCase("Ehdr")) {
                                            byte[] xx = new byte[4];
                                            int2Bytes(datajoin[b - 1 * k * i], xx, 0);
                                            band.WriteRaster(i, k, 1, 1, xx);
                                        } else {
                                            byte[] xx = new byte[4];
                                            int2Bytes(datajoin[k * i], xx, 0);
                                            band.WriteRaster(i, k, 1, 1, xx);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (this.options.getJoin() && exists) {
                        datajoin = null;
                        dsjoin.delete();
                    }

                    if (!this.options.getResampling().equalsIgnoreCase("antialias")) {
                        this.outDrv.CreateCopy(tilefilename.getAbsolutePath(), dstile, 0);
                        if (this.options.getCesium()) {
                            if (this.options.getWaterMask())
                                this.outDrv.CreateCopy(waterfilename.getAbsolutePath(), dswater, 0);
                            try {
                                FileOutputStream dst = FileUtils.openOutputStream(tilefilename.getAbsoluteFile());
//                            dst.write(new int[]{x0f,x00});
                                if (this.options.getWaterMask()) {
                                    FileInputStream water = FileUtils.openInputStream(waterfilename.getAbsoluteFile());
                                    byte[] data = water.readAllBytes();
                                    dst.write(data);
                                    water.close();
                                }
                                dst.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (this.options.getVerbose())
                        logger.info(String.format("\tbuild from zoom", tz + 1));

                    if (!this.options.getVerbose()) {
                        this.progressbar(ti / (double) tcount);
                    }
                }
            }
        }
    }


    private double log2(double x) {
        return Math.log10(x) / Math.log10(2);
    }

    public static int[] getBandList(int bandCount) {
        int[] list = new int[bandCount];
        for (int i = 1; i < bandCount + 1; i++) {
            list[i - 1] = (i);
        }
        return list;
    }

    public static void main(String[] args) {

        gdal.AllRegister();

        gdal.SetConfigOption("GDAL_DATA", Gdal2Tiles.class.getClassLoader().getResource("gdal-data").getFile().substring(1));
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");
        gdal.SetConfigOption("CPL_DEBUG", "OFF");


        try {
            SrtmTileArgs srtmTileArgs = new SrtmTileArgs();
            srtmTileArgs.setInput("F:\\Data\\cjjc\\大红山铁矿现状已有地形影像资料\\塌陷区影像图-坐标系为WGS84\\地理坐标\\20201030塌陷区dem84.地理坐标.tif");
            srtmTileArgs.setOutput("F:\\Data\\cjjc\\大红山铁矿现状已有地形影像资料\\塌陷区影像图-坐标系为WGS84\\地理坐标\\terrain");
            srtmTileArgs.setZoom("0-18");
            srtmTileArgs.setProfile("geodetic");
            srtmTileArgs.setResampling("near");
            srtmTileArgs.setResume(true);
            srtmTileArgs.setVerbose(true);
            srtmTileArgs.setCesium(true);
            srtmTileArgs.setTileExt("terrain");

            File file = new File(srtmTileArgs.getOutput());
            FileUtils.deleteDirectory(file);
            file.mkdirs();

            new Gdal2Tiles(srtmTileArgs).process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
