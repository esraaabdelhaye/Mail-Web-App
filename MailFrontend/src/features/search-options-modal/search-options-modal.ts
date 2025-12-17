import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, SlidersHorizontal, Calendar, X } from 'lucide-angular';
import { ButtonComponent } from '../../shared/button/button';
import { FolderDTO as Folder } from '../../app/models/FolderDTO';
import { SearchRequestDTO as SearchOptions } from '../../app/models/SearchRequestDTO';
import { EmailHandler } from '../../services/emails-handler/email-handler';

// Matches Backend SearchCriteriaDTO exactly

@Component({
  selector: 'app-search-options-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent],
  templateUrl: './search-options-modal.html',
  styleUrls: ['./search-options-modal.css'],
})
export class SearchOptionsModalComponent {
  @Input() folders: Folder[] = [];
  @Output() search = new EventEmitter<SearchOptions>();

  isOpen = false;
  readonly icons = { SlidersHorizontal, Calendar, X };
  private emailHandler = inject(EmailHandler);

  // Initial State (Matches DTO)
  // Empty strings for optional fields mean "don't filter by this criteria"
  readonly defaultOptions: SearchOptions = {
    query: '',
    from: '',
    to: '',
    subject: '',
    body: '',
    hasAttachment: false,
    folder: '',
    priority: '',  // Empty string means search all priority levels
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
    // Create a copy of the search options to modify for submission
    const payload = { ...this.options };
    
    // Convert 'all' folder selection to empty string (search across all folders)
    if (payload.folder === 'all') payload.folder = '';
    
    // Convert empty/falsy priority to null so backend doesn't filter by priority
    // This handles empty string (""), undefined, or any falsy value
    if (!payload.priority) payload.priority = null;
    
    // Emit the search request to parent component
    this.search.emit(payload);

    // Don't call selectFolder for advanced search
    // The search results will be displayed without changing folder navigation
    
    // Reset the form to default values after search
    this.handleReset();
    
    // Close the modal
    this.isOpen = false;
  }

  handleReset() {
    this.options = { ...this.defaultOptions };
  }
}
