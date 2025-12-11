import { Injectable, signal, computed, inject } from '@angular/core';
import {Contact} from '../../app/models/ContactDTO';
import {AuthService} from '../auth/auth-service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {NotificationService} from '../notification/notification-service';



@Injectable({
  providedIn: 'root'
})
export class ContactsHandler {


   mockContacts: Contact[] = [
    {
      id: 'c1',
      name: 'Dr. Khaled Nagy',
      emails: ['khaled.nagy@alex.edu.eg', 'knagy.csed@gmail.com']
    },
    {
      id: 'c2',
      name: 'Eng. Ismail El-Yamany',
      emails: ['ismail.elyamany@alex.edu.eg']
    },
    {
      id: 'c3',
      name: 'Ahmed (Teammate)',
      emails: ['ahmed@student.alex.edu', 'ahmed_work@tech.com']
    },
    {
      id: 'c4',
      name: 'Sarah (Teammate)',
      emails: ['sarah@student.alex.edu']
    },
    {
      id: 'c5',
      name: 'Omar (Teammate)',
      emails: ['omar@student.alex.edu']
    },
    {
      id: 'c6',
      name: 'Faculty Library',
      emails: ['library@alex.edu.eg', 'support@library.alex.edu']
    },
    {
      id: 'c7',
      name: 'Student Affairs',
      emails: ['affairs@alex.edu.eg']
    },
    {
      id: 'c8',
      name: 'Career Center',
      emails: ['careers@alex.edu.eg', 'internships@alex.edu.eg']
    },
     {
       id: 'c1',
       name: 'Dr. Khaled Nagy',
       emails: ['khaled.nagy@alex.edu.eg', 'knagy.csed@gmail.com']
     },
     {
       id: 'c2',
       name: 'Eng. Ismail El-Yamany',
       emails: ['ismail.elyamany@alex.edu.eg']
     },
     {
       id: 'c3',
       name: 'Ahmed (Teammate)',
       emails: ['ahmed@student.alex.edu', 'ahmed_work@tech.com']
     },
     {
       id: 'c4',
       name: 'Sarah (Teammate)',
       emails: ['sarah@student.alex.edu']
     },
     {
       id: 'c5',
       name: 'Omar (Teammate)',
       emails: ['omar@student.alex.edu']
     },
     {
       id: 'c6',
       name: 'Faculty Library',
       emails: ['library@alex.edu.eg', 'support@library.alex.edu']
     },
     {
       id: 'c7',
       name: 'Student Affairs',
       emails: ['affairs@alex.edu.eg']
     },
     {
       id: 'c8',
       name: 'Career Center',
       emails: ['careers@alex.edu.eg', 'internships@alex.edu.eg']
     }
  ];

  private auth: AuthService = inject(AuthService);
  private http: HttpClient = inject(HttpClient);
  public notificationService = inject(NotificationService);

  readonly baseURL: string = "http://localhost:8080/contacts";

  // --- STATE ---
  readonly contacts = signal<Contact[]>([]);

  // UI State for Search (keep it here or in component, up to you)
  readonly searchQuery = signal('');

  // --- COMPUTED ---
  readonly filteredContacts = computed(() => {
    const query = this.searchQuery().toLowerCase();
    const list = this.contacts();

    if (!query) return list;

    return list.filter(c =>
      c.name.toLowerCase().includes(query) ||
      c.emails.some(e => e.toLowerCase().includes(query))
    );
  });

  // --- ACTIONS ---

  loadContacts() {
    console.log("Loading contacts...");
    const userId = this.auth.getCurrentUserId();

    const params = new HttpParams().set('userId', userId!);


    this.http.get<Contact[]>(this.baseURL, {params}).subscribe({
      next: (data) => this.contacts.set(data),
      error: (err) => console.error(err)
    });

    console.log("Loaded contacts: ", this.contacts());
  }

  addContact(contactName: string, contactEmails: string[]) {
    // Call backend
    console.log("Added contact");
    const userId = this.auth.getCurrentUserId();

    const dto: Omit<Contact, 'id'> = {
      'name': contactName,
      'emails': contactEmails,
    };

    const params = new HttpParams().set('userId', userId!);


    this.http.post<Contact>(this.baseURL, dto, {params}).subscribe({
      next: (newContact) => {
        this.contacts.update(list => [...list, newContact])
      },
      error: (err) => console.error(err)
    });
  }

  editContact(contact: Contact,newName: string) {
    const userId = this.auth.getCurrentUserId();

    const dto: Omit<Contact, 'id'> = {
      'name': newName,
      'emails': contact.emails,
    };

    const params = new HttpParams().set('userId', userId!);


    this.http.put<Contact>(this.baseURL + `/${contact.id}`, dto, {params}).subscribe({
      next: (updatedContact) => {
        this.contacts.update(list =>
            list.map(c => c.id === updatedContact.id ? updatedContact : c)
        );
      },
      error: (err) => console.error(err)
    });
  }

  deleteContact(id: string) {
    this.http.delete(this.baseURL + `/${id}`).subscribe({
      next: () => {
        this.contacts.update(list => list.filter(c => c.id !== id))
        this.notificationService.showSuccess(`Successfully Deleted`);
      },
      error: (err) => console.error(err)
    });
    // this.backend.deleteContact(id).subscribe({
    //   next: () => {
    //     this.contacts.update(list => list.filter(c => c.id !== id));
    //   }
    // });
  }
}
