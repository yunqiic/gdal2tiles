package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/* 
* 
* gen by beetlsql 2020-03-15
*/
@Table(name=".gpkg_contents")
public class GpkgContents   {
	
	// alias
	public static final String ALIAS_table_name = "table_name";
	public static final String ALIAS_srs_id = "srs_id";
	public static final String ALIAS_data_type = "data_type";
	public static final String ALIAS_description = "description";
	public static final String ALIAS_identifier = "identifier";
	public static final String ALIAS_last_change = "last_change";
	public static final String ALIAS_max_x = "max_x";
	public static final String ALIAS_max_y = "max_y";
	public static final String ALIAS_min_x = "min_x";
	public static final String ALIAS_min_y = "min_y";
	
	private String tableName ;
	private Integer srsId ;
	private String dataType ;
	private String description ;
	private String identifier ;
	private String lastChange ;
	private Float maxX ;
	private Float maxY ;
	private Float minX ;
	private Float minY ;
	
	public GpkgContents() {
	}
	
	public String getTableName(){
		return  tableName;
	}
	public void setTableName(String tableName ){
		this.tableName = tableName;
	}
	
	public Integer getSrsId(){
		return  srsId;
	}
	public void setSrsId(Integer srsId ){
		this.srsId = srsId;
	}
	
	public String getDataType(){
		return  dataType;
	}
	public void setDataType(String dataType ){
		this.dataType = dataType;
	}
	
	public String getDescription(){
		return  description;
	}
	public void setDescription(String description ){
		this.description = description;
	}
	
	public String getIdentifier(){
		return  identifier;
	}
	public void setIdentifier(String identifier ){
		this.identifier = identifier;
	}
	
	public String getLastChange(){
		return  lastChange;
	}
	public void setLastChange(String lastChange ){
		this.lastChange = lastChange;
	}
	
	public Float getMaxX(){
		return  maxX;
	}
	public void setMaxX(Float maxX ){
		this.maxX = maxX;
	}
	
	public Float getMaxY(){
		return  maxY;
	}
	public void setMaxY(Float maxY ){
		this.maxY = maxY;
	}
	
	public Float getMinX(){
		return  minX;
	}
	public void setMinX(Float minX ){
		this.minX = minX;
	}
	
	public Float getMinY(){
		return  minY;
	}
	public void setMinY(Float minY ){
		this.minY = minY;
	}
	

}
