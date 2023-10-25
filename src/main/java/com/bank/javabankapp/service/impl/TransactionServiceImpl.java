package com.bank.javabankapp.service.impl;

import com.bank.javabankapp.dto.TransactionDto;
import com.bank.javabankapp.entity.Transaction;
import com.bank.javabankapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;


    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .accountNumber(transactionDto.getAccountNumber())
                .status("USPEŠNA")
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved");

    }
}
