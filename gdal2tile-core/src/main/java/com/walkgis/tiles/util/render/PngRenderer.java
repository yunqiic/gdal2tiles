package com.walkgis.tiles.util.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PngRenderer extends Renderer {
    public PngRenderer() throws Exception {
        this._suffix = "png";
    }

    @Override
    public byte[] render(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, _suffix, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
