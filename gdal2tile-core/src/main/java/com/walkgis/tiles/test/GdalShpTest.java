package com.walkgis.tiles.test;

import org.gdal.gdal.gdal;
import org.gdal.ogr.*;

import java.io.File;

public class GdalShpTest {
    public static void main(String[] args) {
        // 注册所有的驱动
        gdal.AllRegister();
        ogr.RegisterAll();
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");

        String strVectorFile = "E:\\Data\\SHP\\diquJie_polyline.shp";
        //打开文件
        DataSource ds = ogr.Open(strVectorFile);
        if (ds == null) {
            System.out.println("打开文件失败！");
            ds.delete();
            return;
        }

        Layer layer = ds.GetLayer(0);

        layer.ResetReading();
        Feature feature = layer.GetNextFeature();
        while (feature != null) {
            System.out.println(feature.GetFID());
        }
        System.out.println("打开文件成功！");
        Driver dv = ogr.GetDriverByName("GeoJSON");
        if (dv == null) {
            System.out.println("打开驱动失败！");
            return;
        }
        System.out.println("打开驱动成功！");
        File file = new File("E:\\Data\\SHP\\node.json");
        if (file.exists()) file.delete();
        dv.CopyDataSource(ds, "E:\\Data\\SHP\\node.json");

        ds.delete();
        System.out.println("转换成功！");
    }
}
