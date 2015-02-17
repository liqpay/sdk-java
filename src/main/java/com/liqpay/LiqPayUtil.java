package com.liqpay;

import java.security.MessageDigest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LiqPayUtil {
    public static byte[] sha1(String param) {
        try {
            MessageDigest SHA = MessageDigest.getInstance("SHA-1");
            SHA.reset();
            SHA.update(param.getBytes("UTF-8"));
            return SHA.digest();
        } catch (Exception e) {
            throw new RuntimeException("Can't calc SHA-1 hash", e);
        }
    }

    public static String base64_encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static String base64_encode(String data) {
        return base64_encode(data.getBytes());
    }

    public static ArrayList<Object> getArray(JSONArray jsonArr) throws ParseException {
        ArrayList<Object> list = new ArrayList<Object>();
        for (Object aJsonArr : jsonArr) {
            if (aJsonArr instanceof JSONObject) {
                list.add(parseJson((JSONObject) aJsonArr));
            } else {
                list.add(aJsonArr);
            }
        }
        return list;
    }

    public static Map<String, Object> parseJson(JSONObject jsonObject) throws ParseException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry: ((Map<String, Object>)jsonObject).entrySet()) {
            if (entry.getValue() instanceof JSONArray) {
                data.put(entry.getKey(), getArray((JSONArray)(entry.getValue())));
            } else {
                if (entry.getValue() instanceof JSONObject) {
                    data.put(entry.getKey(), parseJson((JSONObject) entry.getValue()));
                } else {
                    data.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return data;
    }
}
