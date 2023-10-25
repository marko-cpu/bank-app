package com.bank.javabankapp.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";

    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created! ";

    public static final String ACCOUNT_CREATION_SUCCESS = "002";

    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";

    public static final String ACCOUNT_NOT_EXIST = "003";

    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Account number does not exist!";

    public static final String ACCOUNT_FOUND_CODE = "004";

    public static final String ACCOUNT_FOUND_MESSAGE = "User Account Found!";

    public static final String ACCOUNT_CREDITED_SUCCESS = "005";

    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account has been successfully credited!";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";

    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance!";

    public static final String ACCOUNT_DEBITED_SUCCESS = "007";

    public static final String ACCOUNT_DEBITED_MESSAGE = "Account has been successfully debited!";

    public static final String TRANSFER_SUCCESSFUL_CODE = "008";

    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer Successful!";


    public static String generateAccountNumber(){

        Year currentYear = Year.now();
        int min1 = 100;
        int max1 = 999;

        // Generišite slučajan broj od 100 do 999 (sa 3 cifre)
        int randomNumber = (int) Math.floor(Math.random() * (max1 - min1 + 1) + min1);

        String year = String.valueOf(currentYear);
        String randomNumberStr = String.valueOf(randomNumber);

        // Ako je slučajni broj manji od 100, dodajte nule na početku
        if (randomNumber < 100) {
            int leadingZeros = 3 - randomNumberStr.length();
            StringBuilder leadingZerosStr = new StringBuilder();
            for (int i = 0; i < leadingZeros; i++) {
                leadingZerosStr.append("0");
            }
            randomNumberStr = leadingZerosStr.toString() + randomNumberStr;
        }

        int min = 100_000_000; // Minimum vrednost za 8 cifara
        int max = 999_999_999; // Maksimum vrednost za 9 cifara

        // Generišite slučajan broj između min i max
        int randomNumber1 = (int) Math.floor(Math.random() * (max - min + 1) + min);

        String year1 = String.valueOf(currentYear);
        String randomNumberStr1 = String.valueOf(randomNumber);

        // Ako je slučajni broj manji od 10^8, dodajte nule na početku
        if (randomNumber1 < 10_000_000) {
            int leadingZeros = 8 - randomNumberStr1.length();
            StringBuilder leadingZerosStr = new StringBuilder();
            for (int i = 0; i < leadingZeros; i++) {
                leadingZerosStr.append("0");
            }
            randomNumberStr1 = leadingZerosStr.toString() + randomNumberStr1;
        }

        StringBuilder accountNumber = new StringBuilder();

        // Spojite godinu i slučajni broj
        String accountNumberStr = accountNumber.append(year).append(randomNumber1).append(randomNumberStr).toString();

       return accountNumberStr.toString();
    }



}
