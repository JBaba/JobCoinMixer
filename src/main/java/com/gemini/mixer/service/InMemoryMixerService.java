package com.gemini.mixer.service;

import com.gemini.mixer.exception.AddressIsCurrentlyInUse;
import com.gemini.mixer.service.contract.IAddressManager;
import com.gemini.mixer.service.contract.IDisbursement;
import com.gemini.mixer.service.contract.IMixer;
import com.gemini.mixer.service.contract.ISubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class InMemoryMixerService implements IMixer {

    Logger logger = Logger.getLogger(InMemoryMixerService.class.getName());

    @Autowired
    JobCoinChainService jobCoinChainService;

    @Autowired
    IAddressManager addressManager;

    @Autowired
    ISubscriber subscriber;

    @Autowired
    IDisbursement disbursement;

    String houseAddress;

    /**
     * Initialize one address which will be used as house address
     */
    @PostConstruct
    public void init() {
        houseAddress = generateHouseAddress();
        logger.info(String.format("House address: %s", houseAddress));
        subscriber.startSubscriber();
        logger.info("Subscriber started.");
        disbursement.setFromAddress(houseAddress);
    }

    /**
     * Generate Address which is unused
     *
     * @return
     */
    @Override
    public String generateRandomUnusedAddress() {
        UUID randomAddress = UUID.randomUUID();
        while (!isAddressHasZeroBalance(randomAddress.toString())) {
            randomAddress = UUID.randomUUID();
        }
        return randomAddress.toString();
    }

    private boolean isAddressHasZeroBalance(String address) {
        String addressBal = jobCoinChainService.getAddressInfo(address).getBalance();
        return addressBal.equals("0");
    }

    @Override
    public String generateHouseAddress() {
        return generateRandomUnusedAddress();
    }

    @Override
    public String submitUnusedAddress(List<String> addresses) {
        assertAllAddressesAreUnused(addresses);
        // mixer provides new unused address
        String depositAddress = generateRandomUnusedAddress();
        // keep record where to distribute funds
        addressManager.add(depositAddress, addresses);
        // subscribe to polling
        subscriber.subscribe(depositAddress, this::triggerDisbursement);
        return depositAddress;
    }

    // When polling identifies activity this block is called
    private void triggerDisbursement(String address, String balance) {
        logger.info("Scheduling disbursement...");
        logger.info(String.format("Sending coins {%s} from address {%s} to address {%s}", balance, address, houseAddress));
        // sending coins to house address
        jobCoinChainService.sendTransaction(address, houseAddress, balance);
        // disburse funds
        disbursement.schedule(addressManager.get(address), Double.parseDouble(balance));
    }

    private void assertAllAddressesAreUnused(List<String> addresses) {
        for (String address : addresses) {
            if (!isAddressHasZeroBalance(address)) {
                throw new AddressIsCurrentlyInUse(address, "You can only submit unused address.");
            }
        }
    }
}
