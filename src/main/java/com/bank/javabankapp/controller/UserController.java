package com.bank.javabankapp.controller;

import com.bank.javabankapp.dto.*;
import com.bank.javabankapp.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(
            summary = "Create an account",
            description = "Creating an account - saving a new user into the db"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status Code 201 - Created"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) throws MessagingException, IOException {
        return userService.createAccount(userRequest);
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check how much money is in the account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status Code 200 Success"
    )
    @GetMapping("balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @Operation(
            summary = "Name Enquiry",
            description = "Return the name of the account holder"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status Code 200 Success"
    )
    @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @Operation(
            summary = "Credit Account",
            description = "Credit an account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status Code 200 Success"
    )
    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @Operation(
            summary = "Debit Account",
            description = "Debit an account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status Code 200 Success"
    )
    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @Operation(
            summary = "Transfer",
            description = "Transfer money from one account to another"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status Code 200 Success"
    )
    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }

}
