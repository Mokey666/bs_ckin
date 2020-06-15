package com.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();

    public void setExpireSeconds(int seconds);
}