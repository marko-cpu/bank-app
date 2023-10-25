package com.bank.javabankapp.service.impl;

import com.bank.javabankapp.dto.TransactionDto;
import com.bank.javabankapp.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);
}
