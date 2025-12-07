package com.mailapp.mailbackend.service.User;

import com.mailapp.mailbackend.dto.MainMapper;
import com.mailapp.mailbackend.dto.UserDTO;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.repository.UserRepo;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    private MainMapper userMapper = Mappers.getMapper(MainMapper.class);

    public UserDTO register(UserDTO userDTO) throws Exception {
        User user = userMapper.toUserEntity(userDTO);
        return userMapper.toUserDTO(userRepo.save(user));
    }

    public UserDTO login(UserDTO userDTO) throws Exception {
        User user = userRepo.findByEmail(userDTO.email);
        userDTO.id = user.getId();
        return userDTO;
    }
}
