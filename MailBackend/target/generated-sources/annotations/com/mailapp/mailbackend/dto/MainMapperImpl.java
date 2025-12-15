package com.mailapp.mailbackend.dto;

import com.mailapp.mailbackend.entity.Attachment;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-15T22:39:29+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class MainMapperImpl extends MainMapper {

    @Override
    public User toUserEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( userDTO.id );
        user.setEmail( userDTO.email );
        user.setPassword( userDTO.password );
        user.setFullName( userDTO.fullName );

        return user;
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.id = user.getId();
        userDTO.email = user.getEmail();
        userDTO.password = user.getPassword();
        userDTO.fullName = user.getFullName();

        return userDTO;
    }

    @Override
    public UserDTO updateUserDTOFromEntity(User user, UserDTO targetDTO) {
        if ( user == null ) {
            return targetDTO;
        }

        targetDTO.id = user.getId();
        targetDTO.email = user.getEmail();
        targetDTO.password = user.getPassword();
        targetDTO.fullName = user.getFullName();

        return targetDTO;
    }

    @Override
    public MailSummaryDTO toMailSummaryDTO(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }

        MailSummaryDTO mailSummaryDTO = new MailSummaryDTO();

        mailSummaryDTO.id = userMail.getId();
        mailSummaryDTO.sender = toSenderDTO( userMailMailSender( userMail ) );
        mailSummaryDTO.subject = userMailMailSubject( userMail );
        if ( userMail.getIsRead() != null ) {
            mailSummaryDTO.isRead = userMail.getIsRead();
        }
        mailSummaryDTO.priority = map( userMail.getImportance() );

        mailSummaryDTO.sentAt = userMail.getSentAt() != null ? userMail.getSentAt() : userMail.getMail().getUpdatedAt();
        mailSummaryDTO.to = mapReceivers(userMail.getMail());
        mailSummaryDTO.hasAttachments = hasAttachments(userMail.getMail());
        mailSummaryDTO.preview = userMail.getMail() != null && userMail.getMail().getBody() != null ? userMail.getMail().getBody().split("\\n")[0] : "";

        return mailSummaryDTO;
    }

    @Override
    public MailDetailsDTO toDetailedEmailDTO(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }

        MailDetailsDTO mailDetailsDTO = new MailDetailsDTO();

        mailDetailsDTO.id = userMail.getId();
        mailDetailsDTO.sender = toSenderDTO( userMailMailSender( userMail ) );
        mailDetailsDTO.subject = userMailMailSubject( userMail );
        mailDetailsDTO.body = userMailMailBody( userMail );
        if ( userMail.getIsRead() != null ) {
            mailDetailsDTO.isRead = userMail.getIsRead();
        }
        mailDetailsDTO.priority = map( userMail.getImportance() );
        mailDetailsDTO.folder = userMailFolderFolderName( userMail );

        mailDetailsDTO.sentAt = userMail.getSentAt() != null ? userMail.getSentAt() : userMail.getMail().getUpdatedAt();
        mailDetailsDTO.to = mapReceivers(userMail.getMail());
        mailDetailsDTO.attachments = mapAttachments(userMail.getMail());

        return mailDetailsDTO;
    }

    @Override
    public MailDetailsDTO.SenderDTO toSenderDTO(User user) {
        if ( user == null ) {
            return null;
        }

        MailDetailsDTO.SenderDTO senderDTO = new MailDetailsDTO.SenderDTO();

        senderDTO.name = user.getFullName();
        senderDTO.email = user.getEmail();

        return senderDTO;
    }

    @Override
    public MailDetailsDTO toEmailDTO(Mail mail) {
        if ( mail == null ) {
            return null;
        }

        MailDetailsDTO mailDetailsDTO = new MailDetailsDTO();

        mailDetailsDTO.id = mail.getId();
        mailDetailsDTO.sender = toSenderDTO( mail.getSender() );
        mailDetailsDTO.subject = mail.getSubject();
        mailDetailsDTO.body = mail.getBody();
        mailDetailsDTO.sentAt = mail.getSentAt();
        mailDetailsDTO.priority = mail.getPriority();
        mailDetailsDTO.attachments = attachmentListToAttachmentDTOList( mail.getAttachments() );

        return mailDetailsDTO;
    }

    @Override
    public Mail toMailEntity(MailDetailsDTO emailDTO) {
        if ( emailDTO == null ) {
            return null;
        }

        Mail.MailBuilder mail = Mail.builder();

        mail.id( emailDTO.id );
        mail.sender( senderDTOToUser( emailDTO.sender ) );
        mail.subject( emailDTO.subject );
        mail.body( emailDTO.body );
        mail.sentAt( emailDTO.sentAt );
        mail.priority( emailDTO.priority );
        mail.attachments( attachmentDTOListToAttachmentList( emailDTO.attachments ) );

        return mail.build();
    }

    @Override
    public List<MailDetailsDTO> toEmailDTOs(List<Mail> mails) {
        if ( mails == null ) {
            return null;
        }

        List<MailDetailsDTO> list = new ArrayList<MailDetailsDTO>( mails.size() );
        for ( Mail mail : mails ) {
            list.add( toEmailDTO( mail ) );
        }

        return list;
    }

    private User userMailMailSender(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }
        Mail mail = userMail.getMail();
        if ( mail == null ) {
            return null;
        }
        User sender = mail.getSender();
        if ( sender == null ) {
            return null;
        }
        return sender;
    }

    private String userMailMailSubject(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }
        Mail mail = userMail.getMail();
        if ( mail == null ) {
            return null;
        }
        String subject = mail.getSubject();
        if ( subject == null ) {
            return null;
        }
        return subject;
    }

    private String userMailMailBody(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }
        Mail mail = userMail.getMail();
        if ( mail == null ) {
            return null;
        }
        String body = mail.getBody();
        if ( body == null ) {
            return null;
        }
        return body;
    }

    private String userMailFolderFolderName(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }
        Folder folder = userMail.getFolder();
        if ( folder == null ) {
            return null;
        }
        String folderName = folder.getFolderName();
        if ( folderName == null ) {
            return null;
        }
        return folderName;
    }

    protected AttachmentDTO attachmentToAttachmentDTO(Attachment attachment) {
        if ( attachment == null ) {
            return null;
        }

        AttachmentDTO attachmentDTO = new AttachmentDTO();

        if ( attachment.getId() != null ) {
            attachmentDTO.id = String.valueOf( attachment.getId() );
        }
        attachmentDTO.fileName = attachment.getFileName();
        if ( attachment.getFileSize() != null ) {
            attachmentDTO.fileSize = String.valueOf( attachment.getFileSize() );
        }
        attachmentDTO.fileType = attachment.getFileType();

        return attachmentDTO;
    }

    protected List<AttachmentDTO> attachmentListToAttachmentDTOList(List<Attachment> list) {
        if ( list == null ) {
            return null;
        }

        List<AttachmentDTO> list1 = new ArrayList<AttachmentDTO>( list.size() );
        for ( Attachment attachment : list ) {
            list1.add( attachmentToAttachmentDTO( attachment ) );
        }

        return list1;
    }

    protected User senderDTOToUser(MailDetailsDTO.SenderDTO senderDTO) {
        if ( senderDTO == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( senderDTO.email );

        return user;
    }

    protected Attachment attachmentDTOToAttachment(AttachmentDTO attachmentDTO) {
        if ( attachmentDTO == null ) {
            return null;
        }

        Attachment.AttachmentBuilder attachment = Attachment.builder();

        if ( attachmentDTO.id != null ) {
            attachment.id( Long.parseLong( attachmentDTO.id ) );
        }
        attachment.fileName( attachmentDTO.fileName );
        attachment.fileType( attachmentDTO.fileType );
        if ( attachmentDTO.fileSize != null ) {
            attachment.fileSize( Long.parseLong( attachmentDTO.fileSize ) );
        }

        return attachment.build();
    }

    protected List<Attachment> attachmentDTOListToAttachmentList(List<AttachmentDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Attachment> list1 = new ArrayList<Attachment>( list.size() );
        for ( AttachmentDTO attachmentDTO : list ) {
            list1.add( attachmentDTOToAttachment( attachmentDTO ) );
        }

        return list1;
    }
}
