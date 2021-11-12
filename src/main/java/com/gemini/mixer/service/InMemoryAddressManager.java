package com.gemini.mixer.service;

import com.gemini.mixer.service.contract.IAddressManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InMemoryAddressManager implements IAddressManager {

    Map<String, List<String>> pollingAddressToUsersUnusedAddressesMappingCache;

    public InMemoryAddressManager() {
        pollingAddressToUsersUnusedAddressesMappingCache = new HashMap<>();
    }

    @Override
    public void add(String depositAddress, List<String> usersUnusedAddresses) {
        pollingAddressToUsersUnusedAddressesMappingCache.put(depositAddress, usersUnusedAddresses);
    }

    @Override
    public List<String> get(String depositAddress) {
        return pollingAddressToUsersUnusedAddressesMappingCache.get(depositAddress);
    }

}
