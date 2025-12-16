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
    UserMail findByUserAndId(User user, Long mailId);
    List<UserMail> findByFolder(Folder folder);

    @Query("SELECT um FROM UserMail um WHERE um.folder.folderName = 'Trash' " +
            "AND um.movedAt < :cutoffDate")
    List<UserMail> findOldTrashEmails(@Param("cutoffDate") Date cutoffDate);

    Optional<UserMail> findUserMailByMail(Mail mail);

    Optional<UserMail> findByMailAndUserAndFolder(Mail mail, User user, Folder folder);

    Long countByUserAndFolder(User user, Folder folder);
}
