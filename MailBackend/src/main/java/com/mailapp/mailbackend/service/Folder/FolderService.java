package com.mailapp.mailbackend.service.Folder;

import com.mailapp.mailbackend.dto.UserFolderDTO;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.repository.FolderRepo;
import com.mailapp.mailbackend.repository.UserMailRepo;
import com.mailapp.mailbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserMailRepo userMailRepo;


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

    // Create System folders
    public void createSystemFolders(User user){
        List<String> systemFolderNames = Arrays.asList("Inbox", "Sent", "Trash", "Drafts");

        for (String name: systemFolderNames){
            Folder folder = new Folder();
            folder.setFolderName(name);
            folder.setUser(user);
            folder.setIsSystemFolder(true);
            folderRepo.save(folder);
        }
    }

    // Rename a folder
    public void renameFolder(Long folderId, String newName){
        if (folderRepo.existsByFolderName(newName)){
            throw new RuntimeException("Folder " + newName + "already exists!");
        }
        Folder folder = folderRepo.findById(folderId).orElseThrow(() -> new RuntimeException("Folder not found"));

        if (Boolean.TRUE.equals(folder.getIsSystemFolder())){
            throw new RuntimeException("Can't delete " + folder.getFolderName() + " as it is a system folder!");
        }

        folder.setFolderName(newName);
        folderRepo.save(folder);
    }

    // Delete a folder
    public void deleteFolder(Long folderId){
        Folder folder = folderRepo.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder with id: "+ folderId.toString() +" not found"));

        if (Boolean.TRUE.equals(folder.getIsSystemFolder())){
            throw new RuntimeException("Can't delete " + folder.getFolderName() + " as it is a system folder!");
        }

        // Move emails in this folder to Trash
        User user = folder.getUser();
        Folder trashFolder = folderRepo.findByUserAndFolderName(user, "Trash");


        // Well this shouldn't really happen but just in case
        if (trashFolder == null){
            throw new RuntimeException("Trash folder not found for user");
        }

        List<UserMail> emailsInFolder = userMailRepo.findByFolder(folder);

        // For each email in the folder send it to the trash folder
        for (UserMail userMail : emailsInFolder) {
            userMail.setFolder(trashFolder);
            userMailRepo.save(userMail);
        }

        folderRepo.delete(folder);
    }

    public Folder getInbox(User user) {
        return folderRepo.findByUserAndFolderName(user, "Inbox");
    }

    public Folder getSent(User user) {
        return folderRepo.findByUserAndFolderName(user, "Sent");
    }

    public Folder getDrafts(User user) {
        return folderRepo.findByUserAndFolderName(user, "Drafts");
    }



    public Map<Long, Long> getFolderCounts(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Folder> folders = folderRepo.findByUserId(userId);
        
        return folders.stream()
                .collect(Collectors.toMap(
                        Folder::getFolderId,
                        folder -> userMailRepo.countByUserAndFolder(user, folder)
                ));
    }
}
