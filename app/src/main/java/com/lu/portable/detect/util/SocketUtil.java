package com.lu.portable.detect.util;

import android.util.Log;

import com.lu.portable.detect.configtools.ConfigTools;
import com.lu.portable.detect.configtools.InstrumentConfig;

import java.util.Arrays;

import static com.lu.portable.detect.configtools.InstrumentConfig.AXIS_FULL;
import static com.lu.portable.detect.configtools.InstrumentConfig.FRAME_HEADER;
import static com.lu.portable.detect.configtools.InstrumentConfig.FRAME_TAIL;
import static com.lu.portable.detect.configtools.InstrumentConfig.HEART_HEAD;
import static com.lu.portable.detect.configtools.InstrumentConfig.WEIGHT_ERROR_AXIS;

public class SocketUtil {
    private  final static int MIN_FRAME_LEN =22;
    public static byte[] hexStringToByte(String hexString){
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        int index = 0;
        byte[] bytes = new byte[len / 2];
        while (index < len) {
            try {
                String sub = hexString.substring(index, index + 2).toUpperCase();
                bytes[index / 2] = (byte) Integer.parseInt(sub, 16);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            index += 2;
        }
        return bytes;
    }

    public static boolean isHeart(String msg) {
        if(msg.length()<6){
          return  false;
         }
        if (!msg.startsWith(HEART_HEAD)) {
            return false;
        }

        String heartAddress = msg.substring(4, 6);
        for (String add : InstrumentConfig.HEART_BEATS) {
            if (heartAddress.equals(add)) {
                return true;
            }
        }
        return false;
    }
    public static String bytesToHexString(byte[] bytes, int begin, int end) {
        StringBuilder sb = new StringBuilder(2 * (end - begin));
        for (int i = begin; i < end; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    public static boolean isFrameCommand(String msg) {
        if(msg.length()<MIN_FRAME_LEN){
            return false;
        }
        if (!msg.startsWith(FRAME_HEADER)) {
            return false;
        }
        if(!msg.contains(FRAME_TAIL)){
            return false;
        }
        String commandAddress = msg.substring(2, 4);
        int commandAddressIndex = Arrays.binarySearch(InstrumentConfig.ADDRESS_ARR,commandAddress);
        if (commandAddressIndex<0) {
            return false;
        }
        String commandType = msg.substring(4, 6);
        int  commandTypeIndex=Arrays.binarySearch(InstrumentConfig.COMMAND_TYPE,commandType);
        return commandTypeIndex>=0;
    }
    public static int computeCommandLength(String length) {
        // LogUtil.d("computeCommandLength", length);
        return Integer.parseInt(length, 16);
    }
    public static  boolean isScaleFrameCommand(String msg){
        Log.d("isScaleFrameCommand",msg);
        if(msg.length()<24){
            return false;
        }
       if(!isFrameCommand(msg)){
           return false;
       }
        int len = Integer.parseInt(msg.substring(6,8),16);
       //104,112
        String command = msg.substring(0,8+len*2+2+2);
        Log.d("isScaleFrameCommand","command"+command);
        String address = msg.substring(2,4);
        String commandType = msg.substring(4,6);
        String data = msg.substring(6,8+len*2);
         Log.d("isScaleFrameCommand","msg.length():+"+msg.length()+"msg.substring(8+len*2,8+len*2+2):"+msg.substring(8+len*2,8+len*2+2));
        if(msg.length() < 8+len*2 +2 || !msg.substring(8+len*2+2,8+len*2+4).equals(FRAME_TAIL)){
           return false;
        }
        StringBuilder stringBuffer = new StringBuilder(data.substring(0,2));
        for(int i=2;i<data.length();i=i+2){
            stringBuffer.append(" ");
            stringBuffer.append(data.substring(i,i+2));
        }
        //"066572726F7274"
        if(msg.contains(AXIS_FULL)|| msg.substring(4).startsWith(WEIGHT_ERROR_AXIS)){
            return true;
        }
        String mCheckSum= checkSum(address,commandType,stringBuffer.toString()).toUpperCase();
        String msgCheckSum = msg.substring(8+len*2,8+len*2+2);
        if(mCheckSum.equals(msgCheckSum)){
           // Log.e("isScaleFrameCommand ok:" ,msgCheckSum+"mCheckSum:"+mCheckSum);
            return true;
        }else{
           // Log.e("isScaleFrameCommand:" ,msgCheckSum+"mCheckSum:"+mCheckSum);
            ConfigTools.writeLog(command +":"+msgCheckSum+":"+mCheckSum);
            return false;
        }

    }

    public static String checkSum(String address, String command, String data) {
        try {
           // Log.d("checkSum"," address:"+address +" command:" +command +" data:"+data);
            int iTotal = Integer.parseInt(address, 16) + Integer.parseInt(command, 16);
            String[] hexArr = data.split(" ");
            for (String hex : hexArr) {
                iTotal += Integer.parseInt(hex, 16);
            }
            int iMod = iTotal % 256;
            String sHex = Integer.toHexString(iMod);
            if (sHex.length() < 2) {
                sHex = "0" + sHex;
            }
            return sHex;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e("NumberFormatException", e.getLocalizedMessage() +"address:"+address +"command:"+command +"data:"+data);
            return "00";
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Exception", e.getLocalizedMessage() +"address:"+address +"command:"+command +"data:"+data);
            return  "00";
        }
    }
}
