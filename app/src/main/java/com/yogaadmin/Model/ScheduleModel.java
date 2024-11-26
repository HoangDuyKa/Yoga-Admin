package com.yogaadmin.Model;

public class ScheduleModel {
    private String scheduleId;
    private String courseId;
    private String teacher;
    private String date;
    private String comment;

    public ScheduleModel() {
    }

    public ScheduleModel(String scheduleId, String courseId, String teacher, String date, String comment) {
        this.scheduleId = scheduleId;
        this.courseId = courseId;
        this.teacher = teacher;
        this.date = date;
        this.comment = comment;
    }

    // Getters and Setters
    public String getId() {
        return scheduleId;
    }

    public void setId(String id) {
        this.scheduleId = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
