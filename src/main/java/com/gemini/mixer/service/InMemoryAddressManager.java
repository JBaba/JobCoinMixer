package com.gemini.mixer.service;

import com.gemini.mixer.service.contract.IAddressManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InMemoryAddressManager implements IAddressManager {

    Map<String, Set<String>> pollingAddressToUsersUnusedAddressesMappingCache;

    public InMemoryAddressManager() {
        pollingAddressToUsersUnusedAddressesMappingCache = new HashMap<>();
    }

    @Override
    public void add(String depositAddress, List<String> usersUnusedAddresses) {
        Set<String> mapValues = pollingAddressToUsersUnusedAddressesMappingCache.getOrDefault(depositAddress, new HashSet<>());
        mapValues.addAll(usersUnusedAddresses);
        pollingAddressToUsersUnusedAddressesMappingCache.put(depositAddress, mapValues);
    }

    @Override
    public List<String> get(String depositAddress) {
        return pollingAddressToUsersUnusedAddressesMappingCache.get(depositAddress).stream().toList();
    }

    @Override
    public List<String> getAllDepositAddresses() {
        return pollingAddressToUsersUnusedAddressesMappingCache.keySet().stream().toList();
    }

}
