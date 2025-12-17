package com.mailapp.mailbackend.controller;

import com.mailapp.mailbackend.dto.ContactDTO;
import com.mailapp.mailbackend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    @Autowired
    private ContactService contactService;


    @GetMapping
    public List<ContactDTO> getContacts(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder) {
        return contactService.getContacts(userId, sortBy, sortOrder);
    }


    @PostMapping
    public ContactDTO addContact(@RequestParam Long userId, @RequestBody ContactDTO dto) {
        return contactService.addContact(userId, dto.name, dto.emails);
    }


    @PutMapping("/{id}")
    public ContactDTO editContact(@PathVariable Long id, @RequestBody ContactDTO dto) {
        return contactService.editContact(id, dto.name, dto.emails);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok().build();
    }


    // general search
    @GetMapping("/search")
    public List<ContactDTO> searchContacts(
            @RequestParam Long userId,
            @RequestParam String searchTerm,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder){
        return contactService.searchContacts(userId, searchTerm, sortBy, sortOrder);
    }


     // Search contacts by name only
    @GetMapping("/search/name")
    public List<ContactDTO> searchContactsByName(
            @RequestParam Long userId,
            @RequestParam String searchTerm,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder)
            {
        return contactService.searchContactsByName(userId, searchTerm, sortBy, sortOrder);
    }


     //Search contacts by email only
    @GetMapping("/search/email")
    public List<ContactDTO> searchContactsByEmail(
            @RequestParam Long userId,
            @RequestParam String searchTerm,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder
            ) {
        return contactService.searchContactsByEmail(userId, searchTerm, sortBy, sortOrder);
    }
}