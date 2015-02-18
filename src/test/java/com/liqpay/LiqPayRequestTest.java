package com.liqpay;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LiqPayRequestTest {

    @Test
    public void testGetProxyUser() {
        assertEquals("dXNlcjpwYXNz", LiqPayRequest.getProxyUser("user", "pass"));
    }
}