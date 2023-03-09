package hu.petrik.vizsgaremek;

import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHandler {
    private RequestHandler() {
    }

    public static Response get(String url, String token) throws IOException {
        HttpURLConnection conn = setupConnection(url, token);
        return getResponse(conn);
    }

    public static Response post(String url, String data, String token) throws IOException {
        HttpURLConnection conn = setupConnection(url, token);
        conn.setRequestMethod("POST");

        addRequestBody(conn, data);
        return getResponse(conn);
    }

    public static Response put(String url, String data, String token) throws IOException {
        HttpURLConnection conn = setupConnection(url, token);
        conn.setRequestMethod("PUT");
        addRequestBody(conn, data);
        return getResponse(conn);
    }

    public static Response delete(String url, String token) throws IOException {
        HttpURLConnection conn = setupConnection(url, token);
        conn.setRequestMethod("DELETE");
        return getResponse(conn);
    }

    private static void addRequestBody(HttpURLConnection conn, String data) throws IOException {
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(data);
//        if (data != null) {
//            writer.write(data);
//        }
        writer.flush();
        writer.close();
        os.close();
    }

    private static HttpURLConnection setupConnection(String url, String token) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        Log.d("Token", "setupConnection: " + token);
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        return conn;
    }

    private static Response getResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        InputStream is;
        if (responseCode < 400) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String sor = br.readLine();
        while (sor != null) {
            builder.append(sor);
            sor = br.readLine();
        }
        br.close();
        is.close();
        return new Response(responseCode, builder.toString());
    }
}