package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {
    public List<Contact> findByUserId(Long userId);
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Contact> searchByName(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    @Query("SELECT DISTINCT c FROM Contact c LEFT JOIN c.emails e WHERE c.user.id = :userId AND LOWER(e.emailAddress) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Contact> searchByEmail(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    @Query("SELECT DISTINCT c FROM Contact c LEFT JOIN c.emails e WHERE c.user.id = :userId AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.emailAddress) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Contact> searchByNameOrEmail(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}
