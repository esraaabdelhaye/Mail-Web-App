package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.EmailRequest;
import com.mailapp.mailbackend.entity.Mail;

public interface SendStrategy {
    void sendMail(Mail mail, EmailRequest emailRequest);
}
