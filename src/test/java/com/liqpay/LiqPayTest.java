package com.liqpay;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

import static com.liqpay.LiqPayUtil.base64_encode;
import static org.junit.Assert.*;

public class LiqPayTest {

    static final String FORM = "<form method=\"post\" action=\"https://www.liqpay.com/api/checkout\" accept-charset=\"utf-8\">\n" +
            "<input type=\"hidden\" name=\"data\" value=\"eyJhbW91bnQiOiIxLjUiLCJjdXJyZW5jeSI6IlVTRCIsImRlc2NyaXB0aW9uIjoiRGVzY3JpcHRpb24iLCJsYW5ndWFnZSI6ImVvIiwicHVibGljX2tleSI6InB1YmxpY0tleSIsInZlcnNpb24iOiIzIn0=\" />\n" +
            "<input type=\"hidden\" name=\"signature\" value=\"EgQW6JPjpAdM/He8UlhUfDwlvKI=\" />\n" +
            "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1eo.radius.png\" name=\"btn_text\" />\n" +
            "</form>\n";

    LiqPay lp;

    @Before
    public void setUp(){
        lp = new LiqPay("publicKey", "privateKey");
    }

    @Test
    public void testCnbForm() throws Exception {
        Map<String, String> params = defaultTestParams(null);
        assertEquals(FORM, lp.cnb_form(params));
    }

    private Map<String, String> defaultTestParams(String removedKey) {
        Map<String, String> params = new TreeMap<>();
        params.put("language", "eo");
        params.put("amount", "1.5");
        params.put("currency", "USD");
        params.put("description", "Description");
        if (removedKey!= null) {
            params.remove(removedKey);
        }
        return Collections.unmodifiableMap(params);
    }

    @Test
    public void testCnbParams() throws Exception {
        Map<String, String> cnbParams = defaultTestParams(null);
        lp.checkCnbParams(cnbParams);
        assertEquals("eo", cnbParams.get("language"));
        assertEquals("USD", cnbParams.get("currency"));
        assertEquals("1.5", cnbParams.get("amount"));
        assertEquals("Description", cnbParams.get("description"));
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotAmount() throws Exception {
        Map<String, String> params = defaultTestParams("amount");
        lp.checkCnbParams(params);
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotCurrency() throws Exception {
        Map<String, String> params = defaultTestParams("currency");
        lp.checkCnbParams(params);
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotDescription() throws Exception {
        Map<String, String> params = defaultTestParams("description");
        lp.checkCnbParams(params);
    }

    @Test
    public void testWithBasicApiParams() throws Exception {
        Map<String, String> cnbParams = defaultTestParams(null);
        Map fullParams = lp.withBasicApiParams(cnbParams);
        assertEquals("publicKey", fullParams.get("public_key"));
        assertEquals("3", fullParams.get("version"));
        assertEquals("1.5", fullParams.get("amount"));
    }

    @Test
    public void testStrToSign() throws Exception {
        assertEquals("i0XkvRxqy4i+v2QH0WIF9WfmKj4=", lp.str_to_sign("some string"));
    }

    @Test
    public void testSetProxy() throws Exception {
        lp.setProxy("192.168.0.1", 9999, Proxy.Type.SOCKS);
        Proxy p = lp.getProxy();
        assertEquals("192.168.0.1", ((InetSocketAddress)p.address()).getHostName());
        assertEquals(9999, ((InetSocketAddress) p.address()).getPort());
        assertEquals(Proxy.Type.SOCKS, p.type());
    }

    @Test
    public void testSetProxyUser() throws Exception {
        lp.setProxyUser("user", "pass");
        assertEquals("dXNlcjpwYXNz", lp.getProxyUser());
    }

    @Test
    public void testGetProxyUser() throws Exception {
        lp.setProxyLogin("user");
        lp.setProxyPassword("pass");
        assertEquals("dXNlcjpwYXNz", lp.getProxyUser());
    }

    @Test
    public void testCreateSignature() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("field", "value");
        String base64EncodedData = base64_encode(jsonObject.toString());
        assertEquals("d3dP/5qWQFlZgFR53eAwqJ+xIOQ=", lp.createSignature(base64EncodedData));
    }

    @Test
    public void testGenerateData() throws Exception {
        Map<String, String> invoiceParams = new TreeMap<>();
        invoiceParams.put("email", "client-email@gmail.com");
        invoiceParams.put("amount", "200");
        invoiceParams.put("currency", "USD");
        invoiceParams.put("order_id", "order_id_1");
        invoiceParams.put("goods", "[{amount: 100, count: 2, unit: 'un.', name: 'phone'}]");
        Map<String, String> generated = lp.generateData(Collections.unmodifiableMap(invoiceParams));
        assertEquals("DqcGjvo2aXgt0+zBZECdH4cbPWY=", generated.get("signature"));
        assertEquals("eyJhbW91bnQiOiIyMDAiLCJjdXJyZW5jeSI6IlVTRCIsImVtYWlsIjoiY2xpZW50LWVtYWlsQGdtYWlsLmNvbSIsImdvb2RzIjoiW3thbW91bnQ6IDEwMCwgY291bnQ6IDIsIHVuaXQ6ICd1bi4nLCBuYW1lOiAncGhvbmUnfV0iLCJvcmRlcl9pZCI6Im9yZGVyX2lkXzEiLCJwdWJsaWNfa2V5IjoicHVibGljS2V5IiwidmVyc2lvbiI6IjMifQ==", generated.get("data"));
    }
}