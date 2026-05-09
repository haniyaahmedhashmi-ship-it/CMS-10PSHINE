package com.example.contactmanagement.repository;

import com.example.contactmanagement.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByUserId(Long userId, Pageable pageable);
    
    Page<Contact> findByUserIdAndFirstNameContainingIgnoreCaseOrUserIdAndLastNameContainingIgnoreCase(
            Long userId1, String firstName, Long userId2, String lastName, Pageable pageable);
            
    Optional<Contact> findByIdAndUserId(Long id, Long userId);
}
