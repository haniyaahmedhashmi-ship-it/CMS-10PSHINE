package com.example.contactmanagement.service;

import com.example.contactmanagement.dto.ContactDto;
import com.example.contactmanagement.entity.Contact;
import com.example.contactmanagement.entity.User;
import com.example.contactmanagement.exception.ContactNotFoundException;
import com.example.contactmanagement.exception.UserNotFoundException;
import com.example.contactmanagement.repository.ContactRepository;
import com.example.contactmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
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

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
    }

    @Test
    public void getContacts_Success() {
        Contact contact1 = new Contact();
        contact1.setId(1L);
        contact1.setFirstName("John");

        Contact contact2 = new Contact();
        contact2.setId(2L);
        contact2.setFirstName("Jane");

        List<Contact> contacts = Arrays.asList(contact1, contact2);
        Page<Contact> contactPage = new PageImpl<>(contacts);

        Pageable pageable = PageRequest.of(0, 10);
        when(contactRepository.findByUserId(1L, pageable)).thenReturn(contactPage);

        Page<ContactDto> result = contactService.getContacts(1L, pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    public void searchContacts_Success() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("SearchTest");

        List<Contact> contacts = Arrays.asList(contact);
        Page<Contact> contactPage = new PageImpl<>(contacts);

        Pageable pageable = PageRequest.of(0, 10);
        when(contactRepository.findByUserIdAndFirstNameContainingIgnoreCaseOrUserIdAndLastNameContainingIgnoreCase(
            anyLong(), anyString(), anyLong(), anyString(), any(Pageable.class)
        )).thenReturn(contactPage);

        Page<ContactDto> result = contactService.searchContacts(1L, "SearchTest", pageable);

        assertEquals(1, result.getTotalElements());
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
        ContactDto dto = new ContactDto();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");

        Contact savedContact = new Contact();
        savedContact.setId(2L);
        savedContact.setFirstName("Jane");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        ContactDto result = contactService.createContact(1L, dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    public void createContact_UserNotFound_ThrowsException() {
        ContactDto dto = new ContactDto();
        dto.setFirstName("Test");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> contactService.createContact(1L, dto));
    }

    @Test
    public void updateContact_Success() {
        ContactDto dto = new ContactDto();
        dto.setFirstName("Updated");
        dto.setLastName("Name");

        Contact existingContact = new Contact();
        existingContact.setId(1L);
        existingContact.setFirstName("Old");

        Contact updatedContact = new Contact();
        updatedContact.setId(1L);
        updatedContact.setFirstName("Updated");

        when(contactRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        ContactDto result = contactService.updateContact(1L, 1L, dto);

        assertEquals("Updated", result.getFirstName());
    }

    @Test
    public void deleteContact_Success() {
        Contact contact = new Contact();
        contact.setId(1L);

        when(contactRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(contact));
        doNothing().when(contactRepository).delete(contact);

        contactService.deleteContact(1L, 1L);

        verify(contactRepository, times(1)).delete(contact);
    }
}
