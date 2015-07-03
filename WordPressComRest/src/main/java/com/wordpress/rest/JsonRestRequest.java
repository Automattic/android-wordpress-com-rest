package com.wordpress.rest;

import com.android.volley.VolleyLog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

// Uses methods borrowed from the JsonRequest and JsonObjectRequest classes in volley
public class JsonRestRequest extends RestRequest {
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private String mRequestBody;

    public JsonRestRequest(String url, JSONObject jsonRequest, com.android.volley.Response.Listener<JSONObject> listener,
                           com.android.volley.Response.ErrorListener errorListener) {
        super(jsonRequest == null ? Method.GET : Method.POST, url, null, listener, errorListener);

        if (jsonRequest != null) {
            mRequestBody = jsonRequest.toString();
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
