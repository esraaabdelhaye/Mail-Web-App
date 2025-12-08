import { Component, Input, Output, EventEmitter, OnInit, ChangeDetectorRef } from '@angular/core';
// import { MatIcon } from '@angular/material/icon';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { ButtonComponent } from '../../shared/button/button';

export interface EmailData {
  to: string[];
  cc: string[];
  bcc: string[];
  subject: string;
  body: string;
  priority: number;
  // Note: Angular typically handles file uploads through a service,
  // but we'll keep the File[] structure for consistency.
  attachments: File[];
}

export interface PriorityOption {
  value: string;
  label: string;
  color: string; // The color associated with the priority
}

@Component({
  selector: 'app-compose-email',
  templateUrl: './compose-email.html',
  styleUrls: ['./compose-email.css'], // Placeholder for styling
  imports: [FormsModule, ButtonComponent],
})
export class ComposeEmail {
  // --- Inputs and Outputs (If used as a nested component) ---
  // If using Angular Material Dialog, inputs/outputs are often passed
  // via MatDialog data/result, but we'll include them for component parity.

  @Output() close = new EventEmitter<void>();
  @Output() send = new EventEmitter<EmailData>();
  @Output() saveDraft = new EventEmitter<void>();

  // --- Component State ---
  to: string[] = [];
  toInput: string = '';
  cc: string[] = [];
  ccInput: string = '';
  bcc: string[] = [];
  bccInput: string = '';
  subject: string = '';
  body: string = '';
  priority: string = '3'; // Stored as string, parsed to number on send/save
  attachments: File[] = [];
  isMinimized: boolean = false;
  isCcOpen: boolean = false;
  isBccOpen: boolean = false;
  duplicateMessages: { text: string; id: number }[] = [];
  private duplicateMessageIdCounter = 0;
  autoSaveInterval: any;
  lastAutoSavedState: string = '';
  showSavedToast = false;

  constructor(private cdr: ChangeDetectorRef) {}

  // Map the priority values (1-4) to their details
  priorityMap: Record<string, Omit<PriorityOption, 'value'>> = {
    '1': { label: 'Urgent', color: 'var(--color-priority-urgent, red)' },
    '2': { label: 'High', color: 'var(--color-priority-high, orange)' },
    '3': { label: 'Normal', color: 'var(--color-priority-normal, black)' },
    '4': { label: 'Low', color: 'var(--color-priority-low, green)' },
  };

  getPriorityOption(value: string): PriorityOption {
    const details = this.priorityMap[value];
    return {
      value,
      label: details.label,
      color: details.color,
    };
  }

  // A list to use for the @for loop in the template
  priorityLevels: PriorityOption[] = Object.keys(this.priorityMap).map((key) =>
    this.getPriorityOption(key)
  );

  ngOnInit(): void {
    // Auto save every 3 seconds
    this.autoSaveInterval = setInterval(() => {
      this.performAutoSave();
    }, 3000);
  }

  ngOnDestroy(): void {
    if (this.autoSaveInterval) {
      clearInterval(this.autoSaveInterval);
    }
  }

  performAutoSave() {
    const currentState = JSON.stringify(this.emailData);

    // Only save if something changed
    if (currentState !== this.lastAutoSavedState) {
      console.log("saving..");
      this.lastAutoSavedState = currentState;
      this.saveDraft.emit();
    }
  }

  // showDraftSavedToast() {
  //   this.showSavedToast = true;
  //   this.cdr.detectChanges();
  //   setTimeout(() => {
  //   this.showSavedToast = false;
  //   this.cdr.detectChanges();
  // }, 2000);
  // }

  // --- Handlers ---

  handleAddRecipient(
    event: KeyboardEvent,
    list: string[],
    inputModel: 'toInput' | 'ccInput' | 'bccInput'
  ) {
    if (event.key === 'Enter' || event.key === ',') {
      event.preventDefault();
      const email = this[inputModel].trim().replace(',', '');
      // if (email && !this.to.includes(email)) {
      //   this.to.push(email);
      //   this.toInput = '';
      // }
      if (this.isValidEmail(email) && !list.includes(email)) {
        list.push(email);
        this[inputModel] = ''; // Return empty string to clear the input
      }
    }
  }

  isValidEmail(email: string): boolean {
    if (!email) return false;
    // Basic Regex for client-side check
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  handleRemoveRecipient(email: string, listType: 'to' | 'cc' | 'bcc'): void {
    // Use a switch statement to select the correct array based on the listType
    switch (listType) {
      case 'to':
        this.to = this.to.filter((e) => e !== email);
        break;
      case 'cc':
        this.cc = this.cc.filter((e) => e !== email);
        break;
      case 'bcc':
        this.bcc = this.bcc.filter((e) => e !== email);
        break;
      default:
        console.error('Invalid recipient list type provided.');
    }
  }

  handleFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const newFiles = input.files ? Array.from(input.files) : [];

    if (newFiles.length > 0) {
      // 1. Create a Set of unique identifiers (name + size) for existing attachments
      const existingFileKeys = new Set(this.attachments.map((file) => `${file.name}-${file.size}`));

      // 2. Filter the new files to include only those not already in the Set
      const uniqueNewFiles = newFiles.filter((file) => {
        const key = `${file.name}-${file.size}`;

        // Check if the key already exists
        if (existingFileKeys.has(key)) {
          // Log a message or show a notification for the user
          this.addMessage(`File already attached and skipped: ${file.name}`);
          return false; // Skip this file (it's a duplicate)
        }
        // this.duplicateMessage = '';

        // Add the key to the Set and keep the file
        existingFileKeys.add(key); // Important: In case the user selects the same file multiple times in one go
        return true; // Keep this file (it's unique)
      });

      // 3. Update the attachments array with the unique new files
      this.attachments = [...this.attachments, ...uniqueNewFiles];
    }
    // Reset file input value to allow selecting the same file again
    input.value = '';
  }

  addMessage(text: string) {
    const id = this.duplicateMessageIdCounter++;
    this.duplicateMessages.push({ text, id });

    // Remove message after 5 seconds
    setTimeout(() => {
      this.duplicateMessages = [];
      this.cdr.detectChanges();
    }, 3000);
  }

  handleRemoveAttachment(index: number) {
    this.attachments.splice(index, 1);
  }

  get emailData(): EmailData {
    return {
      to: this.to,
      cc: this.cc,
      bcc: this.bcc,
      subject: this.subject,
      body: this.body,
      priority: parseInt(this.priority),
      attachments: this.attachments,
    };
  }

  handleSend() {
    this.send.emit(this.emailData);
    this.resetForm();
  }

  handleSaveDraft() {
    this.saveDraft.emit();
    // this.showDraftSavedToast();
    this.resetForm();
  }

  resetForm() {
    this.to = [];
    this.toInput = '';
    this.cc = [];
    this.ccInput = '';
    this.bcc = [];
    this.bccInput = '';
    this.subject = '';
    this.body = '';
    this.priority = '3';
    this.attachments = [];
    this.onclose();

    // Close the dialog/component
  }

  // --- Utility Functions ---

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  getPriorityLabel(p: string): string {
    switch (p) {
      case '1':
        return 'Highest';
      case '2':
        return 'High';
      case '3':
        return 'Normal';
      case '4':
        return 'Low';
      default:
        return 'Normal';
    }
  }

  onclose(): void {
    this.close.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onclose();
    }
  }
}
