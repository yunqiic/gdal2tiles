package com.walkgis.tiles.util.storage;


import com.walkgis.tiles.util.mbtiles.MBTiles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MbtilesStorage extends Storage {
    private Integer zoom_offset;
    private List<String> seen;
    public static MBTiles mbtiles;
    private String fileName;

    private MbtilesStorage(Object fileName, Integer zoom_offset, List<String> seen) {
        if (zoom_offset == null)
            zoom_offset = 0;
        this.zoom_offset = zoom_offset;
        if (seen == null)
            seen = new ArrayList<>();
        this.seen = seen;

        if (fileName instanceof String) {
            this.fileName = (String) fileName;
            this.mbtiles = MBTiles.create(fileName.toString(), null, null);
        } else {
            this.mbtiles = (MBTiles) fileName;
            this.fileName = mbtiles.filename;
        }

    }

    public void del() {
        if (this.mbtiles != null)
            this.mbtiles.close(true);
    }

    public void exit() {
        if (this.mbtiles != null)
            this.mbtiles.close(true);
    }

    public static MbtilesStorage create(String filename, Map<String, String> metadata, String version) {
        if (new File(filename).exists()) {
            new File(filename).delete();
        }
        mbtiles = MBTiles.create(filename, metadata, version);

        return new MbtilesStorage(mbtiles, 0, null);
    }


//    @Override
//    public void post_import(TmsPyramid pyramid) {
//        GDALCoordinateTransformation transform = pyramid.dataset().GetCoordinateTransformation(GDALSpatialReference.FromEPSG(4326));
//
//        Extents extents = null;
//        try {
//            extents = pyramid.dataset().GetTiledExtents(transform, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        this.mbtiles.metadata.setitem("bounds",
//                extents.lower_left.x + "," +
//                        extents.lower_left.y + "," +
//                        extents.upper_right.x + "," +
//                        extents.upper_right.y);
//    }

    @Override
    public void save(int x, int y, int z, BufferedImage image) {
        String hashed = this.get_hash(x, y, z);
        if (this.seen.contains(hashed))
            this.mbtiles.insert(x, y, (z + this.zoom_offset), Long.parseLong(hashed), null);
        else {
            this.seen.add(hashed);
//            try {
////                byte[] contents = this.renderer.render(image);
////                this.mbtiles.insert(x, y, z + this.zoom_offset, Long.parseLong(hashed), contents);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public String filepath(int x, int y, int z, boolean hashed) {
        return null;
    }
}
