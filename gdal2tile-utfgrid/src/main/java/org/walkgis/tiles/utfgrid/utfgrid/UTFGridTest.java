package org.walkgis.tiles.utfgrid.utfgrid;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.walkgis.tiles.utfgrid.utfgrid.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author JerFer
 * @version 1.0
 * @date 2020/4/1 13:36
 */
public class UTFGridTest {
    /**
     * Skip the codepoints that cannot be encoded directly in JSON.
     *
     * @return
     */
    public static int escapeCodepoints(int codepoint) {
        if (codepoint == 34)
            codepoint += 1;
        else if (codepoint == 92)
            codepoint += 1;
        return codepoint;
    }

    public static int decodeId(int codepoint) {
        codepoint = ord(codepoint);
        if (codepoint >= 93)
            codepoint -= 1;
        if (codepoint >= 35)
            codepoint -= 1;
        codepoint -= 32;
        return codepoint;
    }

    public static int ord(int value) {
        return value;
    }

//    public static void resolve(Grid grid, List<Long> row, Object col) {
//        String row_ = grid.getRows().get("grid").get(row);
//        Long utfVal = row.get(col);
//
//        codePoint = decodeId(utfVal);
//        key = grid.getRows();
//    }

    public static void main(String[] args) throws Exception {
        gdal.AllRegister();
        // 注册所有的驱动
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

        System.out.println(utfGrid);
    }
}
