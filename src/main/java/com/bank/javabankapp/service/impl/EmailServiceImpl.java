package com.bank.javabankapp.service.impl;

import com.bank.javabankapp.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{



    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    private JavaMailSender mailSender;





    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
            try {

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom(senderEmail);
                helper.setTo(emailDetails.getRecipient());
                helper.setSubject(emailDetails.getSubject());

                String content = emailDetails.getMessageBody() + "<img src='cid:image'>";
                helper.setText(content, true);

//                FileSystemResource resource = new FileSystemResource(new File("/home/marko/Desktop/otp.png"));
//                helper.addInline("image", resource);

                URL imageUrl = new URL("https://logos-world.net/wp-content/uploads/2023/02/OTP-Bank-Logo.png");
                UrlResource imageResource = new UrlResource(imageUrl);
                helper.addInline("image", imageResource, "image/png");


                mailSender.send(message);
                System.out.println("Mail sent successufully");

            } catch (MailException | MessagingException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            mimeMessageHelper.setText(emailDetails.getMessageBody());

            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            mailSender.send(mimeMessage);

            log.info(file.getFilename() + " has been sent to user with email " + emailDetails.getRecipient());

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
