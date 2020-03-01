package com.walkgis.tiles.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteUtils {
    private Connection connection = null;
    private PreparedStatement pstat = null;
    private ResultSet rst = null;

    public SQLiteUtils(String path) throws SQLException, ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public Connection getConnection() {
        return connection;
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
            System.out.println("[SQL EXECUTE]: " + sql);
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

            System.out.println("[SQL EXECUTE RESULT]: " + executeUpdate);
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
    private void close() {
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