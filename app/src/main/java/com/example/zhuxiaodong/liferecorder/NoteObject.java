package com.example.zhuxiaodong.liferecorder;

import android.media.Image;

import java.util.Date;

/**
 * Created by zhuxiaodong on 2017/11/15.
 */

public class NoteObject {
    protected String title;
    protected String content;
    protected Image image;
    private Date date;

    public Date getDate() {
        return date;
    }
}
