package com.walkgis.tiles.util;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.locationtech.jts.geom.Envelope;

public class PDFReader {
    private SpatialReference spatialReference;
    private Envelope envelope;
    private int width;
    private int height;
    private Dataset dataset;

    public void init(String file, String imgSavePath) {
        // 注册所有的驱动
        ogr.RegisterAll();
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");

        this.dataset = gdal.Open(file, gdalconstConstants.GA_ReadOnly);
        if (dataset == null) {
            System.out.println("GDAL read error: " + gdal.GetLastErrorMsg());
        }

        this.width = dataset.getRasterXSize();
        this.height = dataset.getRasterYSize();

        // 左上角点坐标 lon lat: transform[0]、transform[3]
        // 像素分辨率 x、y方向 : transform[1]、transform[5]
        // 旋转角度: transform[2]、transform[4])
        double[] transform = dataset.GetGeoTransform();

        double[] ulCoord = new double[2];
        ulCoord[0] = transform[0];
        ulCoord[1] = transform[3];

        double[] brCoord = new double[2];
        int x = dataset.getRasterXSize();
        int y = dataset.getRasterYSize();
        brCoord[0] = transform[0] + x * transform[1] + y * transform[2];
        brCoord[1] = transform[3] + x * transform[4] + y * transform[5];

//        transform[0]：左上角x坐标
//        transform[1]：东西方向空间分辨率
//        transform[2]：x方向旋转角
//        transform[3]：左上角y坐标
//        transform[4]：y方向旋转角
//        transform[5]：南北方向空间分辨率
        this.envelope = new Envelope(ulCoord[0], brCoord[0], ulCoord[1], brCoord[1]);

        this.spatialReference = new SpatialReference(dataset.GetProjection());

    }

    public int[] getBandList(int bandCount) {
        int[] bandArray = new int[bandCount];
        for (int i = 0; i < bandCount; i++) {
            bandArray[i] = i + 1;
        }
        return bandArray;
    }

    public SpatialReference getSpatialReference() {
        return spatialReference;
    }

    public void setSpatialReference(SpatialReference spatialReference) {
        this.spatialReference = spatialReference;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }
}
