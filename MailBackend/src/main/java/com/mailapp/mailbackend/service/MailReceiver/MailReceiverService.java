package com.mailapp.mailbackend.service.MailReceiver;

import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.MailReceiver;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.repository.MailReceiverRepo;
import com.mailapp.mailbackend.repository.UserRepo;
import com.mailapp.mailbackend.service.Mail.ReceiverEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailReceiverService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailReceiverRepo mailReceiverRepo;

    public void save(Mail mail, ReceiverEntry entry){
        User receiver = userRepo.findByEmail(entry.getReceiverEmail());
        if (receiver == null) {
            throw new RuntimeException("User not found with email: " + entry.getReceiverEmail());
        }
        MailReceiver mailReceiver = MailReceiver.builder()
                .mail(mail)
                .receiver(receiver)
                .receiverType(entry.getReceiverType())
                .build();
        mailReceiverRepo.save(mailReceiver);

    }
}
