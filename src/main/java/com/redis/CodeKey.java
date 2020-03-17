package com.redis;

public class CodeKey extends BasePrefix{

    public CodeKey(String prefix) {
        super(prefix);
    }

    public CodeKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static CodeKey codeKey = new CodeKey(30,"code");
}
