package com.liqpay;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static com.liqpay.LiqPayUtil.base64_encode;
import static com.liqpay.LiqPayUtil.sha1;

public class LiqPay implements LiqPayApi {
    /**
     * @deprecated Use a constant {@link #LIQPAY_API_URL}
     */
    @Deprecated
    public String liqpayApiUrl = "https://www.liqpay.com/api/";
    /**
     * @deprecated Use a constant {@link #LIQPAY_API_CHECKOUT_URL}
     */
    @Deprecated
    public String host_checkout = "https://www.liqpay.com/api/checkout";
    private static final String LIQPAY_API_URL = "https://www.liqpay.com/api/";
    private static final String LIQPAY_API_CHECKOUT_URL = "https://www.liqpay.com/api/checkout";
    private static final String DEFAULT_LANG = "ru";
    private final JSONParser parser = new JSONParser();
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

    @Deprecated
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

    @Override
    public HashMap<String, Object> api(String path, Map<String, String> params) throws Exception {
        HashMap<String, String> data = generateData(params);
        String resp = LiqPayRequest.post(liqpayApiUrl + path, data, this);
        JSONObject jsonObj = (JSONObject) parser.parse(resp);
        return LiqPayUtil.parseJson(jsonObj);
    }

    @SuppressWarnings("unchecked")
    protected HashMap<String, String> generateData(Map<String, String> params) {
        checkApiVersion(params);
        JSONObject json = new JSONObject(params);
        json.put("public_key", publicKey);
        HashMap<String, String> apiData = new HashMap<>();
        String data = base64_encode(json.toString());
        apiData.put("data", data);
        apiData.put("signature", createSignature(data));
        return apiData;
    }

    protected void checkApiVersion(Map<String, String> params) {
        if (params.get("version") == null)
            throw new NullPointerException("version can't be null");
        if (Double.parseDouble(params.get("version")) != 3.0D) {
            throw new IllegalArgumentException("Unsupported version");
        }
    }

    @Override
    public String cnb_form(Map<String, String> params) {
        checkCnbParams(params);
        JSONObject json = new JSONObject(params);
        String data = base64_encode(json.toString());
        String signature = createSignature(data);
        String language = params.get("language") != null ? params.get("language") : DEFAULT_LANG;
        return renderHtmlForm(data, language, signature);
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

    /**
     * Signature for form
     * @deprecated  You don't need it, because it already generated {@link #cnb_form(Map)}}
     */
    @Deprecated
    @Override
    public String cnb_signature(Map<String, String> params) {
        checkCnbParams(params);
        return createSignature(base64_encode(new JSONObject(params).toString()));
    }

    protected void checkCnbParams(Map<String, String> params) {
        checkApiVersion(params);
        if (params.get("amount") == null)
            throw new NullPointerException("amount can't be null");
        if (params.get("currency") == null)
            throw new NullPointerException("currency can't be null");
        if (params.get("description") == null)
            throw new NullPointerException("description can't be null");
        if (params.get("public_key") == null)
            params.put("public_key", publicKey);
    }

    /**
     * @deprecated Just use a full version {@link #setProxy(String, Integer, Proxy.Type)}
     */
    @Deprecated
    public void setProxy(String host, Integer port) {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    public void setProxy(String host, Integer port, Proxy.Type type) {
        proxy = new Proxy(type, new InetSocketAddress(host, port));
    }

    public void setProxyUser(String login, String password) {
        proxyUser = base64_encode(login + ":" + password);
    }

    public Proxy getProxy() {
        return proxy;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    /**
     * @deprecated It's low level method. Why you use it?
     */
    @Deprecated
    public String str_to_sign(String str) {
        return base64_encode(sha1(str));
    }

    protected String createSignature(String base64EncodedData) {
        return str_to_sign(privateKey + base64EncodedData + privateKey);
    }
}
