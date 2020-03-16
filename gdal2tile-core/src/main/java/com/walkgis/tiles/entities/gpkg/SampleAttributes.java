package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/*
 *
 * gen by beetlsql 2020-03-15
 */
@Table(name = ".sample_attributes")
public class SampleAttributes {

    // alias
    public static final String ALIAS_id = "id";
    public static final String ALIAS_boolean_attribute = "boolean_attribute";
    public static final String ALIAS_raster_or_photo = "raster_or_photo";
    public static final String ALIAS_real_attribute = "real_attribute";
    public static final String ALIAS_text_attribute = "text_attribute";

    private Integer id;
    private Integer booleanAttribute;
    private String rasterOrPhoto;
    private Float realAttribute;
    private String textAttribute;

    public SampleAttributes() {
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
