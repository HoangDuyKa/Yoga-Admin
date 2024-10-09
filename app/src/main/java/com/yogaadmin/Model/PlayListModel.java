package com.yogaadmin.Model;

public class PlayListModel {
    private String title, videoUri,enable, key;

    public PlayListModel() {
    }

    public PlayListModel(String title, String videoUri, String enable) {
        this.title = title;
        this.videoUri = videoUri;
        this.enable = enable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
