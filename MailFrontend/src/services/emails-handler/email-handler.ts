import { computed, inject, Injectable, signal } from '@angular/core';
import { Email, Folder } from '../../app/models/email.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmailPageDTO } from '../../app/models/EmailPageDTO';
import { PaginationRequest } from '../../app/models/PaginationRequest';

@Injectable({
  providedIn: 'root',
})
export class EmailHandler {
  private folders = [
    { id: 'inbox', name: 'Inbox', isCustom: false },
    { id: 'sent', name: 'Sent', isCustom: false },
    { id: 'trash', name: 'Trash', isCustom: false },
    { id: 'draft', name: 'Draft', isCustom: false },
    { id: 'project', name: 'CSE 223 Project', isCustom: true },
  ];

  private readonly apiUrl: string = 'http://localhost:8080';
  constructor(private http: HttpClient) {}
  readonly currentFolderId = signal<string>('inbox');

  getMailPage(request: PaginationRequest): Observable<EmailPageDTO> {
    // 1. Construct HttpParams from the request object
    let params = new HttpParams()
      .set('userId', request.userId)
      .set('folderName', request.folderName)
      .set('page', request.page.toString())
      .set('size', request.size.toString());

    if (request.sortBy && request.sortDirection) {
      params = params.set('sort', `${request.sortBy},${request.sortDirection}`);
    }

    return this.http.get<EmailPageDTO>(`${this.apiUrl}/email/`, { params });
  }

  // readonly filteredEmails = computed(() => {
  //   const folderId = this.currentFolderId();
  //   return this.emails().filter((e) => e.folder === folderId);
  // });

  // readonly folderCounts = computed(() => {
  //   const counts: Record<string, number> = {};
  //   const allEmails = this.emails();

  //   // Inbox counts unread, others count total
  //   this.folders.forEach((f) => {
  //     if (f.id === 'inbox') {
  //       counts[f.id] = allEmails.filter((e) => e.folder === 'inbox' && !e.isRead).length;
  //     } else {
  //       counts[f.id] = allEmails.filter((e) => e.folder === f.id).length;
  //     }
  //   });

  //   return counts;
  // });

  //   onRefresh() {
  //     console.log('Refreshing...');
  //   }

  //   openEmail(email: Email) {
  //     this.openedEmail.set(email);
  //     this.markAsRead(email.id);
  //   }

  //   closeEmail() {
  //     this.openedEmail.set(null);
  //   }

  //   moveToFolder() {}

  //   deleteEmails(emailIds: string[]) {
  //     // This should be used to delete the emails from the backend
  //   }

  //   composeEmail() {
  //     console.log('Open compose modal');
  //   }

  //   // Folder actions

  selectFolder(folderId: string) {
    this.currentFolderId.set(folderId);
    console.log('Load emails for:', folderId);
    // In real app: this.emailService.loadEmails(folderId);
  }

  addFolder(folderName: string) {}

  deleteFolder(folderId: string) {}

  editFolder() {}

  contactsClick() {}

  //   // markAsRead(id: string) {
  //   //   this.emails.update((list) => list.map((e) => (e.id === id ? { ...e, isRead: true } : e)));
  //   // }
}
