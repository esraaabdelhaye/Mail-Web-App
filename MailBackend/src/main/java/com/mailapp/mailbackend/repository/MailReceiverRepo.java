package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.MailReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailReceiverRepo extends JpaRepository<MailReceiver, Long> {
    List<MailReceiver> findByMailId(Long id);
}
