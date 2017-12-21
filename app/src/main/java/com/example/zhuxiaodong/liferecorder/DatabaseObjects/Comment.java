package com.example.zhuxiaodong.liferecorder.DatabaseObjects;

/**
 * Created by zhuxiaodong on 2017/12/9.
 */

public class Comment {
    private String name;
    private String comment;

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Comment() {
    }

    public Comment(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }
}
