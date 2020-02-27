package com.walkgis.tiles.entity;

import javafx.beans.property.SimpleStringProperty;
import org.gdal.gdal.Dataset;
import org.locationtech.jts.geom.Envelope;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图层列表绑定对象
 */
public class FileItem {
    private SimpleStringProperty projection = new SimpleStringProperty();
    private SimpleStringProperty transform = new SimpleStringProperty();

    private File file;
    private String name;
    private Integer index;
    private Dataset dataset;
    private Envelope envelope;
    private double[] transformDouble;
    private int width;
    private int height;

    public FileItem() {
    }

    public FileItem(File file, Integer index) {
        this.file = file;
        this.index = index;
        this.name = file.getName();
    }

    public FileItem(File file, String name, Integer index) {
        this.file = file;
        this.name = name;
        this.index = index;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Dataset getDataset() {
        return dataset;
    }

    /**
     * transform[0]：左上角x坐标
     * transform[1]：东西方向空间分辨率
     * transform[2]：x方向旋转角
     * transform[3]：左上角y坐标
     * transform[4]：y方向旋转角
     * transform[5]：南北方向空间分辨率
     *
     * @param dataset
     */
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
        this.transformDouble = dataset.GetGeoTransform();
        this.setProjection(dataset.GetProjection());

        int x = dataset.getRasterXSize();
        int y = dataset.getRasterYSize();

        this.envelope = new Envelope(
                transformDouble[0],
                transformDouble[0] + x * transformDouble[1] + y * transformDouble[2],
                transformDouble[3],
                transformDouble[3] + x * transformDouble[4] + y * transformDouble[5]
        );

        List<String> res = Arrays.stream(transformDouble).mapToObj(a -> String.format("%.9f", a)).collect(Collectors.toList());
        String transformStr = "";
        transformStr += String.join(" ", res.subList(0, 3));
        transformStr += "\n";
        transformStr += String.join(" ", res.subList(3, 6));
        setTransform(transformStr);
    }

    public String getProjection() {
        return projection.get();
    }

    public SimpleStringProperty projectionProperty() {
        return projection;
    }

    public void setProjection(String projection) {
        this.projection.set(projection);
    }

    public String getTransform() {
        return transform.get();
    }

    public SimpleStringProperty transformProperty() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform.set(transform);
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double[] getTransformDouble() {
        return transformDouble;
    }

    public void setTransformDouble(double[] transformDouble) {
        this.transformDouble = transformDouble;
    }
}
