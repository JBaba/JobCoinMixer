package com.gemini.mixer.service;

import com.gemini.mixer.modal.Address;
import com.gemini.mixer.service.contract.IAddressManager;
import com.gemini.mixer.service.contract.IDisbursement;
import com.gemini.mixer.service.contract.IMixer;
import com.gemini.mixer.service.contract.ISubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class InMemoryMixerServiceTest {

    @Autowired
    IMixer mixer;

    @Autowired
    JobCoinChainService jobCoinChainService;

    @Test
    public void generateHouseAddressTest() {
        String actualAddress = mixer.generateHouseAddress();
        ArgumentCaptor<String> fromAddressCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jobCoinChainService, Mockito.atLeast(1)).getAddressInfo(fromAddressCaptor.capture());
        String expectedAddress = fromAddressCaptor.getValue();
        Assertions.assertEquals(expectedAddress, actualAddress);
    }

    @Test
    public void generateRandomUnusedAddressTest() {
        String actualAddress = mixer.generateRandomUnusedAddress();
        ArgumentCaptor<String> fromAddressCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jobCoinChainService, Mockito.atLeast(1)).getAddressInfo(fromAddressCaptor.capture());
        String expectedAddress = fromAddressCaptor.getValue();
        Assertions.assertEquals(expectedAddress, actualAddress);
    }

    @Test
    public void generateHouseAddress() {
        String actualAddress = mixer.submitUnusedAddress(getAddresses());
        ArgumentCaptor<String> fromAddressCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jobCoinChainService, Mockito.atLeast(1)).getAddressInfo(fromAddressCaptor.capture());
        String expectedAddress = fromAddressCaptor.getValue();
        Assertions.assertEquals(expectedAddress, actualAddress);
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
            return Mockito.mock(IAddressManager.class);
        }
        @Bean
        public IMixer mixer() {
            return new InMemoryMixerService();
        }
        @Bean
        public ISubscriber subscriber() {
            return Mockito.mock(ISubscriber.class);
        }
        @Bean
        public JobCoinChainService jobCoinChainService() {
            Address address = new Address();
            address.setBalance("0");
            JobCoinChainService moc = Mockito.mock(JobCoinChainService.class);
            Mockito.when(moc.getAddressInfo(Mockito.anyString())).thenReturn(address);
            return moc;
        }
        @Bean
        public RestTemplate restTemplate() {
            return Mockito.mock(RestTemplate.class);
        }
    }
}
