package com.zc;

/**
 * 12/15/15  2:56 PM
 * Created by JustinZhang.
 */
public class Utils {

    /**
     * 通常用户分离juid中的用户名
     * @param jUid
     * @return
     */
    public static String getStringBeforeAt(String jUid){

        if (jUid == null) {
            return "";
        }

        return jUid.split("@")[0];
    }
}
