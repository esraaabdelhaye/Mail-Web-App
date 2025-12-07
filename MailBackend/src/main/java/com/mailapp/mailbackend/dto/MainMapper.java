package com.mailapp.mailbackend.dto;
import com.mailapp.mailbackend.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MainMapper {

    User toUserEntity(UserDTO userDTO);
    UserDTO toUserDTO(User user);

    UserDTO updateUserDTOFromEntity(User user, @MappingTarget UserDTO targetDTO);

}
