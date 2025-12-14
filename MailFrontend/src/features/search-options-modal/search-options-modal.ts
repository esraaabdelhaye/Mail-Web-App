import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, SlidersHorizontal, Calendar, X } from 'lucide-angular';
import {ButtonComponent} from '../../shared/button/button';
import {FolderDTO as Folder} from '../../app/models/FolderDTO';
import {SearchRequestDTO as SearchOptions} from '../../app/models/SearchRequestDTO';
import {EmailHandler} from '../../services/emails-handler/email-handler';

// Matches Backend SearchCriteriaDTO exactly


@Component({
  selector: 'app-search-options-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent],
  templateUrl: './search-options-modal.html',
  styleUrls: ['./search-options-modal.css']
})
export class SearchOptionsModalComponent {
  @Input() folders: Folder[] = [];
  @Output() search = new EventEmitter<SearchOptions>();

  isOpen = false;
  readonly icons = { SlidersHorizontal, Calendar, X };
  private emailHandler = inject(EmailHandler);

  // Initial State (Matches DTO)
  readonly defaultOptions: SearchOptions = {
    query: '',
    from: '',
    to: '',
    subject: '',
    body: '',
    hasAttachment: false,
    folder: 'all',
    priority: '',
    isRead: false,
    endDate: '',
    startDate: '',
  };

  // Current State
  options: SearchOptions = { ...this.defaultOptions };

  toggleOpen() {
    this.isOpen = !this.isOpen;
  }

  handleSearch() {

    const payload = { ...this.options };
    if (payload.folder === 'all') payload.folder = null;
    if (!payload.priority) payload.priority = null;
    this.search.emit(payload);

    if (payload.folder != null){
      this.emailHandler.selectFolder(payload.folder);
    }

    this.isOpen = false;
  }

  handleReset() {
    this.options = { ...this.defaultOptions };
  }
}
