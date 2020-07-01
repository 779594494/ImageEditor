package com.xinlan.imageeditlibrary.editimage.utils;

import java.util.List;

/**
 * Created by 我的孩子叫好帅
 */

public class ListUtil {
    public static boolean isEmpty(List list) {
        if (list == null)
            return true;

        return list.size() == 0;
    }

}//end class
