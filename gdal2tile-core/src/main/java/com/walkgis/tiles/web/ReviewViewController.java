package com.walkgis.tiles.web;

import com.walkgis.tiles.entity.FileItem;
import de.felixroske.jfxsupport.FXMLController;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

@FXMLController
public class ReviewViewController implements Initializable {
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane root;

    @Autowired
    public MainViewController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void showReview() {
        Object o = mainController.getSelectItem();
        if (o != null && o instanceof FileItem) {
            FileItem fileItem = (FileItem) o;
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
                Image image = ImageIO.read(temp);
                imageView.setImage(SwingFXUtils.toFXImage((BufferedImage) image, null));
                dstile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dstile.delete();

        }
    }
}
