package org.walkgis.tiles.utfgrid;

import com.google.gson.Gson;
import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.walkgis.tiles.utfgrid.utfgrid.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class UtfgridApplicationTests {

    @Test
    void contextLoads() throws Exception {
        // 注册所有的驱动
        gdal.AllRegister();
        ogr.RegisterAll();
        // 为了支持中文路径，请添加下面这句代码
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        // 为了使属性表字段支持中文，请添加下面这句
        gdal.SetConfigOption("SHAPE_ENCODING", "");

        DataSource ds = ogr.Open("E:\\projects\\GIS\\gdal2tiles\\gdal2tile-utfgrid\\asset\\ne_110m_admin_0_countries.shp");

        Layer layer = ds.GetLayer(0);

        Extent box = new Extent(-140, 0, -50, 90);
        Request tile = new Request(256, 256, box);
        CoordTransform ctrans = new CoordTransform(tile);
        Grid grid = new Grid();

        Renderer renderer = new Renderer(grid, ctrans);
        renderer.apply(layer, Arrays.asList("NAME_FORMA", "POP_EST"));

        Map<String, Object> utfGrid = grid.encode();

        Gson gson = new Gson();
        String json = gson.toJson(utfGrid, HashMap.class);
        System.out.println(json);
        //转成JSON输出
    }

}
