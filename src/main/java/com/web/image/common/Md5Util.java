package com.web.image.common;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5公用类
 * Created by Administrator on 2017/7/17.
 */
public class Md5Util {
    /**
     * 密码MD5加密
     *
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String newStr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }
}
