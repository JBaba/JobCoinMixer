package com.gemini.mixer.modal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Transaction {
    private Date timestamp;
    private String toAddress;
    private String fromAddress;
    private String amount;
}
