package com.mailapp.mailbackend.service.User.Folder;

import com.mailapp.mailbackend.dto.UserFolderDTO;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.repository.FolderRepo;
import com.mailapp.mailbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private UserRepo userRepo;


    // Get all folders
    public List<UserFolderDTO> getFolders(Long userId){
        return folderRepo.findByUserId(userId).stream().map(f -> new UserFolderDTO(f.getFolderId(), f.getFolderName(), !f.getIsSystemFolder())).collect(Collectors.toList());
    }

    // Create a folder
    public UserFolderDTO createFolder(Long userId, String folderName){
        if (folderRepo.existsByFolderNameAndUserId(folderName, userId)){
            throw new RuntimeException("Folder " + folderName + "already exists!");
        }

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Folder folder = new Folder();
        folder.setFolderName(folderName);
        folder.setUser(user);
        folder.setIsSystemFolder(false);

        Folder saved = folderRepo.save(folder);
        return new UserFolderDTO(saved.getFolderId(), saved.getFolderName(), true);
    }

}
