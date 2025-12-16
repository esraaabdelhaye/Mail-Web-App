package com.mailapp.mailbackend.service.Mail;


import com.mailapp.mailbackend.dto.*;
import com.mailapp.mailbackend.entity.*;
import com.mailapp.mailbackend.enums.Priority;
import com.mailapp.mailbackend.enums.ReceiverType;
import com.mailapp.mailbackend.repository.*;
import com.mailapp.mailbackend.service.Attachment.AttachmentService;
import com.mailapp.mailbackend.service.MailReceiver.MailReceiverService;
import com.mailapp.mailbackend.service.UserMail.UserMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
    @Autowired
    private AttachmentService attachmentService;

    public Long createDraft(Long userId){
        User sender = userRepo.findById(userId).orElseThrow();

        Mail mail = Mail.builder()
                .sender(sender)
                .priority(2)
                .isDraft(true)
                .updatedAt(new Date())
                .build();

        mailRepo.save(mail);
        userMailService.saveDraft(mail, sender);
        return mail.getId();
    }

    public void saveDraft(DraftDTO request) {
        Mail mail = mailRepo.findById(request.getDraftId())
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        updateDraft(request, mail);
        userMailService.updateDraftInUserMail(mail, request.getPriority());
    }

    private void updateDraft(DraftDTO request, Mail mail) {
        mail.setSubject(request.getSubject());
        mail.setBody(request.getBody());
        mail.setPriority(request.getPriority());
        mail.setUpdatedAt(new Date());
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

    @Transactional(readOnly = true)
    public ComposeDraftDTO getDraftForCompose(Long draftId) {
        System.out.println(draftId);
        UserMail userMail = userMailRepo.findById(draftId).orElseThrow();
        Mail draft =  userMail.getMail();

        System.out.printf("subject" + draft.getSubject());
        System.out.println("did you get here?");
        ComposeDraftDTO dto = new ComposeDraftDTO();
        dto.setDraftId(draft.getId());
        dto.setSubject(draft.getSubject());
        dto.setBody(draft.getBody());
        dto.setPriority(draft.getPriority());
        dto.setTo(toRecipientDTOList(draft, "to"));
        dto.setCc(toRecipientDTOList(draft, "cc"));
        dto.setBcc(toRecipientDTOList(draft, "bcc"));
        List<Attachment> attachments = attachmentRepo.findByMailId(draft.getId());
        List<AttachmentDTO> attachmentDTOs = attachments.stream()
                .map(att -> new AttachmentDTO(
                        att.getId().toString(),
                        att.getOriginalFileName(),
                        att.getFileSize() != null ? att.getFileSize().toString() : "0",
                        att.getFileType()
                ))
                .toList();
        dto.setAttachments(attachmentDTOs);

        System.out.println("Draft: " + draft);
        System.out.println("Subject: " + draft.getSubject());
        System.out.println("Body: " + draft.getBody());
        System.out.println("To: " + dto.getTo());
        System.out.println("Cc: " + dto.getCc());
        System.out.println("Bcc: " + dto.getBcc());
        System.out.println("Attachments: " + attachmentDTOs);

        return dto;
    }

    private List<RecipientComposeDTO> toRecipientDTOList(Mail mail, String type) {
        List<MailReceiver> receivers = mailReceiverRepo.findByMailId(mail.getId());

        return receivers.stream()
                .filter(r -> r.getReceiverType().name().equalsIgnoreCase(type))
                .map(r -> {
                    RecipientComposeDTO dto = new RecipientComposeDTO();
                    dto.setId(r.getId());
                    dto.setEmail(r.getReceiver().getEmail()); // assuming User has getEmail()
                    dto.setType(r.getReceiverType().name());
                    return dto;
                })
                .toList();
    }




//    public void addAttachmentToDraft(Long draftId, MultipartFile file) {
//        Mail mail = mailRepo.findById(draftId)
//                .orElseThrow(() -> new RuntimeException("Draft not found"));
//        try{
//            attachmentService.saveAttachment(mail, file);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to save attachments", e);
//        }
//
//    }
//
//    public void removeAttachmentFromDraft(Long draftId, String fileName) {
//        Mail mail = mailRepo.findById(draftId)
//                .orElseThrow(() -> new RuntimeException("Draft not found"));
//        Attachment attachment = attachmentRepo.findByFileName(fileName);
//        attachmentRepo.deleteByFileNameAndMail(fileName, mail);
//    }
}
