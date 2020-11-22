package com.liqpay;

import static com.liqpay.LiqPayUtil.base64_encode;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class LiqPayUnicoded extends LiqPay {

	public LiqPayUnicoded(String publicKey, String privateKey) {
		super(publicKey, privateKey);
	}

	public LiqPayUnicoded(String publicKey, String privateKey, Proxy proxy, String proxyLogin, String proxyPassword) {
		super(publicKey, privateKey, proxy, proxyLogin, proxyPassword);
	}

	@Override
	protected Map<String, String> generateData(Map<String, String> params) {
		HashMap<String, String> apiData = new HashMap<>();
        String data = "";
		try {
			data = base64_encode(JSONObject.toJSONString(withBasicApiParams(params)).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        apiData.put("data", data);
        apiData.put("signature", createSignature(data));
        return apiData;
	}

}
