package com.lu.portable.detect.model;


import com.kernal.demo.plateid.R;
import com.lu.portable.detect.Record;
import com.lu.portable.detect.util.AxisTypeJsonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Car {
    private static HashMap<String, Double> allTypeLimitWeight;

    public static String twoAxisesTypes[] = {"t2_1_1"};
    public static String twoLabel[] = {"11"};
    public static String threeAxisesTypes[] = {"t3_2_1", "t3_1_2", "t3_1_1_1", "t3_1_1_1_s"};
    public static String threeLabel[] = {"21", "12+", "111", "111A"};
    public static int threeAxisesTypeIcons[] = {R.mipmap.t3_2_1, R.mipmap.t3_1_2, R.mipmap.t3_1_1_1_0, R.mipmap.t3_1_1_1_s};
    public static int threeLimit[] = {25, 25, 27, 27};
    public static String fourAxisesTypes[] = {"t4_1_1_2_0", "t4_1_1_2", "t4_1_1_1_1", "t4_1_1_2_s", "t4_1_2_1"};
    public static String fourLabel[] = {"112+", "112", "1111A", "112A", "121A+"};
    public static int fourAxisesTypeIcons[] = {R.mipmap.t4_1_1_2_0, R.mipmap.t4_1_1_2_s, R.mipmap.t4_1_1_1_1, R.mipmap.t4_1_1_2, R.mipmap.t4_1_2_1};
    public static int fourLimit[] = {31, 36, 36, 36, 35};

    public static String[] fiveAxisesTypes = {"t5_1_1_3", "t5_1_1_1_2", "t5_1_2_2", "t5_1_1_1_2_s", "t5_1_2_2_s", "t5_1_1_1_1_1", "t5_1_2_1_1"};
    public static String[] fiveLabel = {"113", "1112", "122+", "1112A", "122A+", "11111A", "1211A+"};
    public static String[] sixLabel = {"123", "123+", "1113", "123A", "123A+", "1122A", "1122A+", "11211A", "11211A+"};
    public static int[] sixAxisesTypeIcons = {R.mipmap.t6_1_2_3, R.mipmap.t6_1_2_3_49, R.mipmap.t6_1_1_1_3, R.mipmap.t6_1_2_3_s, R.mipmap.t6_1_2_3_s_49, R.mipmap.t6_1_1_2_2, R.mipmap.t6_1_1_2_2_49, R.mipmap.t6_1_1_2_1_1, R.mipmap.t6_1_1_2_1_1_49};
    public static int fiveAxisesTypeIcons[] = {R.mipmap.t5_1_1_3, R.mipmap.t5_1_1_1_2, R.mipmap.t5_1_2_2, R.mipmap.t5_1_1_1_2_s, R.mipmap.t5_1_2_2_s, R.mipmap.t5_1_1_1_1_1, R.mipmap.t5_1_2_1_1};
    public static int fiveLimit[] = {42, 43, 43, 43, 43, 43, 43};
    public static String sixAxisesTypes[] = {"t6_1_2_3", "t6_1_2_3_49", "t6_1_1_1_3", "t6_1_2_3_s", "t6_1_2_3_s_49", "t6_1_1_2_2", "t6_1_1_2_2_49", "t6_1_1_2_1_1", "t6_1_1_2_1_1_49"};
    public static int sixLimit[] = {46, 49, 46, 46, 49, 46, 49, 46, 49};

    static {
        allTypeLimitWeight = new HashMap();
    }

    public static String AxisNumberToTruckType(int axisCount) {
        if (axisCount <= 1) {
            return "未识别车型";
        } else {
            return String.format("%d轴车", axisCount);
        }
    }

    public static String getCarLabel(int axisCount, String axisType) {
        String types[];
        String labels[];
        switch (axisCount) {
            case 2:
                types = twoAxisesTypes;
                labels = twoLabel;
                break;
            case 3:
                types = threeAxisesTypes;
                labels = threeLabel;
                break;
            case 4:
                types = fourAxisesTypes;
                labels = fourLabel;
                break;
            case 5:
                types = fiveAxisesTypes;
                labels = fiveLabel;
                break;
            default:
                types = sixAxisesTypes;
                labels = sixLabel;
                break;
        }
        for (int i = 0; i < types.length; i++) {
            if(types[i].equals(axisType)){
                return labels[i];
            }
        }
       return "";
    }

    public static String getAxis(String axleNumber) {
        try {
            int num = Integer.parseInt(axleNumber);
            switch (num) {
                case 2:
                    return "t_2";
                case 3:
                    return "t_3";
                case 4:
                    return "t_4";
                case 5:
                    return "t_5";
                case 6:
                    return "t_6";
                default:
                    return "t_2";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "t_2";
    }

    public static String getAxisNumber(Record record) {
        return record.axleNumber;
    }

    public static double getCarOverWeight(Record record, double weight) {
        double limit_weight = weight;
        try {
            if (record.special_tag == 1) {
                if (!record.truckType.contains("t_2"))
                    limit_weight = record.limit_weight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        record.limit_weight = limit_weight;
        weight = record.weight - limit_weight;
        if (weight < 0.0) {
            weight = 0.0;
        }
        return weight;
    }


    public static double getLimitWeight(Record record) {
        if (record.truckType.contains("big")) {
            return 0.0;
        }
        if (record.axleNumber.equals("2")) {
            return 18000.0;
        } else if (record.axleNumber.equals("3")) {
            for (int i = 0; i < threeAxisesTypes.length; i++) {
                if (record.truckType.equals(threeAxisesTypes[i])) {
                    return threeLimit[i] * 1000.0;
                }
            }
            return 27000.0;
        } else if (record.axleNumber.equals("4")) {
            for (int i = 0; i < fourAxisesTypes.length; i++) {
                if (record.truckType.equals(fourAxisesTypes[i])) {
                    return fourLimit[i] * 1000.0;
                }
            }
            return 36000.0;
        } else if (record.axleNumber.equals("5")) {
            for (int i = 0; i < fiveAxisesTypes.length; i++) {
                if (record.truckType.equals(fiveAxisesTypes[i])) {
                    return fiveLimit[i] * 1000.0;
                }
            }
            return 43000.0;
        } else if (record.axleNumber.equals("6")) {
            for (int i = 0; i < sixAxisesTypes.length; i++) {
                if (record.truckType.equals(sixAxisesTypes[i])) {
                    return sixLimit[i] * 1000.0;
                }
            }
            return 49000.0;
        } else {
            return 0.0;
        }
    }


    public static void setAllTypeWeight(AxisTypeJsonHelper axisTypeJsonHelper) {
        List<AxisTypeJsonHelper.Axis> axisList = axisTypeJsonHelper.getAllAxises();
        for (AxisTypeJsonHelper.Axis axis : axisList) {
            List<AxisTypeJsonHelper.AxisType> axisTypes = axis.getTypes();
            for (AxisTypeJsonHelper.AxisType axisType : axisTypes) {
                allTypeLimitWeight.put(axisType.getName(), axisType.getLimit());
            }
        }
    }
}
