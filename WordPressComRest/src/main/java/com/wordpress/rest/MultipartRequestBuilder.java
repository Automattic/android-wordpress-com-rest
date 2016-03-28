package com.wordpress.rest;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultipartRequestBuilder {

    private final String TWO_HYPHENS = "--";
    private final String LINE_END = "\r\n";

    private String mBoundary = "-------------" + System.currentTimeMillis();
    private String mMimeType = "multipart/form-data; boundary=" + mBoundary;

    private ByteArrayOutputStream mByteArrayOutputStream;
    private DataOutputStream mDataOutputStream;

    private Response.Listener<JSONObject> mListener;
    private Response.ErrorListener mErrorListener;

    public MultipartRequestBuilder() {
        mByteArrayOutputStream = new ByteArrayOutputStream();
        mDataOutputStream = new DataOutputStream(mByteArrayOutputStream);
    }

    public String getBoundary() {
        return mBoundary;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public MultipartRequestBuilder setResponseListener(Response.Listener<JSONObject> listener) {
        mListener = listener;
        return this;
    }

    public MultipartRequestBuilder setResponseErrorListener(Response.ErrorListener errorListener) {
        mErrorListener = errorListener;
        return this;
    }

    public MultipartRequestBuilder addPart(String textData) throws IOException {
        mDataOutputStream.writeBytes(TWO_HYPHENS + mBoundary + LINE_END);
        mDataOutputStream.writeBytes(textData);
        mDataOutputStream.writeBytes(LINE_END);
        return this;
    }

    public MultipartRequestBuilder addPart(String name, File file) throws IOException {
        mDataOutputStream.writeBytes(TWO_HYPHENS + mBoundary + LINE_END);
        mDataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
                + file.getName() + "\"" + LINE_END);
        // dataOutputStream.writeBytes("Content-Type: image/jpeg" + LINE_END);
        mDataOutputStream.writeBytes(LINE_END);

        InputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            mDataOutputStream.write(buffer, 0, bytesRead);
        }

        mDataOutputStream.writeBytes(LINE_END);
        mDataOutputStream.writeBytes(TWO_HYPHENS + mBoundary + TWO_HYPHENS + LINE_END);
        return this;
    }

    public MultipartRequestBuilder addPart(String parameterName, String parameterValue) throws IOException {
        mDataOutputStream.writeBytes(TWO_HYPHENS + mBoundary + LINE_END);
        mDataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + LINE_END);
        mDataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + LINE_END);
        mDataOutputStream.writeBytes(LINE_END);
        mDataOutputStream.writeBytes(parameterValue + LINE_END);
        return this;
    }

    public RestRequest build(String url) {
        return new RestRequest(Request.Method.POST, url, mByteArrayOutputStream.toByteArray(), mMimeType, mListener,
                mErrorListener);
    }
}