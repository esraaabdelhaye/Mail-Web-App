import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, SlidersHorizontal, Calendar, X } from 'lucide-angular';
import {ButtonComponent} from '../../shared/button/button';
import {FolderDTO as Folder} from '../../app/models/FolderDTO';

export interface SearchOptions {
  from: string;
  to: string;
  subject: string;
  includesWords: string;
  doesntHave: string;
  sizeOperator: 'greater' | 'less';
  sizeValue: string;
  sizeUnit: 'MB' | 'KB' | 'bytes';
  dateWithin: string;
  dateValue: string;
  searchFolder: string;
  hasAttachment: boolean;
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
  // We can use a boolean input/output for open state, or just a method to open it.
  // Let's stick to the React pattern: internal state for open/close managed by a trigger button.

  isOpen = false;

  readonly icons = { SlidersHorizontal, Calendar, X };

  // Initial State
  readonly defaultOptions: SearchOptions = {
    from: '',
    to: '',
    subject: '',
    includesWords: '',
    doesntHave: '',
    sizeOperator: 'greater',
    sizeValue: '',
    sizeUnit: 'MB',
    dateWithin: '1 day',
    dateValue: '',
    searchFolder: 'all',
    hasAttachment: false,
  };

  // Current State
  options: SearchOptions = { ...this.defaultOptions };

  // --- Actions ---

  toggleOpen() {
    this.isOpen = !this.isOpen;
  }

  handleSearch() {
    this.search.emit(this.options);
    this.isOpen = false;
  }

  handleReset() {
    this.options = { ...this.defaultOptions };
  }
}
