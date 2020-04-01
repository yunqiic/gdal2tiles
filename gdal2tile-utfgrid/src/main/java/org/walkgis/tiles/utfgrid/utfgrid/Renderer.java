package org.walkgis.tiles.utfgrid.utfgrid;

import org.gdal.ogr.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JerFer
 * @date 2019/4/9---12:58
 */
public class Renderer {
    private Grid grid;
    private CoordTransform ctrans;
    private Request req;

    public Renderer(Grid grid, CoordTransform ctrans) {
        this.grid = grid;
        this.ctrans = ctrans;
        this.req = ctrans.getRequest();
    }

    public void apply(Layer layer, List<String> fieldNames) throws Exception {
        FeatureDefn layerDef = layer.GetLayerDefn();
        List<Field> fields = new ArrayList<>();

        String geomName = layer.GetLayerDefn().GetGeomFieldDefn(0).GetName();

        for (int i = 0, length = layerDef.GetFieldCount(); i < length; i++) {
            FieldDefn field = layerDef.GetFieldDefn(i);
            if (fieldNames.contains(field.GetName()))
                fields.add(new Field(field.GetName(), field.GetFieldTypeName(field.GetFieldType())));
        }
        if (fields.size() == 0)
            throw new Exception("No valid fields, field_names was " + fieldNames.toString());

        layer.ResetReading();
        for (int y = 0; y < this.req.getHeight(); y += this.grid.getResolution()) {
            List<String> row = new ArrayList<>();
            for (int x = 0; x < this.req.getWidth(); x += this.grid.getResolution()) {
                double[] minxmaxy = this.ctrans.backward(x, y);
                double[] maxxminy = this.ctrans.backward(x + 1, y + 1);

                double minx = minxmaxy[0], maxy = minxmaxy[1], maxx = maxxminy[0], miny = maxxminy[1];
                String wkt = String.format("POLYGON ((%f %f, %f %f, %f %f,%f %f, %f %f))", minx, miny, minx, maxy, maxx, maxy, maxx, miny, minx, miny);
                Geometry g = ogr.CreateGeometryFromWkt(wkt);
                layer.SetSpatialFilter(g);
                boolean found = false;
                Feature feat = layer.GetNextFeature();
                while (feat != null) {
                    Geometry geom = feat.GetGeomFieldRef(geomName);
                    if (geom.Intersect(g)) {
                        String feature_id = String.valueOf(feat.GetFID());
                        row.add(feature_id);
                        Map<String, Object> attr = new HashMap<>();

                        for (int k = 0, len = fields.size(); k < len; k++) {
                            Field field = fields.get(k);

                            String fieldType = field.getType();
                            String fieldName = field.getName();

                            if (fieldType.equals("Integer"))
                                attr.put(fieldName, feat.GetFieldAsInteger(k));
                            else if (fieldType.equals("Real"))
                                attr.put(fieldName, feat.GetFieldAsDouble(k));
                            else
                                attr.put(fieldName, feat.GetFieldAsString(k));
                        }
                        this.grid.getFeatureCache().put(feature_id, attr);
                        found = true;
                        feat = null;
                    } else
                        feat = layer.GetNextFeature();
                }

                if (!found)
                    row.add("");
            }
            this.grid.getRows().add(row);
        }
    }

    public class Field {
        private String name;
        private String type;

        public Field() {
        }

        public Field(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
