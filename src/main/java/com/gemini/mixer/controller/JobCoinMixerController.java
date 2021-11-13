package com.gemini.mixer.controller;

import com.gemini.mixer.modal.CheckFundResponse;
import com.gemini.mixer.service.InMemoryMixerService;
import com.gemini.mixer.service.JobCoinChainService;
import com.gemini.mixer.service.contract.IAddressManager;
import com.gemini.mixer.service.contract.IMixer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/mixer")
public class JobCoinMixerController {

    Logger logger = Logger.getLogger(JobCoinMixerController.class.getName());

    @Autowired
    IMixer inMemoryMixerService;

    @Autowired
    IAddressManager addressManager;

    @Autowired
    JobCoinChainService jobCoinChainService;

    //disable automated flow
    //@PostConstruct
    public void initWorkFlow() {
        createEvents();
    }

    @PostMapping("/submitAddresses/{alpha}/{beta}/{omega}")
    public ResponseEntity submitAddresses(@PathVariable("alpha") String alpha, @PathVariable("beta") String beta,
                                          @PathVariable("omega") String omega) {
        List<String> addresses = new ArrayList<>();
        addresses.add(alpha);
        addresses.add(beta);
        addresses.add(omega);
        logger.info(String.format("created random addresses %s", addresses));
        String mixerProvidedAddress = inMemoryMixerService.submitUnusedAddress(addresses);
        logger.info(String.format("Mixer provided address: %s", mixerProvidedAddress));
        return ResponseEntity.ok(String.format("{\"depositAddress\":\"%s\"}", mixerProvidedAddress));
    }

    @GetMapping("/checkFunds")
    public ResponseEntity checkFunds() {
        List<CheckFundResponse> fundResponses = new ArrayList<>();
        for (String key : addressManager.getAllDepositAddresses()) {
            CheckFundResponse mixerAddress = new CheckFundResponse();
            mixerAddress.setAddress(key);
            mixerAddress.setBalance(jobCoinChainService.getAddressInfo(key).getBalance());
            mixerAddress.setDepositAddresses(new ArrayList<>());
            for(String depositedAddress : addressManager.get(key)) {
                CheckFundResponse depositedAddressRes = new CheckFundResponse();
                depositedAddressRes.setAddress(depositedAddress);
                depositedAddressRes.setBalance(jobCoinChainService.getAddressInfo(depositedAddress).getBalance());
                mixerAddress.getDepositAddresses().add(depositedAddressRes);
            }
            fundResponses.add(mixerAddress);
        }
        return ResponseEntity.ok(fundResponses);
    }

    //disable automated flow
    public void createEvents() {
        logger.info("starting workflow...");
        List<String> randomUnusedAddresses = generateRandomAddress(2);
        logger.info(String.format("created random addresses %s", randomUnusedAddresses.toString()));
        String mixerProvidedAddress = inMemoryMixerService.submitUnusedAddress(randomUnusedAddresses);
        logger.info(String.format("Mixer provided address: %s", mixerProvidedAddress));
    }

    public List<String> generateRandomAddress(@PathVariable int no) {
        List<String> randomUnusedAddresses = new ArrayList<>();
        for (int i = 0; i < no; i++) {
            randomUnusedAddresses.add(inMemoryMixerService.generateRandomUnusedAddress());
        }
        return randomUnusedAddresses;
    }

}
