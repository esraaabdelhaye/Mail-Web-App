package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FolderRepo extends JpaRepository<Folder, Integer> {
    Folder findByUserAndFolderName(User user, String folderName);
}
