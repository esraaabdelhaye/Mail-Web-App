import {Component, inject, computed, OnInit, EventEmitter, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  Inbox,
  Send,
  FileText,
  Trash2,
  Plus,
  Pencil,
  X,
  Users,
  Folder,
  ChevronDown,
  Mail,
} from 'lucide-angular';
import { ButtonComponent } from '../../shared/button/button';
import { FolderDTO } from '../../app/models/FolderDTO';
import { EmailHandler } from '../../services/emails-handler/email-handler';
import { ComposeEmail } from '../compose-email/compose-email';
import { NotificationService } from '../../services/notification/notification-service';

@Component({
  selector: 'app-email-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent, ComposeEmail],
  templateUrl: './email-sidebar.html',
  styleUrls: ['./email-sidebar.css'],
})
export class EmailSidebarComponent implements OnInit {
  @Output() contactsClick = new EventEmitter<void>(); // Used to open the contacts-view from the main-page

  protected emailHandler = inject(EmailHandler);
  protected notificationService = inject(NotificationService);
  showComposeEmailDialog = false;

  ngOnInit() {
    this.emailHandler.loadFolders();
  }

  // --- State ---
  isAddingFolder = false;
  newFolderName = '';
  editingFolderId: string | null = null;
  editingFolderName = '';
  isFoldersOpen = true;
  showDraftSavedToast: boolean = false;
  userMessage = '';

  // --- Icons ---
  readonly icons = {
    Inbox,
    Send,
    FileText,
    Trash2,
    Plus,
    Pencil,
    X,
    Users,
    Folder,
    ChevronDown,
    Mail,
  };

  public readonly defaultFolders = computed(() => {
    return this.emailHandler.folders().filter((f) => !f.isCustom);
  });

  public readonly customFolders = computed(() => {
    return this.emailHandler.folders().filter((f) => f.isCustom);
  });

  // getFolderCount(folderId: string) {
  //   return this.emailHandler.folderCounts()[folderId] || 0;
  // }

  // Helper to map system IDs to Icons
  getIconForFolder(id: string): any {
    switch (id) {
      case 'inbox':
        return this.icons.Inbox;
      case 'sent':
        return this.icons.Send;
      case 'drafts':
        return this.icons.FileText;
      case 'trash':
        return this.icons.Trash2;
      default:
        return this.icons.Folder;
    }
  }

  // --- Actions ---
  handleAddFolder() {
    let name = this.newFolderName.trim();
    if (!name) this.notificationService.showError("Can't have an empty name!");
    else {
      this.emailHandler.addFolder(name);

      this.newFolderName = '';
      this.isAddingFolder = false;
    }
  }

  handleEditFolder(id: string, oldName: string) {
    let newName = this.editingFolderName.trim();
    if (newName == oldName) this.notificationService.showError('Same Name');
    else if (!newName) this.notificationService.showError("Can't have an empty name");
    else {
      this.emailHandler.editFolder(id, newName);
      this.editingFolderId = null;
      this.editingFolderName = '';
    }
  }

  openComposeEmailDialog() {
    this.showComposeEmailDialog = true;
  }

  closeComposeEmailDialog() {
    this.showComposeEmailDialog = false;
  }

  onDraftSaved() {
    // Here you can also call backend to save the draft
    this.showDraftSavedToast = true;

    setTimeout(() => {
      this.showDraftSavedToast = false;
    }, 2000);
  }

  startEditing(folder: FolderDTO, event: Event) {
    event.stopPropagation();
    this.editingFolderId = folder.folderID.toString();
    this.editingFolderName = folder.folderName;
  }
}
