package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/* 
* 
* gen by beetlsql 2020-03-15
*/
@Table(name=".gpkg_tile_matrix")
public class GpkgTileMatrix   {
	
	// alias
	public static final String ALIAS_table_name = "table_name";
	public static final String ALIAS_zoom_level = "zoom_level";
	public static final String ALIAS_matrix_height = "matrix_height";
	public static final String ALIAS_matrix_width = "matrix_width";
	public static final String ALIAS_tile_height = "tile_height";
	public static final String ALIAS_tile_width = "tile_width";
	public static final String ALIAS_pixel_x_size = "pixel_x_size";
	public static final String ALIAS_pixel_y_size = "pixel_y_size";
	
	private String tableName ;
	private Integer zoomLevel ;
	private Integer matrixHeight ;
	private Integer matrixWidth ;
	private Integer tileHeight ;
	private Integer tileWidth ;
	private Float pixelXSize ;
	private Float pixelYSize ;
	
	public GpkgTileMatrix() {
	}
	
	public String getTableName(){
		return  tableName;
	}
	public void setTableName(String tableName ){
		this.tableName = tableName;
	}
	
	public Integer getZoomLevel(){
		return  zoomLevel;
	}
	public void setZoomLevel(Integer zoomLevel ){
		this.zoomLevel = zoomLevel;
	}
	
	public Integer getMatrixHeight(){
		return  matrixHeight;
	}
	public void setMatrixHeight(Integer matrixHeight ){
		this.matrixHeight = matrixHeight;
	}
	
	public Integer getMatrixWidth(){
		return  matrixWidth;
	}
	public void setMatrixWidth(Integer matrixWidth ){
		this.matrixWidth = matrixWidth;
	}
	
	public Integer getTileHeight(){
		return  tileHeight;
	}
	public void setTileHeight(Integer tileHeight ){
		this.tileHeight = tileHeight;
	}
	
	public Integer getTileWidth(){
		return  tileWidth;
	}
	public void setTileWidth(Integer tileWidth ){
		this.tileWidth = tileWidth;
	}
	
	public Float getPixelXSize(){
		return  pixelXSize;
	}
	public void setPixelXSize(Float pixelXSize ){
		this.pixelXSize = pixelXSize;
	}
	
	public Float getPixelYSize(){
		return  pixelYSize;
	}
	public void setPixelYSize(Float pixelYSize ){
		this.pixelYSize = pixelYSize;
	}
	

}
