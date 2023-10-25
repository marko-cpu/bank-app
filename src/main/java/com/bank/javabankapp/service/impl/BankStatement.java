package com.bank.javabankapp.service.impl;

import com.bank.javabankapp.dto.EmailDetails;
import com.bank.javabankapp.entity.Transaction;
import com.bank.javabankapp.entity.User;
import com.bank.javabankapp.repository.TransactionRepository;
import com.bank.javabankapp.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private static final String FILE = "/home/marko/Desktop/banking-app/MyStatements.pdf";

    // retrive list of transactions within a date range given an account number
    // generate a pdf file of transactions
    // send the file via email

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws IOException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        //BaseFont baseFont = BaseFont.createFont("/usr/share/fonts/cMap/NotoSerifCJK-Regular.ttc", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
       // Font font = new Font(baseFont, 12);


        Font green = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);


        // Kreirajte Image objekat
       // Image image = Image.getInstance("/home/marko/Desktop/otp.png");
        // Postavite veličinu slike (npr. 100x100)
      //  image.scaleAbsolute(70, 70);


        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell();
        bankName.setBorder(0);

// Kreirajte tabelu sa jednim redu
        PdfPTable innerTable = new PdfPTable(2);
        innerTable.setWidthPercentage(100);
        PdfPCell textCell = new PdfPCell(new Phrase("OTP BANKA", green));
        textCell.setBackgroundColor(BaseColor.GRAY);
        textCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        textCell.setPaddingTop(30f);
        textCell.setBorder(0);
        PdfPCell imageCell = new PdfPCell();
        imageCell.setBorder(0);
        imageCell.setBackgroundColor(BaseColor.GRAY);

// Kreirajte sliku
        Image image = Image.getInstance("/home/marko/Desktop/otp.png");
        image.scaleAbsolute(70, 70);
        imageCell.addElement(image);

// Dodajte ćelije u unutrašnju tabelu
        innerTable.addCell(textCell);
        innerTable.addCell(imageCell);



// Dodajte unutrašnju tabelu u glavnu ćeliju
        bankName.addElement(innerTable);

// Postavite horizontalno poravnanje unutrašnje tabele na sredinu
        innerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell bankAddress = new PdfPCell(new Phrase("20, Nikole Pasica, Kragujevac Srbija"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Pocetni datum: " + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("IZVOD TRANSAKCIJA"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("Krajnji datum: " + endDate));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Ime korisnika: " + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Adresa korisnika: " + user.getAddress()));
        address.setBorder(0);


        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("Datum", boldFont));
        date.setBackgroundColor(BaseColor.GRAY);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("Tip Transakcije" , boldFont));
        transactionType.setBackgroundColor(BaseColor.GRAY);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("Iznos Transakcije" , boldFont));
        transactionAmount.setBackgroundColor(BaseColor.GRAY);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("Status", boldFont));
        status.setHorizontalAlignment(Element.ALIGN_RIGHT);
        status.setBackgroundColor(BaseColor.GRAY);
        status.setBorder(0);

        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction .getTransactionType()));
           transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
           transactionsTable.addCell(new PdfPCell(new Phrase(transaction.getStatus()))).setHorizontalAlignment(Element.ALIGN_RIGHT);
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);


        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("IZVOD RAČUNA")
                .messageBody("Poštovana/i, \n\nSa zadovoljstvom vam dostavljamo traženi izvod računa u prilogu! \n\n\n")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);



        return transactionList;
    }


}
