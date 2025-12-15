package com.mailapp.mailbackend.service.Mail;


import com.mailapp.mailbackend.dto.DraftDTO;
import com.mailapp.mailbackend.dto.MainMapper;
import com.mailapp.mailbackend.dto.RecipientDTO;
import com.mailapp.mailbackend.entity.*;
import com.mailapp.mailbackend.enums.Priority;
import com.mailapp.mailbackend.enums.ReceiverType;
import com.mailapp.mailbackend.repository.*;
import com.mailapp.mailbackend.service.MailReceiver.MailReceiverService;
import com.mailapp.mailbackend.service.UserMail.UserMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class DraftService {

    @Autowired
    private MailRepo mailRepo;

    @Autowired
    private UserMailRepo userMailRepo;

    @Autowired
    private FolderRepo folderRepo;


    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AttachmentRepo attachmentRepo;
    @Autowired
    private UserMailService userMailService;
    @Autowired
    private MailReceiverService mailReceiverService;
    @Autowired
    private MailReceiverRepo mailReceiverRepo;

    public Long createDraft(Long userId){
        User sender = userRepo.findById(userId).orElseThrow();

        Mail mail = Mail.builder()
                .sender(sender)
                .priority(2)
                .isDraft(true)
                .build();

        mailRepo.save(mail);
        userMailService.saveDraft(mail, sender);
        return mail.getId();
    }

    public void saveDraft(DraftDTO request) {
        Mail mail = mailRepo.findById(request.getDraftId())
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        updateDraft(request, mail);

    }

    private void updateDraft(DraftDTO request, Mail mail) {
        mail.setSubject(request.getSubject());
        mail.setBody(request.getBody());
        mail.setPriority(request.getPriority());
        mailRepo.save(mail);
    }

    public Long saveRecipient(RecipientDTO request) {
        Mail mail = mailRepo.findById(request.getDraftId())
                .orElseThrow(() -> new RuntimeException("Draft not found"));
        ReceiverType type;
        try {
            type = ReceiverType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recipient type");
        }
        ReceiverEntry entry = new ReceiverEntry(request.getRecipientEmail(), type);
        return mailReceiverService.save(mail, entry);
    }

    public void deleteDraftRecipientById(Long recipientId) {
        MailReceiver recipient = mailReceiverRepo.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));


        mailReceiverRepo.deleteById(recipientId);
    }

    public boolean isValid(String recipientEmail) {

        User user = userRepo.findByEmail(recipientEmail);
        if (user != null) {
            return true; // Found, so it's valid
        } else {
            return false; // Not found, so it's invalid
        }
    }

}
