package com.xinlan.imageeditlibrary;

import java.io.Serializable;

/**
 * 作者：我的孩子叫好帅 on 2019-12-12 10:09
 * Q Q：779594494
 * 邮箱：18733215730@163.com
 */
public class Pickers implements Serializable {
    private static final long serialVersionUID = 1L;

    private String showConetnt;
    private String showId;

    public String getShowConetnt() {
        return showConetnt;
    }

    public String getShowId() {
        return showId;
    }

    public Pickers(String showConetnt, String showId) {
        super();
        this.showConetnt = showConetnt;
        this.showId = showId;
    }

    public Pickers() {
        super();
    }

}
