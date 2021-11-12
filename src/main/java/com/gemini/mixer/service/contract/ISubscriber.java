package com.gemini.mixer.service.contract;

public interface ISubscriber {
    void subscribe(String address, OnTransactionReceived onTransactionReceived);

    void startSubscriber();
}
