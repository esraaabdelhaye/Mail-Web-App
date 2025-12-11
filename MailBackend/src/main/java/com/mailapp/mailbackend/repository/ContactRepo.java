package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {
    public List<Contact> findByUserId(Long userId);
}
