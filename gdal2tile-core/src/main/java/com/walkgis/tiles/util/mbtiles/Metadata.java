package com.walkgis.tiles.util.mbtiles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Metadata {
    protected MBTiles mbtiles;
    public static String VERSION = null;
    public Map<String, String> MANDATORY = null;
    protected Map<String, Object> OPTIONAL = null;
    protected Map<String, Object> _all = null;

    public Metadata(MBTiles mbtiles) {
        this.mbtiles = mbtiles;
    }

    public Boolean delitem(Object key) {
        if (this.MANDATORY.containsKey(key))
            return false;
        return this._delitem(key);
    }

    private boolean _delitem(Object key) {
        return this.mbtiles._conn.executesNuQuery(" DELETE FROM metadata " +
                "                WHERE name = ?", key);
    }

    public String getitem(Object key) {
        ResultSet resultSet = this.mbtiles._conn.executesQuery("SELECT value FROM metadata " +
                "            WHERE name = ?", key);
        try {
            return resultSet.getString("value");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setitem(String key, Object value) {
        this._setitem(key, value);
    }

    private void  _setitem(String key, Object value) {
        this.mbtiles._conn.executesUpdate("INSERT OR REPLACE INTO metadata (name, value) " +
                "                    VALUES (?, ?)", key, value);
    }

    public int len() {
        return this.keys().size();
    }

    public Set<String> keys() {
        Set<String> strings = new HashSet<>();
        try {
            ResultSet resultSet = this.mbtiles._conn.executesQuery("SELECT name FROM metadata");
            while (resultSet.next()) {
                strings.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public void _setup(Map<String, String> metadata) {
        MANDATORY.putAll(metadata);
    }
}
