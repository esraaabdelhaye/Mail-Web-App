import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  Inbox, Send, FileText, Trash2, Plus, Pencil, X, Users, Folder, ChevronDown, Mail
} from 'lucide-angular';
import {ButtonComponent} from '../../shared/button/button';
import {Folder as FolderModel} from '../../app/models/email.model'

@Component({
  selector: 'app-email-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent],
  templateUrl: './email-sidebar.html',
  styleUrls: ['./email-sidebar.css']
})
export class EmailSidebarComponent {
  // --- Inputs (Props) ---
  @Input() folders: FolderModel[] = [];
  @Input() activeFolder = 'inbox';
  @Input() emailCounts: Record<string, number> = {};

  // --- Outputs (Callbacks) ---
  @Output() folderSelect = new EventEmitter<string>();
  @Output() composeClick = new EventEmitter<void>();
  @Output() contactsClick = new EventEmitter<void>();
  @Output() addFolder = new EventEmitter<string>();
  @Output() editFolder = new EventEmitter<{id: string, name: string}>();
  @Output() deleteFolder = new EventEmitter<string>();

  // --- State (useState) ---
  isAddingFolder = false;
  newFolderName = '';
  editingFolderId: string | null = null;
  editingFolderName = '';
  isFoldersOpen = true;

  // --- Icons ---
  readonly icons = { Inbox, Send, FileText, Trash2, Plus, Pencil, X, Users, Folder, ChevronDown, Mail };

  // --- Computed (Getters) ---
  get defaultFolders(): FolderModel[] {
    return this.folders.filter(f => !f.isCustom);
  }

  get customFolders(): FolderModel[] {
    return this.folders.filter(f => f.isCustom);
  }

  // Helper to map system IDs to Icons
  getIconForFolder(id: string): any {
    switch (id) {
      case 'inbox': return this.icons.Inbox;
      case 'sent': return this.icons.Send;
      case 'drafts': return this.icons.FileText;
      case 'trash': return this.icons.Trash2;
      default: return this.icons.Folder;
    }
  }

  // --- Actions ---
  handleAddFolder() {
    if (this.newFolderName.trim()) {
      this.addFolder.emit(this.newFolderName.trim());
      this.newFolderName = '';
      this.isAddingFolder = false;
    }
  }

  handleEditFolder(id: string) {
    if (this.editingFolderName.trim()) {
      this.editFolder.emit({ id, name: this.editingFolderName.trim() });
      this.editingFolderId = null;
      this.editingFolderName = '';
    }
  }

  startEditing(folder: FolderModel, event: Event) {
    event.stopPropagation();
    this.editingFolderId = folder.id;
    this.editingFolderName = folder.name;
  }
}
