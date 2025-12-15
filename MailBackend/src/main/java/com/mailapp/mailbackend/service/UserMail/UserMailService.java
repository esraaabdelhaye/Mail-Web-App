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
        save(mail, sender, senderFolder);
        User receiver = userRepo.findByEmail(entry.getReceiverEmail());
        Folder receiverFolder = folderService.getInbox(receiver);
        save(mail, receiver, receiverFolder);
    }

    private void save(Mail mail, User user, Folder folder) {
        UserMail userMail = UserMail.builder()
                .user(user)
                .mail(mail)
                .folder(folder)
                .sentAt(mail.getSentAt())
                .isRead(false)
                .importance(Priority.fromValue(mail.getPriority()))
                .isArchived(false)
                .build();
        userMailRepo.save(userMail);


    }
}
