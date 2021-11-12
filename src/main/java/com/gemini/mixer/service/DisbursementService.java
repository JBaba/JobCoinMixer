package com.gemini.mixer.service;

import com.gemini.mixer.service.contract.IDisbursement;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Service
public class DisbursementService implements IDisbursement {

    Logger logger = Logger.getLogger(DisbursementService.class.getName());

    String fromAddress;

    @Autowired
    JobCoinChainService jobCoinChainService;

    @Value("${fee}")
    int fee;

    @Value("${delay}")
    int delay;

    ExecutorService executorService;

    public DisbursementService() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }

    @Override
    public void setFromAddress(String address) {
        this.fromAddress = address;
    }

    @Override
    public void schedule(List<String> depositAddresses, double balanceToDeposit) {
        double balanceAfterFee = chargeFee(balanceToDeposit);
        if (balanceAfterFee == 0) return;
        executorService.submit(new DisburseTask(depositAddresses, balanceAfterFee));
    }

    private double chargeFee(double balanceToDeposit) {
        if (balanceToDeposit > fee)
            return balanceToDeposit - fee;
        return 0;
    }

    class DisburseTask implements Runnable {

        List<String> addresses;
        double totalAmountToDisburse;
        double lastAmount = 1.0;

        public DisburseTask(List<String> addresses, double totalAmountToDisburse) {
            this.addresses = addresses;
            this.totalAmountToDisburse = totalAmountToDisburse;
        }

        @SneakyThrows
        @Override
        public void run() {
            while (Math.round(totalAmountToDisburse) > 0) {
                double randomNum = ThreadLocalRandom.current().nextDouble(1.0, 2.1);
                for (String address : addresses) {
                    lastAmount *= randomNum;
                    double amountToDisburse = Math.min(lastAmount, totalAmountToDisburse);
                    amountToDisburse = Math.round(amountToDisburse * 100.0) / 100.0;
                    jobCoinChainService.sendTransaction(fromAddress, address, amountToDisburse + "");
                    totalAmountToDisburse -= amountToDisburse;
                }
                Thread.sleep(delay * 1000L);
            }
            logger.info("Disbursement complete for addresses " + addresses);
        }
    }
}
