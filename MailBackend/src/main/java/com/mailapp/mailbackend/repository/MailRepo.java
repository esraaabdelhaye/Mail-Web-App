package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailRepo extends JpaRepository<Mail, Long> {
    // Find emails where folder matches (assuming you have a folder logic or column)
    // If folder is complex, you might just fetch all for now or filter by user
    List<Mail> findBySenderId(Long userId);
}
