package com.example.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GeminiService {

    private final String apiKey;
    private final OkHttpClient client;

    public GeminiService(@Value("${gemini.api.key:}") String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public String askAI(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is not configured");
        }

        String json = """
        {
          "contents":[
            {
              "parts":[{"text":"%s"}]
            }
          ]
        }
        """.formatted(prompt.replace("\"", "\\\""));

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Gemini request failed with status " + response.code());
            }
            if (response.body() == null) {
                throw new RuntimeException("Empty Gemini response body");
            }
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }

    public String extractText(String responseJson) {
        JsonObject obj = JsonParser.parseString(responseJson).getAsJsonObject();
        return obj.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text")
                .getAsString();
    }
}
