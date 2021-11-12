package com.gemini.mixer.service.contract;

public interface OnTransactionReceived {
    void onReceived(String address, String balance);
}
