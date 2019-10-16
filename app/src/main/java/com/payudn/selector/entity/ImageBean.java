package com.payudn.selector.entity;

import java.util.Date;

import lombok.Data;

@Data
public class ImageBean {
    private long id;
    //图片
    private Type type;
    private String path;
    private long size;
    private String displayName;
    private Date createTime;
    private Date updateTime;
    //0:未同步 1:已同步
    private int status = 0;
    //-1:文件已不存在 1：存在
    private int isExist = 1;
    //视频
    private String thumbPath;
    private int duration;
    public ImageBean(Type type, String path, int size, String displayName) {
        this.type = type;
        this.path = path;
        this.size = size;
        this.displayName = displayName;
    }

    public ImageBean(Type type, String path, long size, String displayName, String thumbPath, int duration) {
        this.type = type;
        this.path = path;
        this.size = size;
        this.displayName = displayName;
        this.thumbPath = thumbPath;
        this.duration = duration;
    }
    public enum Type {
        Image,Video
    }
}
