package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/*
 *
 * gen by beetlsql 2020-03-15
 */
@Table(name = ".sample_feature_table")
public class SampleFeatureTable {

    // alias
    public static final String ALIAS_id = "id";
    public static final String ALIAS_boolean_attribute = "boolean_attribute";
    public static final String ALIAS_geometry = "geometry";
    public static final String ALIAS_raster_or_photo = "raster_or_photo";
    public static final String ALIAS_real_attribute = "real_attribute";
    public static final String ALIAS_text_attribute = "text_attribute";

    private Integer id;
    private Integer booleanAttribute;
    private String geometry;
    private String rasterOrPhoto;
    private Float realAttribute;
    private String textAttribute;

    public SampleFeatureTable() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBooleanAttribute() {
        return booleanAttribute;
    }

    public void setBooleanAttribute(Integer booleanAttribute) {
        this.booleanAttribute = booleanAttribute;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getRasterOrPhoto() {
        return rasterOrPhoto;
    }

    public void setRasterOrPhoto(String rasterOrPhoto) {
        this.rasterOrPhoto = rasterOrPhoto;
    }

    public Float getRealAttribute() {
        return realAttribute;
    }

    public void setRealAttribute(Float realAttribute) {
        this.realAttribute = realAttribute;
    }

    public String getTextAttribute() {
        return textAttribute;
    }

    public void setTextAttribute(String textAttribute) {
        this.textAttribute = textAttribute;
    }


}
