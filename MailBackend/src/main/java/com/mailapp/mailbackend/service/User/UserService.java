package com.mailapp.mailbackend.service.User;

import com.mailapp.mailbackend.dto.MainMapper;
import com.mailapp.mailbackend.dto.UserDTO;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.enums.ChainType;
import com.mailapp.mailbackend.repository.UserRepo;
import com.mailapp.mailbackend.service.User.Folder.FolderService;
import com.mailapp.mailbackend.service.User.auth.ChainFactory;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    ChainFactory chainFactory;
    @Autowired
    FolderService folderService;


    private final MainMapper userMapper = Mappers.getMapper(MainMapper.class);

    public UserDTO register(UserDTO userDTO) throws Exception {
        if(chainFactory.getChain(ChainType.Register).handle(userDTO)) {
            User user = userMapper.toUserEntity(userDTO);
            user = this.userRepo.save(user);

            folderService.createSystemFolders(user);

            userMapper.updateUserDTOFromEntity(user, userDTO);
            return userDTO;

        }
        return userDTO;
    }

    public UserDTO login(UserDTO userDTO) throws Exception {

        if(chainFactory.getChain(ChainType.Login).handle(userDTO)) {
            User user = userRepo.findByEmail(userDTO.email);
            userDTO.id = user.getId();

            return userDTO;
        }
        return userDTO;
    }
}
