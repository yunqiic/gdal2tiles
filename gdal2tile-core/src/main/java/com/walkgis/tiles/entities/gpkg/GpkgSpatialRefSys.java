package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/* 
* 
* gen by beetlsql 2020-03-15
*/
@Table(name=".gpkg_spatial_ref_sys")
public class GpkgSpatialRefSys   {
	
	// alias
	public static final String ALIAS_srs_id = "srs_id";
	public static final String ALIAS_organization_coordsys_id = "organization_coordsys_id";
	public static final String ALIAS_definition = "definition";
	public static final String ALIAS_description = "description";
	public static final String ALIAS_organization = "organization";
	public static final String ALIAS_srs_name = "srs_name";
	
	private Integer srsId ;
	private Integer organizationCoordsysId ;
	private String definition ;
	private String description ;
	private String organization ;
	private String srsName ;
	
	public GpkgSpatialRefSys() {
	}
	
	public Integer getSrsId(){
		return  srsId;
	}
	public void setSrsId(Integer srsId ){
		this.srsId = srsId;
	}
	
	public Integer getOrganizationCoordsysId(){
		return  organizationCoordsysId;
	}
	public void setOrganizationCoordsysId(Integer organizationCoordsysId ){
		this.organizationCoordsysId = organizationCoordsysId;
	}
	
	public String getDefinition(){
		return  definition;
	}
	public void setDefinition(String definition ){
		this.definition = definition;
	}
	
	public String getDescription(){
		return  description;
	}
	public void setDescription(String description ){
		this.description = description;
	}
	
	public String getOrganization(){
		return  organization;
	}
	public void setOrganization(String organization ){
		this.organization = organization;
	}
	
	public String getSrsName(){
		return  srsName;
	}
	public void setSrsName(String srsName ){
		this.srsName = srsName;
	}
	

}
