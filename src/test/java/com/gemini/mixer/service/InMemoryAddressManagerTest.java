package com.gemini.mixer.service;

import com.gemini.mixer.service.contract.IAddressManager;
import com.gemini.mixer.service.contract.IDisbursement;
import com.gemini.mixer.service.contract.IMixer;
import com.gemini.mixer.service.contract.ISubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class InMemoryAddressManagerTest {

    @Autowired
    IAddressManager addressManager;

    @Test
    public void addressPresent() {
        addressManager.add("from", getAddresses());
        List<String> addresses = addressManager.get("from");
        addresses.forEach(item -> Assertions.assertTrue(item.equals("a1") || item.equals("a2")));
    }

    @Test
    public void addressNotPresent() {
        List<String> addresses = addressManager.get("to");
        Assertions.assertNull(addresses);
    }

    private List<String> getAddresses() {
        List<String> as = new ArrayList<>();
        as.add("a1");
        as.add("a2");
        return as;
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        public IDisbursement disbursement() {
            return Mockito.mock(IDisbursement.class);
        }
        @Bean
        public IAddressManager addressManager() {
            return new InMemoryAddressManager();
        }
        @Bean
        public IMixer mixer() {
            return Mockito.mock(IMixer.class);
        }
        @Bean
        public ISubscriber subscriber() {
            return Mockito.mock(ISubscriber.class);
        }
        @Bean
        public JobCoinChainService jobCoinChainService() {
            return Mockito.mock(JobCoinChainService.class);
        }
        @Bean
        public RestTemplate restTemplate() {
            return Mockito.mock(RestTemplate.class);
        }
    }
}
