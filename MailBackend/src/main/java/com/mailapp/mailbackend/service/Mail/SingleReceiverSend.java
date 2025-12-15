package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.EmailRequest;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.MailReceiver;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.enums.ReceiverType;
import com.mailapp.mailbackend.repository.MailReceiverRepo;
import com.mailapp.mailbackend.repository.UserRepo;
import com.mailapp.mailbackend.service.MailReceiver.MailReceiverService;
import com.mailapp.mailbackend.service.User.UserService;
import com.mailapp.mailbackend.service.UserMail.UserMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SingleReceiverSend implements SendStrategy{

    @Autowired
    private MailReceiverService mailReceiverService;

    @Autowired
    private UserMailService userMailService;

    @Override
    public void sendMail(Mail mail, EmailRequest req){
        ReceiverEntry entry = findSingleReceiver(req);
        try {
//            mailReceiverService.save(mail, entry);
            userMailService.save(mail, entry);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to send mail: " + e.getMessage(), e);
        }

    }

    private ReceiverEntry findSingleReceiver(EmailRequest req){
        if (req.getTo().size()>0) return new ReceiverEntry(req.getTo().get(0), ReceiverType.TO);
        if (req.getBcc().size()>0) return new ReceiverEntry(req.getBcc().get(0), ReceiverType.BCC);
        if (req.getCc().size()>0) return new ReceiverEntry(req.getCc().get(0), ReceiverType.CC);
        throw new RuntimeException("No receivers provided");
    }




    }

