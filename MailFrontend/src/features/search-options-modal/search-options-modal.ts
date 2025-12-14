import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, SlidersHorizontal, Calendar, X } from 'lucide-angular';
import {ButtonComponent} from '../../shared/button/button';
import {FolderDTO as Folder} from '../../app/models/FolderDTO';


// Matches Backend SearchCriteriaDTO exactly
export interface SearchOptions {
  from: string;
  to: string;
  subject: string;
  body: string;           // Maps to 'Includes words'
  hasAttachment: boolean;
  folder: string;
  sentAt: string;         // Maps to Date input
  priority: string;       // Maps to Priority dropdown
  attachmentName: string; // New field
}

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

  // Initial State (Matches DTO)
  readonly defaultOptions: SearchOptions = {
    from: '',
    to: '',
    subject: '',
    body: '',
    hasAttachment: false,
    folder: 'all',
    sentAt: '',
    priority: '',
    attachmentName: ''
  };

  // Current State
  options: SearchOptions = { ...this.defaultOptions };

  toggleOpen() {
    this.isOpen = !this.isOpen;
  }

  handleSearch() {
    // Filter out empty 'all' folder if backend handles it differently
    const payload = { ...this.options };
    if (payload.folder === 'all') payload.folder = ''; // Or keep 'all' if backend handles it

    this.search.emit(payload);
    this.isOpen = false;
  }

  handleReset() {
    this.options = { ...this.defaultOptions };
  }
}
