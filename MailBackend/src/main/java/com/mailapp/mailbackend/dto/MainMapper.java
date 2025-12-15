//package com.mailapp.mailbackend.dto;
//import com.mailapp.mailbackend.entity.Folder;
//import com.mailapp.mailbackend.entity.Mail;
//import com.mailapp.mailbackend.entity.User;
//import com.mailapp.mailbackend.entity.UserMail;
//import org.mapstruct.*;
//
//import java.util.List;
//
//
//
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
//public interface MainMapper {
//
//
//    @Mapping(source = "id", target = "id")
//    @Mapping(source = "mail.sender", target = "sender")
//    @Mapping(source = "mail.subject", target = "subject")
//    @Mapping(source = "mail.body", target = "body")
//    @Mapping(source = "sentAt", target = "sentAt")
//    @Mapping(source = "isRead", target = "isRead")
//    @Mapping(source = "importance", target = "priority")
//    @Mapping(source = "folder.folderName", target = "folder")
//
//
//
//    // User methods
//    User toUserEntity(UserDTO userDTO);
//    UserDTO toUserDTO(User user);
//    UserDTO updateUserDTOFromEntity(User user, @MappingTarget UserDTO targetDTO);
//
//
//
//    // Mail methods
//    MailDetailsDTO toEmailDTO(Mail mail);
//    Mail toMailEntity(MailDetailsDTO mailDetailsDTO);
//    MailDetailsDTO toUserMailDTO(UserMail userMail);
//    MailDetailsDTO toEmailDTO(UserMail userMail);
//    default String map(Folder folder) {
//        if (folder == null) {
//            return null;
//        }
//        return folder.getFolderName(); // <-- Extracts the required String field
//    }
//
//    MailSummaryDTO toMailSummaryDTO(UserMail userMail);
//
//    // List Mappings
//    List<MailDetailsDTO> toEmailDTOs(List<Mail> mails);
//
//
//
//
//}


// src/main/java/com/mailapp/mailbackend/dto/MainMapper.java
package com.mailapp.mailbackend.dto;

import com.mailapp.mailbackend.entity.*;
import com.mailapp.mailbackend.enums.Priority;
import com.mailapp.mailbackend.repository.AttachmentRepo;
import com.mailapp.mailbackend.repository.MailReceiverRepo;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.mailapp.mailbackend.entity.*;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MainMapper {

    @Autowired
    protected MailReceiverRepo mailReceiverRepo;

    @Autowired
    protected AttachmentRepo attachmentRepo;

    // User methods
    public abstract User toUserEntity(UserDTO userDTO);
    public abstract UserDTO toUserDTO(User user);
    public abstract UserDTO updateUserDTOFromEntity(User user, @MappingTarget UserDTO targetDTO);

    // ============================================
    // COMPLETE UserMail to EmailDTO Mapping
    // ============================================

    @Mapping(source = "userMail.id", target = "id")
    @Mapping(source = "userMail.mail.sender", target = "sender")
    @Mapping(source = "userMail.mail.subject", target = "subject")
    @Mapping(source = "userMail.sentAt", target = "sentAt")
    @Mapping(source = "userMail.isRead", target = "isRead")
    @Mapping(source = "userMail.importance", target = "priority")
    @Mapping(target = "to", expression = "java(mapReceivers(userMail.getMail()))")
    @Mapping(target = "hasAttachments", expression = "java(hasAttachments(userMail.getMail()))")
    @Mapping(
            target = "preview",
            expression = "java(" +
                    "userMail.getMail() != null && userMail.getMail().getBody() != null " +
                    "? userMail.getMail().getBody().split(\"\\\\n\")[0] " +
                    ": \"\"" + // Return an empty string if the body is null
                    ")"
    )
    public abstract MailSummaryDTO toMailSummaryDTO(UserMail userMail);


    @Mapping(source = "userMail.id", target = "id")
    @Mapping(source = "userMail.mail.sender", target = "sender")
    @Mapping(source = "userMail.mail.subject", target = "subject")
    @Mapping(source = "userMail.mail.body", target = "body")
    @Mapping(source = "userMail.sentAt", target = "sentAt")
    @Mapping(source = "userMail.isRead", target = "isRead")
    @Mapping(source = "userMail.importance", target = "priority")
    @Mapping(source = "userMail.folder.folderName", target = "folder")
    @Mapping(target = "to", expression = "java(mapReceivers(userMail.getMail()))")
    @Mapping(target = "attachments", expression = "java(mapAttachments(userMail.getMail()))")
    public abstract MailDetailsDTO toDetailedEmailDTO(UserMail userMail);


    @Mapping(source = "fullName", target = "name")
    @Mapping(source = "email", target = "email")
    public abstract MailDetailsDTO.SenderDTO toSenderDTO(User user);


    protected List<String> mapReceivers(Mail mail) {
        if (mail == null) {
            return List.of();
        }

        // Fetch receivers from database
        List<MailReceiver> receivers = mailReceiverRepo.findByMailId(mail.getId());

        return receivers.stream()
                .map(mr -> mr.getReceiver().getEmail())
                .collect(Collectors.toList());
    }


    protected List<AttachmentDTO> mapAttachments(Mail mail) {
        if (mail == null) {
            return List.of();
        }

        // Fetch attachments from database
        List<Attachment> attachments = attachmentRepo.findByMailId(mail.getId());

        return attachments.stream()
                .map(att -> new AttachmentDTO(
                        att.getId().toString(),
                        att.getOriginalFileName(),
                        formatFileSize(att.getFileSize()),
                        att.getFileType()
                ))
                .collect(Collectors.toList());
    }

    protected boolean hasAttachments(Mail mail) {
        if (mail == null) {
            return false;
        }
        List<Attachment> attachments = attachmentRepo.findByMailId(mail.getId());
        return !attachments.isEmpty();
    }

    protected int map(Priority value) {
        if (value == null) return 0;

        return switch (value) {
            case URGENT -> 4;
            case HIGH -> 3;
            case NORMAL -> 2;
            case LOW -> 1;
        };
    }



    private String formatFileSize(Long bytes) {
        if (bytes == null) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    // Original methods
    public abstract MailDetailsDTO toEmailDTO(Mail mail);
    public abstract Mail toMailEntity(MailDetailsDTO emailDTO);
    public abstract List<MailDetailsDTO> toEmailDTOs(List<Mail> mails);
}