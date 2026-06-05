package com.example.contactmanagement.repository;

import com.example.contactmanagement.entity.Contact;
import com.example.contactmanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("testcontactrepo@example.com");
        testUser.setPhoneNumber("123456");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testFindByUserId() {
        Contact contact1 = new Contact();
        contact1.setFirstName("John");
        contact1.setLastName("Doe");
        contact1.setUser(testUser);
        contactRepository.save(contact1);

        Contact contact2 = new Contact();
        contact2.setFirstName("Jane");
        contact2.setLastName("Smith");
        contact2.setUser(testUser);
        contactRepository.save(contact2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> contacts = contactRepository.findByUserId(testUser.getId(), pageable);

        assertEquals(2, contacts.getTotalElements());
    }

    @Test
    public void testFindByIdAndUserId() {
        Contact contact = new Contact();
        contact.setFirstName("Alice");
        contact.setLastName("Johnson");
        contact.setUser(testUser);
        contact = contactRepository.save(contact);

        Optional<Contact> found = contactRepository.findByIdAndUserId(contact.getId(), testUser.getId());
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getFirstName());
    }

    @Test
    public void testSearchContacts() {
        Contact contact1 = new Contact();
        contact1.setFirstName("Bob");
        contact1.setLastName("Brown");
        contact1.setUser(testUser);
        contactRepository.save(contact1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> results = contactRepository.findByUserIdAndFirstNameContainingIgnoreCaseOrUserIdAndLastNameContainingIgnoreCase(
            testUser.getId(), "Bob", testUser.getId(), "Brown", pageable
        );

        assertEquals(1, results.getTotalElements());
    }
}
