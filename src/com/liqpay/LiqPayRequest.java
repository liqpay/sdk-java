package com.liqpay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class LiqPayRequest {
		
	public static String post(String url, HashMap<String, String> list, LiqPay lp) throws Exception {
		
		
	   String urlParameters = "";
        
        for (Map.Entry<String, String> entry: list.entrySet())
        	urlParameters += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
		
        URL obj = new URL(url);
        DataOutputStream wr;
        BufferedReader in;
        
    	if(url.indexOf( "https:" ) != -1){
    		HttpsURLConnection con;
    		if(lp.getProxy() == null){
    			con = (HttpsURLConnection) obj.openConnection();
    		}else{
    			con = (HttpsURLConnection) obj.openConnection(lp.getProxy());
    			if(lp.getProxyUser() != null)
    			con.setRequestProperty("Proxy-Authorization", "Basic " + lp.getProxyUser());
    		}
        	con.setRequestMethod("POST");
        	con.setDoOutput(true);
        	wr = new DataOutputStream(con.getOutputStream());
        	// Send post request     
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            
        	in = new BufferedReader(  new InputStreamReader(con.getInputStream()));
    	}else{
        	HttpURLConnection con;
    		if(lp.getProxy() == null){
    			con = (HttpURLConnection) obj.openConnection();
    		}else{
    			con = (HttpURLConnection) obj.openConnection(lp.getProxy());
    			if(lp.getProxyUser() != null)
    			con.setRequestProperty("Proxy-Authorization", "Basic " + lp.getProxyUser());
    		}
        	con.setRequestMethod("POST");
        	con.setDoOutput(true);
        	wr = new DataOutputStream(con.getOutputStream());
        	// Send post request     
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            
        	in = new BufferedReader(  new InputStreamReader(con.getInputStream()));
    	}	
 
        
 
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }
}
