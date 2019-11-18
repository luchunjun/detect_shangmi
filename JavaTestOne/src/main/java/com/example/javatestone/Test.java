package com.example.javatestone;

public class Test {
    public static void main(String[] args) {
        //55 adr F5 06 DD DD DD DD DD DD ** 5A
        //AFAFAFAFAFAF
        //F1 06 6B 70 6F 6E
        //5504F16B 70 6F 6E 00 00ad5A
       // String sum = checkSum("04", "f1", "6B 70 6F 6E");
        //04F106616B6B7
        //5504F106 61 6B 6B 70 0000A25A

        // 5501F4069C1F0F000000C55A DEDFEFDA01 687421687440687421687440687421
        //2019-08-23 22:31:31.510 10689-12052/com.lu.portable.detect E/msgCheckSum:: C5mCheckSum:04
        //04 F4 06 65 72 72 6F 72 74
       // String sum = checkSum("01", "f1", "06 61 6B 6B 70 00 00");
        //01 f4 06 65 72 72 6f 72 74
        //5501F3326C701B007CADBB006A154A00680BAB0065E54A0072A5F80004490043004200330059003500550030006D0032008700310019135A
        String sum = checkSum("01", "F3", "32 6C 70 1B 00 7C AD BB 00 6A 15 4A 00 68 0B AB 00 65 E5 4A 00 72 A5 F8 00 04 49 00 43 00 42 00 33 00 59 00 35 00 55 00 30 00 6D 00 32 00 87 00 31 00 19");
        System.out.println(sum);
        // 5504F506FAFAFAFAFAFADB5A
       // System.out.println(Long.parseLong("3DF07",16));

    }

    public static final String checkSum(String address, String command, String data) {
        //int dataLength = data.length() / 2;
        int iTotal = Integer.parseInt(address, 16) + Integer.parseInt(command, 16);
        String hexArr[]=data.split(" ");
        for(String hex:hexArr){
            iTotal += Integer.parseInt(hex, 16);
        }

        int iMod = iTotal % 256;
        String sHex = Integer.toHexString(iMod);
        if (sHex.length() < 2) {
            sHex = "0" + sHex;
        }
        return sHex;
    }
//alt +command+L
}
