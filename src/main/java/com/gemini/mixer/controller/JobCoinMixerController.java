package com.gemini.mixer.controller;

import com.gemini.mixer.service.InMemoryMixerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/mixer")
public class JobCoinMixerController {

    Logger logger = Logger.getLogger(JobCoinMixerController.class.getName());
    @Autowired
    InMemoryMixerService inMemoryMixerService;

    @PostConstruct
    public void initWorkFlow() {
        createEvents();
    }

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
