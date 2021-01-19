package com.dingtalk.h5app.quickstart.config;

import java.util.UUID;

/**
 * UUID是一种唯一的标识码，由当前日期和时间，时钟序列，IEEE机器识别号组成，在java中有专门封装好的生成UUID的类
 */
public class UuidUtil {
    public static String getUUID32(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
