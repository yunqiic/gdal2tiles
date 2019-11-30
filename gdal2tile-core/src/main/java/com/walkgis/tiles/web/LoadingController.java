package com.walkgis.tiles.web;

import com.walkgis.tiles.control.LoadingView;
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
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class LoadingController implements Initializable {
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane root;

    @Autowired
    private LoadingView loadingView;

    @Autowired
    public MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Object o = mainController.getSelectItem();
        if (o != null && o instanceof FileItem) {
            FileItem fileItem = (FileItem) o;
            Dataset dataset = fileItem.getDataset();

            Driver driver = gdal.GetDriverByName("MEM");
            if (driver == null) return;
            Dataset dstile = driver.Create("", (int) imageView.getFitWidth(), (int) imageView.getFitHeight(), gdalconst.GDT_Int32);

            int count = dataset.GetRasterCount();
            for (int i = 1; i <= count; i++) {
                gdal.RegenerateOverview(dataset.GetRasterBand(i), dstile.GetRasterBand(i), "average");
            }
            dataset.delete();

            int[] shorts = new int[dstile.getRasterXSize() * dstile.getRasterYSize() * 3];
            dstile.ReadRaster(0, 0, dstile.getRasterXSize(), dstile.getRasterYSize(), dstile.getRasterXSize(), dstile.getRasterYSize(), gdalconst.GDT_Byte, shorts, new int[]{1, 2, 3});

            BufferedImage bufferedImage = new BufferedImage((int) imageView.getFitWidth(), (int) imageView.getFitHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            bufferedImage.setRGB(0, 0, (int) imageView.getFitWidth(), (int) imageView.getFitHeight(), shorts, 0, 0);
            dstile.delete();
            imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        }
    }
}
