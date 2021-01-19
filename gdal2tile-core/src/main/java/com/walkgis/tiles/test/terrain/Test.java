package com.walkgis.tiles.test.terrain;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        File file = new File("D:\\java\\ds_DMTZ_linestring.geojson");
        FileInputStream src = FileUtils.openInputStream(file);
        byte[] data2 = new byte[4];

        while (src.read(data2)!=-1){
            System.out.println(data2);
        }
        src.close();

        FileUtils.forceDelete(file);
    }


}
