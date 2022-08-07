/*
 * MIT License
 *
 * Copyright (c) 2017 Web Cerebrium
 * Copyright (c) 2021 Anatole Tresch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.webcerebrium.binance.api;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class WebRequest {

    public String userAgent = "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0";
    public HttpsURLConnection conn = null;
    public String requestUrl = "";
    public String method = "GET";
    public String lastResponse = "";
    public Integer connectionTimeoutSeconds;

    public String apiKey = "";
    public String secretKey = "";

    public Map<String, String> headers = new HashMap<>();

    // Internal JSON parser
    private JsonParser jsonParser = new JsonParser();
    private String requestBody = "";
    private long serverTimeOffset = 0L;

    // Creating public request
    public WebRequest(long serverTimeOffset, String requestUrl) {
        this.requestUrl = requestUrl;
        this.serverTimeOffset = serverTimeOffset;
    }

    // HMAC encoding
    public static String encode(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return String.valueOf(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes("UTF-8"))));
    }

    /**
     * Requests signing - with public and secret key
     * @param apiKey string of public API Key
     * @param secretKey string of secret Key
     * @param options map of additional parameters to include
     * @return this request object
     * @throws ApiException in case of any error
     */
    public WebRequest sign(String apiKey, String secretKey, Map<String, String> options) throws ApiException {
        String humanMessage = "Please check environment variables or VM options";
        if (Strings.isNullOrEmpty(apiKey))
            throw new ApiException("Missing BINANCE_API_KEY. " + humanMessage);
        if (Strings.isNullOrEmpty(secretKey))
            throw new ApiException("Missing BINANCE_SECRET_KEY. " + humanMessage);

        if (!Strings.isNullOrEmpty(secretKey) && !requestUrl.contains("&signature=")) {
            List<String> list = new LinkedList<>();
            if (options != null) {
                for (String key : options.keySet()) {
                    list.add(key + "=" + options.get(key));
                }
            }
            list.add("recvWindow=" + 7000);
            list.add("timestamp=" + String.valueOf(System.currentTimeMillis()-50+ serverTimeOffset));
            String queryToAdd = String.join("&", list);
            String query = "";
            log.debug("Signature: RequestUrl = {}", requestUrl);
            if (requestUrl.contains("?")) {
                query = requestUrl.substring(requestUrl.indexOf('?') + 1) + "&";
            }
            query = query.concat(queryToAdd);

            log.debug("Signature: query to be included  = {} queryToAdd={}", query, queryToAdd);
            try {
                String signature = encode(secretKey, query); // set the HMAC hash header
                String concatenator = requestUrl.contains("?") ? "&" : "?";
                requestUrl += concatenator + queryToAdd + "&signature=" + signature;
            } catch (Exception e ) {
                throw new ApiException("Encryption error " + e.getMessage());
            }
        }
        headers.put("X-MBX-APIKEY", apiKey);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return this;
    }

    /**
     * Requests signing - just with a public key
     * @param apiKey public key string
     * @return this request object
     * @throws ApiException in case of any error
     */
    public WebRequest sign(String apiKey) throws ApiException {
        String humanMessage = "Please check environment variables or VM options";
        if (Strings.isNullOrEmpty(apiKey))
            throw new ApiException("Missing BINANCE_API_KEY. " + humanMessage);

        headers.put("X-MBX-APIKEY", apiKey);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return this;
    }

    /**
     * Settings method as post, keeping interface fluid
     * @return this request object
     */
    public WebRequest post() {
        this.setMethod("POST");
        return this;
    }

    /**
     * Settings method as PUT, keeping interface fluid
     * @return this request object
     */
    public WebRequest put() {
        this.setMethod("PUT");
        return this;
    }


    /**
     * Settings method as DELETE, keeping interface fluid
     * @return this request object
     */
    public WebRequest delete() {
        this.setMethod("DELETE");
        return this;
    }

    /**
     * Opens HTTPS connection and save connection Handler
      @return this request object
     * @throws ApiException in case of any error
     */
    public WebRequest connect() throws ApiException {

        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        URL url = null;
        try {
            url = new URL(requestUrl);
            log.debug("{} {}", getMethod(), url);
        } catch (MalformedURLException e) {
            throw new ApiException("Mailformed URL " + e.getMessage());
        }
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("SSL Error " + e.getMessage() );
        } catch (KeyManagementException e) {
            throw new ApiException("Key Management Error " + e.getMessage() );
        }

        try {
            conn = (HttpsURLConnection)url.openConnection();
        } catch (IOException e) {
            throw new ApiException("HTTPS Connection error " + e.getMessage());
        }

        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new ApiException("HTTP method error " + e.getMessage());
        }
        conn.setRequestProperty("User-Agent", getUserAgent());
        for(String header: headers.keySet()) {
            conn.setRequestProperty(header, headers.get(header));
        }
        if(connectionTimeoutSeconds!=null) {
            conn.setConnectTimeout(connectionTimeoutSeconds);
        }
        return this;
    }

    /**
     * Saving response into local string variable
     * @return this request object
     * @throws ApiException in case of any error
     */
    public WebRequest read() throws ApiException {
        if (conn == null) {
            connect();
        }
        try {

            // posting payload it we do not have it yet
            if (!Strings.isNullOrEmpty(getRequestBody())) {
                log.debug("Payload: {}", getRequestBody());
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(getRequestBody());
                writer.close();
            }

            InputStream is;
            if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                is = conn.getInputStream();
            } else {
                /* error from server */
                is = conn.getErrorStream();
            }

            BufferedReader br = new BufferedReader( new InputStreamReader(is));
            lastResponse = IOUtils.toString(br);
            log.debug("Response: {}", lastResponse);

            if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                // Try to parse JSON
                JsonObject obj = (JsonObject)jsonParser.parse(lastResponse);
                if (obj.has("code") && obj.has("msg")) {
                    throw new ApiException("ERROR: " +
                            obj.get("code").getAsString() + ", " + obj.get("msg").getAsString() );
                }
            }
        } catch (IOException e) {
            throw new ApiException("Error in reading response " + e.getMessage());
        }
        return this;
    }

    public WebRequest payload(JsonObject payload) {
        if (payload == null) return this; // this is a valid case
        // according to documentation we need to have this header if we have preload
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        this.requestBody = payload.toString();
        return this;
    }

    /**
     * Getting last response as google JsonObject
     * @return response as Json Object
     */
    public JsonObject asJsonObject() {
        return (JsonObject)jsonParser.parse(getLastResponse());
    }
    /**
     * Getting last response as google GAON JsonArray
     * @return response as Json Array
     */
    public JsonArray asJsonArray() {
        return (JsonArray)jsonParser.parse(getLastResponse());
    }

    public WebRequest connectionTimeoutSeconds(Integer seconds){
        this.connectionTimeoutSeconds = seconds;
        return this;
    }
}
