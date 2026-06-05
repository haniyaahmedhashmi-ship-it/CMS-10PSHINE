package com.example.contactmanagement.controller;

import com.example.contactmanagement.dto.ContactDto;
import com.example.contactmanagement.dto.MessageResponse;
import com.example.contactmanagement.security.UserDetailsImpl;
import com.example.contactmanagement.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<Page<ContactDto>> getAllContacts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<ContactDto> contacts = contactService.getContacts(userDetails.getId(), pageable);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ContactDto>> searchContacts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<ContactDto> contacts = contactService.searchContacts(userDetails.getId(), query, pageable);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        ContactDto contact = contactService.getContactById(id, userDetails.getId());
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    public ResponseEntity<ContactDto> createContact(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ContactDto contactDto) {
        ContactDto createdContact = contactService.createContact(userDetails.getId(), contactDto);
        return ResponseEntity.ok(createdContact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ContactDto contactDto) {
        ContactDto updatedContact = contactService.updateContact(id, userDetails.getId(), contactDto);
        return ResponseEntity.ok(updatedContact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        contactService.deleteContact(id, userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("Contact deleted successfully!"));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadContacts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("file") MultipartFile file) {
        try {
            List<ContactDto> importedContacts = contactService.importContacts(userDetails.getId(), file);
            return ResponseEntity.ok(new MessageResponse("Successfully imported " + importedContacts.size() + " contacts."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to import contacts: " + e.getMessage()));
        }
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportContactsToCsv(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            byte[] csvData = contactService.exportContactsToCsv(userDetails.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "contacts.csv");
            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportContactsToExcel(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            byte[] excelData = contactService.exportContactsToExcel(userDetails.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "contacts.xlsx");
            return ResponseEntity.ok().headers(headers).body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
