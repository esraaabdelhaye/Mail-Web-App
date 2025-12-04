import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Search, RefreshCw, Trash2, FolderInput, ArrowUpDown, Flag, AlertCircle, ChevronLeft, ChevronRight, LayoutList, LayoutGrid } from 'lucide-angular';
import { ButtonComponent } from '../../shared/button/button'
import { Email, Folder } from '../../app/models/email.model';
import {EmailHandler} from '../../services/emails-handler/email-handler'; // Ensure this path matches yours

@Component({
  selector: 'app-email-list',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent],
  templateUrl: './email-list.html',
  styleUrls: ['./email-list.css']
})
export class EmailListComponent {


  protected emailHandler = inject(EmailHandler);


  // --- State ---
  searchQuery = '';
  sortBy: 'date' | 'sender' | 'importance' | 'subject' = 'date';
  viewMode: 'default' | 'priority' = 'default';
  itemsPerPage = 5;
  currentPage = 1;
  isRefreshing = false;
  selectedEmailId: string | null = null;

  // Set to track selected IDs
  selectedIds = new Set<string>();

  // --- Icons for Template ---
  readonly icons = { Search, RefreshCw, Trash2, FolderInput, ArrowUpDown, Flag, AlertCircle, ChevronLeft, ChevronRight, LayoutList, LayoutGrid };


  get processedEmails(): Email[] {
    const baseList = this.emailHandler.filteredEmails();

    // 1. Filter
    let result = baseList.filter(email => {
      const query = this.searchQuery.toLowerCase();
      return (
        email.subject.toLowerCase().includes(query) ||
        email.sender.name.toLowerCase().includes(query) ||
        email.sender.email.toLowerCase().includes(query) ||
        email.body.toLowerCase().includes(query)
      );
    });

    // 2. Sort
    result.sort((a, b) => {
      // Priority View Logic
      if (this.viewMode === 'priority') {
        if (a.priority !== b.priority) return a.priority - b.priority; // Lower number = Higher priority
      }

      // Standard Sort Logic
      switch (this.sortBy) {
        case 'date': return new Date(b.date).getTime() - new Date(a.date).getTime();
        case 'sender': return a.sender.name.localeCompare(b.sender.name);
        case 'importance': return a.priority - b.priority;
        case 'subject': return a.subject.localeCompare(b.subject);
        default: return 0;
      }
    });

    return result;
  }

  get paginatedEmails(): Email[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.processedEmails.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.processedEmails.length / this.itemsPerPage);
  }

  get customFolders(): Folder[] {
    return this.emailHandler.folders().filter(f => f.isCustom || f.id === 'trash');
  }

  // --- Actions ---

  handleRefresh() {
     this.emailHandler.onRefresh();
  }
  handleDelete() {
    this.emailHandler.deleteEmails(Array.from(this.selectedIds));
    this.selectedIds.clear();
  }

  toggleSelectAll(event: any) {
    if (event.target.checked) {
      this.paginatedEmails.forEach(e => this.selectedIds.add(e.id));
    } else {
      this.selectedIds.clear();
    }
  }

  toggleEmailSelection(id: string, event: any) {
    if (event.target.checked) {
      this.selectedIds.add(id);
    } else {
      this.selectedIds.delete(id);
    }
    event.stopPropagation(); // Prevent row click
  }

  handleMove(folderId: string) {
    // this.emailHandler.moveToFolder(Array.from(this.selectedIds), folderId )
    this.selectedIds.clear();
  }

  openEmailDetail(){}

  changePage(delta: number) {
    this.currentPage = Math.max(1, Math.min(this.totalPages, this.currentPage + delta));
  }

  // Helper for date formatting
  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    const now = new Date();
    const isToday = date.toDateString() === now.toDateString();

    if (isToday) return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
  }
}
