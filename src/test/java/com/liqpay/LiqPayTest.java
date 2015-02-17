package com.liqpay;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import static com.liqpay.LiqPayUtil.base64_encode;
import static org.junit.Assert.*;

public class LiqPayTest {

    static final String FORM = "<form method=\"post\" action=\"https://www.liqpay.com/api/checkout\" accept-charset=\"utf-8\">\n" +
            "<input type=\"hidden\" name=\"data\" value=\"eyJhbW91bnQiOiIxLjUiLCJkZXNjcmlwdGlvbiI6IkRlc2NyaXB0aW9uIiwibGFuZ3VhZ2UiOiJlbyIsInB1YmxpY19rZXkiOiJwdWJsaWNLZXkiLCJ2ZXJzaW9uIjoiMyIsImN1cnJlbmN5IjoiVVNEIn0=\" />\n" +
            "<input type=\"hidden\" name=\"signature\" value=\"DEggXkxcCsuZFwt/R4+zDekMPZ4=\" />\n" +
            "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1eo.radius.png\" name=\"btn_text\" />\n" +
            "</form>\n";

    LiqPay lp;

    @Before
    public void setUp(){
        lp = new LiqPay("publicKey", "privateKey");
    }

    @Test
    public void testCnbForm() throws Exception {
        Map<String, String> params = defaultTestParams();
        assertEquals(FORM, lp.cnb_form(params));
    }

    @Test
    public void testCnbSignature() throws Exception {
        Map<String, String> params = defaultTestParams();
        assertEquals("DEggXkxcCsuZFwt/R4+zDekMPZ4=", lp.cnb_signature(params));
    }

    private Map<String, String> defaultTestParams() {
        Map<String, String> params = new HashMap<>();
        params.put("language", "eo");
        params.put("version", "3");
        params.put("amount", "1.5");
        params.put("currency", "USD");
        params.put("description", "Description");
        return params;
    }

    @Test
    public void testCnbParams() throws Exception {
        Map<String, String> cnbParams = defaultTestParams();
        lp.checkCnbParams(cnbParams);
        assertEquals("eo", cnbParams.get("language"));
        assertEquals("3", cnbParams.get("version"));
        assertEquals("USD", cnbParams.get("currency"));
        assertEquals("1.5", cnbParams.get("amount"));
        assertEquals("Description", cnbParams.get("description"));
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotVersion() throws Exception {
        Map<String, String> params = defaultTestParams();
        params.remove("version");
        lp.checkCnbParams(params);
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotAmount() throws Exception {
        Map<String, String> params = defaultTestParams();
        params.remove("amount");
        lp.checkCnbParams(params);
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotCurrency() throws Exception {
        Map<String, String> params = defaultTestParams();
        params.remove("currency");
        lp.checkCnbParams(params);
    }

    @Test(expected = NullPointerException.class)
    public void testCnbParamsTrowsNpeIfNotDescription() throws Exception {
        Map<String, String> params = defaultTestParams();
        params.remove("description");
        lp.checkCnbParams(params);
    }

    @Test
    public void testCnbParamsWillUseDefaultPublicKey() throws Exception {
        Map<String, String> cnbParams = defaultTestParams();
        cnbParams.remove("public_key");
        lp.checkCnbParams(cnbParams);
        assertEquals("publicKey", cnbParams.get("public_key"));
    }

    @Test
    public void testCnbParamsOverwritePublicKey() throws Exception {
        Map<String, String> cnbParams = defaultTestParams();
        cnbParams.put("public_key", "overriden public key");
        lp.checkCnbParams(cnbParams);
        assertEquals("overriden public key", cnbParams.get("public_key"));
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
    public void testSetProxyHttp() throws Exception {
        lp.setProxy("192.168.0.1", 9999);
        Proxy p = lp.getProxy();
        assertEquals("192.168.0.1", ((InetSocketAddress)p.address()).getHostName());
        assertEquals(9999, ((InetSocketAddress) p.address()).getPort());
        assertEquals(Proxy.Type.HTTP, p.type());
    }

    @Test
    public void testProxyUser() throws Exception {
        lp.setProxyUser("user", "pass");
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
        Map<String, String> invoiceParams = new HashMap<>();
        invoiceParams.put("version", "3");
        invoiceParams.put("email", "client-email@gmail.com");
        invoiceParams.put("amount", "200");
        invoiceParams.put("currency", "USD");
        invoiceParams.put("order_id", "order_id_1");
        invoiceParams.put("goods", "[{amount: 100, count: 2, unit: 'un.', name: 'phone'}]");
        Map<String, String> generated = lp.generateData(invoiceParams);
        assertEquals("qU/Ms2zhS43H8VISlHeHgCOdjJc=", generated.get("signature"));
        assertEquals("eyJhbW91bnQiOiIyMDAiLCJlbWFpbCI6ImNsaWVudC1lbWFpbEBnbWFpbC5jb20iLCJnb29kcyI6Ilt7YW1vdW50OiAxMDAsIGNvdW50OiAyLCB1bml0OiAndW4uJywgbmFtZTogJ3Bob25lJ31dIiwicHVibGljX2tleSI6InB1YmxpY0tleSIsIm9yZGVyX2lkIjoib3JkZXJfaWRfMSIsInZlcnNpb24iOiIzIiwiY3VycmVuY3kiOiJVU0QifQ==", generated.get("data"));
    }
}