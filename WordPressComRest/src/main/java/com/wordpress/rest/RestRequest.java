package com.wordpress.rest;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Map;
import java.util.HashMap;

import java.io.UnsupportedEncodingException;

public class RestRequest extends Request<JSONObject> {
    public static final String USER_AGENT_HEADER = "User-Agent";
    public static final String REST_AUTHORIZATION_HEADER = "Authorization";
    public static final String REST_AUTHORIZATION_FORMAT = "Bearer %s";

    private static OnAuthFailedListener mOnAuthFailedListener;

    public interface Listener extends Response.Listener<JSONObject> {
    } //This is just a shortcut for Response.Listener<JSONObject>
    public interface ErrorListener extends Response.ErrorListener {
    } //This is just a shortcut for Response.ErrorListener

    public interface OnAuthFailedListener {
        void onAuthFailed();
    }

    private final com.android.volley.Response.Listener<JSONObject> mListener;
    private final Map<String, String> mParams;
    private final Map<String, String> mHeaders = new HashMap<String, String>(2);

    private final byte[] mMultipartBody;
    private final String mBodyContentType;

    /**
     * Prepare a REST request based on URL-encoded form data
     * @param method HTTP method to execute
     * @param url the URL to execute the request against
     * @param params the map of form data pairs to encode
     * @param listener the listener for successful completion
     * @param errorListener the listener for failed completion
     */
    public RestRequest(int method, String url, Map<String, String> params,
                       com.android.volley.Response.Listener<JSONObject> listener,
                       com.android.volley.Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mParams = params;
        mMultipartBody = null;
        mBodyContentType = null;
        mListener = listener;
    }

    /**
     * Prepare a REST request based on MultiPart form data
     * @param method HTTP method to execute
     * @param url the URL to execute the request against
     * @param multipartBody the byte array of the body
     * @param bodyContentType the body content type including the parts boundary string
     * @param listener the listener for successful completion
     * @param errorListener the listener for failed completion
     */
    public RestRequest(int method, String url, byte[] multipartBody, String bodyContentType,
                       com.android.volley.Response.Listener<JSONObject> listener,
                       com.android.volley.Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mParams = null;
        mMultipartBody = multipartBody;
        mBodyContentType = bodyContentType;
        mListener = listener;
    }

    public void removeAccessToken() {
        setAccessToken(null);
    }

    public void setAccessToken(String token) {
        if (token == null) {
            mHeaders.remove(REST_AUTHORIZATION_HEADER);
        } else {
            mHeaders.put(REST_AUTHORIZATION_HEADER, String.format(REST_AUTHORIZATION_FORMAT, token));
        }
    }

    public void setUserAgent(String userAgent) {
        mHeaders.put(USER_AGENT_HEADER, userAgent);
    }

    public void setOnAuthFailedListener(OnAuthFailedListener onAuthFailedListener) {
        mOnAuthFailedListener = onAuthFailedListener;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return (mMultipartBody == null ? super.getBody() : mMultipartBody);
    }

    @Override
    public String getBodyContentType() {
        return (mBodyContentType == null ? super.getBodyContentType() : mBodyContentType);
    }

    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);

        // Fire OnAuthFailedListener if we receive an invalid token error
        if (error.networkResponse != null && error.networkResponse.statusCode >= 400 && mOnAuthFailedListener != null) {
            String jsonString;
            try {
                jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
            } catch (UnsupportedEncodingException e) {
                jsonString = "";
            }

            JSONObject responseObject;
            try {
                responseObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                responseObject = new JSONObject();
            }

            String restError = responseObject.optString("error", "");
            if (restError.equals("authorization_required") || restError.equals("invalid_token")) {
                mOnAuthFailedListener.onAuthFailed();
            }
        }
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            try {
                return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            } catch (JSONException parseErr) {
                // Try to parse the response document as Array
                JSONArray responseArray = new JSONArray(jsonString);
                JSONObject wrapper = new JSONObject();
                wrapper.put("originalResponse", responseArray);
                return Response.success(wrapper, HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
