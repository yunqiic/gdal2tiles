package com.walkgis.tiles.util;

import ch.qos.logback.core.util.FileUtil;
import javafx.concurrent.Task;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.osr.SpatialReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static Dataset cachedDs;

    /**
     * Extract the NODATA values from the dataset or use the passed arguments as override if any
     *
     * @return
     */
    public static Double[] setup_no_data_values(Dataset input_dataset, OptionObj options) {
        List<Double> in_nodata = new ArrayList<>();
        if (options.srcnodata != null) {
            List<Double> nds = Arrays.stream(options.srcnodata.split(",")).map(k -> Double.parseDouble(k)).collect(Collectors.toList());
            if (nds.size() < input_dataset.getRasterCount()) {
                for (int i = 0; i < input_dataset.getRasterCount(); i++)
                    nds.addAll(nds);
                in_nodata = nds.subList(0, input_dataset.getRasterCount());
            } else
                in_nodata = nds;
        } else {
            for (int i = 1; i < input_dataset.GetRasterCount() + 1; i++) {
                Double[] raster_no_data = new Double[1];
                input_dataset.GetRasterBand(i).GetNoDataValue(raster_no_data);
                if (raster_no_data != null)
                    in_nodata.addAll(Arrays.stream(raster_no_data).collect(Collectors.toList()));
            }
        }

        return in_nodata.stream().toArray(Double[]::new);
    }

    /**
     * Determines and returns the Input Spatial Reference System (SRS) as an osr object and as a
     * WKT representation
     * <p>
     * Uses in priority the one passed in the command line arguments. If None, tries to extract them
     * from the input dataset
     *
     * @param input_dataset
     * @param options
     */
    public static String setup_input_srs(Dataset input_dataset, OptionObj options, SpatialReference input_srs) {
        String input_srs_wkt = "";
        if (!StringUtils.isEmpty(options.s_srs)) {
//            input_srs = new SpatialReference();
            input_srs.SetFromUserInput(options.s_srs);
            input_srs_wkt = input_srs.ExportToWkt();
        } else {
            input_srs_wkt = input_dataset.GetProjection();
            if (!StringUtils.isEmpty(input_srs_wkt) && input_dataset.GetGCPCount() != 0)
                input_srs_wkt = input_dataset.GetGCPProjection();
            if (!StringUtils.isEmpty(input_srs_wkt)) {
//                input_srs = new SpatialReference();
                input_srs.SetFromUserInput(input_srs_wkt);
            }
        }
//        input_srs.SetAxisMappingStrategy(osr.OAMS_TRADITIONAL_GIS_ORDER)
        return input_srs_wkt;
    }

    /**
     * Setup the desired SRS (based on options)
     *
     * @param input_srs
     * @param options
     * @return
     */
    public static SpatialReference setup_output_srs(SpatialReference input_srs, OptionObj options) {
        SpatialReference output_srs = new SpatialReference();

        if (options.profile.equalsIgnoreCase("mercator"))
            output_srs.ImportFromEPSG(3857);
        else if (options.profile.equalsIgnoreCase("geodetic"))
            output_srs.ImportFromEPSG(4326);
        else
            output_srs = input_srs;

        return output_srs;
    }

    public static boolean has_georeference(Dataset dataset) {
        return dataset.GetGeoTransform() != new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0} || dataset.GetGCPCount() != 0;
    }

    /**
     * Returns the input dataset in the expected "destination" SRS.
     * If the dataset is already in the correct SRS, returns it unmodified
     *
     * @param from_dataset
     * @param from_srs
     * @param to_srs
     * @return
     */
    public static Dataset reproject_dataset(Dataset from_dataset, SpatialReference from_srs, SpatialReference to_srs) throws Exception {
        Dataset to_dataset = null;
        if (from_srs == null || to_srs == null)
            throw new Exception("from and to SRS must be defined to reproject the dataset");
        if (!from_srs.ExportToProj4().equalsIgnoreCase(to_srs.ExportToProj4()) ||
                from_dataset.GetGCPCount() != 0) {
            to_dataset = gdal.AutoCreateWarpedVRT(from_dataset, from_srs.ExportToWkt(), to_srs.ExportToWkt());

            to_dataset.GetDriver().CreateCopy("_tiles.vrt", to_dataset);
            return to_dataset;
        } else return from_dataset;
    }

    /**
     * Takes an array of NODATA values and forces them on the WarpedVRT file dataset passed
     *
     * @param warped_input_dataset
     * @param nodata_values
     * @param options
     * @return
     */
    public static Dataset update_no_data_values(Dataset warped_input_dataset, Double[] nodata_values, OptionObj options) {
        assert nodata_values != new Double[]{};
//        String vrt_string = warped_input_dataset.GetMetadataItem("xml:VRT");
        String vrt_string = getvrt(warped_input_dataset);

        Map<String, String> args = new HashMap<>();
        args.put("INIT_DEST", "NO_DATA");
        args.put("UNIFIED_SRC_NODATA", "YES");
        vrt_string = add_gdal_warp_options_to_string(vrt_string, args);

        Dataset corrected_dataset = gdal.Open(vrt_string);

        corrected_dataset.SetMetadataItem("NODATA_VALUES", String.join(" ", Arrays.stream(nodata_values).map(k -> k.toString()).collect(Collectors.toList())));

        if (options != null) {

        }

        return corrected_dataset;
    }

    private static String getvrt(Dataset dataset) {
        File file = null;
        try {
            file = File.createTempFile(UUID.randomUUID().toString(), "vrt", new File(System.getProperty("java.io.tmpdir")));
            dataset.GetDriver().CreateCopy(file.getAbsolutePath(), dataset);
            return String.join("", Files.readAllLines(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) file.deleteOnExit();
        }
        return "";
    }

    private static String add_gdal_warp_options_to_string(String vrt_string, Map<String, String> warp_options) {
        if (warp_options == null || warp_options.size() == 0)
            return vrt_string;
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new ByteArrayInputStream(vrt_string.getBytes("UTF-8")));
            Element rootElement = document.getRootElement();

            Element element = rootElement.element("GDALWarpOptions");
            if (element == null) return vrt_string;

            for (Map.Entry<String, String> entry : warp_options.entrySet()) {
                Element optionEle = element.addElement("Option");
                optionEle.addText(entry.getValue());
                optionEle.addAttribute("name", entry.getKey());
            }
            return document.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return vrt_string;
    }

    /**
     * Handles dataset with 1 or 3 bands, i.e. without alpha channel, in the case the nodata value has
     * not been forced by options
     *
     * @param warped_input_dataset
     * @param options
     * @return
     */
    public static Dataset update_alpha_value_for_non_alpha_inputs(Dataset warped_input_dataset, OptionObj options) {
        if (warped_input_dataset.GetRasterCount() == 1 || warped_input_dataset.GetRasterCount() == 3) {
            String vrt_string = warped_input_dataset.GetMetadataItem("xml:VRT");
            vrt_string = add_alpha_band_to_string_vrt(vrt_string);
            warped_input_dataset = gdal.Open(vrt_string);
            if (options != null) {

            }
            return warped_input_dataset;
        }

        return null;
    }

    private static String add_alpha_band_to_string_vrt(String vrt_string) {
        SAXReader reader = new SAXReader();
        Document document = null;

        int index = 0;
        int nb_bands = 0;

        try {
            document = reader.read(new ByteArrayInputStream(vrt_string.getBytes("UTF-8")));
            Element rootElement = document.getRootElement();

            Iterator iterator = rootElement.elementIterator();
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();

                if (element.getName().equalsIgnoreCase("VRTRasterBand")) {
                    nb_bands += 1;
                    Node color_node = element.element("ColorInterp");
                    if (color_node != null && color_node.getName().equalsIgnoreCase("Alpha"))
                        throw new Exception("Alpha band already present");
                } else {
                    if (nb_bands != 0)
                        break;
                }
                index += 1;
            }


            Element bandEle = rootElement.addElement("VRTRasterBand");
            bandEle.addAttribute("dataType", "Byte");
            bandEle.addAttribute("band", String.valueOf((nb_bands + 1)));
            bandEle.addAttribute("subClass", "VRTWarpedRasterBand");

            Element colorEle = bandEle.addElement("ColorInterp");
            colorEle.setText("Alpha");


            Element gdalWarpEle = rootElement.element("GDALWarpOptions");
            Element alphaBandEle = gdalWarpEle.addElement("DstAlphaBand");
            alphaBandEle.setText(String.valueOf(nb_bands + 1));

            Element optionEle = gdalWarpEle.addElement("Option");
            optionEle.addAttribute("name", "INIT_DEST");
            optionEle.setText("0");

            return document.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vrt_string;
    }

    /**
     * Return the number of data (non-alpha) bands of a gdal dataset
     *
     * @param dataset
     * @return
     */
    public static Integer nb_data_bands(Dataset dataset) {
        Band alphaband = dataset.GetRasterBand(1).GetMaskBand();
        if (alphaband.GetMaskFlags() == gdalconst.GMF_ALPHA ||
                dataset.GetRasterCount() == 4 ||
                dataset.GetRasterCount() == 2)
            return dataset.GetRasterCount() - 1;

        return dataset.GetRasterCount();
    }

    public static void generate_kml(int tx, int ty, int tz, String tileext, int tile_size, double[] tileswne, OptionObj options, List<int[]> children) {

    }

    public static TileJobInfo worker_tile_details(String input_file, String output_folder, OptionObj options, List<TileDetail> tile_details) throws Exception {
        GDAL2TilesTemp gdal2TilesTemp = new GDAL2TilesTemp(input_file, output_folder, options);
        gdal2TilesTemp.open_input();
        gdal2TilesTemp.generate_metadata();

        return gdal2TilesTemp.generate_base_tiles(tile_details);
    }

    public static void create_overview_tiles(TileJobInfo tileJobInfo, String outputFolder, OptionObj options, javafx.scene.control.ProgressBar progressBar) {
        Driver mem_driver = gdal.GetDriverByName("MEM");
        String tile_driver = tileJobInfo.tileDriver;
        Driver out_driver = gdal.GetDriverByName(tile_driver);

        int tilebands = tileJobInfo.nbDataBands + 1;

        int tcount = 0;
        for (int tz = tileJobInfo.tmaxz - 1; tz > tileJobInfo.tminz - 1; tz--) {
            int[] tminxytmaxxy = tileJobInfo.tminmax.get(tz);
            tcount += (1 + Math.abs(tminxytmaxxy[2] - tminxytmaxxy[0])) * (1 + Math.abs(tminxytmaxxy[3] - tminxytmaxxy[1]));
        }

        int ti = 0;

        if (tcount == 0) return;

        ProgressBar progress_bar = new ProgressBar(tcount, progressBar);
        progress_bar.updateTop("Generating Overview Tiles:");
        progress_bar.start();

        for (int tz = tileJobInfo.tmaxz - 1; tz > tileJobInfo.tminz - 1; tz--) {
            int[] tminxytmaxxy = tileJobInfo.tminmax.get(tz);
            for (int ty = tminxytmaxxy[3]; ty > tminxytmaxxy[1] - 1; ty--) {
                for (int tx = tminxytmaxxy[0]; tx < tminxytmaxxy[2] + 1; tx++) {
                    ti += 1;
                    String tilefilename = outputFolder + File.separator + tz + File.separator + String.format("%s_%s.%s", tx, ty, tileJobInfo.tileExtension);

                    logger.debug(tilefilename);

                    progress_bar.log_progress();
                    if (new File(tilefilename).exists()) {
                        continue;
                    }

                    FileUtil.createMissingParentDirectories(new File(tilefilename));

                    Dataset dsquery = mem_driver.Create("", 2 * tileJobInfo.tileSize, 2 * tileJobInfo.tileSize, tilebands);
                    Dataset dstile = mem_driver.Create("", tileJobInfo.tileSize, tileJobInfo.tileSize, tilebands);

                    int dataType = 1;
                    List<int[]> children = new ArrayList<>();
                    for (int y = 2 * ty; y < 2 * ty + 2; y++) {
                        for (int x = 2 * tx; x < 2 * tx + 2; x++) {
                            int[] minxytmaxxy = tileJobInfo.tminmax.get(tz + 1);
                            if (x >= minxytmaxxy[0] && x <= minxytmaxxy[2] &&
                                    y >= minxytmaxxy[1] && y <= minxytmaxxy[3]) {
                                String base_tile_path = tileJobInfo.outputFilePath + File.separator + ((tz + 1)) + File.separator + String.format("%s_%s.%s", x, y, tileJobInfo.tileExtension);
                                if (!new File(base_tile_path).exists()) {
                                    logger.debug("文件：" + base_tile_path + "不存在");
                                    continue;
                                }

                                Dataset dsquerytile = gdal.Open(base_tile_path, gdalconst.GA_ReadOnly);

                                int tileposy, tileposx;
                                if ((ty == 0 && y == 1) || (ty != 0 && (y % (2 * ty)) != 0)) {
                                    tileposy = 0;
                                } else {
                                    tileposy = tileJobInfo.tileSize;
                                }

                                if (tx > 0)
                                    tileposx = x % (2 * tx) * tileJobInfo.tileSize;
                                else if (tx == 0 && x == 1) {
                                    tileposx = tileJobInfo.tileSize;
                                } else {
                                    tileposx = 0;
                                }

                                byte[] temp = new byte[1024 * 1024 * 4];
                                dsquerytile.ReadRaster(0, 0, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, dataType, temp,
                                        new int[]{1, 2, 3, 4});
                                dsquery.WriteRaster(tileposx, tileposy, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, dataType, temp,
                                        getBandList(dsquery.getRasterCount()));

                                children.add(new int[]{x, y, tz + 1});
                            }
                        }
                    }

                    if (children.size() > 0) {
                        scale_query_to_tile(dsquery, dstile, options, tilefilename);

                        if (!options.resampling.equalsIgnoreCase("antialias"))
                            out_driver.CreateCopy(tilefilename, dstile, 0);

                        if (tileJobInfo.kml)
                            generate_kml(tx, ty, tz, tileJobInfo.tileExtension, tileJobInfo.tileSize, get_tile_swne(tileJobInfo, options), options, children);
                    }

                    progress_bar.log_progress();
                }
            }
        }
    }

    public static void create_base_tile(TileJobInfo tileJobInfo, TileDetail tileDetail) {
        int dataBandsCount = tileJobInfo.nbDataBands;
        String output = tileJobInfo.outputFilePath;
        String tileext = tileJobInfo.tileExtension;
        Integer tileSize = tileJobInfo.tileSize;
        OptionObj options = tileJobInfo.options;

        int tileBands = dataBandsCount + 1;
        Dataset ds;


        if (cachedDs != null)
            ds = cachedDs;
        else {
            ds = gdal.Open(tileJobInfo.srcFile, gdalconst.GA_ReadOnly);
            cachedDs = ds;
        }

        Driver mem_drv = gdal.GetDriverByName("MEM");
        Driver out_drv = gdal.GetDriverByName(tileJobInfo.tileDriver);
        Band alphaband = ds.GetRasterBand(1).GetMaskBand();


        int tx = tileDetail.tx;
        int ty = tileDetail.ty;
        int tz = tileDetail.tz;
        int rx = tileDetail.rx;
        int ry = tileDetail.ry;
        int rxsize = tileDetail.rxsize;
        int rysize = tileDetail.rysize;
        int wx = tileDetail.wx;
        int wy = tileDetail.wy;
        int wxsize = tileDetail.wxsize;
        int wysize = tileDetail.wysize;
        int querysize = tileDetail.querysize;
        int dataType = 1;


        String tilefilename = output + File.separator + tz + File.separator + String.format("%s_%s.%s", tx, ty, tileext);
        FileUtil.createMissingParentDirectories(new File(tilefilename));

        Dataset dstile = mem_drv.Create("", tileSize, tileSize, tileBands);

        byte[] data = new byte[1024 * 1024 * ds.GetRasterCount()];
        byte[] alpha = new byte[1024 * 1024];
        if (rxsize != 0 && rysize != 0 && wxsize != 0 && wysize != 0) {
            ds.ReadRaster(rx, ry, rxsize, rysize, wxsize, wysize, dataType, data, getBandList(ds.GetRasterCount()));
            alphaband.ReadRaster(rx, ry, rxsize, rysize, wxsize, wysize, dataType, alpha);

            logger.debug(String.format("rx,ry,rxsize,rysize,wxsize,wysize=%d,%d,%d,%d,%d,%d", rx, ry, rxsize, rysize, wxsize, wysize));
        } else return;

        if (tileSize == querysize) {
            dstile.WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, dataType, data, getBandList(ds.getRasterCount()));
            dstile.WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, dataType, alpha, new int[]{4});
        } else {
            Dataset dsquery = mem_drv.Create("", querysize, querysize, tileBands);
            dsquery.WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, dataType, data, getBandList(ds.getRasterCount()));
            dsquery.WriteRaster(wx, wy, wxsize, wysize, wxsize, wysize, dataType, alpha, new int[]{4});

            scale_query_to_tile(dsquery, dstile, options, tilefilename);

            dsquery.delete();
        }
        data = null;
        alpha = null;
        //antialias
        if (!options.resampling.equalsIgnoreCase("antialias"))
            out_drv.CreateCopy(tilefilename, dstile, 0);

        dstile = null;

        if (tileJobInfo.kml) {
            generate_kml(tx, ty, tz, tileJobInfo.tileExtension, tileJobInfo.tileSize, get_tile_swne(tileJobInfo, options), tileJobInfo.options, null);
        }
    }

    private static double[] get_tile_swne(TileJobInfo tileJobInfo, OptionObj options) {
        return null;
    }

    public static int[] getBandList(int bandCount) {
        int[] bandArray = new int[bandCount];
        for (int i = 0; i < bandCount; i++) {
            bandArray[i] = i + 1;
        }
        return bandArray;
    }

    private static void scale_query_to_tile(Dataset dsquery, Dataset dstile, OptionObj options, String tilefilename) {
        int querysize = dsquery.GetRasterXSize();
        int tilesize = dstile.getRasterXSize();
        int tilebands = dstile.getRasterCount();

        if (options.resampling.equalsIgnoreCase("average")) {
            for (int i = 1; i <= tilebands; i++) {
                int res = gdal.RegenerateOverview(dsquery.GetRasterBand(i), dstile.GetRasterBand(i), "average");
                if (res != 0)
                    logger.error(String.format("RegenerateOverview() failed on %s, error %f", tilefilename, res));
            }
        } else if (options.resampling.equalsIgnoreCase("antialias")) {//antialias
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
            if (options.resampling.equalsIgnoreCase("near"))
                gdal_resampling = gdalconst.GRA_NearestNeighbour;
            else if (options.resampling.equalsIgnoreCase("bilinear"))
                gdal_resampling = gdalconst.GRA_Bilinear;
            else if (options.resampling.equalsIgnoreCase("cubic"))
                gdal_resampling = gdalconst.GRA_Cubic;
            else if (options.resampling.equalsIgnoreCase("cubicspline"))
                gdal_resampling = gdalconst.GRA_CubicSpline;
            else if (options.resampling.equalsIgnoreCase("lanczos"))
                gdal_resampling = gdalconst.GRA_Lanczos;
            dsquery.SetGeoTransform(new double[]{0.0, tilesize / (double) querysize, 0.0, 0.0, 0.0, tilesize / (double) querysize});
            dstile.SetGeoTransform(new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0});
            int res = gdal.ReprojectImage(dsquery, dstile, null, null, gdal_resampling);
            if (res != 0)
                logger.error("ReprojectImage() failed on %s, error %f", tilefilename, res);
        }
    }

    public double[] lonLat2Mercator(double lon, double lat) {
        double x = lon * 20037508.342789 / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180);
        y = y * 20037508.34789 / 180;
        return new double[]{x, y};
    }

    public static class BaseTileTask extends Task<Void> {
        private TileDetail tileDetail;
        private TileJobInfo tileJobInfo;

        public BaseTileTask(TileJobInfo tileJobInfo, TileDetail tileDetail) {
            this.tileJobInfo = tileJobInfo;
            this.tileDetail = tileDetail;
        }

        @Override
        public Void call() throws Exception {
            this.updateMessage("");
            create_base_tile(tileJobInfo, tileDetail);
            return null;
        }
    }
}
