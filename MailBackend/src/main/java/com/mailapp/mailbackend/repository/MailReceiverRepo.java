package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.MailReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailReceiverRepo extends JpaRepository<MailReceiver, Long> {
}
