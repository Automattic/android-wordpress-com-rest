package com.wordpress.rest;


import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public interface RestInterface {
    String USER_AGENT_HEADER = "User-Agent";
    String REST_AUTHORIZATION_HEADER = "Authorization";
    String REST_AUTHORIZATION_FORMAT = "Bearer %s";

    Map<String, String> mHeaders = new HashMap<String, String>();

    interface Listener extends Response.Listener<JSONObject> {
    } //This is just a shortcut for Response.Listener<JSONObject>
    interface ErrorListener extends Response.ErrorListener {
    } //This is just a shortcut for Response.ErrorListener

    interface OnAuthFailedListener {
        void onAuthFailed();
    }
}
