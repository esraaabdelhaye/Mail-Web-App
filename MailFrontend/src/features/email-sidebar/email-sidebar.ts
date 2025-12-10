import {
  Component,
  EventEmitter,
  inject,
  Input,
  Output,
  signal,
  computed,
  OnInit,
} from '@angular/core';
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
import { ComposeEmail, EmailData } from '../compose-email/compose-email';

@Component({
  selector: 'app-email-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent, ComposeEmail],
  templateUrl: './email-sidebar.html',
  styleUrls: ['./email-sidebar.css'],
})
export class EmailSidebarComponent implements OnInit {
  protected emailHandler = inject(EmailHandler);

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

  startEditing(folder: FolderDTO, event: Event) {
    event.stopPropagation();
    this.editingFolderId = folder.folderID.toString();
    this.editingFolderName = folder.folderName;
  }
}
