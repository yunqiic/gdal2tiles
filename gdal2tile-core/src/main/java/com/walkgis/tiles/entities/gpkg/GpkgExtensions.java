package com.walkgis.tiles.entities.gpkg;

import org.beetl.sql.core.annotatoin.Table;


/*
 *
 * gen by beetlsql 2020-03-15
 */
@Table(name = ".gpkg_extensions")
public class GpkgExtensions {

    // alias
    public static final String ALIAS_column_name = "column_name";
    public static final String ALIAS_definition = "definition";
    public static final String ALIAS_extension_name = "extension_name";
    public static final String ALIAS_scope = "scope";
    public static final String ALIAS_table_name = "table_name";

    private String columnName;
    private String definition;
    private String extensionName;
    private String scope;
    private String tableName;

    public GpkgExtensions() {
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


}
