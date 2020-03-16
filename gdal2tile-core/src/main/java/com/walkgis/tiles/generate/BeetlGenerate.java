package com.walkgis.tiles.generate;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.SQLiteStyle;

import java.util.List;

public class BeetlGenerate {
    public static void main(String[] args) {
        ConnectionSource source = ConnectionSourceHelper.getSimple(
                "org.sqlite.JDBC",
                "jdbc:sqlite:E:\\Data\\Raster\\tiles.gpkg",
                "",
                "");
        DBStyle sqLiteStyle = new SQLiteStyle();
        // sql语句放在classpagth的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/sql");
        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        SQLManager sqlManager = new SQLManager(sqLiteStyle, loader, source, nc, new Interceptor[]{});

        SQLReady sqlReady = new SQLReady("select name from sqlite_master where type='table' order by name");
        List<String> tabNames = sqlManager.execute(sqlReady, String.class);
        for (String string : tabNames) {
            try {
                sqlManager.genPojoCode(string,"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
