package com.example.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResponseParser {

    public static String extractText(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        return obj
                .getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }
}
