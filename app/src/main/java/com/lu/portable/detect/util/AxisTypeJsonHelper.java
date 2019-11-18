package com.lu.portable.detect.util;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AxisTypeJsonHelper {
    private static AxisTypeJsonHelper axisTypeJsonHelper;
    private static List<Axis> allaxises;

    public static List<AxisType> getAxisAllTypes(String name) {
        int i = 0;
        int j = axisTypeJsonHelper.allaxises.size();
        for (Axis axis : allaxises) {
            if (axis.getName().equals(name)) {
                return axis.getTypes();
            }
        }
        return null;
    }

    public static AxisTypeJsonHelper newInstance(String axisTypeInfo) {
        try {
            axisTypeJsonHelper = new Gson().fromJson(axisTypeInfo, AxisTypeJsonHelper.class);
            JSONObject config = new JSONObject(axisTypeInfo);
            JSONArray allaxisesArray = config.getJSONArray("allaxises");
            allaxises = new ArrayList<>();
            for (int i = 0; i < allaxisesArray.length(); i++) {
                JSONObject axisObject = allaxisesArray.getJSONObject(i);
                Axis axis = new Axis();
                axis.setName(axisObject.getString("name"));
                JSONArray typesArray = axisObject.getJSONArray("types");
                axis.types = new ArrayList<>();
                for (int j = 0; j < typesArray.length(); j++) {
                    JSONObject typeObject = typesArray.getJSONObject(i);
                    AxisType axisType = new AxisType();
                    axisType.setName(typeObject.getString("name"));
                    axisType.setLimit(typeObject.getDouble("limit"));
                    axis.types.add(axisType);
                }
                allaxises.add(axis);
            }
            return axisTypeJsonHelper;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Axis> getAllAxises() {
        return allaxises;
    }

    public void setAllAxises(List<Axis> axises) {
        allaxises = axises;
    }

    public static class Axis {
        private String name;
        private List<AxisType> types;

        public String getName() {
            return this.name;
        }

        public void setName(String paramString) {
            this.name = paramString;
        }

        public List<AxisType> getTypes() {
            return this.types;
        }

        public void setTypes(List<AxisType> paramList) {
            this.types = paramList;
        }
    }

    public static class AxisType {
        private double limit;
        private String name;

        public double getLimit() {
            return limit;
        }

        public void setLimit(double limit) {
            this.limit = limit;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
