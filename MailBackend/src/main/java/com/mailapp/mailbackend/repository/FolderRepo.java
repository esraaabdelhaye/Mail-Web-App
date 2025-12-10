package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.dto.UserFolderDTO;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepo extends JpaRepository<Folder, Long> {
    Folder findByUserAndFolderName(User user, String folderName);

    List<Folder> findByUserId(Long userId);

    boolean existsByFolderNameAndUserId(String folderName, Long userId);
}
