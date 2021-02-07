package com.walkgis.tiles.test.terrain;

import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;

/**
 * <font style="font-weight: bold;color:red;">昆明能讯科技有限责任公司 *Copyright (c) 2008-2009 PISOFT, All rights reserved 创建日期:Jan 8, *2009</font>
 *
 * <p>
 * 类用途描述
 * </p>
 *
 * @author JerFer
 * @version 1.0
 * @date 2021/2/7 11:11
 * @see GdalClip
 * @since 从那个版本开始加入
 */
public class GdalClip {
    public static void main(String[] args) {
        gdal.AllRegister();

        gdal.SetConfigOption("GDAL_DATA", GDAL2Srtmtiles.class.getClassLoader().getResource("gdal-data").getFile().substring(1));
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");
        gdal.SetConfigOption("CPL_DEBUG", "OFF");

        Dataset inDs = gdal.Open("E:\\Data\\10米DEM\\TIFF.tif", gdalconstConstants.GA_ReadOnly);

        Band band1 = inDs.GetRasterBand(1);

        int offsetX = 2441, offsetY = 1677;
        int blockXsize = 400, blockYsize = 400;


        short[] bytes = new short[blockXsize * blockYsize];
        band1.ReadRaster(offsetX, offsetY, blockXsize, blockYsize, band1.getDataType(), bytes);

        Driver tiffDriver = gdal.GetDriverByName("GTiff");
        Dataset outDs = tiffDriver.Create("E:\\Data\\10米DEM\\clip.tif", blockXsize, blockYsize, 1, band1.getDataType());

        double[] oriTransform = inDs.GetGeoTransform();

        double topLeftX = oriTransform[0];// 左上角x坐标
        double wePixelResolution = oriTransform[1];//东西方向像素分辨率
        double topLeftY = oriTransform[3];//左上角y坐标
        double nsPixelResolution = oriTransform[5];// 南北方向像素分辨率

        topLeftX = topLeftX + offsetX * wePixelResolution;
        topLeftY = topLeftY + offsetY * nsPixelResolution;

        outDs.SetGeoTransform(new double[]{topLeftX, oriTransform[1], oriTransform[2], topLeftY, oriTransform[4], oriTransform[5]});

        outDs.SetProjection(inDs.GetProjection());
        outDs.WriteRaster(0, 0, blockXsize, blockYsize, blockXsize, blockYsize, band1.getDataType(), bytes, new int[]{1});

        outDs.FlushCache();

        outDs.delete();
        inDs.delete();
    }
}
