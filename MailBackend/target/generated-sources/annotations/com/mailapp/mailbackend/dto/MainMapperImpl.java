package com.mailapp.mailbackend.dto;

import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-08T19:25:52+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class MainMapperImpl implements MainMapper {

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
    public EmailDTO toUserMailDTO(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }

        EmailDTO emailDTO = new EmailDTO();

        emailDTO.id = userMail.getId();
        emailDTO.sentAt = userMail.getSentAt();
        if ( userMail.getIsRead() != null ) {
            emailDTO.isRead = userMail.getIsRead();
        }
        emailDTO.folder = map( userMail.getFolder() );

        return emailDTO;
    }

    @Override
    public EmailDTO toEmailDTO(UserMail userMail) {
        if ( userMail == null ) {
            return null;
        }

        EmailDTO emailDTO = new EmailDTO();

        emailDTO.id = userMail.getId();
        emailDTO.sentAt = userMail.getSentAt();
        if ( userMail.getIsRead() != null ) {
            emailDTO.isRead = userMail.getIsRead();
        }
        emailDTO.folder = map( userMail.getFolder() );

        return emailDTO;
    }
}
