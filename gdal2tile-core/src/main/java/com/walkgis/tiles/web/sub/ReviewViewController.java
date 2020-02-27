package com.walkgis.tiles.web.sub;

import com.walkgis.tiles.entity.FileItem;
import com.walkgis.tiles.web.MainViewController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class ReviewViewController implements Initializable {
    @FXML
    private ImageView imageView;

    public ReviewViewController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void showReview(FileItem fileItem) {
        Dataset dataset = fileItem.getDataset();

        Driver pngDriver = gdal.GetDriverByName("PNG");
        Driver memDriver = gdal.GetDriverByName("MEM");
        if (pngDriver == null) return;
        Dataset dstile = memDriver.Create("", (int) imageView.getFitWidth(), (int) imageView.getFitHeight(), dataset.GetRasterCount(), gdalconst.GDT_Byte);
        try {
            for (int i = 1, count = dataset.getRasterCount(); i <= count; i++) {
                gdal.RegenerateOverview(dataset.GetRasterBand(i), dstile.GetRasterBand(i), "average");
            }

            File temp = File.createTempFile(UUID.randomUUID().toString(), ".png");
            temp.deleteOnExit();
            pngDriver.CreateCopy(temp.getAbsolutePath(), dstile, 0);


            BufferedImage bf = null;
            try {
                bf = ImageIO.read(temp);
                WritableImage wr = null;
                if (bf != null) {
                    wr = new WritableImage(bf.getWidth(), bf.getHeight());
                    PixelWriter pw = wr.getPixelWriter();
                    for (int x = 0; x < bf.getWidth(); x++) {
                        for (int y = 0; y < bf.getHeight(); y++) {
                            pw.setArgb(x, y, bf.getRGB(x, y));
                        }
                    }
                }
                imageView.setImage(wr);
            } catch (IOException ex) {
                System.out.println("Image failed to load.");
            }

            dstile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dstile.delete();

    }
}
