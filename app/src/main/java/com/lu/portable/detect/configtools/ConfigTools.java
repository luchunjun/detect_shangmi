package com.lu.portable.detect.configtools;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.lu.portable.detect.MyLineRegression;
import com.lu.portable.detect.PortableBalanceApplication;
import com.lu.portable.detect.configtools.model.StaticValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConfigTools {
    public static final String LINE_BREAK = "\r\n";
    private static final String SENSORA = "A_StaticWeight.txt";
    private static final String SENSORB = "B_StaticWeight.txt";
    private static final String SENSORC = "C_StaticWeight.txt";
    private static final String SENSORD = "D_StaticWeight.txt";
    public static double[] scaleK = new double[4];
    public static double[] scaleR = new double[4];
    public static String logFileName ;
    public static void buildSCaleKR() {
        MyLineRegression myLineRegression = new MyLineRegression();
        try {
            for (int i = 0; i < 4; i++) {
                List<StaticValue> staticValueList = getSensorStaticWeight(i);
                if (staticValueList == null || staticValueList.size() <= 5) {
                    scaleK[i] = 0.002441849218636739;
                } else {
                    double X[] = new double[staticValueList.size()];
                    double Y[] = new double[staticValueList.size()];
                    for (int k = 0; k < staticValueList.size(); k++) {
                        X[k] = staticValueList.get(k).getStaticValue();
                        Y[k] = staticValueList.get(k).getLoadValue();
                    }
                    Map<String, Double> result = myLineRegression.lineRegression(X, Y);
                   // Log.d("buildSCaleKR", result.toString());
                    scaleK[i] =  result.get("k");
                   // writeLog("buildSCaleK:" + i + ":" + scaleK[i]);
                    scaleR[i] = result.get("r");
                    if (scaleR[i] < 0.98D) {
                        Log.e("buildSCaleKR", "scaleR" + i + scaleR[i]);
                       // writeLog("buildSCaleR:" + i + ":" + scaleR[i]);
                        Toast.makeText(PortableBalanceApplication.getAppContext(), "第" + i + "号称台标定系数好", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PortableBalanceApplication.getAppContext(),"标定系数文件有问题",Toast.LENGTH_LONG).show();
            writeLog("buildSCaleKR" + e.toString());
        }

    }

    public static List <StaticValue> getSensorStaticWeight(int position) {
        ArrayList <StaticValue> staticValueArrayList = new ArrayList();
        try {
            String pathDir = Environment.getExternalStorageDirectory().getPath() + "/portableBalance/";
            File dirPath = new File(pathDir);
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
            String fileName, sensorName;
            switch (position) {
                case 0:
                    fileName = SENSORA;
                    sensorName = "A";
                    break;
                case 1:
                    fileName = SENSORB;
                    sensorName = "B";
                    break;
                case 2:
                    fileName = SENSORC;
                    sensorName = "C";
                    break;
                case 3:
                default:
                    fileName = SENSORD;
                    sensorName = "D";
                    break;
            }
            File file = new File(pathDir.concat(fileName));
            if (!file.exists()) {
               // Log.d("getSensorStaticWeight", " file.createNewFile()：" + file.createNewFile());
                Toast.makeText(PortableBalanceApplication.getAppContext(),"缺少标定文件",Toast.LENGTH_LONG).show();
                return null;
            }
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String jsonStr = "";
            String str2;
            while ((str2 = bufferedReader.readLine()) != null) {
                jsonStr += str2;
            }
            bufferedReader.close();
            Log.d("ConfigTools", "jsonStr:" + jsonStr);
            if (!jsonStr.isEmpty()) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                Iterator <String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    StaticValue staticValue = new StaticValue(Double.parseDouble(key), jsonObject.getDouble(key), sensorName);
                    staticValueArrayList.add(staticValue);
                }
                Collections.sort(staticValueArrayList);
            }
        } catch (JSONException | FileNotFoundException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staticValueArrayList;
    }

    public static void writeLog(String log) {
       // writeFile(logFileName, log);
    }
    public static void writeBatteryLog(String log) {
       // writeFile("Battery.log", log);
    }
    public static void writeAxisWeight(String log) {
        //writeFile("AxisWeight.log", log);
    }
    private static void writeFile(String fileName, String log) {
        String pathDir = Environment.getExternalStorageDirectory().getPath() + "/portableBalance/";
        File dirPath = new File(pathDir);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }
        File file = new File(pathDir.concat(fileName));
        try {
            if (!file.exists()) {
                Log.d("getSensorStaticWeight", " file.createNewFile()：" + file.createNewFile());
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(log);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeStaticWeightLog(String log) {
       // writeFile("staticWeightLog.log", log);
    }
}
