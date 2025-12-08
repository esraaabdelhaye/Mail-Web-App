import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
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
import { Folder as FolderModel } from '../../app/models/email.model';
import { EmailHandler } from '../../services/emails-handler/email-handler';
import { ComposeEmail, EmailData } from '../compose-email/compose-email';

@Component({
  selector: 'app-email-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent, ComposeEmail],
  templateUrl: './email-sidebar.html',
  styleUrls: ['./email-sidebar.css'],
})
export class EmailSidebarComponent {
  protected emailHandler = inject(EmailHandler);

  showComposeEmailDialog = false;

  // --- State ---
  isAddingFolder = false;
  newFolderName = '';
  editingFolderId: string | null = null;
  editingFolderName = '';
  isFoldersOpen = true;
  showDraftSavedToast: boolean = false;

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

  // default folders are the system default folders (not custom)
  get defaultFolders(): FolderModel[] {
    return this.emailHandler.folders().filter((f) => !f.isCustom);
  }

  // Custom folders are the non default folders (user created, i.e. custom)
  get customFolders(): FolderModel[] {
    return this.emailHandler.folders().filter((f) => f.isCustom);
  }

  getFolderCount(folderId: string) {
    return this.emailHandler.folderCounts()[folderId] || 0;
  }

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
    if (this.newFolderName.trim()) {
      this.emailHandler.addFolder(this.newFolderName.trim());
      this.newFolderName = '';
      this.isAddingFolder = false;
    }
  }

  handleEditFolder(id: string) {
    if (this.editingFolderName.trim()) {
      // this.emailHandler.editFolder.emit({ id, name: this.editingFolderName.trim() });
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

  startEditing(folder: FolderModel, event: Event) {
    event.stopPropagation();
    this.editingFolderId = folder.id;
    this.editingFolderName = folder.name;
  }
}
