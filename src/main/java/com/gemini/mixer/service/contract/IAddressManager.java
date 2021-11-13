package com.gemini.mixer.service.contract;

import java.util.List;

public interface IAddressManager {
    void add(String depositAddress, List<String> usersUnusedAddresses);
    List<String> get(String depositAddress);
    List<String> getAllDepositAddresses();
}
