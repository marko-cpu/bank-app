package com.bank.javabankapp.repository;

import com.bank.javabankapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
