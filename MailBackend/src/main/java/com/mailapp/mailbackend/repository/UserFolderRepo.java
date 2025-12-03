package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.UserFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFolderRepo extends JpaRepository<UserFolder, Long> {
}
