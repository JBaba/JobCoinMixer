package com.gemini.mixer.service;

import com.gemini.mixer.service.contract.IAddressManager;
import com.gemini.mixer.service.contract.IDisbursement;
import com.gemini.mixer.service.contract.IMixer;
import com.gemini.mixer.service.contract.ISubscriber;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DisbursementServiceTest {

    @Autowired
    private IDisbursement disbursementService;

    @Autowired
    private JobCoinChainService jobCoinChainService;

    @SneakyThrows
    @Test
    public void disburseFunds(){
        setup();
        assertAddressAmount(50);
        assertAddressAmount(100);
        assertAddressAmount(500);
        assertAddressAmount(5000);
        assertAddressAmount(50000);
        assertAddressAmount(500000);
    }

    private List<String> getAddresses() {
        List<String> as = new ArrayList<>();
        as.add("a1");
        as.add("a2");
        return as;
    }

    private void assertAddressAmount(double assertSum) throws InterruptedException {
        Mockito.reset(jobCoinChainService);
        // call function
        disbursementService.schedule(getAddresses(), assertSum);
        Thread.sleep(1000);
        // verify and assert
        ArgumentCaptor<String> fromAddressCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toAddressCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> amount = ArgumentCaptor.forClass(String.class);
        Mockito.verify(jobCoinChainService, Mockito.atLeast(1)).sendTransaction(fromAddressCaptor.capture()
                , toAddressCaptor.capture(), amount.capture());
        fromAddressCaptor.getAllValues().stream().forEach(item -> Assertions.assertEquals("fromAddress", item));
        toAddressCaptor.getAllValues().forEach(item -> Assertions.assertTrue(item.equals("a1") || item.equals("a2")));
        // assert disbursement amount
        double sum = 0;
        for(String i : new ArrayList<>(amount.getAllValues())) {
            sum += Double.parseDouble(i);
        }
        sum = Math.round(sum * 100.0) / 100.0;
        Assertions.assertEquals(assertSum, sum);
    }

    private void setup() {
        // setup
        disbursementService.setFromAddress("fromAddress");
        ReflectionTestUtils.setField(disbursementService, "fee", 0);
        ReflectionTestUtils.setField(disbursementService, "delay", 0);
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        public IDisbursement disbursement() {
            return new DisbursementService();
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
