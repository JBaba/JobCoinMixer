package com.gemini.mixer.modal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckFundResponse {
    String address;
    String balance;
    List<CheckFundResponse> depositAddresses;
}
