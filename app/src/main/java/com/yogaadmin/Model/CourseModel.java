package com.yogaadmin.Model;

public class CourseModel {
    private String title, duration, rating, description;
    private long price;
    private String thumbnail,introVideo,postId,postedBy,enable,type,time,dayOfWeek;

    public CourseModel() {
    }

    public CourseModel(String title, String duration, String rating, String description, long price, String thumbnail, String introVideo, String postId, String postedBy, String enable, String type, String time, String dayOfWeek) {
        this.title = title;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
        this.price = price;
        this.thumbnail = thumbnail;
        this.introVideo = introVideo;
        this.postId = postId;
        this.postedBy = postedBy;
        this.enable = enable;
        this.type = type;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(String introVideo) {
        this.introVideo = introVideo;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}
