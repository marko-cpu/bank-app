package com.bank.javabankapp.service.impl;

import com.bank.javabankapp.config.JwtTokenProvider;
import com.bank.javabankapp.dto.*;
import com.bank.javabankapp.entity.Role;
import com.bank.javabankapp.entity.User;
import com.bank.javabankapp.repository.UserRepository;
import com.bank.javabankapp.utils.AccountUtils;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;



    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) throws MessagingException {

        // Creating an account - saving a new user into the db
        // check if user already has an account

        if (userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNubmer(userRequest.getPhoneNubmer())
                .alternativePhoneNubmer(userRequest.getAlternativePhoneNubmer())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();

        User savedUser = userRepository.save(newUser);


        // send email
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("KREIRANJE RAČUNA")
                .messageBody("Poštovana/i,<br><br>"
                        + "Čestitamo! Vaš nalog je uspešno kreiran.<br><br>"
                        + "Detalji vašeg naloga:<br>"
                        + "Ime naloga: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "<br>"
                        + "Broj naloga: " + savedUser.getAccountNumber() + "<br><br><br>")
                .build();


        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();


    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword())
                );
                EmailDetails loginAlert = EmailDetails.builder()
                        .subject("Ulogovani ste!")
                        .recipient(loginDto.getEmail())
                        .messageBody("Poštovana/i,<br><br>"
                                + "Ulogovani ste na svoj nalog. Ako niste inicirali ovaj zahtev, molimo vas da kontaktirate banku." + "<br><br><br>")
                        .build();

                emailService.sendEmailAlert(loginAlert);
                return BankResponse.builder()
                        .responseCode("Uspešno ste se ulogovali!")
                        .responseMessage(jwtTokenProvider.generateJwtToken(authentication))
                        .build();

      }


    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
            // check if the provided account number exists in the db
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser =  userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        // checking if the account exixsts
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        // save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .accountNumber(userToCredit.getAccountNumber())
                .build();

        transactionService.saveTransaction(transactionDto);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        // checking if the account exixsts
        // check f the amount you intend to withdraw is not more then the current account balance
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                            .build();
        }

        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            TransactionDto transactionDto = TransactionDto.builder()
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .accountNumber(userToDebit.getAccountNumber())
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .build()
                            )
                    .build();





        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        // get the account to debit (check if it exists)
        // check if the account i'm debiting is not more then the current account balance
        // debit the account
        // get the account to credit
        // credit the account


        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());

        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();
        userRepository.save(sourceAccountUser);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("Obaveštenje o zaduženju - OTP Banka")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("Poštovana/i,<br><br>"
                + "Iznos " + request.getAmount() + "rsd je skinut sa vašeg računa!<br>"
                + "Trenutno stanje na računu je " + sourceAccountUser.getAccountBalance() + "rsd")
                .build();

        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
    //    String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
        userRepository.save(destinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("Obaveštenje o uplati - OTP Banka")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("Poštovana/i,<br><br>"
                + "Iznos " + request.getAmount() + "rsd. je poslat na vaš račun od korsnika " + sourceUsername + ".<br>"
                + "Vaše trenutno stanje iznosi " + destinationAccountUser.getAccountBalance() + "rsd." + "<br><br><br>")
                .build();

        emailService.sendEmailAlert(creditAlert);

        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .accountNumber(destinationAccountUser.getAccountNumber())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();



    }




}
