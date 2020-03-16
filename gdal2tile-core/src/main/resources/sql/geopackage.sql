--属性实例
CREATE TABLE sample_attributes (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  text_attribute TEXT,
  real_attribute REAL,
  boolean_attribute BOOLEAN,
  raster_or_photo BLOB
);


INSERT INTO sample_attributes(text_attribute, real_attribute, boolean_attribute, raster_or_photo) VALUES ("place",  1,  true,  "BLOB VALUE");
--矢量表示例
CREATE TABLE sample_feature_table (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  geometry GEOMETRY,
  text_attribute TEXT,
  real_attribute REAL,
  boolean_attribute BOOLEAN,
  raster_or_photo BLOB
);


CREATE TRIGGER "sample_feature_table_real_insert"
BEFORE INSERT ON "sample_feature_table"
FOR EACH ROW BEGIN
SELECT RAISE(ABORT, 'insert on table ''sample_feature_table''
violates constraint: real_attribute must be greater than 0')
WHERE NOT (NEW.real_attribute > 0);
END;

CREATE TRIGGER "sample_feature_table_real_update"
BEFORE UPDATE OF "real_attribute" ON "sample_feature_table"
FOR EACH ROW BEGIN
SELECT RAISE (ABORT, 'update of ''real_attribute'' on table
''sample_feature_table'' violates constraint: real_attribute value
must be > 0')
WHERE NOT (NEW.real_attribute > 0);
END;

-- 切片表示例

CREATE TABLE sample_tile_pyramid (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  zoom_level INTEGER NOT NULL,
  tile_column INTEGER NOT NULL,
  tile_row INTEGER NOT NULL,
  tile_data BLOB NOT NULL,
  UNIQUE (zoom_level, tile_column, tile_row)
);
INSERT INTO gpkg_tile_matrix VALUES ("sample_tile_pyramid",0,  1,  1,  512,  512,  2.0,  2.0);
INSERT INTO sample_matrix_pyramid VALUES (1, 1, 1, 1,  "BLOB VALUE");

CREATE TRIGGER "sample_tile_pyramid_zoom_insert"
BEFORE INSERT ON "sample_tile_pyramid"
FOR EACH ROW BEGIN
SELECT RAISE(ABORT, 'insert on table ''sample_tile_pyramid'' violates constraint: zoom_level not specified for table in gpkg_tile_matrix')
WHERE NOT (NEW.zoom_level IN (SELECT zoom_level FROM gpkg_tile_matrix WHERE table_name = 'sample_tile_pyramid')) ;
END;

CREATE TRIGGER "sample_tile_pyramid_zoom_update"
BEFORE UPDATE OF zoom_level ON "sample_tile_pyramid"
FOR EACH ROW BEGIN
SELECT RAISE(ABORT, 'update on table ''sample_tile_pyramid'' violates constraint: zoom_level not specified for table in gpkg_tile_matrix')
WHERE NOT (NEW.zoom_level IN (SELECT zoom_level FROM gpkg_tile_matrix WHERE table_name = 'sample_tile_pyramid')) ;
END;

CREATE TRIGGER "sample_tile_pyramid_tile_column_insert"
BEFORE INSERT ON "sample_tile_pyramid"
FOR EACH ROW BEGIN
SELECT RAISE(ABORT, 'insert on table ''sample_tile_pyramid'' violates constraint: tile_column cannot be < 0')
WHERE (NEW.tile_column < 0) ;
SELECT RAISE(ABORT, 'insert on table ''sample_tile_pyramid'' violates constraint: tile_column must by < matrix_width specified for table and zoom level in gpkg_tile_matrix')
WHERE NOT (NEW.tile_column < (SELECT matrix_width FROM gpkg_tile_matrix WHERE table_name = 'sample_tile_pyramid' AND zoom_level = NEW.zoom_level));
END;

CREATE TRIGGER "sample_tile_pyramid_tile_column_update"
BEFORE UPDATE OF tile_column ON "sample_tile_pyramid"
FOR EACH ROW BEGIN
SELECT RAISE(ABORT, 'update on table ''sample_tile_pyramid'' violates constraint: tile_column cannot be < 0')
WHERE (NEW.tile_column < 0) ;
SELECT RAISE(ABORT, 'update on table ''sample_tile_pyramid'' violates constraint: tile_column must by < matrix_width specified for table and zoom level in gpkg_tile_matrix')
WHERE NOT (NEW.tile_column < (SELECT matrix_width FROM gpkg_tile_matrix WHERE table_name = 'sample_tile_pyramid' AND zoom_level = NEW.zoom_level));
END;

CREATE TRIGGER "sample_tile_pyramid_tile_row_insert"
BEFORE INSERT ON "sample_tile_pyramid"
FOR EACH ROW BEGIN
SELECT RAISE(ABORT, 'insert on table ''sample_tile_pyramid'' violates constraint: tile_row cannot be < 0')
WHERE (NEW.tile_row < 0) ;
SELECT RAISE(ABORT, 'insert on table ''sample_tile_pyramid'' violates constraint: tile_row must by < matrix_height specified for table and zoom level in gpkg_tile_matrix')
WHERE NOT (NEW.tile_row < (SELECT matrix_height FROM gpkg_tile_matrix WHERE table_name = 'sample_tile_pyramid' AND zoom_level = NEW.zoom_level));
END;

CREATE TRIGGER "sample_tile_pyramid_tile_row_update"
BEFORE UPDATE OF tile_row ON "sample_tile_pyramid"
FOR EACH ROW BEGIN
SELECT RAISE(ABORT, 'update on table ''sample_tile_pyramid'' violates constraint: tile_row cannot be < 0')
WHERE (NEW.tile_row < 0) ;
SELECT RAISE(ABORT, 'update on table ''sample_tile_pyramid'' violates constraint: tile_row must by < matrix_height specified for table and zoom level in gpkg_tile_matrix')
WHERE NOT (NEW.tile_row < (SELECT matrix_height FROM gpkg_tile_matrix WHERE table_name = 'sample_tile_pyramid' AND zoom_level = NEW.zoom_level));
END;