package com.pojo;

import java.util.Date;

public class Sign {
    private int groupId; //讨论组号
    private Date createTime;//创建时间
    private Integer limitTime;//允许时长
    private Location location;//位置信息（经纬度）

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Integer limitTime) {
        this.limitTime = limitTime;
    }

    @Override
    public String toString() {
        return "Sign{" +
                "groupId=" + groupId +
                ", createTime=" + createTime +
                ", limitTime=" + limitTime +
                ", location=" + location +
                '}';
    }
}
