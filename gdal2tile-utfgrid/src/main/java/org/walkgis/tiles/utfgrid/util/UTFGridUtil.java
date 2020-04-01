package org.walkgis.tiles.utfgrid.util;

/**
 * @author JerFer
 * @version 1.0
 * @date 2020/4/1 13:36
 */
public class UTFGridUtil {
    /**
     * Skip the codepoints that cannot be encoded directly in JSON.
     *
     * @return
     */
    public static int escapeCodepoints(int codepoint) {
        if (codepoint == 34)
            codepoint += 1;
        else if (codepoint == 92)
            codepoint += 1;
        return codepoint;
    }

    public static int decodeId(int codepoint) {
        codepoint = ord(codepoint);
        if (codepoint >= 93)
            codepoint -= 1;
        if (codepoint >= 35)
            codepoint -= 1;
        codepoint -= 32;
        return codepoint;
    }

   public static int ord(int value) {
       return value;
    }

//    public static void resolve(Grid grid, List<Long> row, Object col) {
//        row = grid.getRows().get(0);
//        Long utfVal = row.get(col);
//
//        codePoint = decode_id(utfVal);
//        key = grid.getRows();
//    }
}
