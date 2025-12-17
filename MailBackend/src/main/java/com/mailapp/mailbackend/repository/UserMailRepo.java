package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserMailRepo extends JpaRepository<UserMail, Long> {
    Page<UserMail> findByUserAndFolder(User user, Folder folder, Pageable pageable);
    List<UserMail> findByUserAndFolder(User user,Folder folder);

    UserMail findByUserAndId(User user, Long mailId);
    List<UserMail> findByFolder(Folder folder);

    @Query("SELECT um FROM UserMail um " +
            "JOIN FETCH um.folder f " +
            "JOIN FETCH um.user u " +
            "WHERE f.folderName = 'Trash' " +
            "AND um.movedAt < :cutoffDate")
    List<UserMail> findOldTrashEmails(@Param("cutoffDate") Date cutoffDate);

    Optional<UserMail> findUserMailByMail(Mail mail);

    Optional<UserMail> findByMailAndUserAndFolder(Mail mail, User user, Folder folder);

    Long countByUserAndFolder(User user, Folder folder);
}
