package com.liqpay;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class LiqPayTest {

    static final String FORM = "<form method=\"post\" action=\"https://www.liqpay.com/api/checkout\" accept-charset=\"utf-8\">\n"+
            "<input type=\"hidden\" name=\"data\" value=\"eyJhbW91bnQiOiIxLjUiLCJkZXNjcmlwdGlvbiI6IkRlc2NyaXB0aW9uIiwibGFuZ3VhZ2UiOiJlbyIsInB1YmxpY19rZXkiOiJwdWJsaWNLZXkiLCJ2ZXJzaW9uIjoiMy4wIiwiY3VycmVuY3kiOiJVU0QifQ==\" />\n"+
            "<input type=\"hidden\" name=\"signature\" value=\"f7mYsOPuQP0hOiThNFFxUy9OZuc=\" />\n"+
            "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1eo.radius.png\" name=\"btn_text\" />\n"+
            "</form>\n";

    @Test
    public void testCnbForm() throws Exception {
        LiqPay lp = new LiqPay("publicKey", "privateKey");
        Map<String, String> params = defaultTestParams();
        assertEquals(FORM, lp.cnb_form(params));
    }

    private Map<String, String> defaultTestParams() {
        Map<String, String> params = new HashMap<>();
        params.put("language", "eo");
        params.put("version", "3.0");
        params.put("amount", "1.5");
        params.put("currency", "USD");
        params.put("description", "Description");
        return params;
    }

    @Test
    public void testStrToSign() throws Exception {
        LiqPay lp = new LiqPay("publicKey", "privateKey");
        assertEquals("i0XkvRxqy4i+v2QH0WIF9WfmKj4=", lp.str_to_sign("some string"));
    }

    @Test
    public void testSetProxy() throws Exception {
        LiqPay lp = new LiqPay("publicKey", "privateKey");
        lp.setProxy("192.168.0.1", 9999, Proxy.Type.SOCKS);
        Proxy p = lp.getProxy();
        assertEquals("192.168.0.1", ((InetSocketAddress)p.address()).getHostName());
        assertEquals(9999, ((InetSocketAddress) p.address()).getPort());
        assertEquals(Proxy.Type.SOCKS, p.type());
    }
    @Test
    public void testSetProxyHttp() throws Exception {
        LiqPay lp = new LiqPay("publicKey", "privateKey");
        lp.setProxy("192.168.0.1", 9999);
        Proxy p = lp.getProxy();
        assertEquals("192.168.0.1", ((InetSocketAddress)p.address()).getHostName());
        assertEquals(9999, ((InetSocketAddress) p.address()).getPort());
        assertEquals(Proxy.Type.HTTP, p.type());
    }

    @Test
    public void testProxyUser() throws Exception {
        LiqPay lp = new LiqPay("publicKey", "privateKey");
        lp.setProxyUser("user", "pass");
        assertEquals("dXNlcjpwYXNz", lp.getProxyUser());
    }
}