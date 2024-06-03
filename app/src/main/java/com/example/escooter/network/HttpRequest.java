package com.example.escooter.network;

import com.example.escooter.callback.HttpResultCallback;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    private String link;

    public HttpRequest(String link) {
        this.link = link;
    }

    public static void httpRequest(String link, String method, JSONObject jsonObject, HttpResultCallback<JSONObject> result) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                connection = (HttpURLConnection) new URL(link).openConnection();
                connection.setRequestMethod(method);
                connection.addRequestProperty("Content-Type", "application/json");

                if (method.equals("POST") || method.equals("PUT")) {
                    connection.setDoOutput(true);
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(jsonObject.toString().getBytes());
                        os.flush();
                    }
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }

                result.onResult(new JSONObject(response.toString()));
            } catch (Exception e) {
                result.onError(e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    public void httpPost(JSONObject jsonObject, ResultCallback<JSONObject> result) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.addRequestProperty("Content-Type", "application/json");
                connection.getOutputStream().write(jsonObject.toString().getBytes());

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();
                connection.disconnect();
                result.onResult(new JSONObject(response.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public interface ResultCallback<T> {
        void onResult(T result);
    }
}
