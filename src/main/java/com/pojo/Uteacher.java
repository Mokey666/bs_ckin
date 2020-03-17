package com.pojo;

public class Uteacher {
    private String tid;

    private String job;

    private String photo;

    public Uteacher(String tid, String job, String photo) {
        this.tid = tid;
        this.job = job;
        this.photo = photo;
    }

    public Uteacher() {
        super();
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid == null ? null : tid.trim();
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job == null ? null : job.trim();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo == null ? null : photo.trim();
    }
}