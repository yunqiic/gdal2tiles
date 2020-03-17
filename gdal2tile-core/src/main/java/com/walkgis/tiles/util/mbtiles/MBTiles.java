package com.walkgis.tiles.util.mbtiles;


import com.walkgis.tiles.util.sqlite.DatabaseHelper;
import com.walkgis.tiles.util.storage.TileData;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBTiles {
    private static MBTiles instance;
    public static DatabaseHelper _conn;
    public String filename;
    private String _version;
    public static Metadata metadata;
    private Metadata _metadata;
    private static Map<String, String> _connection_options = new HashMap<>();

    static {
        _connection_options.put("auto_vacuum", "NONE");
        _connection_options.put("encoding", "'UTF-8'");
        _connection_options.put("foreign_keys", "0");
        _connection_options.put("journal_mode", "MEMORY");
        _connection_options.put("locking_mode", "EXCLUSIVE");
        _connection_options.put("synchronous", "OFF");
    }

    private MBTiles(String filename, String version, Map<String, String> options, boolean create) {
        this.filename = filename;
        this._conn = null;
        this._version = version;
        this.metadata = new Metadata_1_2(this);

        this.open(options, create);
    }

    public void close(Boolean remove_journal) {
        if (this._conn != null) {
            if (remove_journal)
                _conn.executescript("PRAGMA journal_mode = DELETE");
            this._conn.close();
            this._conn = null;
        }
    }

    public boolean closed() {
        return !(this._conn != null);
    }

    public DatabaseHelper open(Map<String, String> options, boolean create) {
        DatabaseHelper databaseHelper = this._open(options, create);
        this.metadata();
        return databaseHelper;
    }

    private DatabaseHelper _open(Map<String, String> options, boolean create) {
        this.close(true);

        if (this.filename.equalsIgnoreCase(":memory:")) {
            String mode = create ? "wb" : "rb";
            _conn = DatabaseHelper.getInstance().create(this.filename, mode);
        }
        this._conn = DatabaseHelper.getInstance().create(this.filename, null);

        if (options == null)
            options = this._connection_options;

        for (Map.Entry<String, String> entry : options.entrySet()) {
            this._conn.executescript(String.format("PRAGMA %s = %s;", entry.getKey(), entry.getValue()));
        }
        return this._conn;
    }

    public static MBTiles create(String filename, Map<String, String> _metadata, String version) {
        if (version == null) {
            version = Metadata_1_2.VERSION;
        }

        instance = MBTiles._create(filename, version);
        instance.metadata._setup(_metadata);
        return instance;
    }

    private static MBTiles _create(String filename, String version) {
        File file = new File(filename);
        if (file.exists())
            file.delete();

        instance = new MBTiles(filename, version, null, true);

        _conn = instance._conn;
        _conn.executescript("CREATE TABLE images ( " +
                "                    tile_id INTEGER PRIMARY KEY, " +
                "                    tile_data BLOB NOT NULL " +
                "                )");
        _conn.executescript("CREATE TABLE map ( " +
                "                    zoom_level INTEGER NOT NULL, " +
                "                    tile_column INTEGER NOT NULL, " +
                "                    tile_row INTEGER NOT NULL, " +
                "                    tile_id INTEGER NOT NULL " +
                "                        REFERENCES images (tile_id) " +
                "                        ON DELETE CASCADE ON UPDATE CASCADE, " +
                "                    PRIMARY KEY (zoom_level, tile_column, tile_row) " +
                "                )");
        _conn.executescript("CREATE VIEW tiles AS " +
                "                    SELECT zoom_level, tile_column, tile_row, tile_data " +
                "                    FROM map, images " +
                "                    WHERE map.tile_id = images.tile_id");
        _conn.executescript("CREATE TABLE metadata ( " +
                "                    name TEXT PRIMARY KEY, " +
                "                    value TEXT NOT NULL " +
                "                )");
        return instance;
    }

    public String version() {
        if (this._version == null)
            this._version = this.metadata.VERSION;
        return this._version;
    }

    public Metadata metadata() {
        if (this._metadata == null) {
            this._metadata = new Metadata_1_2(this);
        }
        return this._metadata;
    }

    public void insert(int x, int y, int z, Long hashed, byte[] data) {
        if (data != null) {
            this._conn.executesUpdate("INSERT OR REPLACE INTO images (tile_id, tile_data) VALUES (?, ?)", hashed, data);
            this._conn.executesUpdate("INSERT OR REPLACE INTO map (zoom_level, tile_column, tile_row, tile_id) VALUES (?, ?, ?, ?)", z, x, y, hashed);
        }
    }

    public TileData get(int x, int y, int z) throws SQLException {
        ResultSet resultSet = this._conn.executesQuery("SELECT tile_data FROM tiles " +
                "            WHERE zoom_level = ? AND " +
                "                  tile_column = ? AND " +
                "                  tile_row = ?", x, y, z);
        return new TileData(
                resultSet.getInt("zoom_level"),
                resultSet.getInt("tile_column"),
                resultSet.getInt("tile_row"),
                resultSet.getBlob("tile_data")
        );
    }

    public List<TileData> all() throws SQLException {
        ResultSet resultSet = this._conn.executesQuery("SELECT zoom_level, tile_column, tile_row, tile_data FROM tiles " +
                "            ORDER BY zoom_level, tile_column, tile_row");
        List<TileData> result = new ArrayList();
        while (resultSet.next()) {
            result.add(new TileData(
                    resultSet.getInt("zoom_level"),
                    resultSet.getInt("tile_column"),
                    resultSet.getInt("tile_row"),
                    resultSet.getBlob("tile_data")
            ));
        }
        return result;
    }
}
