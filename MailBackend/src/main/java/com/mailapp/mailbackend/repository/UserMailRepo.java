package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMailRepo extends JpaRepository<UserMail, Long> {
    Page<UserMail> findByUserAndFolder(User user, Folder folder, Pageable pageable);
    UserMail findByUserAndId(User user, Long mailId);
}
