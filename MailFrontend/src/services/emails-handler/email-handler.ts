import {computed, inject, Injectable, signal} from '@angular/core';
import {BackendController} from '../backend-controller/backend-controller';
import {Email, Folder} from '../../app/models/email.model';

@Injectable({
  providedIn: 'root',
})
export class EmailHandler {
  private backendController: BackendController = inject(BackendController);

  readonly currentFolderId = signal<string>('inbox');
  readonly emails = signal<Email[]>(this.backendController.getEmails())

  // Mock Folders
  readonly folders = signal<Folder[]>(this.backendController.getFolders());


  // Mock Emails
  readonly filteredEmails = computed(() => {
    const folderId = this.currentFolderId();
    return this.emails().filter(e => e.folder === folderId);
  })

  // --- Actions ---

  readonly counts = computed(() => {
    const counts: Record<string, number> = {};
    const allEmails = this.emails();

    // Inbox counts unread, others count total
    this.folders().forEach(f => {
      if (f.id === 'inbox'){
        counts[f.id] = allEmails.filter(e => e.folder === 'inbox' && !e.isRead).length;
      }
      else{
        counts[f.id] = allEmails.filter(e => e.folder === f.id).length
      }
    })

    return counts;
  })

  // Email-list actions

  onRefresh() {
    console.log('Refreshing...');
  }

  onEmailSelect(){}

  moveToFolder(){}

  onDeleteEmails(event: string[]){
    // This should be used to delete the emails from the backend
  }

  // Sidebar actions

  onFolderSelected(folderId: string) {
    this.currentFolderId.set(folderId);
    console.log('Load emails for:', folderId);
    // In real app: this.emailService.loadEmails(folderId);
  }

  onCompose() {
    console.log('Open compose modal');
  }

  addFolder(){}

  deleteFolder(){}

  editFolder(){}

  contactsClick(){}
}
