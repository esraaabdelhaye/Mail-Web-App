package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Attachment;
import com.mailapp.mailbackend.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepo extends JpaRepository<Attachment, Long> {
    List<Attachment> findByMailId(Long id);

    void deleteByFileNameAndMail(String fileName, Mail mail);

    Attachment findByFileName(String fileName);
}
