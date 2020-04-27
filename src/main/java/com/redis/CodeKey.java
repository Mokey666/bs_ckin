package com.redis;

public class CodeKey extends BasePrefix{

    public CodeKey(String prefix) {
        super(prefix);
    }

    public CodeKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static CodeKey codeKey = new CodeKey(30,"code");
    public static CodeKey signKey = new CodeKey(30,"sign");
    public static CodeKey singsKey = new CodeKey(30,"signUsers");
    //public static CodeKey webSocketKey = new CodeKey(30,"webSocket");
    public static CodeKey historicRecordKey = new CodeKey(30,"historicRecord");
    public static CodeKey faceKey = new CodeKey(0,"face");
}
