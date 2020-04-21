package com.util;

import java.util.Random;

public class ClassGroupIdUtil {

    public static Integer creatRandom(){
        Random random = new Random();
        Integer rs = random.nextInt(99999);
        return rs;
    }
}
