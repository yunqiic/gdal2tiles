package com.walkgis.tiles.util.sqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private Connection connection;
    private String driver = "org.sqlite.JDBC";
    private PreparedStatement pstat = null;
    private ResultSet rst = null;
    private String filename;


    private static class DatabaseHelperInstance {
        private static final DatabaseHelper instance = new DatabaseHelper();
    }

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance() {
        return DatabaseHelperInstance.instance;
    }

    public DatabaseHelper create(String filename, String mode) {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
            executescript("PRAGMA application_id = 1196437808");
            this.filename = filename;
            return this;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executescript(String sql) {
        if (connection != null) {
            try {
                if (connection.isClosed()) {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
                }
                Statement statement = connection.createStatement();
                statement.execute(sql);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void executesUpdate(String sql, Object... args) {
        if (connection != null) {
            try {
                if (connection.isClosed()) {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
                }
                PreparedStatement statement = connection.prepareStatement(sql);
                for (int i = 1; i <= args.length; i++) {
                    statement.setObject(i, args[i - 1]);
                }
                statement.executeUpdate();
                statement.clearParameters();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet executesQuery(String sql, Object... args) {
        if (connection != null) {
            try {
                if (connection.isClosed()) {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
                }
                PreparedStatement statement = connection.prepareStatement(sql);
                for (int i = 1; i <= args.length; i++) {
                    statement.setObject(i, args[i - 1]);
                }
                ResultSet resultSet = statement.executeQuery();
                statement.clearParameters();
                statement.close();
                return resultSet;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean executesNuQuery(String sql, Object... args) {
        if (connection != null) {
            PreparedStatement statement = null;
            try {
                if (connection.isClosed()) {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
                }
                statement = connection.prepareStatement(sql);
                for (int i = 1; i <= args.length; i++) {
                    statement.setObject(i, args[i - 1]);
                }
                Boolean result = statement.execute();
                statement.clearParameters();
                statement.close();
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 执行修改删除插入操作
     *
     * @param sql
     * @param args
     * @return
     */
    public int execute(String sql, Object... args) {
        int executeUpdate = 0;
        try {
            pstat = connection.prepareStatement(sql);
            if (args.length > 0) {
                for (int i = 0, len = args.length; i < len; i++) {
                    pstat.setObject(i + 1, args[i]);
                }
            }

            executeUpdate = pstat.executeUpdate();
            rst = pstat.getGeneratedKeys();

            if (rst != null) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return executeUpdate;
    }

    /**
     * 查询一条记录
     *
     * @param sql
     * @return
     */
    public Map unique(String sql) {
        Map result = null;
        try {
            System.out.println("[SQL UNIQUE]: " + sql);
            pstat = connection.prepareStatement(sql);
            rst = pstat.executeQuery();

            //获取到
            ResultSetMetaData metaData = rst.getMetaData();
            int cols = metaData.getColumnCount();

            if (rst.next()) {
                //封装一行数据
                result = new HashMap();
                for (int i = 0; i < cols; i++) {
                    String key = metaData.getColumnName(i + 1);
                    Object value = rst.getObject(i + 1);
                    result.put(key, value);
                }
            }
            System.out.println("[SQL UNIQUE RESULT]: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }

    /**
     * 查询一个列表中的数据
     *
     * @param sql
     * @return
     */
    public List list(String sql) {
        List results = new ArrayList();
        try {
            System.out.println("[SQL LIST]: " + sql);
            pstat = connection.prepareStatement(sql);
            rst = pstat.executeQuery();

            //获取到
            ResultSetMetaData metaData = rst.getMetaData();
            int cols = metaData.getColumnCount();

            while (rst.next()) {
                //封装一行数据
                Map map = new HashMap();
                for (int i = 0; i < cols; i++) {
                    String key = metaData.getColumnName(i + 1);
                    Object value = rst.getObject(i + 1);
                    map.put(key, value);
                }
                results.add(map);
            }
            System.out.println("[SQL LIST]: " + results);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return results;
    }

    public int count(String tableName) {
        return count(tableName, null);
    }

    /**
     * 查看统计计数
     *
     * @param tableName
     * @param where
     * @return
     */
    public int count(String tableName, String where) {
        int count = 0;
        try {
            String sql = "select count(*) from " + tableName + " " + (where == null ? "" : where);
            System.out.println("[SQL Count]: " + sql);
            pstat = connection.prepareStatement("select count(*) from " + tableName + " " + (where == null ? "" : where));
            rst = pstat.executeQuery();

            if (rst.next()) count = rst.getInt(1);
            System.out.println("[SQL Count Result]: " + count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return count;
    }

    /**
     * 清除单次查询的连接
     */
    public void close() {
        if (rst != null) {
            try {
                rst.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (pstat != null) {
            try {
                pstat.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void relase() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[SQL LITE]: 关闭connection连接");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
