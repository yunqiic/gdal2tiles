package org.walkgis.tiles.utfgrid.utfgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.walkgis.tiles.utfgrid.utfgrid.UTFGridTest.escapeCodepoints;

/**
 * @author JerFer
 * @date 2019/4/9---13:02
 */
public class Grid {
    private double resolution = 4.0;
    private List<List<String>> rows;
    private Map<String, Map<String, Object>> featureCache;

    public Grid() {
        this.rows = new ArrayList<>();
        this.featureCache = new HashMap();
        this.resolution = 4.0;
    }

    public Grid(double resolution) {
        this.rows = new ArrayList<>();
        this.featureCache = new HashMap();
        this.resolution = resolution;
    }

    public int width() {
        return rows.size();
    }

    public int height() {
        return rows.size();
    }

    public Map<String, Object> encode() {
        Map<String, Integer> keys = new HashMap();
        List<String> key_order = new ArrayList<>();
        Map data = new HashMap();
        List<String> utf_rows = new ArrayList<>();
        int codepoint = 32;
        for (int y = 0, length = height(); y < length; y++) {
            String row_utf = "";
            List<String> row = this.rows.get(y);
            for (int x = 0, length2 = width(); x < length2; x++) {
                String feature_id = row.get(x);
                if (keys.containsKey(feature_id))
                    row_utf += (char) keys.get(feature_id).intValue();
                else {
                    codepoint = escapeCodepoints(codepoint);
                    keys.put(feature_id, codepoint);
                    key_order.add(feature_id);
                    if (this.featureCache.get(feature_id) != null)
                        data.put(feature_id, this.featureCache.get(feature_id));
                    row_utf += (char) codepoint;
                    codepoint += 1;
                }
            }
            utf_rows.add(row_utf);
        }

        Map<String, Object> utf = new HashMap();
        utf.put("grid", utf_rows);
        utf.put("keys", key_order);
        utf.put("data", data);

        return utf;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public double getResolution() {
        return resolution;
    }

    public Map<String, Map<String, Object>> getFeatureCache() {
        return featureCache;
    }
}
