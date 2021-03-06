package com.redis;

public class CodeKey extends BasePrefix{

    public CodeKey(String prefix) {
        super(prefix);
    }

    public CodeKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }//expireSeconds有效时长，prefix前缀


    public static CodeKey codeKey = new CodeKey(30,"code");
    public static CodeKey signKey = new CodeKey(30,"sign");
    public static CodeKey singsKey = new CodeKey(0,"signUsers");
    //public static CodeKey historicRecordKey = new CodeKey(0,"historicRecord");
    public static CodeKey faceKey = new CodeKey(0,"face");


}
