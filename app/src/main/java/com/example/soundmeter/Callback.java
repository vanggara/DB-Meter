package com.example.soundmeter;
public interface Callback {
    void onBufferAvailable(byte[] buffer);
}