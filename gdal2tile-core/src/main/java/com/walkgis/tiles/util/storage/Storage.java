package com.walkgis.tiles.util.storage;

import java.awt.image.BufferedImage;

public abstract class Storage {

    public Storage() {
    }

    public String get_hash(int tileX, int tileY, int levelOfDetail) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) {
                digit++;
            }
            if ((tileY & mask) != 0) {
                digit++;
                digit++;
            }

            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    public abstract String filepath(int x, int y, int z, boolean hashed);

//    public abstract void post_import(TmsPyramid pyramid);

    public abstract void save(int x, int y, int z, BufferedImage image);

    public void save_border(int x, int y, int z) {
        this.save(x, y, z, this.border_image());
    }

    protected BufferedImage border_image() {
        return null;
    }
}
