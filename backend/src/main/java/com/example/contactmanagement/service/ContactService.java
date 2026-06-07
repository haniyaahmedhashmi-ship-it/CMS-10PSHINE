package com.example.contactmanagement.service;

import com.example.contactmanagement.dto.ContactDto;
import com.example.contactmanagement.entity.Contact;
import com.example.contactmanagement.entity.User;
import com.example.contactmanagement.exception.ContactNotFoundException;
import com.example.contactmanagement.exception.UserNotFoundException;
import com.example.contactmanagement.repository.ContactRepository;
import com.example.contactmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    public Page<ContactDto> getContacts(Long userId, Pageable pageable) {
        logger.info("Fetching contacts for user id: {}", userId);
        return contactRepository.findByUserId(userId, pageable)
                .map(this::mapToDto);
    }

    public Page<ContactDto> searchContacts(Long userId, String query, Pageable pageable) {
        logger.info("Searching contacts for user id: {} with query: {}", userId, query);
        return contactRepository.findByUserIdAndFirstNameContainingIgnoreCaseOrUserIdAndLastNameContainingIgnoreCase(
                        userId, query, userId, query, pageable)
                .map(this::mapToDto);
    }

    public ContactDto getContactById(Long contactId, Long userId) {
        logger.info("Fetching contact id: {} for user id: {}", contactId, userId);
        Contact contact = contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));
        return mapToDto(contact);
    }

    public ContactDto createContact(Long userId, ContactDto contactDto) {
        logger.info("Creating new contact for user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Contact contact = new Contact();
        contact.setFirstName(contactDto.getFirstName());
        contact.setLastName(contactDto.getLastName());
        contact.setTitle(contactDto.getTitle());
        contact.setEmailAddresses(contactDto.getEmailAddresses());
        contact.setPhoneNumbers(contactDto.getPhoneNumbers());
        contact.setUser(user);

        Contact savedContact = contactRepository.save(contact);
        logger.info("Contact created successfully with id: {}", savedContact.getId());
        return mapToDto(savedContact);
    }

    public ContactDto updateContact(Long contactId, Long userId, ContactDto contactDto) {
        logger.info("Updating contact id: {} for user id: {}", contactId, userId);
        Contact contact = contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        contact.setFirstName(contactDto.getFirstName());
        contact.setLastName(contactDto.getLastName());
        contact.setTitle(contactDto.getTitle());
        contact.setEmailAddresses(contactDto.getEmailAddresses());
        contact.setPhoneNumbers(contactDto.getPhoneNumbers());

        Contact updatedContact = contactRepository.save(contact);
        logger.info("Contact updated successfully with id: {}", updatedContact.getId());
        return mapToDto(updatedContact);
    }

    public void deleteContact(Long contactId, Long userId) {
        logger.info("Deleting contact id: {} for user id: {}", contactId, userId);
        Contact contact = contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        contactRepository.delete(contact);
        logger.info("Contact deleted successfully id: {}", contactId);
    }

    public List<ContactDto> importContacts(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Contact> contacts = new ArrayList<>();
        String filename = file.getOriginalFilename();

        if (filename != null && filename.endsWith(".csv")) {
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                 CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
                
                Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                for (CSVRecord csvRecord : csvRecords) {
                    Contact contact = new Contact();
                    contact.setFirstName(csvRecord.get("firstName"));
                    contact.setLastName(csvRecord.isMapped("lastName") ? csvRecord.get("lastName") : null);
                    contact.setTitle(csvRecord.isMapped("title") ? csvRecord.get("title") : null);
                    
                    if (csvRecord.isMapped("email")) {
                        contact.setEmailAddresses(Arrays.asList(csvRecord.get("email").split(",")));
                    }
                    if (csvRecord.isMapped("phone")) {
                        contact.setPhoneNumbers(Arrays.asList(csvRecord.get("phone").split(",")));
                    }
                    contact.setUser(user);
                    contacts.add(contact);
                }
            }
        } else if (filename != null && filename.endsWith(".xlsx")) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);
                
                int firstNameIdx = -1, lastNameIdx = -1, emailIdx = -1, phoneIdx = -1, titleIdx = -1;
                
                for (Cell cell : headerRow) {
                    String header = cell.getStringCellValue().trim().toLowerCase();
                    if (header.equals("firstname")) firstNameIdx = cell.getColumnIndex();
                    else if (header.equals("lastname")) lastNameIdx = cell.getColumnIndex();
                    else if (header.equals("email")) emailIdx = cell.getColumnIndex();
                    else if (header.equals("phone")) phoneIdx = cell.getColumnIndex();
                    else if (header.equals("title")) titleIdx = cell.getColumnIndex();
                }

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Contact contact = new Contact();
                    if (firstNameIdx != -1 && row.getCell(firstNameIdx) != null) contact.setFirstName(getCellValue(row.getCell(firstNameIdx)));
                    if (lastNameIdx != -1 && row.getCell(lastNameIdx) != null) contact.setLastName(getCellValue(row.getCell(lastNameIdx)));
                    if (titleIdx != -1 && row.getCell(titleIdx) != null) contact.setTitle(getCellValue(row.getCell(titleIdx)));
                    
                    if (emailIdx != -1 && row.getCell(emailIdx) != null) {
                        contact.setEmailAddresses(Arrays.asList(getCellValue(row.getCell(emailIdx)).split(",")));
                    }
                    if (phoneIdx != -1 && row.getCell(phoneIdx) != null) {
                        contact.setPhoneNumbers(Arrays.asList(getCellValue(row.getCell(phoneIdx)).split(",")));
                    }
                    contact.setUser(user);
                    contacts.add(contact);
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload a .csv or .xlsx file.");
        }

        List<Contact> savedContacts = contactRepository.saveAll(contacts);
        return savedContacts.stream().map(this::mapToDto).toList();
    }

    public byte[] exportContactsToCsv(Long userId) throws Exception {
        List<Contact> contacts = contactRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
             org.apache.commons.csv.CSVPrinter csvPrinter = new org.apache.commons.csv.CSVPrinter(pw, CSVFormat.DEFAULT.withHeader("FirstName", "LastName", "Title", "Email", "Phone"))) {
            for (Contact contact : contacts) {
                csvPrinter.printRecord(
                    contact.getFirstName(),
                    contact.getLastName() != null ? contact.getLastName() : "",
                    contact.getTitle() != null ? contact.getTitle() : "",
                    contact.getEmailAddresses() != null ? String.join(",", contact.getEmailAddresses()) : "",
                    contact.getPhoneNumbers() != null ? String.join(",", contact.getPhoneNumbers()) : ""
                );
            }
        }
        return out.toByteArray();
    }

    public byte[] exportContactsToExcel(Long userId) throws Exception {
        List<Contact> contacts = contactRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contacts");
            Row headerRow = sheet.createRow(0);
            String[] columns = {"FirstName", "LastName", "Title", "Email", "Phone"};
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }
            int rowNum = 1;
            for (Contact contact : contacts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(contact.getFirstName());
                row.createCell(1).setCellValue(contact.getLastName() != null ? contact.getLastName() : "");
                row.createCell(2).setCellValue(contact.getTitle() != null ? contact.getTitle() : "");
                row.createCell(3).setCellValue(contact.getEmailAddresses() != null ? String.join(",", contact.getEmailAddresses()) : "");
                row.createCell(4).setCellValue(contact.getPhoneNumbers() != null ? String.join(",", contact.getPhoneNumbers()) : "");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }

    private ContactDto mapToDto(Contact contact) {
        ContactDto dto = new ContactDto();
        dto.setId(contact.getId());
        dto.setFirstName(contact.getFirstName());
        dto.setLastName(contact.getLastName());
        dto.setTitle(contact.getTitle());
        dto.setEmailAddresses(contact.getEmailAddresses());
        dto.setPhoneNumbers(contact.getPhoneNumbers());
        return dto;
    }
}
