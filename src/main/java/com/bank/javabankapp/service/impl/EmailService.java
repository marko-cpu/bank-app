package com.bank.javabankapp.service.impl;

import com.bank.javabankapp.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);

    void sendEmailWithAttachment(EmailDetails emailDetails);


}
