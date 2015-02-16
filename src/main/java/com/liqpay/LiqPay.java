package com.liqpay;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static com.liqpay.LiqPayUtil.base64_encode;
import static com.liqpay.LiqPayUtil.sha1;

public class LiqPay {
    public String liqpayApiUrl = "https://www.liqpay.com/api/";
    public String host_checkout = "https://www.liqpay.com/api/checkout";
    private static final String DEFAULT_LANG = "ru";
    private final String publicKey;
    private final String privateKey;
    private Proxy proxy;
    private String proxyUser;
//    protected List<String> supportedCurrencies = Arrays.asList("EUR", "UAH", "USD", "RUB", "GEL");
//    protected List<String> supportedParams = Arrays.asList("public_key", "amount", "currency", "description", "order_id", "result_url", "server_url", "type", "signature", "language", "sandbox");

    public LiqPay(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        checkRequired();
    }

    public LiqPay(String publicKey, String privateKey, String liqpayApiUrl) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.liqpayApiUrl = liqpayApiUrl;
        checkRequired();
    }

    private void checkRequired() {
        if (this.publicKey == null || this.publicKey.isEmpty()) {
            throw new IllegalArgumentException("publicKey is empty");
        }
        if (this.privateKey == null || this.privateKey.isEmpty()) {
            throw new IllegalArgumentException("privateKey is empty");
        }
        if (this.liqpayApiUrl == null || this.liqpayApiUrl.isEmpty()) {
            throw new IllegalArgumentException("liqpayApiUrl is empty");
        }
    }


    @SuppressWarnings("unchecked")
    public HashMap<String, Object> api(String path, Map<String, String> params) throws Exception {
        if (params.get("version") == null)
            throw new NullPointerException("version can't be null");

        JSONObject json = new JSONObject();
        json.put("public_key", publicKey);

        for (Map.Entry<String, String> entry : params.entrySet())
            json.put(entry.getKey(), entry.getValue());

        String dataJson = base64_encode(json.toString().getBytes());
        String signature = str_to_sign(privateKey + dataJson + privateKey);

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


    public String cnb_form(Map<String, String> params) {
        String  language = params.get("language") != null ? params.get("language") : DEFAULT_LANG;
        JSONObject json = cnb_params(params);
        String data = base64_encode(json.toString().getBytes());
        String signature = cnb_signature(params);
        String form = renderHtmlForm(data, language, signature);
        return form;
    }

    private String renderHtmlForm(String data, String language, String signature) {
        String form = "";
        form += "<form method=\"post\" action=\"" + host_checkout + "\" accept-charset=\"utf-8\">\n";
        form += "<input type=\"hidden\" name=\"data\" value=\"" + data + "\" />\n";
        form += "<input type=\"hidden\" name=\"signature\" value=\"" + signature + "\" />\n";
        form += "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1" + language + ".radius.png\" name=\"btn_text\" />\n";
        form += "</form>\n";
        return form;
    }


    public String cnb_signature(Map<String, String> params) {
        JSONObject json = cnb_params(params);
        String sign_str = privateKey + base64_encode(json.toString().getBytes()) + privateKey;
        return str_to_sign(sign_str);
    }

    @SuppressWarnings("unchecked")
    protected JSONObject cnb_params(Map<String, String> params) {
        if (params.get("version") == null)
            throw new NullPointerException("version can't be null");
        if (params.get("amount") == null)
            throw new NullPointerException("amount can't be null");
        if (params.get("currency") == null)
            throw new NullPointerException("currency can't be null");
        if (params.get("description") == null)
            throw new NullPointerException("description can't be null");

        if (params.get("public_key") == null)
            params.put("public_key", publicKey);

        JSONObject json = new JSONObject();
        for (Map.Entry<String, String> entry : params.entrySet())
            json.put(entry.getKey(), entry.getValue());

        return json;
    }


    public void setProxy(String host, Integer port) {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    public void setProxy(String host, Integer port, Proxy.Type type) {
        proxy = new Proxy(type, new InetSocketAddress(host, port));
    }

    public void setProxyUser(String login, String password) {
        proxyUser = base64_encode((login + ":" + password).getBytes());
    }

    public Proxy getProxy() {
        return proxy;
    }

    public String getProxyUser() {
        return proxyUser;
    }


    public String str_to_sign(String str) {
        String signature = base64_encode(sha1(str));
        return signature;
    }
}
