package com.lu.portable.detect.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
    private String coil_num = "---";
    private String coil_port = "---";
    private String custom_name = "--------";
    private String custom_num = "---";
    private String hash_code = "----";
    private String hostUrl = "http://thinknet.jios.org:10010/config/get_config/";
    private String instrument_ip = "192.168.88.230";
    private String instrument_mac = "------";
    private String instrument_num = "---";
    private String mobile_mac = "------";
    private String mobile_num = "---";
    private String plate_ip = "---.---.---.---";
    private String plate_mac = "------";
    private String plate_num = "---";
    private String product_num = "---";
    private JSONObject replyJson;
    private JSONArray weighting_a;
    private String weighting_a_ip = "192.168.88.253";
    private String weighting_a_mac = "------";
    private String weighting_a_num = "---";
    private JSONArray weighting_b = null;
    private String weighting_b_ip = "192.168.88.254";
    private String weighting_b_mac = "------";
    private String weighting_b_num = "---";


//    public JsonHelper(String jsonStr) throws JSONException {
//        replyJson = new JSONObject(jsonStr);
//        hostUrl = getString("hostUrl", hostUrl);
//        product_num = getString("product_num", product_num);
//        mobile_num = getString("mobile_num", mobile_num);
//        mobile_mac = getString("mobile_mac", mobile_mac);
//        instrument_num = getString("instrument_num", instrument_num);
//        instrument_ip = getString("instrument_ip", instrument_ip);
//        instrument_mac = getString("instrument_mac", instrument_mac);
//        plate_num = getString("plate_num", plate_num);
//        plate_ip = getString("plate_ip", plate_ip);
//        plate_mac = getString("plate_mac", plate_mac);
//        weighting_a_num = getString("weighting_a_num", weighting_a_num);
//        weighting_a_ip = getString("weighting_a_ip", weighting_a_ip);
//        weighting_a_mac = getString("weighting_a_mac", weighting_a_mac);
//        weighting_b_num = getString("weighting_b_num", weighting_b_num);
//        weighting_b_mac = getString("weighting_b_mac", weighting_b_mac);
//        weighting_b_ip = getString("weighting_b_ip", weighting_b_ip);
//        coil_num = getString("coil_num", coil_num);
//        coil_port = getString("coil_port", coil_port);
//        custom_num = getString("custom_num", custom_num);
//        custom_name = getString("custom_name", custom_name);
//        hash_code = getString("hash_code", hash_code);
//        weighting_a = replyJson.getJSONArray("weighting_a");
//        weighting_b = replyJson.getJSONArray("weighting_b");
//        custom_name = getString("custom_name", custom_name);
//    }

    public boolean contains(String paramString) {
        return replyJson.has(paramString);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            if (replyJson.has(key)) {
                return replyJson.getBoolean(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public int getInt(String key, int defaultValue) {
        try {
            if (replyJson.has(key)) {
                return replyJson.getInt(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;

    }

    public String getString(String key, String defaultValue) {
        try {
            if (replyJson.has(key)) {
                return replyJson.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public JSONArray getWeighting_a_value() {
        return weighting_a;
    }

    public JSONArray getWeighting_b_value() {
        return weighting_b;
    }

    public String getcoil_num() {
        return coil_num;
    }

    public String getcoil_port() {
        return coil_port;
    }

    public String getcustom_name() {
        return custom_name;
    }

    public String getcustom_num() {
        return custom_num;
    }

    public String gethash_code() {
        return hash_code;
    }

    public String getinstrument_ip() {
        return instrument_ip;
    }

    public String getinstrument_mac() {
        return instrument_mac;
    }

    public String getinstrument_num() {
        return instrument_num;
    }

    public String getmobile_mac() {
        return mobile_mac;
    }

    public String getmobile_num() {
        return mobile_num;
    }

    public String getplate_ip() {
        return plate_ip;
    }

    public String getplate_mac() {
        return plate_mac;
    }

    public String getplate_num() {
        return plate_num;
    }

    public String getproduct_num() {
        return product_num;
    }

    public String getweighting_a_ip() {
        return weighting_a_ip;
    }

    public String getweighting_a_mac() {
        return weighting_a_mac;
    }

    public String getweighting_a_num() {
        return weighting_a_num;
    }

    public String getweighting_b_ip() {
        return weighting_b_ip;
    }

    public String getweighting_b_mac() {
        return weighting_b_mac;
    }

    public String getweighting_b_num() {
        return weighting_b_num;
    }
}
