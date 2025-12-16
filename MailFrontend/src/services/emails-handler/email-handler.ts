import { computed, inject, Injectable, signal, OnInit } from '@angular/core';
import { FolderDTO } from '../../app/models/FolderDTO';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmailPageDTO } from '../../app/models/EmailPageDTO';
import { PaginationRequest } from '../../app/models/PaginationRequest';
import { AuthService } from '../auth/auth-service';
import { NotificationService } from '../notification/notification-service';
import { MailDetailsDTO } from '../../app/models/DetailedMail';
import { SearchRequestDTO } from '../../app/models/SearchRequestDTO';
import { ComposeDraftDTO } from '../../app/models/ComposeDraftDTO';

@Injectable({
  providedIn: 'root',
})
export class EmailHandler {
  private folderSignal = signal<FolderDTO[]>([]);
  readonly folders = this.folderSignal.asReadonly();

  private folderCountsSignal = signal<Record<number, number>>({}); // {"folderId":"count"}
  readonly folderCounts = this.folderCountsSignal.asReadonly();

  private readonly apiUrl: string = 'http://localhost:8080';
  constructor(private http: HttpClient, private auth: AuthService) {}
  readonly currentFolderName = signal<string>('Inbox');
  // readonly currentFolderName = signal<string>()

  public opStatus = signal(false);
  public opMessage = signal('');
  public notificationService = inject(NotificationService);

  public isComposeOpen = signal(false);
  public composeDraft = signal<ComposeDraftDTO | null>(null);

  private emailListComp: any;

  fetchMail(){
    this.emailListComp.fetchMail();
  }

  getMailPage(request: PaginationRequest): Observable<EmailPageDTO> {
    // 1. Construct HttpParams from the request object
    let params = new HttpParams()
      .set('userId', request.userId)
      .set('folderName', request.folderName)
      .set('page', request.page.toString())
      .set('size', request.size.toString());

    if (request.sortBy) {
      params = params.set('sortBy', `${request.sortBy}`);
    }
    console.log(params);

    return this.http.get<EmailPageDTO>(`${this.apiUrl}/email/page`, { params });
  }

  getMailDetails(userId: number, mailId: number): Observable<MailDetailsDTO> {
    let params = new HttpParams().set('userId', userId).set('mailId', mailId);

    return this.http.get<MailDetailsDTO>(`${this.apiUrl}/email/getDetails`, { params });
  }

  getDraftForCompose(draftId: number): Observable<ComposeDraftDTO> {
    const userId = this.auth.getCurrentUserId();
    if (!userId) throw new Error("User not logged in");

    // let params = new HttpParams().set('userId', userId);

    return this.http.get<ComposeDraftDTO>(
      `${this.apiUrl}/email/draft/compose/${draftId}`
    );
  }

  openComposeWithDraft(draft: ComposeDraftDTO) {
  this.composeDraft.set(draft);
    this.isComposeOpen.set(true);
}


  doAdvancedSearch(request: SearchRequestDTO): Observable<EmailPageDTO> {
    let params = new HttpParams().set('userId', this.auth.getCurrentUserId()!);
    // .set('criteria', request);

    return this.http.post<EmailPageDTO>(`${this.apiUrl}/email/search/advanced`, request, {
      params,
    });
  }

  permanentlyDeleteEmails(mailIds: Number[], onSuccess?: () => void): void {
    const userId = this.auth.getCurrentUserId();

    let params = new HttpParams().set('userId', userId!);

    mailIds.forEach((id) => {
      params = params.append('mailId', id.toString());
    });

    this.http
      .delete<string>(`${this.apiUrl}/email/delete`, { params, responseType: 'text' as 'json' })
      .subscribe({
        next: (response) => {
          this.notificationService.show('Emails permanently deleted', 'success');
          this.loadFolderCounts();
          if (onSuccess) onSuccess();
        },
        error: (error) => {
          this.notificationService.show('Failed to permanently delete emails', 'error');
          console.error('Error deleting emails:', error);
        },
      });
  }
  moveEmailsToFolder(
    mailIds: Number[],
    targetFolderName: string,
    successMsg?: string,
    onSuccess?: () => void
  ): void {
    const userId = this.auth.getCurrentUserId();

    let params = new HttpParams().set('userId', userId!).set('targetFolder', targetFolderName);

    mailIds.forEach((id) => {
      params = params.append('mailId', id.toString());
    });

    this.http
      .put<string>(`${this.apiUrl}/email/move`, null, { params, responseType: 'text' as 'json' })
      .subscribe({
        next: (response) => {
          this.notificationService.show(successMsg || 'Emails moved successfully', 'success');
          if (successMsg) {
            this.opStatus.set(true);
            this.opMessage.set(successMsg);
          }
          this.loadFolderCounts();
          if (onSuccess) onSuccess();
        },
        error: (error) => {
          this.notificationService.show('Failed to move emails', 'error');
          this.opStatus.set(false);
          this.opMessage.set(error.message || 'Failed to move emails');
          console.error('Error moving emails:', error);
        },
      });
  }

  // readonly filteredEmails = computed(() => {
  //   const folderId = this.currentFolderId();
  //   return this.emails().filter((e) => e.folder === folderId);
  // });




  // Attachment actions
  uploadAttachment(mailId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('mailId', mailId.toString());
    formData.append('file', file);

    return this.http.post(`${this.apiUrl}/attachments/upload`, formData);
  }

  getAttachmentsByMail(mailId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/attachments/mail/${mailId}`);
  }

  downloadAttachment(attachmentId: number): void {
    window.open(`${this.apiUrl}/attachments/download/${attachmentId}`, '_blank');
  }

  deleteAttachment(attachmentId: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/attachments/${attachmentId}`,
      { responseType: 'text' as 'json' });
  }

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
        console.log('Read folders: ' + JSON.stringify(this.folderSignal(), null, 2));

        // Load counts after folders are loaded
        this.loadFolderCounts();
      },
      error: (err) => {
        console.log('Error in retrieving folders: ' + err);
      },
    });
  }

  public loadFolderCounts(): void {
    const userId = this.auth.getCurrentUserId();
    if (!userId) return;

    let params = new HttpParams().set('userId', userId);

    this.http.get<Record<number, number>>(`${this.apiUrl}/folders/counts`, { params }).subscribe({
      next: (counts) => {
        this.folderCountsSignal.set(counts);
        console.log('Folder counts:', counts);
      },
      error: (err) => {
        console.error('Error loading folder counts:', err);
      },
    });
  }

  regList(component: any) {
    this.emailListComp = component;
  }

  selectFolder(folderName: string) {
    this.currentFolderName.set(folderName);
    if (this.emailListComp != null) {
      this.fetchMail();
    }
    console.log('Load emails for:', folderName);
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

        this.folderSignal.update((currentList) => [...currentList, newFolder]);

        this.notificationService.showSuccess(`Folder "${folderName}" was added successfully`);
      },
      error: (err) => {
        console.error('Failed to create folder:', err);
        this.notificationService.showError(`Failed to add folder named "${folderName}"!`);
      },
    });
  }

  deleteFolder(folderId: string) {
    this.http.delete(`${this.apiUrl}/folders/${folderId}`).subscribe({
      next: () => {
        console.log('Deleted folder with id: ' + folderId);
        this.notificationService.showSuccess(`Successfully Deleted folder`);
        // Refresh the List (Remove it from the screen)
        this.loadFolders();

        // Take him back to the inbox
        this.currentFolderName.set('Inbox');
        this.emailListComp.fetchMail(); // Update the email list
      },

      error: (err) => {
        console.error('Failed to delete folder:', err);
        this.notificationService.showError('Failed to delete folder');
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
    const body = { folderName: newName };
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
        this.notificationService.showSuccess(`Folder successfully renamed to "${newName}"!`);
      },
      error: (err) => {
        console.error('Failed to rename folder:', err);
        this.opStatus.set(true);
        this.opMessage.set('Failed to Rename Folder');
        this.notificationService.showError(`Failed to Rename Folder to "${newName}"!`);
      },
    });
  }

  //   // markAsRead(id: string) {
  //   //   this.emails.update((list) => list.map((e) => (e.id === id ? { ...e, isRead: true } : e)));
  //   // }
}
