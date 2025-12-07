package com.mailapp.mailbackend.dto;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.Mail;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MainMapper {


    // User methods
    User toUserEntity(UserDTO userDTO);
    UserDTO toUserDTO(User user);

    UserDTO updateUserDTOFromEntity(User user, @MappingTarget UserDTO targetDTO);

    // Email methods
    EmailDTO toEmailDTO(Mail mail);
    Mail toMailEntity(EmailDTO emailDTO);

    // List Mappings
    List<EmailDTO> toEmailDTOs(List<Mail> mails);

}
