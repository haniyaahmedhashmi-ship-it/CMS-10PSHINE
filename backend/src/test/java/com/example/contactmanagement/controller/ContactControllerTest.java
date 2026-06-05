package com.example.contactmanagement.controller;

import com.example.contactmanagement.dto.ContactDto;
import com.example.contactmanagement.dto.MessageResponse;
import com.example.contactmanagement.security.JwtService;
import com.example.contactmanagement.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@example.com")
    public void getAllContacts_Success() throws Exception {
        ContactDto dto1 = new ContactDto(1L, "John", "Doe", null, null, null);
        ContactDto dto2 = new ContactDto(2L, "Jane", "Smith", null, null, null);
        List<ContactDto> dtoList = Arrays.asList(dto1, dto2);
        Page<ContactDto> dtoPage = new PageImpl<>(dtoList);

        when(contactService.getContacts(eq(1L), any(Pageable.class))).thenReturn(dtoPage);

        mockMvc.perform(get("/api/contacts")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void searchContacts_Success() throws Exception {
        ContactDto dto = new ContactDto(1L, "John", "Doe", null, null, null);
        List<ContactDto> dtoList = Arrays.asList(dto);
        Page<ContactDto> dtoPage = new PageImpl<>(dtoList);

        when(contactService.searchContacts(eq(1L), eq("John"), any(Pageable.class))).thenReturn(dtoPage);

        mockMvc.perform(get("/api/contacts/search")
                .param("query", "John")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void getContactById_Success() throws Exception {
        ContactDto dto = new ContactDto(1L, "John", "Doe", null, null, null);

        when(contactService.getContactById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void createContact_Success() throws Exception {
        ContactDto requestDto = new ContactDto(null, "Jane", "Doe", null, null, null);
        ContactDto responseDto = new ContactDto(1L, "Jane", "Doe", null, null, null);

        when(contactService.createContact(eq(1L), any(ContactDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void updateContact_Success() throws Exception {
        ContactDto requestDto = new ContactDto(null, "Updated", "Doe", null, null, null);
        ContactDto responseDto = new ContactDto(1L, "Updated", "Doe", null, null, null);

        when(contactService.updateContact(eq(1L), eq(1L), any(ContactDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/contacts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void deleteContact_Success() throws Exception {
        doNothing().when(contactService).deleteContact(1L, 1L);

        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void uploadContacts_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "firstName,lastName\nJohn,Doe".getBytes()
        );

        List<ContactDto> imported = Arrays.asList(new ContactDto(1L, "John", "Doe", null, null, null));
        when(contactService.importContacts(eq(1L), any())).thenReturn(imported);

        mockMvc.perform(multipart("/api/contacts/upload").file(file))
                .andExpect(status().isOk());
    }
}
