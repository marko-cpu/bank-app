package com.bank.javabankapp.service.impl;

import com.bank.javabankapp.dto.*;
import jakarta.mail.MessagingException;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest) throws MessagingException;
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);
    BankResponse login(LoginDto login);

}
