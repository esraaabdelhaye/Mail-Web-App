package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.EmailRequest;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.MailReceiver;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.enums.ReceiverType;
import com.mailapp.mailbackend.repository.MailReceiverRepo;
import com.mailapp.mailbackend.repository.UserRepo;
import com.mailapp.mailbackend.service.MailReceiver.MailReceiverService;
import com.mailapp.mailbackend.service.UserMail.UserMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Service
public class MultiReceiverSend implements SendStrategy{

    @Autowired
    private MailReceiverService mailReceiverService;

    @Autowired
    private UserMailService userMailService;

    public void sendMail(Mail mail, EmailRequest req){
        Queue<ReceiverEntry> queue = getReceiversQueue(req);
        
        // Save once in sender's Sent folder regardless of number of recipients
        userMailService.saveSentMail(mail);
        
        // Track unique recipients to avoid duplicate inbox entries
        Set<String> processedEmails = new HashSet<>();
        // Track unique (email, type) pairs to avoid duplicate MailReceiver records
        Set<String> processedReceiverEntries = new HashSet<>();
        
        // Process each recipient individually
        while (!queue.isEmpty()){
            ReceiverEntry entry = queue.poll();
            try {
                // Create unique key for this (email, type) combination
                String receiverKey = entry.getReceiverEmail() + ":" + entry.getReceiverType();
                
                // Only store if this (email, type) pair hasn't been saved yet
                if (!processedReceiverEntries.contains(receiverKey)) {
                    mailReceiverService.save(mail, entry);
                    processedReceiverEntries.add(receiverKey);
                }
                
                // Only create inbox entry once per unique email address
                if (!processedEmails.contains(entry.getReceiverEmail())) {
                    userMailService.saveReceiverMail(mail, entry);
                    processedEmails.add(entry.getReceiverEmail());
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to send mail: " + e.getMessage(), e);
            }

        }

    }

    private Queue<ReceiverEntry> getReceiversQueue(EmailRequest req){
        Queue<ReceiverEntry> queue = new LinkedList<>();
        if (!req.getTo().isEmpty()){
            for (String email : req.getTo()) {
                queue.add(new ReceiverEntry(email, ReceiverType.TO));
            }
        }
        if (!req.getBcc().isEmpty()){
            for (String email : req.getBcc()) {
                queue.add(new ReceiverEntry(email, ReceiverType.BCC));
            }
        }
        if (!req.getCc().isEmpty()){
            for (String email : req.getCc()) {
                queue.add(new ReceiverEntry(email, ReceiverType.CC));
            }
        }
        return queue;
    }



}
