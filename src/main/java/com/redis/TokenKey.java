package com.redis;

public class TokenKey extends BasePrefix{


    public TokenKey(String prefix) {
        super(prefix);
    }

    public TokenKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static TokenKey tokenKey = new TokenKey(30,"token");
}
