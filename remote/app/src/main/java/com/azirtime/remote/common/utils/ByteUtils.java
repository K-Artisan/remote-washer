package com.azirtime.remote.common.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class ByteUtils {

    /*
    public static byte[] getAscBytes (String str) {
        return getAscBytes(str.toCharArray());
    }

    // char转Asc byte
    public static byte[] getAscBytes (char[] chars) {
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i< chars.length; i++){
            bytes[i]=(byte) chars[i];
        }
        return  bytes;
    }*/

    // char转Asc byte
    public static byte[] getAscBytes(String str) {
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    //Asc bytes 转 chart
    public static char[] getCharByAscBytes(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }

    public  static String getStringByAscBytes (List<Byte> bytesList) {
        byte[] bytes = new byte[bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++) {
            bytes[i] = bytesList.get(i);
        }

        return getStringByAscBytes(bytes);
    }

    public static String getStringByAscBytes(byte[] bytes) {
        char[] chars = getCharByAscBytes(bytes);
        return String.valueOf(chars);
    }

    public static String getStringByAscBytes(byte b) {
        char c = (char) b;
        return String.valueOf(c);
    }


    public static Integer getIntegerByAscBytes(byte[] bytes) {
        try {
            return Integer.valueOf(getStringByAscBytes(bytes));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Integer getIntegerByAscStrin(String ascStr) {
        try {
            return ByteUtils.getIntegerByAscBytes(ByteUtils.getAscBytes(ascStr));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static byte[] copyBytes(byte[] src, int from, int to){
        int length = to-from + 1;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = src[from + i];
        }

        return result;
    }
}
