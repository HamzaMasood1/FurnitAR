package com.razi.furnitar.API;

import android.annotation.SuppressLint;

import com.razi.furnitar.Utils.GlobalData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class APIService {

    public static final String RESPONSE_UNWANTED = "UNWANTED";
    public static int responseCode = 0;

    public static APIResponse POST(String url, String bodyStr) throws IOException {
        trustEveryone();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.addRequestProperty("Cache-Control", "no-cache");
        con.addRequestProperty("Content-Type", "application/json");
        con.addRequestProperty("Accept", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream outputStream = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(bodyStr);
        writer.close();
        outputStream.close();

        //Retrieving Data
        BufferedReader bufferResponse;
        if (con.getResponseCode() / 100 == 2) {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String line;
        StringBuilder newResponse = new StringBuilder();
        while ((line = bufferResponse.readLine()) != null) {
            newResponse.append(line);
        }

        bufferResponse.close();
        return new APIResponse(con.getResponseCode(), newResponse.toString());
    }

    public static APIResponse POSTWithHeader(String url, String bodyStr) throws IOException {
        trustEveryone();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.addRequestProperty("Cache-Control", "no-cache");
        con.addRequestProperty("Content-Type", "application/json");
        con.addRequestProperty("Accept", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream outputStream = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(bodyStr);
        writer.close();
        outputStream.close();

        //Retrieving Data
        BufferedReader bufferResponse;
        if (con.getResponseCode() / 100 == 2) {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String line;
        StringBuilder newResponse = new StringBuilder();
        while ((line = bufferResponse.readLine()) != null) {
            newResponse.append(line);
        }

        bufferResponse.close();
        return new APIResponse(con.getResponseCode(), newResponse.toString());
    }

    public static APIResponse GETWithHeader(String url) throws IOException {
        trustEveryone();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.addRequestProperty("Cache-Control", "no-cache");
        con.addRequestProperty("Content-Type", "application/json");
        con.addRequestProperty("Accept", "application/json");
        con.addRequestProperty("Authorization", GlobalData.user_api_hash);
        con.addRequestProperty("lang", "en");
        con.setDoInput(true);
        con.setDoOutput(true);

        BufferedReader bufferResponse;
        if (con.getResponseCode() / 100 == 2) {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String line;
        StringBuilder newResponse = new StringBuilder();
        while ((line = bufferResponse.readLine()) != null) {
            newResponse.append(line);
        }

        bufferResponse.close();
        return new APIResponse(con.getResponseCode(), newResponse.toString());
    }

    private static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
