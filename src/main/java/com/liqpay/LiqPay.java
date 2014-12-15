package com.liqpay;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LiqPay {
    private Proxy __PROXY = null;
    private String __PROXY_AUTH = null;

    public String host = "https://www.liqpay.com/api/";
    public String host_checkout = "https://www.liqpay.com/api/checkout";
    private String pub_key = "";
    private String priv_key = "";

    public LiqPay(String public_key, String private_key) {
        pub_key = public_key;
        priv_key = private_key;
    }

    public LiqPay(String public_key, String private_key, String url) {
        pub_key = public_key;
        priv_key = private_key;
        host = url;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Object> api(String path, HashMap<String, String> list) throws Exception {
        if (list.get("version") == null)
            throw new NullPointerException("version can't be null");

        JSONObject json = new JSONObject();
        json.put("public_key", pub_key);

        for (Map.Entry<String, String> entry : list.entrySet())
            json.put(entry.getKey(), entry.getValue());

        String dataJson = LiqPayUtil.base64_encode(json.toString().getBytes());
        String signature = LiqPayUtil.base64_encode(LiqPayUtil.sha1(priv_key + dataJson + priv_key));

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("data", dataJson);
        data.put("signature", signature);
        String resp = LiqPayRequest.post(host + path, data, this);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(resp);
        JSONObject jsonObj = (JSONObject) obj;

        HashMap<String, Object> res_json = LiqPayUtil.parseJson(jsonObj);

        return res_json;
    }


    public String cnb_form(HashMap<String, String> list) {
        String language = "ru";
        if (list.get("language") != null)
            language = list.get("language");

        JSONObject json = cnb_params(list);
        String data = LiqPayUtil.base64_encode(json.toString().getBytes());
        String signature = cnb_signature(list);

        String form = "";
        form += "<form method=\"post\" action=\"" + host_checkout + "\" accept-charset=\"utf-8\">\n";
        form += "<input type=\"hidden\" name=\"data\" value=\"" + data + "\" />\n";
        form += "<input type=\"hidden\" name=\"signature\" value=\"" + signature + "\" />\n";
        form += "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1" + language + ".radius.png\" name=\"btn_text\" />\n";
        form += "</form>\n";

        return form;
    }


    public String cnb_signature(HashMap<String, String> list) {
        JSONObject json = cnb_params(list);
        String sign_str = priv_key + LiqPayUtil.base64_encode(json.toString().getBytes()) + priv_key;
        return str_to_sign(sign_str);
    }


    @SuppressWarnings("unchecked")
    private JSONObject cnb_params(HashMap<String, String> list) {
        if (list.get("version") == null)
            throw new NullPointerException("version can't be null");
        if (list.get("amount") == null)
            throw new NullPointerException("amount can't be null");
        if (list.get("currency") == null)
            throw new NullPointerException("currency can't be null");
        if (list.get("description") == null)
            throw new NullPointerException("description can't be null");

        if (list.get("public_key") == null)
            list.put("public_key", pub_key);

        JSONObject json = new JSONObject();
        for (Map.Entry<String, String> entry : list.entrySet())
            json.put(entry.getKey(), entry.getValue());

        return json;
    }


    public void setProxy(String host, Integer port) {
        __PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    public void setProxy(String host, Integer port, Proxy.Type type) {
        __PROXY = new Proxy(type, new InetSocketAddress(host, port));
    }


    public void setProxyUser(String login, String password) {
        __PROXY_AUTH = LiqPayUtil.base64_encode((login + ":" + password).getBytes());
    }

    public Proxy getProxy() {
        return __PROXY;
    }

    public String getProxyUser() {
        return __PROXY_AUTH;
    }


    public String str_to_sign(String str) {
        String signature = LiqPayUtil.base64_encode(LiqPayUtil.sha1(str));
        return signature;
    }
}
