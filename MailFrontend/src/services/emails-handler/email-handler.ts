import { computed, inject, Injectable, signal, OnInit } from '@angular/core';
import { FolderDTO } from '../../app/models/FolderDTO';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmailPageDTO } from '../../app/models/EmailPageDTO';
import { PaginationRequest } from '../../app/models/PaginationRequest';

import { AuthService } from '../auth/auth-service';

@Injectable({
  providedIn: 'root',
})
export class EmailHandler {
  private folderSignal = signal<FolderDTO[]>([]);
  readonly folders = this.folderSignal.asReadonly();

  private readonly apiUrl: string = 'http://localhost:8080';
  constructor(private http: HttpClient, private auth: AuthService) {}
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

  // --------------------- Folder actions ----------------------

  public loadFolders(): void {
    const userId = this.auth.getCurrentUserId();
    if (!userId) {
      console.error('User not logged in!');
      return;
    }

    let params = new HttpParams().set('userId', userId);

    this.http.get<FolderDTO[]>(`${this.apiUrl}/folders`, { params }).subscribe({
      next: (data) => {
        const mappedFolders: FolderDTO[] = data.map((dto) => ({
          folderID: dto.folderID,
          folderName: dto.folderName,
          isCustom: dto.isCustom,
        }));

        this.folderSignal.set(mappedFolders as FolderDTO[]);
        console.log('here');

        console.log('Read folders: ' + JSON.stringify(this.folderSignal(), null, 2));
      },
      error: (err) => {
        console.log('Error in retrieving folders: ' + err);
      },
    });
  }

  selectFolder(folderId: string) {
    this.currentFolderId.set(folderId);
    console.log('Load emails for:', folderId);
    // In real app: this.emailService.loadEmails(folderId);
  }

  addFolder(folderName: string) {
    const userId = this.auth.getCurrentUserId();

    // 1. Guard: Check if user is logged in
    if (!userId) {
      console.error('Cannot create folder: User not logged in');
      return;
    }

    console.log(`Creating folder: ${folderName}...`);

    // 2. Prepare Request Data
    // Query Param: ?userId=1
    const params = new HttpParams().set('userId', userId.toString());

    // Body: { "folderName": "New Folder" }
    // (Ensure key matches backend DTO field 'folderName')
    const body = { folderName: folderName };

    // 3. Make the HTTP Call directly
    this.http.post<FolderDTO>(`${this.apiUrl}/folders`, body, { params }).subscribe({
      next: (newFolder) => {
        console.log('Folder created successfully:', newFolder);

        // 4. Update the Signal (Optimistic UI Update)
        // We append the new folder to the existing list
        this.folderSignal.update((currentList) => [...currentList, newFolder]);
      },
      error: (err) => {
        console.error('Failed to create folder:', err);
        // Bonus: You could show a toast notification here
      },
    });
  }

  deleteFolder(folderId: string) {
    this.http.delete(`${this.apiUrl}/folders/${folderId}`).subscribe({
      next: () => {
        console.log('Deleted folder with id: ' + folderId);

        // Refresh the List (Remove it from the screen)
        this.loadFolders();
      },

      error: (err) => {
        console.error('Failed to delete folder:', err);
      },
    });
  }

  editFolder(folderId: string, newName: string) {
    // 1. Guard: Check if user is logged in
    const userId = this.auth.getCurrentUserId();
    if (!userId) {
      console.error('Cannot edit folder: User not logged in');
      return;
    }

    console.log(`Renaming folder ${folderId} to ${newName}...`);

    // 2. Prepare Request Data
    // NOTE: Your backend expects the new name in the BODY inside a DTO
    // DTO field name depends on your backend (likely 'name' or 'folderName')
    // Based on previous chats, your DTO uses 'name'.
    const body = { folderName: newName };

    // 3. Make the HTTP Call
    // URL: PUT /api/folders/{id}
    this.http.put<void>(`${this.apiUrl}/folders/${folderId}`, body).subscribe({
      next: () => {
        console.log('Folder renamed successfully');

        // 4. Update the Signal (Optimistic UI Update)
        // We map over the current list: if ID matches, update name. If not, keep as is.
        this.folderSignal.update((currentList) =>
          currentList.map((f) =>
            f.folderID.toString() === folderId ? { ...f, folderName: newName } : f
          )
        );
      },
      error: (err) => {
        console.error('Failed to rename folder:', err);
        // Bonus: Revert logic or show error message
      },
    });
  }

  contactsClick() {}

  //   // markAsRead(id: string) {
  //   //   this.emails.update((list) => list.map((e) => (e.id === id ? { ...e, isRead: true } : e)));
  //   // }
}
