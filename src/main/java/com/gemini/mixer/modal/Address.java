package com.gemini.mixer.modal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Address {
    String balance;
    List<Transaction> transactions;
}
