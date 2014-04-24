package com.liqpay;

import java.util.HashMap;

public class LiqPayTest {
	public static void main(String [] args) throws Exception
	{
		String host = "";
		LiqPay liqpay = new LiqPay("", "", host);
		
		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("payment_id", "3940");
//		
//		HashMap<String, Object> result = liqpay.api("payment/status", params);
//		
//		System.out.println(result);
		
			
		params.put("amount", "3940");
		params.put("currency", "UAH");
		params.put("description", "description");
		params.put("test", "cccc");
		String result = liqpay.cnb_form(params);
		System.out.println(result);
	}
}
