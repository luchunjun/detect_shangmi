package com.lu.portable.detect.util;


import java.security.MessageDigest;

public class Md5 {
    private String md5_16;
    private String md5_32;
    public Md5(String plaintext) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(plaintext.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (int digest : bytes) {
                int j = digest;
                if (digest < 0) {
                    j += 256;
                }
                if (j < 16) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toHexString(j));
            }
            setMd5_16(stringBuilder.toString().substring(8, 24));
            setMd5_32(stringBuilder.toString());
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void setMd5_16(String md5_16) {
        this.md5_16 = md5_16;
    }

    private void setMd5_32(String md5_32) {
        this.md5_32 = md5_32;
    }

    public String get16() {
        return md5_16;
    }

    public String get32() {
        return md5_32;
    }
}