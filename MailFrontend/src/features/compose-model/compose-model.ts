import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, X, Paperclip, Send, Save, Minus } from 'lucide-angular';
import {EmailHandler} from '../../services/emails-handler/email-handler';
import {ButtonComponent} from '../../shared/button/button';


@Component({
  selector: 'app-compose-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule, ButtonComponent],
  templateUrl: './compose-model.html',
  styleUrls: ['./compose-model.css']
})
export class ComposeModalComponent {
  // Inputs/Outputs
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();

  // Injections
  private emailHandler = inject(EmailHandler);

  // State
  to: string[] = [];
  toInput = '';
  subject = '';
  body = '';
  priority = '3'; // Default Normal
  attachments: File[] = [];
  isMinimized = false;

  readonly icons = { X, Paperclip, Send, Save, Minus };

  // --- Actions ---

  handleAddRecipient(event: Event) {
    const keyboardEvent = event as KeyboardEvent;
    if (keyboardEvent.key === 'Enter' || keyboardEvent.key === ',') {
      keyboardEvent.preventDefault();
      const email = this.toInput.trim().replace(',', '');

      if (email && !this.to.includes(email)) {
        this.to.push(email);
        this.toInput = '';
      }
    }
  }

  removeRecipient(email: string) {
    this.to = this.to.filter(e => e !== email);
  }

  handleFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.attachments.push(...Array.from(input.files));
    }
  }

  removeAttachment(index: number) {
    this.attachments.splice(index, 1);
  }

  handleSend() {
    // Construct the payload
    const emailData = {
      to: this.to,
      subject: this.subject,
      body: this.body,
      priority: parseInt(this.priority),
      attachments: this.attachments
    };

    console.log("Sending Email:", emailData);

    // Call Service (You will need to implement sendEmail in EmailHandler)
    // this.emailHandler.sendEmail(emailData);

    this.closeModal();
  }

  handleSaveDraft() {
    console.log("Saving Draft...");
    // this.emailHandler.saveDraft(...);
    this.closeModal();
  }

  closeModal() {
    this.resetForm();
    this.close.emit();
  }

  resetForm() {
    this.to = [];
    this.toInput = '';
    this.subject = '';
    this.body = '';
    this.priority = '3';
    this.attachments = [];
    this.isMinimized = false;
  }

  // --- Helpers ---

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }
}
