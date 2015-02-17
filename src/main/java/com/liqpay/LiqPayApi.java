package com.liqpay;

import java.util.HashMap;
import java.util.Map;

public interface LiqPayApi {
    HashMap<String, Object> api(String path, Map<String, String> params) throws Exception;

    /**
     * Liq&Buy
     * Payment acceptance on the site client->server
     * To accept payments on your site you will need:
     * Register on www.liqpay.com
     * Create a store in your account using install master
     * Get a ready HTML-button or create a simple HTML form
     * HTML form should be sent by POST to URL https://www.liqpay.com/api/checkout Two parameters data and signature, where:
     * data - function result base64_encode( $json_string )
     * signature - function result base64_encode( sha1( $private_key . $data . $private_key ) )
     * @param params
     * @return HTML form
     */
    String cnb_form(Map<String, String> params);
}
