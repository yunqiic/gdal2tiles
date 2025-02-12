createGpkgSpatialRefSys
===
    * 创建
    CREATE TABLE IF NOT EXISTS gpkg_spatial_ref_sys
    (
         srs_name TEXT NOT NULL,
         srs_id INTEGER NOT NULL PRIMARY KEY,
         organization TEXT NOT NULL,
         organization_coordsys_id INTEGER NOT NULL,
         definition  TEXT NOT NULL,
         description TEXT
     );
insertGpkgSpatialRefSys
===
    *写入初始数据
    INSERT INTO gpkg_spatial_ref_sys(srs_name,srs_id,organization,organization_coordsys_id,definition)
                    SELECT 'Undefined Cartesian', -1, 'NONE', -1, 'undefined'
                    WHERE NOT EXISTS(SELECT 1 FROM gpkg_spatial_ref_sys WHERE srs_id=-1);
    
    
    INSERT INTO gpkg_spatial_ref_sys(srs_name,srs_id,organization,organization_coordsys_id,definition)
                    SELECT 'Undefined Geographic', 0, 'NONE', 0, 'undefined'
                    WHERE NOT EXISTS(SELECT 1 FROM gpkg_spatial_ref_sys WHERE srs_id=0);
    
    INSERT INTO gpkg_spatial_ref_sys(srs_name,srs_id,organization,organization_coordsys_id,definition)
                    SELECT 'WGS84', 4326, 'EPSG', 4326, 'GEOGCS["WGS 84",
                                                            DATUM["WGS_1984",
                                                                SPHEROID["WGS 84",6378137,298.257223563,
                                                                    AUTHORITY["EPSG","7030"]],
                                                                AUTHORITY["EPSG","6326"]],
                                                            PRIMEM["Greenwich",0,
                                                                AUTHORITY["EPSG","8901"]],
                                                            UNIT["degree",0.01745329251994328,
                                                                AUTHORITY["EPSG","9122"]],
                                                            AUTHORITY["EPSG","4326"]]'
                    WHERE NOT EXISTS(SELECT 1 FROM gpkg_spatial_ref_sys WHERE srs_id=4326);
                    
     
createViewStSpatialRefSys
===
    * 创建视图
    CREATE VIEW st_spatial_ref_sys AS
      SELECT
        srs_name,
        srs_id,
        organization,
        organization_coordsys_id,
        definition,
        description
      FROM gpkg_spatial_ref_sys;
createViewSpatialRefSys
===
    * 创建视图
    CREATE VIEW spatial_ref_sys AS
      SELECT
        srs_id AS srid,
        organization AS auth_name,
        organization_coordsys_id AS auth_srid,
        definition AS srtext
      FROM gpkg_spatial_ref_sys;
createGpkgContents
===
    * 创建gpkg_contents
    CREATE TABLE IF NOT EXISTS gpkg_contents
    (
         table_name TEXT NOT NULL PRIMARY KEY,
         data_type TEXT NOT NULL,
         identifier TEXT UNIQUE,
         description TEXT DEFAULT '',
         last_change DATETIME NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
         min_x DOUBLE,
         min_y DOUBLE,
         max_x DOUBLE,
         max_y DOUBLE,
         srs_id INTEGER,
         CONSTRAINT fk_gc_r_srs_id FOREIGN KEY (srs_id) REFERENCES gpkg_spatial_ref_sys(srs_id)
    );
createGpkgGeometryColumns
===
    * 创建
    CREATE TABLE gpkg_geometry_columns (
      table_name TEXT NOT NULL,
      column_name TEXT NOT NULL,
      geometry_type_name TEXT NOT NULL,
      srs_id INTEGER NOT NULL,
      z TINYINT NOT NULL,
      m TINYINT NOT NULL,
      CONSTRAINT pk_geom_cols PRIMARY KEY (table_name, column_name),
      CONSTRAINT uk_gc_table_name UNIQUE (table_name),
      CONSTRAINT fk_gc_tn FOREIGN KEY (table_name) REFERENCES gpkg_contents(table_name),
      CONSTRAINT fk_gc_srs FOREIGN KEY (srs_id) REFERENCES gpkg_spatial_ref_sys (srs_id)
    );
createViewStGeometryColumns
===
    * 创建视图
    CREATE VIEW st_geometry_columns AS
      SELECT
        table_name,
        column_name,
        "ST_" || geometry_type_name,
        g.srs_id,
        srs_name
      FROM gpkg_geometry_columns as g JOIN gpkg_spatial_ref_sys AS s
      WHERE g.srs_id = s.srs_id;
createViewGeometryColumns
===
    * geometry_columns
    CREATE VIEW geometry_columns AS
      SELECT
        table_name AS f_table_name,
        column_name AS f_geometry_column,
        code4name (geometry_type_name) AS geometry_type,
        2 + (CASE z WHEN 1 THEN 1 WHEN 2 THEN 1 ELSE 0 END) + (CASE m WHEN 1 THEN 1 WHEN 2 THEN 1 ELSE 0 END) AS coord_dimension,
        srs_id AS srid
      FROM gpkg_geometry_columns;      
createSimpleFeatureTable
===
    * 
    CREATE TABLE sample_feature_table (
      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
      geometry GEOMETRY,
      text_attribute TEXT,
      real_attribute REAL,
      boolean_attribute BOOLEAN,
      raster_or_photo BLOB
    );      
createGpkgTileMatrixSet
===
    * 
    CREATE TABLE IF NOT EXISTS gpkg_tile_matrix_set
    (
         table_name TEXT NOT NULL PRIMARY KEY,
         srs_id INTEGER NOT NULL,
         min_x DOUBLE NOT NULL,
         min_y DOUBLE NOT NULL,
         max_x DOUBLE NOT NULL,
         max_y DOUBLE NOT NULL,
         CONSTRAINT fk_gtms_table_name FOREIGN KEY (table_name) REFERENCES gpkg_contents(table_name),
         CONSTRAINT fk_gtms_srs FOREIGN KEY (srs_id) REFERENCES gpkg_spatial_ref_sys (srs_id)
    );
createGpkgTileMatrix
===
    * 
     CREATE TABLE IF NOT EXISTS gpkg_tile_matrix
     (
          table_name TEXT NOT NULL,
          zoom_level INTEGER NOT NULL,
          matrix_width INTEGER NOT NULL,
          matrix_height INTEGER NOT NULL,
          tile_width INTEGER NOT NULL,
          tile_height INTEGER NOT NULL,
          pixel_x_size DOUBLE NOT NULL,
          pixel_y_size DOUBLE NOT NULL,
          CONSTRAINT pk_ttm PRIMARY KEY (table_name, zoom_level),
          CONSTRAINT fk_tmm_table_name FOREIGN KEY (table_name) REFERENCES gpkg_contents(table_name)
     );        
createGpkgExtensions
===
    *
     CREATE TABLE gpkg_extensions (
       table_name TEXT,
       column_name TEXT,
       extension_name TEXT NOT NULL,
       definition TEXT NOT NULL,
       scope TEXT NOT NULL,
       CONSTRAINT ge_tce UNIQUE (table_name, column_name, extension_name)
     );     
createTrigger
===
    * 创建触发器
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_zoom_level_insert'
                        BEFORE INSERT ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'insert on table ''gpkg_tile_matrix'' violates constraint: zoom_level cannot be less than 0')
                        WHERE (NEW.zoom_level < 0);
                        END;
    
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_zoom_level_update'
                        BEFORE UPDATE OF zoom_level ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'update on table ''gpkg_tile_matrix'' violates constraint: zoom_level cannot be less than 0')
                        WHERE (NEW.zoom_level < 0);
                        END;
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_matrix_width_insert'
                        BEFORE INSERT ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'insert on table ''gpkg_tile_matrix'' violates constraint: matrix_width cannot be less than 1')
                        WHERE (NEW.matrix_width < 1);
                        END;
    
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_matrix_width_update'
                        BEFORE UPDATE OF matrix_width ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'update on table ''gpkg_tile_matrix'' violates constraint: matrix_width cannot be less than 1')
                        WHERE (NEW.matrix_width < 1);
                        END;
    
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_matrix_height_insert'
                        BEFORE INSERT ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'insert on table ''gpkg_tile_matrix'' violates constraint: matrix_height cannot be less than 1')
                        WHERE (NEW.matrix_height < 1);
                        END;
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_matrix_height_update'
                        BEFORE UPDATE OF matrix_height ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'update on table ''gpkg_tile_matrix'' violates constraint: matrix_height cannot be less than 1')
                        WHERE (NEW.matrix_height < 1);
                        END;
    
     CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_pixel_x_size_insert'
                        BEFORE INSERT ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'insert on table ''gpkg_tile_matrix'' violates constraint: pixel_x_size must be greater than 0')
                        WHERE NOT (NEW.pixel_x_size > 0);
                        END;
    
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_pixel_x_size_update'
                        BEFORE UPDATE OF pixel_x_size ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'update on table ''gpkg_tile_matrix'' violates constraint: pixel_x_size must be greater than 0')
                        WHERE NOT (NEW.pixel_x_size > 0);
                        END;
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_pixel_y_size_insert'
                        BEFORE INSERT ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'insert on table ''gpkg_tile_matrix'' violates constraint: pixel_y_size must be greater than 0')
                        WHERE NOT (NEW.pixel_y_size > 0);
                        END;
    
    CREATE TRIGGER IF NOT EXISTS 'gpkg_tile_matrix_pixel_y_size_update'
                        BEFORE UPDATE OF pixel_y_size ON 'gpkg_tile_matrix'
                        FOR EACH ROW BEGIN
                        SELECT RAISE(ABORT, 'update on table ''gpkg_tile_matrix'' violates constraint: pixel_y_size must be greater than 0')
                        WHERE NOT (NEW.pixel_y_size > 0);
                        END; 