package com.pojo;

public class Ustudent {
    private String sid;

    private String major;

    private String grade;

    private String photo;

    public Ustudent(String sid, String major, String grade, String photo) {
        this.sid = sid;
        this.major = major;
        this.grade = grade;
        this.photo = photo;
    }

    public Ustudent() {
        super();
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid == null ? null : sid.trim();
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major == null ? null : major.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo == null ? null : photo.trim();
    }
}