package com.gemini.mixer.service.contract;

import java.util.List;

public interface IMixer {
    // generate random unused address
    String generateRandomUnusedAddress();

    // mixer's primary house address
    String generateHouseAddress();

    // Point 1) Point 2) Use case impl
    // submit you unused addresses and mixer provides new unused address where you will transfer your funds
    String submitUnusedAddress(List<String> addresses);
}
