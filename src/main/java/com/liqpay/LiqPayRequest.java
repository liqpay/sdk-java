package com.liqpay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.liqpay.LiqPayUtil.base64_encode;

public class LiqPayRequest {

    public static String post(String url, Map<String, String> list, String proxyLogin, String proxyPassword, Proxy proxy) throws Exception {
        String urlParameters = "";

        for (Map.Entry<String, String> entry : list.entrySet())
            urlParameters += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";

        URL obj = new URL(url);
        DataOutputStream wr;
        BufferedReader in;
        HttpURLConnection con;
        if (proxy == null) {
            con = (HttpURLConnection) obj.openConnection();
        } else {
            con = (HttpURLConnection) obj.openConnection(proxy);
            if (proxyLogin != null)
                con.setRequestProperty("Proxy-Authorization", "Basic " + getProxyUser(proxyLogin, proxyPassword));
        }
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        wr = new DataOutputStream(con.getOutputStream());
        // Send post request
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static String getProxyUser(String proxyLogin, String proxyPassword) {
        return base64_encode(proxyLogin + ":" + proxyPassword);
    }
}
