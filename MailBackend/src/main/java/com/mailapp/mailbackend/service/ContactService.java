package com.mailapp.mailbackend.service;

import com.mailapp.mailbackend.dto.ContactDTO;
import com.mailapp.mailbackend.entity.Contact;
import com.mailapp.mailbackend.entity.ContactEmail;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.repository.ContactRepo;
import com.mailapp.mailbackend.repository.ContactEmailRepo; // Need this for child table
import com.mailapp.mailbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private ContactEmailRepo contactEmailRepo;

    @Autowired
    private UserRepo userRepo;


    public List<ContactDTO> getContacts(Long userId, String sortBy, String sortOrder) {
        List<Contact> contacts = contactRepo.findByUserId(userId);

        // Convert to DTOs first
        List<ContactDTO> contactDTOs = contacts.stream().map(contact -> {
            // Extract the emails of the contact
            List<String> emailList = contact.getEmails().stream()
                    .map(ContactEmail::getEmailAddress)
                    .collect(Collectors.toList());

            return new ContactDTO(
                    contact.getId().toString(),
                    contact.getName(),
                    emailList
            );
        }).collect(Collectors.toList());

        // Sort the contacts based on the sortBy parameter
        return sortContacts(contactDTOs, sortBy, sortOrder);
    }

    private List<ContactDTO> sortContacts(List<ContactDTO> contacts, String sortBy, String sortOrder) {
        Comparator<ContactDTO> comparator;

        // Choose comparator based on sortBy parameter
        switch (sortBy.toLowerCase()) {
            case "email":
                comparator = Comparator.comparing(contact -> 
                    contact.getEmails().isEmpty() ? "" : contact.getEmails().get(0),
                    String.CASE_INSENSITIVE_ORDER
                );
                break;
            case "name":
            default:
                comparator = Comparator.comparing(
                    ContactDTO::getName,
                    String.CASE_INSENSITIVE_ORDER
                );
                break;
        }

        // Reverse if descending order
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return contacts.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }


    @Transactional // Save Contact AND Emails together or fail together
    public ContactDTO addContact(Long userId, String name, List<String> emailAddresses) {

        // Get user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create the Contact Entity
        Contact contact = new Contact();
        contact.setName(name);
        contact.setUser(user);

        // Set primary email as the first one in the list (if it exists)
        if (emailAddresses != null && !emailAddresses.isEmpty()) {
            contact.setPrimaryEmail(emailAddresses.get(0));
        }

        // Save contact first to generate ID
        Contact savedContact = contactRepo.save(contact);

        // Create ContactEmails object/entity for each email in the emailAddresses
        if (emailAddresses != null) {
            List<ContactEmail> emailEntities = new ArrayList<>();
            for (String email : emailAddresses) {
                ContactEmail emailEntity = new ContactEmail();
                emailEntity.setEmailAddress(email);
                emailEntity.setContact(savedContact); // Link to parent
                emailEntities.add(emailEntity);
            }
            contactEmailRepo.saveAll(emailEntities);

            // Update the list in the parent object
            savedContact.setEmails(emailEntities);
        }

        return new ContactDTO(
                savedContact.getId().toString(),
                savedContact.getName(),
                emailAddresses
        );
    }

    public ContactDTO editContact(Long contactId, String newName, List<String> newEmails){

        // Find the contact
        Contact contact = contactRepo.findById(contactId).orElseThrow(() -> new RuntimeException("Contact not found in the database!"));

        // Update contact's name
        contact.setName(newName);

        // Update primary email
        if (newEmails != null && !newEmails.isEmpty()){
            contact.setPrimaryEmail(newEmails.get(0));
        }

        // Update emails list in the db
        // My approach is to clear the emails then readding the updated list
        // This can be done by removing the email field from the contact (since removeOrphans is true)
        contact.getEmails().clear();

        // We add the new emails
        if (newEmails != null){
            for (String email : newEmails){
                ContactEmail emailEntity = new ContactEmail(); // Make a new row in the ContactEmail table
                emailEntity.setEmailAddress(email); // Give that row the email address
                emailEntity.setContact(contact); // Link that row/email with the contact
                contact.getEmails().add(emailEntity); // Also links the contact table with the contact-emails
                // The line above doesn't really do anything as the real link is done by the line before it
                // But it is essential to establish the link from both sides, from the contact and from the contact-email
            }
        }

        // Save changes of contact
        Contact savedContact = contactRepo.save(contact);

        return new ContactDTO(
                savedContact.getId().toString(),
                savedContact.getName(),
                newEmails
        );
    }

    public void deleteContact(Long contactId){
        if (!contactRepo.existsById(contactId)){
            throw new RuntimeException("Contact not found in the database!");
        }
        contactRepo.deleteById(contactId);
    }

    public List<ContactDTO> searchContacts(Long userId, String searchTerm) {
        List<ContactDTO> contactDtos = new ArrayList<>();
        List<Contact> contacts = contactRepo.searchByNameOrEmail(userId, searchTerm);
        for (Contact contact : contacts) {
            List<String> emailList = contact.getEmails().stream()
                    .map(ContactEmail::getEmailAddress)
                    .collect(Collectors.toList());
            contactDtos.add(new ContactDTO(contact.getId().toString(), contact.getName(), emailList));
        }
        return contactDtos;

    }

    public List<ContactDTO> searchContactsByName(Long userId, String searchTerm) {
        List<ContactDTO> contactDtos = new ArrayList<>();
        List<Contact> contacts = contactRepo.searchByName(userId, searchTerm);
        for (Contact contact : contacts) {
            List<String> emailList = contact.getEmails().stream()
                    .map(ContactEmail::getEmailAddress)
                    .collect(Collectors.toList());
            contactDtos.add(new ContactDTO(contact.getId().toString(), contact.getName(), emailList));
        }
        return contactDtos;

    }


    public List<ContactDTO> searchContactsByEmail(Long userId, String searchTerm) {
        List<ContactDTO> contactDtos = new ArrayList<>();
        List<Contact> contacts = contactRepo.searchByEmail(userId, searchTerm);
        for (Contact contact : contacts) {
            List<String> emailList = contact.getEmails().stream()
                    .map(ContactEmail::getEmailAddress)
                    .collect(Collectors.toList());
            contactDtos.add(new ContactDTO(contact.getId().toString(), contact.getName(), emailList));
        }
        return contactDtos;

    }
}