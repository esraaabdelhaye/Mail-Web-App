package com.mailapp.mailbackend.dto;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.enums.Priority;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MainMapper {


    // User methods
    User toUserEntity(UserDTO userDTO);
    UserDTO toUserDTO(User user);

    UserDTO updateUserDTOFromEntity(User user, @MappingTarget UserDTO targetDTO);
    EmailDTO toUserMailDTO(UserMail userMail);
    EmailDTO toEmailDTO(UserMail userMail);
    default String map(Folder folder) {
        if (folder == null) {
            return null;
        }
        return folder.getFolderName(); // <-- Extracts the required String field
    }

    // Email methods
    EmailDTO toEmailDTO(Mail mail);
    Mail toMailEntity(EmailDTO emailDTO);

    // List Mappings
    List<EmailDTO> toEmailDTOs(List<Mail> mails);

}
