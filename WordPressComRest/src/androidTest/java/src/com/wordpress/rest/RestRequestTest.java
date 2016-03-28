package com.wordpress.rest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import android.net.Uri;
import android.os.Debug;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RestRequestTest extends AndroidTestCase {

    public void testMultiPartBodyAndContentType() throws AuthFailureError {
        final String bodyStr = "request body";
        final String bodyContentType = "multipart/form-data; boundary=---------------------1234";

        RestRequest restRequest = new RestRequest(Request.Method.POST, "http://url", bodyStr.getBytes(),
                bodyContentType, null, null);
        assertEquals(bodyStr, new String(restRequest.getBody()));
        assertEquals(bodyContentType, restRequest.getBodyContentType());
    }

    public void testMultiPartBuilderMimeType() throws AuthFailureError, IOException {
        MultipartRequestBuilder mpb = new MultipartRequestBuilder();
        final String boundary = mpb.getBoundary();

        RestRequest request = mpb.build("http://url");
        assertEquals("multipart/form-data; boundary=" + boundary, request.getBodyContentType());
    }

    public void testMultiPartBuilderText() throws AuthFailureError, IOException {
        MultipartRequestBuilder mpb = new MultipartRequestBuilder();

        final String simpleText = "simple text";
        mpb.addPart(simpleText);

        RestRequest restRequest = mpb.build("http://url");

        // See "7.2.1 Multipart: The common syntax" at https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
        final String encapsulationBoundary = "--" + mpb.getBoundary();

        final String lineEnd = "\r\n";
        assertEquals(encapsulationBoundary + lineEnd + simpleText + lineEnd, new String(restRequest.getBody()));
    }

    public void testMultiPartBuilderFile() throws AuthFailureError, IOException {
        final byte[] bytes = new byte[] {5, 6, 7, 8, 9, 10};

        File tempFile = new File(getContext().getCacheDir(), "tempFile.jpg");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bytes);
        fos.flush();
        fos.close();

        MultipartRequestBuilder mpb = new MultipartRequestBuilder();
        mpb.addPart("data", tempFile);
        RestRequest restRequest = mpb.build("http://url");

        // See "7.2.1 Multipart: The common syntax" at https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
        final String encapsulationBoundary = "--" + mpb.getBoundary();

        final String lineEnd = "\r\n";

        String expected =
                encapsulationBoundary + lineEnd
                + "Content-Disposition: form-data; name=\"data\"; filename=\"" + tempFile.getName() + "\"" + lineEnd
                + lineEnd
                + new String(bytes)
                + lineEnd
                + encapsulationBoundary
                + "--" + lineEnd;

        assertEquals(expected, new String(restRequest.getBody()));
    }
}
