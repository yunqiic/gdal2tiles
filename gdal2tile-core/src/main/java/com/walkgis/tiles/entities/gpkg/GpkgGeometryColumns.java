package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/* 
* 
* gen by beetlsql 2020-03-15
*/
@Table(name=".gpkg_geometry_columns")
public class GpkgGeometryColumns   {
	
	// alias
	public static final String ALIAS_column_name = "column_name";
	public static final String ALIAS_table_name = "table_name";
	public static final String ALIAS_m = "m";
	public static final String ALIAS_srs_id = "srs_id";
	public static final String ALIAS_z = "z";
	public static final String ALIAS_geometry_type_name = "geometry_type_name";
	
	private String columnName ;
	private String tableName ;
	private Integer m ;
	private Integer srsId ;
	private Integer z ;
	private String geometryTypeName ;
	
	public GpkgGeometryColumns() {
	}
	
	public String getColumnName(){
		return  columnName;
	}
	public void setColumnName(String columnName ){
		this.columnName = columnName;
	}
	
	public String getTableName(){
		return  tableName;
	}
	public void setTableName(String tableName ){
		this.tableName = tableName;
	}
	
	public Integer getM(){
		return  m;
	}
	public void setM(Integer m ){
		this.m = m;
	}
	
	public Integer getSrsId(){
		return  srsId;
	}
	public void setSrsId(Integer srsId ){
		this.srsId = srsId;
	}
	
	public Integer getZ(){
		return  z;
	}
	public void setZ(Integer z ){
		this.z = z;
	}
	
	public String getGeometryTypeName(){
		return  geometryTypeName;
	}
	public void setGeometryTypeName(String geometryTypeName ){
		this.geometryTypeName = geometryTypeName;
	}
	

}
