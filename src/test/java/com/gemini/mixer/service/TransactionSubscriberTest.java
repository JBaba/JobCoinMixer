package com.gemini.mixer.service;

import com.gemini.mixer.modal.Address;
import com.gemini.mixer.service.contract.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class TransactionSubscriberTest {

    @Autowired
    ISubscriber iSubscriber;

    @Autowired
    JobCoinChainService jobCoinChainService;

    @SneakyThrows
    @Test
    public void testSubscriber() {
        Address address = new Address();
        address.setBalance("50");
        Mockito.when(jobCoinChainService.getAddressInfo(Mockito.anyString())).thenReturn(address);
        OnTransactionReceived onTransactionReceived = (address1, balance) -> {
            Assertions.assertEquals("50", balance);
            Assertions.assertEquals("fromAddress", address1);
        };
        iSubscriber.subscribe("fromAddress", onTransactionReceived);
        iSubscriber.startSubscriber();
        Thread.sleep(1000);
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        public IDisbursement disbursement() {
            return Mockito.mock(IDisbursement.class);
        }
        @Bean
        public IAddressManager addressManager() {
            return Mockito.mock(IAddressManager.class);
        }
        @Bean
        public IMixer mixer() {
            return Mockito.mock(IMixer.class);
        }
        @Bean
        public ISubscriber subscriber() {
            return new TransactionSubscriber();
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
