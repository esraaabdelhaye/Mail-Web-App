import {
  Component,
  Output,
  EventEmitter,
  OnInit,
  OnDestroy,
  ChangeDetectorRef,
} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from './../../services/auth/auth-service';
import { ButtonComponent } from '../../shared/button/button';
import { Observable, of } from 'rxjs';
import {EmailHandler} from '../../services/emails-handler/email-handler';

export interface EmailData {
  draftId: number | null;  // Optional draft ID (if sending from draft)
  senderId: number;
  to: string[];
  cc: string[];
  bcc: string[];
  subject: string;
  body: string;
  priority: number;
  attachments: File[];
}

export interface PriorityOption {
  value: string;
  label: string;
  color: string;
}

interface Recipient {
  id?: number; // the MailReceiver id assigned by backend
  email: string;
  type: 'to' | 'cc' | 'bcc';
}

@Component({
  selector: 'app-compose-email',
  templateUrl: './compose-email.html',
  styleUrls: ['./compose-email.css'],
  imports: [FormsModule, ButtonComponent],
  standalone: true
})
export class ComposeEmail implements OnInit, OnDestroy {
  @Output() close = new EventEmitter<void>();
  @Output() send = new EventEmitter<EmailData>();
  @Output() saveDraft = new EventEmitter<void>();

  draftId: number | null = null;
  senderId = 0;
  to: Recipient[] = [];
  toInput = '';
  cc: Recipient[] = [];
  ccInput = '';
  bcc: Recipient[] = [];
  bccInput = '';
  subject = '';
  body = '';
  priority = '2';
  attachments: File[] = [];
  attachmentIds: (number | null)[] = [];

  isMinimized = false;
  isCcOpen = false;
  isBccOpen = false;

  duplicateMessages: { text: string; id: number; type: 'success' | 'error' }[] = [];

  private duplicateMessageIdCounter = 0;

  private readonly apiUrl = 'http://localhost:8080';
  private autoSaveInterval: any;
  private lastAutoSavedState = '';
  showSavedToast = false;

  constructor(
    private cdr: ChangeDetectorRef,
    private http: HttpClient,
    private auth: AuthService,
    private emailHandler: EmailHandler,
  ) {}

  // Priority mapping
  priorityMap: Record<string, Omit<PriorityOption, 'value'>> = {
    '1': { label: 'Low', color: 'green' },
    '2': { label: 'Normal', color: 'black' },
    '3': { label: 'High', color: 'orange' },
    '4': { label: 'Urgent', color: 'red' },
  };

  get priorityLevels(): PriorityOption[] {
    return Object.keys(this.priorityMap).map((key) => ({
      value: key,
      label: this.priorityMap[key].label,
      color: this.priorityMap[key].color,
    }));
  }

  ngOnInit(): void {
    const senderId = this.auth.getCurrentUserId();

    if (!senderId) {
      console.error('No sender id');
      return;
    }

    if (this.draftId) {
      console.log('already created' + this.draftId);
      return;
    }

    console.log(this.draftId);

    this.senderId = senderId;

    this.http
      .post<{ draftId: number }>(`${this.apiUrl}/email/draft/create`, { senderId })
      .subscribe({
        next: (res) => {
          this.draftId = res.draftId;
          console.log(this.draftId);
        },
        error: (err) => console.error('Draft creation failed', err),
      });

    this.autoSaveInterval = setInterval(() => this.performAutoSave(), 3000);
  }

  ngOnDestroy(): void {
    clearInterval(this.autoSaveInterval);
  }

  performAutoSave() {
    if (!this.draftId) return;

    const currentState = JSON.stringify(this.draftAutoSaveData);
    if (currentState !== this.lastAutoSavedState) {
      this.lastAutoSavedState = currentState;
      this.http
        .post(`${this.apiUrl}/email/draft/save`, this.draftAutoSaveData, { responseType: 'text' })
        .subscribe({
          next: (res: string) => {
            console.log(res);
            this.showSavedToast = true;
            setTimeout(() => (this.showSavedToast = false), 1500);
          },
          error: (err) => console.error('Draft save error', err),
        });
    }
  }

  get draftAutoSaveData() {
    return {
      draftId: this.draftId,
      subject: this.subject,
      body: this.body,
      priority: parseInt(this.priority),
    };
  }

  // handleAddRecipient(
  //   event: KeyboardEvent,
  //   list: Recipient[],
  //   inputModel: 'toInput' | 'ccInput' | 'bccInput',
  //   type: 'to' | 'cc' | 'bcc'
  // ) {
  //   if (event.key === 'Enter' || event.key === ',') {
  //     event.preventDefault();
  //     const email = this[inputModel].trim().replace(',', '');

  //     console.log(this.isValidEmail(email));

  //     // Check if the email is already in the list
  //     if (this.isValidEmail(email) && !list.some((r) => r.email === email)) {

  //       // Create a Recipient object
  //       const recipient: Recipient = { email, type };

  //       // Push it to the list
  //       list.push(recipient);

  //       // Clear input
  //       this[inputModel] = '';

  //       // Call backend to save draft and get id
  //       this.addDraftRecipient(type, recipient);
  //     }
  //   }
  // }

  // Assuming your service method is now: isValidEmail(email: string): Observable<boolean>

  handleAddRecipient(
    event: KeyboardEvent,
    list: Recipient[],
    inputModel: 'toInput' | 'ccInput' | 'bccInput',
    type: 'to' | 'cc' | 'bcc'
  ) {
    if (event.key === 'Enter' || event.key === ',') {
      event.preventDefault();
      const email = this[inputModel].trim().replace(',', '');

      if (!email) {
        return; // Exit if the email is empty
      }

      // 1. Check if the email is already in the list (synchronous check)
      const isAlreadyInList = list.some((r) => r.email === email);

      if (isAlreadyInList) {
        console.log(`Email ${email} is already in the list.`);
        this[inputModel] = ''; // Clear input if it's a duplicate
        return;
      }

      // 2. Call the asynchronous validation service and SUBSCRIBE
      this.isValidEmail(email).subscribe({
        next: (isValid: boolean) => {
          // 3. All subsequent logic now executes ONLY when the boolean result arrives
          if (isValid) {
            console.log(`Validation successful for ${email}`);

            // Create a Recipient object
            const recipient: Recipient = { email, type };
            console.log('after recipient');

            // Push it to the list
            list.push(recipient);
            this.cdr.markForCheck();
            console.log('after push');

            // Clear input
            this[inputModel] = '';
            console.log('afetr input');

            // Call backend to save draft and get id
            this.addDraftRecipient(type, recipient);
            console.log('after backend');
          } else {
            console.log(`Validation failed: User not found for ${email}`);
            // Optional: Display an error message to the user
            // this.showValidationError = true;
          }
        },
        error: (err) => {
          console.error('Validation API failed:', err);
          // Handle network error (e.g., show a network error message)
        },
      });
    }
  }

  handleRemoveRecipient(recipient: Recipient) {
    if (!recipient.id) return;

    switch (recipient.type) {
      case 'to':
        this.to = this.to.filter((r) => r.id !== recipient.id);
        break;
      case 'cc':
        this.cc = this.cc.filter((r) => r.id !== recipient.id);
        break;
      case 'bcc':
        this.bcc = this.bcc.filter((r) => r.id !== recipient.id);
        break;
    }

    this.http.delete(`${this.apiUrl}/email/draft/recipient/${recipient.id}`).subscribe({
      next: () => {
        console.log('Recipient deleted on backend');
      },
      error: (err) => {
        // If 404, it was already deleted, ignore
        if (err.status === 404) {
          console.warn('Recipient was already deleted');
        } else {
          console.error('Failed to delete recipient:', err);
          // Optionally: revert frontend state if needed
        }
      },
    });
  }

  isValidEmail(email: string): Observable<boolean> {
    if (!email) {
      // Return an Observable of false immediately if input is empty
      return of(false);
    }

    // 3. The post method must be typed to expect a boolean response
    return this.http.post<boolean>(
      // Fix: Assuming your backend controller path is just "recipient/validate"
      `${this.apiUrl}/email/draft/recipient/validate`,
      email // Pass the email string as the request body
    );
  }

  addDraftRecipient(type: 'to' | 'cc' | 'bcc', recipient: Recipient) {
    if (!this.draftId) return;

    this.http
      .post<{ id: number }>(`${this.apiUrl}/email/draft/recipient/add`, {
        draftId: this.draftId,
        type,
        recipientEmail: recipient.email,
      })
      .subscribe((res) => {
        // Store the returned id in the recipient object
        recipient.id = res.id;
      });
  }

  // deleteDraftRecipient(type: 'to' | 'cc' | 'bcc', recipientId: number) {
  //   if (!this.draftId) return;
  //   this.http
  //     .post(`${this.apiUrl}/email/draft/recipient/delete`, { draftId: this.draftId, type, recipientId })
  //     .subscribe();
  // }

  // --- Attachments ---
  handleFileChange(event: Event) {
    if (!this.draftId) {
      this.addMessage('Please wait for draft to be created', "error");
      return;
    }
    const input = event.target as HTMLInputElement;
    if (!input.files) return;
    const newFiles = Array.from(input.files);

    const existingKeys = new Set(this.attachments.map((f) => `${f.name}-${f.size}`));
    const uniqueFiles = newFiles.filter((f) => {
      const key = `${f.name}-${f.size}`;
      if (existingKeys.has(key)) {
        this.addMessage(`File already attached: ${f.name}`, "error");
        return false;
      }
      existingKeys.add(key);
      return true;
    });

    // this.attachments.push(...uniqueFiles);
    uniqueFiles.forEach((f) => this.uploadAttachment(f));
    input.value = '';
  }

  handleRemoveAttachment(index: number) {
    if (!this.attachments[index]) return;

    const file = this.attachments[index];
    const attachmentId = this.attachmentIds[index];

    // If file has backend ID, delete from server
    if (attachmentId) {
      this.emailHandler.deleteAttachment(attachmentId).subscribe({
        next: () => {
          this.attachments.splice(index, 1);
          this.attachmentIds.splice(index, 1);
          console.log("deleted");
          
          this.addMessage(`${file.name} removed`, "success");
          this.cdr.detectChanges(); 
        },
        error: (err: any) => {
          console.error('Delete failed:', err);
          this.addMessage(`Failed to remove ${file.name}`, "error");
        },
      });
    } else {
      // Just remove from both arrays if not uploaded yet
      this.attachments.splice(index, 1);
      this.attachmentIds.splice(index, 1);
      this.addMessage(`${file.name} removed`, "success");
    }
  }

  uploadAttachment(file: File) {
    if (!this.draftId) {
      console.error('No draft ID for attachment upload');
      return;
    }

    // Add file to array first
    const fileIndex = this.attachments.length;
    this.attachments.push(file);
    this.attachmentIds.push(null); // No ID yet

    this.emailHandler.uploadAttachment(this.draftId, file).subscribe({
      next: (response: any) => {
        console.log('File uploaded successfully:', response);
        // Store backend attachment ID at the same index
        this.attachmentIds[fileIndex] = response.id;
        this.addMessage(`${file.name} uploaded successfully`, "success");
        this.cdr.detectChanges(); 
        
      },
      error: (err: any) => {
        console.error('Upload failed:', err);
        // Remove failed upload from both arrays
        this.attachments.splice(fileIndex, 1);
        this.attachmentIds.splice(fileIndex, 1);
        this.addMessage(`Failed to upload ${file.name}`, "error");
      },
    });
  }

  // --- Sending / Saving ---
  handleSend() {
    if (!this.auth.getCurrentUserId()) return;
    const userId = this.auth.getCurrentUserId();
    if (!userId) {
      console.error('No logged-in user');
      return;
    }
    this.senderId = userId;

    const data: EmailData = {
      draftId: this.draftId,  // Include draft ID so backend uses existing draft
      senderId: this.senderId,
      to: this.to.map((r) => r.email),
      cc: this.cc.map((r) => r.email),
      bcc: this.bcc.map((r) => r.email),
      subject: this.subject,
      body: this.body,
      priority: parseInt(this.priority),
      attachments: [], // Attachments already uploaded to backend
    };

    const form = new FormData();
    form.append('email', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    // Don't append files - they're already uploaded and linked to the draft

    this.http
      .post(`${this.apiUrl}/email/send`, form, { responseType: 'text' as 'json' })
      .subscribe({
        next: (res) => console.log('sent', res),
        error: (err) => console.error(err),
      });

    this.resetForm();
  }

  handleSaveDraft() {
    this.saveDraft.emit();
    this.resetForm();
  }

  resetForm() {
    this.to = [];
    this.cc = [];
    this.bcc = [];
    this.toInput = '';
    this.ccInput = '';
    this.bccInput = '';
    this.subject = '';
    this.body = '';
    this.priority = '3';
    this.attachments = [];
    this.attachmentIds = [];
    this.onclose();
  }

  // --- Utilities ---
  addMessage(text: string, type: 'success' | 'error' = 'error') {
  const id = this.duplicateMessageIdCounter++;
  this.duplicateMessages.push({ text, id, type });
  this.cdr.detectChanges();

  setTimeout(() => {
    this.duplicateMessages = [];
    this.cdr.detectChanges();
  }, 3000);
}


  formatFileSize(bytes: number) {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  getPriorityOption(value: string): PriorityOption {
    const details = this.priorityMap[value];
    return { value, label: details.label, color: details.color };
  }

  onclose() {
    this.close.emit();
  }

  onOverlayClick(event: MouseEvent) {
    if (event.target === event.currentTarget) this.onclose();
  }
}
