package com.mailapp.mailbackend.service.UserMail;


import com.mailapp.mailbackend.dto.UserFolderDTO;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.enums.Priority;
import com.mailapp.mailbackend.repository.FolderRepo;
import com.mailapp.mailbackend.repository.UserMailRepo;
import com.mailapp.mailbackend.repository.UserRepo;
import com.mailapp.mailbackend.service.Folder.FolderService;
import com.mailapp.mailbackend.service.Mail.ReceiverEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserMailService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserMailRepo userMailRepo;

    @Autowired
    private FolderService folderService;


    public void save(Mail mail, ReceiverEntry entry) {
        User sender = mail.getSender();
        Folder senderFolder = folderService.getSent(sender);
        save(mail, sender, senderFolder, true);

        Folder draftFolder = folderService.getDrafts(sender);
        Optional<UserMail> draftEntry = userMailRepo.findByMailAndUserAndFolder(mail, sender, draftFolder);
        draftEntry.ifPresent(userMailRepo::delete);

        User receiver = userRepo.findByEmail(entry.getReceiverEmail());
        Folder receiverFolder = folderService.getInbox(receiver);
        save(mail, receiver, receiverFolder, false);
    }

    public void saveDraft(Mail mail, User sender) {
        Folder senderFolder = folderService.getDrafts(sender);
        save(mail, sender, senderFolder, true);
    }


    private void save(Mail mail, User user, Folder folder, boolean isRead) {
        UserMail userMail = UserMail.builder()
                .user(user)
                .mail(mail)
                .folder(folder)
                .sentAt(mail.getSentAt())
                .isRead(isRead)
                .importance(Priority.fromValue(mail.getPriority()))
                .isArchived(false)
                .build();
        userMailRepo.save(userMail);
    }


    public void updateDraftInUserMail(Mail mail, int priority) {
        UserMail userMail = userMailRepo.findUserMailByMail(mail)
                .orElseThrow(() -> new RuntimeException("usermail not found"));

        userMail.setImportance(Priority.fromValue(priority));
        userMailRepo.save(userMail);
    }
}

