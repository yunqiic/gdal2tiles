package com.walkgis.tiles.test;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.SpatialReference;

public class GdalGeotiffTest {
    public static void main(String[] args) {
        // 注册所有的驱动
        gdal.AllRegister();
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");

        Driver driverGeotiff = gdal.GetDriverByName("GTiff");
        if (driverGeotiff == null) {
            System.out.println("FAILURE: Output driver 'GTiff' not recognized.");
        }

        Dataset dataset = gdal.Open("E:\\Data\\CAOBAO\\abc.tif", gdalconstConstants.GA_ReadOnly);
        if (dataset == null) {
            System.out.println("GDAL read error: " + gdal.GetLastErrorMsg());
        }

        System.out.println("driver short name: " + driverGeotiff.getShortName());
        System.out.println("driver long name: " + driverGeotiff.getLongName());
        System.out.println("metadata list: " + driverGeotiff.GetMetadata_List());

        int xsize = dataset.getRasterXSize();
        int ysize = dataset.getRasterYSize();
        int count = dataset.getRasterCount();
        String proj = dataset.GetProjection();
        SpatialReference sp = new SpatialReference(proj);
        Band band = dataset.GetRasterBand(1);

        short[] shorts = new short[256 * 256 * 3];
        int[] ints = new int[256 * 256 * 3];
        byte[] bytes = new byte[256 * 256 * 3];

        dataset.ReadRaster(256, 256, 256, 256, 256, 256, 1, bytes, new int[]{1, 2, 3});

        Dataset dataset1 = gdal.GetDriverByName("MEM").Create("", 256, 256, 3, 1);
        dataset1.WriteRaster(0, 0, 256, 256, 256, 256, 1, bytes, new int[]{1, 2, 3});

        driverGeotiff.CreateCopy("E:\\Data\\CAOBAO\\tiles\\abc.png", dataset1);

        // 左上角点坐标 lon lat: transform[0]、transform[3]
        // 像素分辨率 x、y方向 : transform[1]、transform[5]
        // 旋转角度: transform[2]、transform[4])
        double[] transform = dataset.GetGeoTransform();
        for (int i = 0; i < transform.length; i++) {
            System.out.println("transform: " + transform[i]);
        }
    }
}
