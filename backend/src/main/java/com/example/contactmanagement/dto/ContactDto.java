package com.example.contactmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ContactDto {
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String title;

    private List<String> emailAddresses;

    private List<String> phoneNumbers;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getEmailAddresses() { return emailAddresses; }
    public void setEmailAddresses(List<String> emailAddresses) { this.emailAddresses = emailAddresses; }

    public List<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(List<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }
}
