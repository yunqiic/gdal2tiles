package com.walkgis.tiles.util.render;


import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JpegRenderer extends Renderer {
    private int compression;

    public JpegRenderer(Integer compression) throws Exception {
        super._suffix = "jpg";
        if (compression == null)
            compression = 75;
        if (!(0 <= compression && compression <= 100))
            throw new Exception("ompression must be between 0 and 100: {0!r}");
        this.compression = compression;
    }

    @Override
    public byte[] render(BufferedImage image) throws IOException {
        Thumbnails.Builder builder = Thumbnails.of(image);
        BufferedImage newImage = Thumbnails.fromImages(builder.iterableBufferedImages()).outputQuality(this.compression).asBufferedImage();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(newImage, _suffix, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
