package com.example.raiburst.ui.home;

public interface ChatGPTCallback {
    void onResponse(String response);
    void onError(String error);
}
