package com.example.contactmanagement.service;

import com.example.contactmanagement.dto.ContactDto;
import com.example.contactmanagement.entity.Contact;
import com.example.contactmanagement.entity.User;
import com.example.contactmanagement.exception.ContactNotFoundException;
import com.example.contactmanagement.repository.ContactRepository;
import com.example.contactmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getContactById_ContactExists_ReturnsDto() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("John");

        when(contactRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(contact));

        ContactDto result = contactService.getContactById(1L, 1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    public void getContactById_ContactDoesNotExist_ThrowsException() {
        when(contactRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> contactService.getContactById(1L, 1L));
    }

    @Test
    public void createContact_Success() {
        User user = new User();
        user.setId(1L);

        ContactDto dto = new ContactDto();
        dto.setFirstName("Jane");

        Contact savedContact = new Contact();
        savedContact.setId(2L);
        savedContact.setFirstName("Jane");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        ContactDto result = contactService.createContact(1L, dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Jane", result.getFirstName());
    }
}
