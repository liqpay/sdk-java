package com.liqpay;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class LiqPayTest {

    static final String FORM = "<form method=\"post\" action=\"https://www.liqpay.com/api/checkout\" accept-charset=\"utf-8\">\n"+
            "<input type=\"hidden\" name=\"data\" value=\"eyJhbW91bnQiOiIxLjUiLCJkZXNjcmlwdGlvbiI6IkRlc2NyaXB0aW9uIiwibGFuZ3VhZ2UiOiJlbyIsInB1YmxpY19rZXkiOiJwdWJsaWNLZXkiLCJ2ZXJzaW9uIjoiMy4wIiwiY3VycmVuY3kiOiJVU0QifQ==\" />\n"+
            "<input type=\"hidden\" name=\"signature\" value=\"f7mYsOPuQP0hOiThNFFxUy9OZuc=\" />\n"+
            "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1eo.radius.png\" name=\"btn_text\" />\n"+
            "</form>\n";

    @Test
    public void testCnb_form() throws Exception {
        LiqPay lp = new LiqPay("publicKey", "privateKey");
        HashMap<String, String> prams = new HashMap<>();
        prams.put("language", "eo");
        prams.put("version", "3.0");
        prams.put("amount", "1.5");
        prams.put("currency", "USD");
        prams.put("description", "Description");
        assertEquals(FORM, lp.cnb_form(prams));
    }

    @Test
    public void testStr_to_sign() throws Exception {
        LiqPay lp = new LiqPay("publicKey", "privateKey");
        assertEquals("i0XkvRxqy4i+v2QH0WIF9WfmKj4=", lp.str_to_sign("some string"));
    }
}