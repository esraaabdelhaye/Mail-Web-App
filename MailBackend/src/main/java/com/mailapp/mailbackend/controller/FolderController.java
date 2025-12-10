package com.mailapp.mailbackend.controller;


import com.mailapp.mailbackend.dto.UserFolderDTO;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.service.User.Folder.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folders")
@CrossOrigin(origins = "http://localhost:4200")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping
    public List<UserFolderDTO> getFolders(@RequestParam Long userId){
        return folderService.getFolders(userId);
    }

    @PostMapping
    public UserFolderDTO createFolder(@RequestParam Long userId, @RequestBody UserFolderDTO dto){
        return folderService.createFolder(userId, dto.folderName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> renameFolder(@PathVariable Long id, @RequestBody UserFolderDTO dto){
        folderService.renameFolder(id, dto.folderName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id){
        folderService.deleteFolder(id);
        return ResponseEntity.ok().build();
    }
}
