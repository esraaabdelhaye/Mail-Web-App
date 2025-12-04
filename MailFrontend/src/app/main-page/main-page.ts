import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmailSidebarComponent } from '../../features/email-sidebar/email-sidebar';
import { EmailListComponent } from '../../features/email-list/email-list';
import { Email, Folder } from '../models/email.model';
import {BackendController} from '../../services/backend-controller';
import {inject} from '@angular/core';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [CommonModule, EmailSidebarComponent, EmailListComponent],
  templateUrl: './main-page.html',
  styleUrls: ['./main-page.css']
})
export class MainPage {

  private backendController: BackendController = inject(BackendController);

  currentFolderId = 'inbox';

  // Mock Folders
  folders: Folder[] = this.backendController.getFolders();

  // Mock Counts
  counts = { 'inbox': 2, 'trash': 5 };

  // Mock Emails
  filteredEmails: Email[] = this.backendController.getEmails();

  // --- Actions ---
  onFolderSelected(folderId: string) {
    this.currentFolderId = folderId;
    console.log('Load emails for:', folderId);
    // In real app: this.emailService.loadEmails(folderId);
  }

  onCompose() {
    console.log('Open compose modal');
  }

  onRefresh() {
    console.log('Refreshing...');
  }
}
