package com.liqpay;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static com.liqpay.LiqPayUtil.base64_encode;
import static com.liqpay.LiqPayUtil.sha1;

public class LiqPay {
    private Proxy __PROXY = null;
    private String __PROXY_AUTH = null;

    public String liqpayApiUrl = "https://www.liqpay.com/api/";
    public String host_checkout = "https://www.liqpay.com/api/checkout";
    private final String publicKey;
    private final String privateKey;

    public LiqPay(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public LiqPay(String publicKey, String privateKey, String liqpayApiUrl) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.liqpayApiUrl = liqpayApiUrl;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, Object> api(String path, HashMap<String, String> list) throws Exception {
        if (list.get("version") == null)
            throw new NullPointerException("version can't be null");

        JSONObject json = new JSONObject();
        json.put("public_key", publicKey);

        for (Map.Entry<String, String> entry : list.entrySet())
            json.put(entry.getKey(), entry.getValue());

        String dataJson = base64_encode(json.toString().getBytes());
        String signature = base64_encode(sha1(privateKey + dataJson + privateKey));

        HashMap<String, String> data = new HashMap<>();
        data.put("data", dataJson);
        data.put("signature", signature);
        String resp = LiqPayRequest.post(liqpayApiUrl + path, data, this);

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
        String data = base64_encode(json.toString().getBytes());
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
        String sign_str = privateKey + base64_encode(json.toString().getBytes()) + privateKey;
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
            list.put("public_key", publicKey);

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
        __PROXY_AUTH = base64_encode((login + ":" + password).getBytes());
    }

    public Proxy getProxy() {
        return __PROXY;
    }

    public String getProxyUser() {
        return __PROXY_AUTH;
    }


    public String str_to_sign(String str) {
        String signature = base64_encode(sha1(str));
        return signature;
    }
}
