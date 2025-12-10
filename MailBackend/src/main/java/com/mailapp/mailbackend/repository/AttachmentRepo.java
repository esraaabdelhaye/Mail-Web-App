package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepo extends JpaRepository<Attachment, Long> {

}
