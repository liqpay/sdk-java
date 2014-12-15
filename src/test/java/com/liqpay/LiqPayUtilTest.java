package com.liqpay;

import static com.liqpay.LiqPayUtil.base64_encode;
import static com.liqpay.LiqPayUtil.sha1;
import static org.junit.Assert.*;

public class LiqPayUtilTest {

    @org.junit.Test
    public void testSha1() {
        assertEquals("i0XkvRxqy4i+v2QH0WIF9WfmKj4=", base64_encode(sha1("some string")));
    }

    @org.junit.Test
    public void testBase64_encode() {
        assertEquals("c29tZSBzdHJpbmc=", base64_encode("some string".getBytes()));
    }
}