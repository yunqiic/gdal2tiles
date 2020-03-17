package com.walkgis.tiles.util;

import com.walkgis.tiles.util.storage.Storage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.osr.SpatialReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CommonUtils {
    private final static Log logger = LogFactory.getLog(CommonUtils.class);

    private static ThreadFactory namedThreadFactory = new ThreadFactory() {
        private int i = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("thread-" + i++);
            return thread;
        }
    };

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
        if (!(options.s_srs == null || "".equals(options.s_srs))) {
//            input_srs = new SpatialReference();
            input_srs.SetFromUserInput(options.s_srs);
            input_srs_wkt = input_srs.ExportToWkt();
        } else {
            input_srs_wkt = input_dataset.GetProjection();

            if (!(input_srs_wkt == null || "".equals(input_srs_wkt)) && input_dataset.GetGCPCount() != 0)
                input_srs_wkt = input_dataset.GetGCPProjection();
            if (!(input_srs_wkt == null || "".equals(input_srs_wkt))) {
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
        //创建一个DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
            Document document = db.parse(new StringBufferInputStream(vrt_string));

            //获取所有book节点的集合
            NodeList bookList = document.getElementsByTagName("GDALWarpOptions");
            if (bookList == null || bookList.getLength() == 0) return vrt_string;
            Node gdalWarpOptions = bookList.item(0);

            for (Map.Entry<String, String> entry : warp_options.entrySet()) {
                Element optionNode = document.createElement("Option");
                optionNode.setTextContent(entry.getValue());
                optionNode.setAttribute("name", entry.getKey());
                gdalWarpOptions.appendChild(optionNode);
            }

            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            return writer.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
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
        //创建一个DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        int index = 0;
        int nb_bands = 0;
        try {
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
            Document document = db.parse(new StringBufferInputStream(vrt_string));

            NodeList children = document.getChildNodes();
            Element rootElement = (Element) children.item(0);

            for (int i = 0; i < rootElement.getChildNodes().getLength(); i++) {
                Element element = (Element) children.item(i);
                if (element.getNodeName().equalsIgnoreCase("VRTRasterBand")) {
                    nb_bands += 1;
                    NodeList color_nodes = element.getElementsByTagName("ColorInterp");
                    if (color_nodes.getLength() > 0) {
                        Element color_node = (Element) color_nodes.item(0);
                        if (color_node.getElementsByTagName("Alpha").getLength() > 0)
                            throw new Exception("Alpha band already present");
                    }
                } else {
                    if (nb_bands != 0)
                        break;
                }
                index += 1;
            }

            Element bandEle = document.createElement("VRTRasterBand");
            bandEle.setAttribute("dataType", "Byte");
            bandEle.setAttribute("band", String.valueOf((nb_bands + 1)));
            bandEle.setAttribute("subClass", "VRTWarpedRasterBand");
            rootElement.appendChild(bandEle);

            Element colorEle = document.createElement("ColorInterp");
            colorEle.setTextContent("Alpha");
            bandEle.appendChild(colorEle);

            NodeList gdalWarpOptions = rootElement.getElementsByTagName("GDALWarpOptions");
            Element gdalWarpEle = (Element) gdalWarpOptions.item(0);
            Element alphaBandEle = document.createElement("DstAlphaBand");
            alphaBandEle.setTextContent(String.valueOf(nb_bands + 1));
            gdalWarpEle.appendChild(alphaBandEle);

            Element optionEle = document.createElement("Option");
            optionEle.setAttribute("name", "INIT_DEST");
            optionEle.setTextContent("0");
            gdalWarpEle.appendChild(optionEle);

            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            return writer.toString();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
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

    public static TileJobInfo worker_tile_details(GDAL2Tiles gdal2TilesTemp, List<TileDetail> tile_details) throws Exception {
        gdal2TilesTemp.generate_metadata();

        return gdal2TilesTemp.generate_base_tiles(tile_details);
    }

    public static void create_overview_tiles(GDAL2Tiles gdal2TilesTemp, TileJobInfo tileJobInfo, String outputFolder, RunTask runTask) throws IOException {
        Driver mem_driver = gdal.GetDriverByName("MEM");
        String tile_driver = tileJobInfo.tileDriver;
        Driver out_driver = gdal.GetDriverByName(tile_driver);
        TileSwne tileSwne = tileJobInfo.tileSwne;
        Storage storage = tileJobInfo.storage;

        int tilebands = tileJobInfo.nbDataBands + 1;

        int tcount = 0;
        for (int tz = tileJobInfo.tmaxz - 1; tz > tileJobInfo.tminz - 1; tz--) {
            int[] tminxytmaxxy = tileJobInfo.tminmax.get(tz);
            tcount += (1 + Math.abs(tminxytmaxxy[2] - tminxytmaxxy[0])) * (1 + Math.abs(tminxytmaxxy[3] - tminxytmaxxy[1]));
        }

        int ti = 0;

        if (tcount == 0) return;

        runTask.updateTitle("Generating Overview Tiles");
        runTask.updateProgress(0, tcount);

        long flag = 0;
        for (int tz = tileJobInfo.tmaxz - 1; tz > tileJobInfo.tminz - 1; tz--) {
            runTask.updateMessage("正在生成第" + tz + "级切片");
            int[] tminxytmaxxy = tileJobInfo.tminmax.get(tz);
            for (int ty = tminxytmaxxy[3]; ty > tminxytmaxxy[1] - 1; ty--) {
                for (int tx = tminxytmaxxy[0]; tx < tminxytmaxxy[2] + 1; tx++) {
                    ti += 1;

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
                                dsquerytile.ReadRaster(0, 0, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, dataType, temp, new int[]{1, 2, 3, 4});

                                dsquery.WriteRaster(tileposx, tileposy, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, tileJobInfo.tileSize, dataType, temp, getBandList(dsquery.getRasterCount()));

                                children.add(new int[]{x, y, tz + 1});
                            }
                        }
                    }

                    if (children.size() > 0) {
                        OptionObj options = gdal2TilesTemp.getOptions();
                        scale_query_to_tile(dsquery, dstile, options, outputFolder + File.separator + tz + File.separator + String.format("%s_%s.%s", tx, ty, tileJobInfo.tileExtension));

                        if (!gdal2TilesTemp.getOptions().resampling.equalsIgnoreCase("antialias"))
                            tileSwne.dealImage(storage, out_driver, dstile, tx, ty, tz, tileJobInfo.tileExtension);

                        if (tileJobInfo.kml)
                            generate_kml(tx, ty, tz, tileJobInfo.tileExtension, tileJobInfo.tileSize, get_tile_swne(tileJobInfo, options), options, children);
                    }

                    runTask.updateProgress(flag + 1, tcount);
                    flag++;
                }
            }
        }

        runTask.updateTitle("完成");
        runTask.updateMessage("");
    }

    public static void create_base_tile(TileJobInfo tileJobInfo, TileDetail tileDetail) throws IOException {
        int dataBandsCount = tileJobInfo.nbDataBands;
        String output = tileJobInfo.outputFilePath;
        String tileext = tileJobInfo.tileExtension;
        Integer tileSize = tileJobInfo.tileSize;
        OptionObj options = tileJobInfo.options;
        TileSwne tileSwne = tileJobInfo.tileSwne;
        Storage storage = tileJobInfo.storage;

        int tileBands = dataBandsCount + 1;
        Dataset ds = gdal.Open(tileJobInfo.srcFile, gdalconst.GA_ReadOnly);
        synchronized (ds) {
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

                scale_query_to_tile(dsquery, dstile, options, output + File.separator + tz + File.separator + String.format("%s_%s.%s", tx, ty, tileext));

                dsquery.delete();
            }
            //antialias
            if (!options.resampling.equalsIgnoreCase("antialias")){
                tileSwne.dealImage(storage, out_drv, dstile, tx, ty, tz, tileext);
            }

            if (tileJobInfo.kml) {
                generate_kml(tx, ty, tz, tileJobInfo.tileExtension, tileJobInfo.tileSize, get_tile_swne(tileJobInfo, options), tileJobInfo.options, null);
            }
            dstile.delete();
            ds.delete();
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
                logger.error(String.format("ReprojectImage() failed on %s, error %f", tilefilename, res));
        }
    }

    public static void single_threaded_tiling(GDAL2Tiles gdal2TilesTemp, RunTask runTask) throws Exception {
        List<TileDetail> tile_details = new ArrayList<>();
        runTask.updateTitle("初始化切片信息");
        TileJobInfo conf = CommonUtils.worker_tile_details(gdal2TilesTemp, tile_details);
        //初始化
        runTask.updateTitle("生成基础切片");
        runTask.updateProgress(tile_details.size(), 0);
        int flag = 0;
        for (TileDetail tileDetail : tile_details) {
            CommonUtils.create_base_tile(conf, tileDetail);
            runTask.updateProgress(flag + 1, tile_details.size());
            runTask.updateMessage(tileDetail.toString());
            flag++;
        }

        CommonUtils.create_overview_tiles(gdal2TilesTemp, conf, gdal2TilesTemp.getOutput_folder(), runTask);
    }

    public static void multi_threaded_tiling(GDAL2Tiles gdal2TilesTemp, RunTask runTask) {
        int nb_processes = gdal2TilesTemp.getOptions().nb_processes == null ? 1 : gdal2TilesTemp.getOptions().nb_processes;
        gdal.SetConfigOption("GDAL_CACHEMAX", String.valueOf(gdal.GetCacheMax() / nb_processes));

        List<TileDetail> tile_details = new ArrayList<>();
        ExecutorService service = null;

        try {
            runTask.updateTitle("初始化切片信息");
            TileJobInfo conf = CommonUtils.worker_tile_details(gdal2TilesTemp, tile_details);
            runTask.updateProgress(0, tile_details.size());

            service = new ThreadPoolExecutor(1, nb_processes, 100L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1000), namedThreadFactory,
                    new ThreadPoolExecutor.CallerRunsPolicy());

            // 1.定义CompletionService
            CompletionService<Long> completionService = new ExecutorCompletionService<>(service);

            runTask.updateTitle("生成基础切片");
            for (TileDetail tileDetail : tile_details) {
                completionService.submit(new CommonUtils.BaseTileTask(conf, tileDetail));
            }

            for (int i = 0; i < tile_details.size(); i++) {
                Long result = completionService.take().get();
                runTask.updateProgress(result + 1, tile_details.size());
            }

            CommonUtils.create_overview_tiles(gdal2TilesTemp, conf, gdal2TilesTemp.getOutput_folder(), runTask);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (service != null) service.shutdown();
        }
    }

    public double[] lonLat2Mercator(double lon, double lat) {
        double x = lon * 20037508.342789 / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180);
        y = y * 20037508.34789 / 180;
        return new double[]{x, y};
    }

    public static class BaseTileTask implements Callable<Long> {
        private TileDetail tileDetail;
        private TileJobInfo tileJobInfo;

        public BaseTileTask(TileJobInfo tileJobInfo, TileDetail tileDetail) {
            this.tileJobInfo = tileJobInfo;
            this.tileDetail = tileDetail;
        }

        @Override
        public Long call() {
            try {
                create_base_tile(tileJobInfo, tileDetail);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                return tileDetail.index;
            }
        }
    }
}
