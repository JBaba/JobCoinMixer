package com.gemini.mixer.service;

import com.gemini.mixer.modal.Address;
import com.gemini.mixer.modal.SendTransactionResponse;
import com.gemini.mixer.modal.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Logger;

@Service
public class JobCoinChainService {

    Logger logger = Logger.getLogger(JobCoinChainService.class.getName());
    @Value("${apiUrl}")
    String apiUrl;
    @Value("${apiAddressSuffix}")
    String apiAddressSuffix;
    @Value("${apiTransactionsSuffix}")
    String apiTransactionsSuffix;
    @Autowired
    private RestTemplate restTemplate;

    public Address getAddressInfo(String address) {
        String addressInfoUrl = apiUrl + apiAddressSuffix + String.format("/%s", address);
        ResponseEntity<Address> response = restTemplate.exchange(addressInfoUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<Address>() {
                });
//        logger.info(String.format("Get Address Info: %s, %s",address, response.getBody().toString()));
        return response.getBody();
    }

    public List<Transaction> getTransactions() {
        String addressInfoUrl = apiUrl + apiTransactionsSuffix;
        ResponseEntity<List<Transaction>> response = restTemplate.exchange(addressInfoUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Transaction>>() {
                });
//        logger.info(String.format("Get Trans: %s", response.getBody().toString()));
        return response.getBody();
    }

    public SendTransactionResponse sendTransaction(String fromAddress, String toAddress, String amount) {
        String fragment = String.format("?fromAddress=%s&toAddress=%s&amount=%s", fromAddress, toAddress, amount);
        String addressInfoUrl = apiUrl + apiTransactionsSuffix + fragment;
        ResponseEntity<SendTransactionResponse> response = restTemplate.exchange(addressInfoUrl, HttpMethod.POST,
                null, new ParameterizedTypeReference<SendTransactionResponse>() {
                });
        logger.info(String.format("Send Transaction: from: %s, to: %s, balance: %s", fromAddress, toAddress, amount));
        return response.getBody();
    }

}
