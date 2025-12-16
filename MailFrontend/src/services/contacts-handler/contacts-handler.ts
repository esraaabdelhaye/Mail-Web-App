import { Injectable, signal, computed, inject } from '@angular/core';
import { Contact } from '../../app/models/ContactDTO';
import { AuthService } from '../auth/auth-service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { NotificationService } from '../notification/notification-service';
import { Observable, tap, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ContactsHandler {
  private auth: AuthService = inject(AuthService);
  private http: HttpClient = inject(HttpClient);
  public notificationService = inject(NotificationService);

  readonly baseURL: string = 'http://localhost:8080/contacts';

  // --- STATE ---
  readonly contacts = signal<Contact[]>([]);

  // UI State for Search (keep it here or in component, up to you)
  readonly searchQuery = signal('');

  // Store current sort settings
  private currentSortBy: string = 'name';
  private currentSortOrder: string = 'asc';

  // --- ACTIONS ---

  loadContacts(sortBy: string = 'name', sortOrder: string = 'asc') {
    console.log('Loading contacts...');
    const userId = this.auth.getCurrentUserId();

    // Store the current sort settings
    this.currentSortBy = sortBy;
    this.currentSortOrder = sortOrder;

    let params = new HttpParams()
      .set('userId', userId!)
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    this.http.get<Contact[]>(this.baseURL, { params }).subscribe({
      next: (data) => this.contacts.set(data),
      error: (err) => console.error(err),
    });

    console.log('Loaded contacts: ', this.contacts());
  }

  addContact(contactName: string, contactEmails: string[]) {
    // Call backend
    console.log('Added contact');
    const userId = this.auth.getCurrentUserId();

    const dto: Omit<Contact, 'id'> = {
      name: contactName,
      emails: contactEmails,
    };

    const params = new HttpParams().set('userId', userId!);

    this.http.post<Contact>(this.baseURL, dto, { params }).subscribe({
      next: (newContact) => {
        // Reload contacts with current sort settings instead of just appending
        this.loadContacts(this.currentSortBy, this.currentSortOrder);
      },
      error: (err) => console.error(err),
    });
  }

  editContact(contact: Contact, newName: string) {
    const userId = this.auth.getCurrentUserId();

    const dto: Omit<Contact, 'id'> = {
      name: newName,
      emails: contact.emails,
    };

    const params = new HttpParams().set('userId', userId!);

    this.http.put<Contact>(this.baseURL + `/${contact.id}`, dto, { params }).subscribe({
      next: (updatedContact) => {
        // Reload contacts with current sort settings to maintain sort order
        this.loadContacts(this.currentSortBy, this.currentSortOrder);
      },
      error: (err) => console.error(err),
    });
  }

  deleteContact(id: string) {
    this.http.delete(this.baseURL + `/${id}`).subscribe({
      next: () => {
        this.contacts.update((list) => list.filter((c) => c.id !== id));
        this.notificationService.showSuccess(`Successfully Deleted`);
      },
      error: (err) => console.error(err),
    });
    // this.backend.deleteContact(id).subscribe({
    //   next: () => {
    //     this.contacts.update(list => list.filter(c => c.id !== id));
    //   }
    // });
  }

  search(query: string): Observable<Contact[]> {
    console.log('Searching contacts...');
    const userId = this.auth.getCurrentUserId();

    let params = new HttpParams().set('userId', userId!.toString());
    params = params.set('searchTerm', query);

    return this.http.get<Contact[]>(`${this.baseURL}/search`, { params }).pipe(
      tap((data: Contact[]) => {
        console.log('API Response received, updating Signal.');
        this.contacts.set(data);
      }),

      catchError((err) => {
        console.error('Search failed:', err);

        return of([]);
      })
    );
  }
}
