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
    public List<ContactDTO> getContacts(@RequestParam Long userId) {
        return contactService.getContacts(userId);
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
}