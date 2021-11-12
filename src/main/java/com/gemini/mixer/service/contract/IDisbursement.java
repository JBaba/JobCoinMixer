package com.gemini.mixer.service.contract;

import java.util.List;

public interface IDisbursement {
    void setFromAddress(String address);

    void schedule(List<String> depositAddresses, double balanceToDeposit);
}
