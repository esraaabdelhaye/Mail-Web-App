package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactEmailRepo extends JpaRepository<ContactEmail, Long> {
}
