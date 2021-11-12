package com.gemini.mixer.service;

import com.gemini.mixer.modal.Address;
import com.gemini.mixer.service.contract.ISubscriber;
import com.gemini.mixer.service.contract.OnTransactionReceived;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class TransactionSubscriber implements ISubscriber {

    private static final int SLEEP_TIME = 5000;
    Logger logger = Logger.getLogger(TransactionSubscriber.class.getName());
    @Autowired
    JobCoinChainService jobCoinChainService;
    // make it thread safe
    Map<String, OnTransactionReceived> notificationQueue = new ConcurrentHashMap<>();
    Thread workerThread;

    @Override
    public void subscribe(String address, OnTransactionReceived onTransactionReceived) {
        // current time when we poll chain when we see new bigger date execute onTransactionReceived
        notificationQueue.put(address, onTransactionReceived);
    }

    @Override
    public void startSubscriber() {
        workerThread = new Thread(() -> {
            boolean isExit = false;
            while (!isExit) {
                notificationQueue.keySet().parallelStream().forEach(this::pollAddressInfo);
                try {
                    logger.info(String.format("Sleeping for %s sec.....", SLEEP_TIME / 1000));
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isExit = true;
                }
            }
        });
        workerThread.start();
    }

    private void pollAddressInfo(String address) {
        OnTransactionReceived callBack = notificationQueue.get(address);
        // polling chain
        Address addressInfo = jobCoinChainService.getAddressInfo(address);
        double balance = Double.parseDouble(addressInfo.getBalance());
        if (balance == 0) return;
        // notify mixer of transaction activity
        callBack.onReceived(address, addressInfo.getBalance());
    }

}
