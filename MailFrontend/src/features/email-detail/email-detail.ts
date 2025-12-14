import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideAngularModule,
  ArrowLeft,
  Sparkles,
  Download,
  Trash2,
  FileText,
  Image as ImageIcon,
  File,
  Loader2,
} from 'lucide-angular';
import { AttachmentDTO, MailDetailsDTO } from '../../app/models/DetailedMail';
import { ButtonComponent } from '../../shared/button/button';

@Component({
  selector: 'app-email-detail',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, ButtonComponent],
  templateUrl: './email-detail.html',
  styleUrls: ['./email-detail.css'],
})
export class EmailDetailComponent {
  @Input({ required: true }) mailDetail!: MailDetailsDTO;
  @Output() back = new EventEmitter<void>();
  @Output() deleteAttachment = new EventEmitter<string>();

  // detailedEmail = signal
  // --- State ---
  // isSummarizing = signal(false);
  // summary = signal<string | null>(null);
  mailDetails = signal<MailDetailsDTO | null>(null);
  readonly icons = { ArrowLeft, Sparkles, Download, Trash2, FileText, ImageIcon, File, Loader2 };

  // --- Actions ---

  downloadAttachment(file: AttachmentDTO) {}

  // async handleSummarize() {
  //   this.isSummarizing.set(true);

  //   // Simulate API delay
  //   await new Promise((resolve) => setTimeout(resolve, 2000));

  //   const text =
  //     `This email from ${
  //       this.mailDetails.sender.name
  //     } discusses ${this.mailDetails.subject.toLowerCase()}. ` +
  //     `Key points include action items that require attention. ` +
  //     `The sender is requesting a response or follow-up.`;

  //   this.summary.set(text);
  //   this.isSummarizing.set(false);
  // }

  // --- Helpers ---

  getAttachmentIcon(type: string) {
    if (type.includes('pdf')) return this.icons.FileText;
    if (type.includes('image')) return this.icons.ImageIcon;
    return this.icons.File;
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleString([], {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      weekday: 'long',
    });
  }

  // Helper to split body into paragraphs
  get paragraphs(): string[] {
    return this.mailDetail.body.split('\n');
  }

  // Creates a downloadable link
  private download(blob: Blob, fileName: string) {
    // 1. Create a URL for the Blob (like a temporary file path in memory)
    const url = window.URL.createObjectURL(blob);

    // 2. Create an invisible anchor tag <a>
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName; // Tells browser: "Don't open this, SAVE it as..."
    link.style.display = 'none'; // Hide it from the user

    // 3. Add to the page, click it, and remove it
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    // 4. Free up the memory (Important!)
    window.URL.revokeObjectURL(url);
  }
}
