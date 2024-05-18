package com.example.escooter.network;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    private String link;

    public HttpRequest(String link) {
        this.link = link;
    }

    public void httpGet(ResultCallback<JSONObject> result) {
        new Thread(() -> {
            try {
                result.onResult(new JSONObject(String.valueOf(new URL(link).openStream())));
            } catch (Exception e) {
                e.printStackTrace();
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

    public void httpPut(JSONObject jsonObject, ResultCallback<JSONObject> result) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
                connection.setRequestMethod("PUT");
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
