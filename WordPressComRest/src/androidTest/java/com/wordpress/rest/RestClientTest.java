package com.wordpress.rest;

import android.test.AndroidTestCase;

import java.util.HashMap;
import java.util.Map;

public class RestClientTest extends AndroidTestCase {
    public void testGetAbsoluteURLWithLeadingSlash() {
        String path = "/sites/mobileprojects.wordpress.com/posts";
        RestClient restClient = new RestClient(null);
        String url = restClient.getAbsoluteURL(path);
        String expected = String.format("https://public-api.wordpress.com/rest/v1%s", path);
        assertEquals(expected, url);
    }

    public void testRestClientWithVersion0CreatesRestClientV0() {
        RestClient restClientV0 = new RestClient(null, RestClient.REST_CLIENT_VERSIONS.V0);
        assertEquals(RestClient.REST_API_ENDPOINT_URL_V0, restClientV0.getEndpointURL());
    }

    public void testRestClientWithNoVersionCreatesRestClientV1() {
        RestClient restClient = new RestClient(null);
        assertEquals(RestClient.REST_API_ENDPOINT_URL_V1, restClient.getEndpointURL());
    }

    public void testRestClientGetAbsoluteURLWithParameters() {
        RestClient restClient = new RestClient(null);
        Map<String, String> params = new HashMap<>();
        params.put("a", "1");
        params.put("b", "c");
        String url = restClient.getAbsoluteURL("test", params);
        String expected = "https://public-api.wordpress.com/rest/v1/test?a=1&b=c";
        assertEquals(expected, url);
    }
}
