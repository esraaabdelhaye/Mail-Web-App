import {Component, EventEmitter, inject, Input, OnInit, Output, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Search, Plus, Pencil, Trash2, X, User, Mail, ArrowLeft } from 'lucide-angular';
import {Contact} from '../../app/models/ContactDTO';
import {ButtonComponent} from '../../shared/button/button';
import {ContactsHandler} from '../../services/contacts-handler/contacts-handler';


@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent],
  templateUrl: './contacts-view.html',
  styleUrls: ['./contacts-view.css']
})
export class ContactsComponent implements OnInit{
  protected contactsHandler = inject(ContactsHandler);


  ngOnInit(): void {
    this.contactsHandler.loadContacts();
  }

  // Outputs
  @Output() back = new EventEmitter<void>();

  // Note: For Add/Edit/Delete, you might want to call a Service directly
  // if you want to avoid @Output, just like we discussed for Folders.
  // For now, I will keep them as Outputs to match your request "unless needed",
  // but feel free to inject ContactService here!


  // State
  searchQuery = '';
  isModalOpen = false;
  editingContact: Contact | null = null;

  // Form State
  name = '';
  emails: string[] = [];
  emailInput = '';

  readonly icons = { Search, Plus, Pencil, Trash2, X, User, Mail, ArrowLeft };

  // Computed Filter
  get filteredContacts(): Contact[] {
    const query = this.searchQuery.toLowerCase();
    return this.contactsHandler.contacts().filter(contact =>
      contact.name.toLowerCase().includes(query) ||
      contact.emails.some(e => e.toLowerCase().includes(query))
    );
  }

  // --- Modal Actions ---

  openModal(contact?: Contact) {
    // When editing a contact
    if (contact) {
      this.editingContact = contact;
      this.name = contact.name;
      this.emails = [...contact.emails]; // Copy array
    }
    // When making a new contact
    else {
      this.editingContact = null;
      this.name = '';
      this.emails = [];
    }
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.editingContact = null;
    this.name = '';
    this.emails = [];
    this.emailInput = '';
  }

  // --- Form Actions ---


  // This probably still needs validation before accepting the email
  addEmail(event: Event) {
    const e = event as KeyboardEvent;
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      const email = this.emailInput.trim().replace(',', '');

      if (email && !this.emails.includes(email)) {
        this.emails.push(email); // Standard array push
        this.emailInput = '';
      }
    }
  }

  removeEmail(email: string) {
    this.emails = this.emails.filter(e => e !== email);
  }

  save() {
    if (!this.name.trim() || this.emails.length === 0) return;

    const contactData = { name: this.name.trim(), emails: this.emails };

    if (this.editingContact) {
      this.contactsHandler.editContact( {...this.editingContact, emails: this.emails}, this.name );
    } else {
      this.contactsHandler.addContact(contactData.name, contactData.emails);
    }
    this.closeModal();
  }
}
