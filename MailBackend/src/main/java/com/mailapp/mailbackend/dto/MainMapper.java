package com.mailapp.mailbackend.dto;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.enums.Priority;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MainMapper {

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

    // Recommended: Also add a mapping for the Priority Enum if you have one
    default String map(Priority importance) {
        if (importance == null) {
            return null;
        }
        return importance.name();
    }
}
