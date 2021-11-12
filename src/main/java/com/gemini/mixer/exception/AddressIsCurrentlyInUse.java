package com.gemini.mixer.exception;

public class AddressIsCurrentlyInUse extends RuntimeException {
    public AddressIsCurrentlyInUse(String address, String msg) {
        super(String.format("{%s} JobCoin address is already in use. %s", address, msg));
    }
}
