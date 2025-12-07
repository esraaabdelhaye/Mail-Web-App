import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, ArrowLeft, Sparkles, Download, Trash2, FileText, Image as ImageIcon, File, Loader2 } from 'lucide-angular';
import {Email} from '../../app/models/email.model';
import {ButtonComponent} from '../../shared/button/button';



@Component({
  selector: 'app-email-detail',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, ButtonComponent],
  templateUrl: './email-detail.html',
  styleUrls: ['./email-detail.css']
})
export class EmailDetailComponent {
  @Input({ required: true }) email!: Email;
  @Output() back = new EventEmitter<void>();
  @Output() deleteAttachment = new EventEmitter<string>();

  // --- State ---
  isSummarizing = signal(false);
  summary = signal<string | null>(null);

  readonly icons = { ArrowLeft, Sparkles, Download, Trash2, FileText, ImageIcon, File, Loader2 };

  // --- Actions ---

  async handleSummarize() {
    this.isSummarizing.set(true);

    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 2000));

    const text = `This email from ${this.email.sender.name} discusses ${this.email.subject.toLowerCase()}. ` +
      `Key points include action items that require attention. ` +
      `The sender is requesting a response or follow-up.`;

    this.summary.set(text);
    this.isSummarizing.set(false);
  }

  // --- Helpers ---

  getAttachmentIcon(type: string) {
    if (type.includes('pdf')) return this.icons.FileText;
    if (type.includes('image')) return this.icons.ImageIcon;
    return this.icons.File;
  }


  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleString([], {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit', weekday: 'long'
    });
  }

  // Helper to split body into paragraphs
  get paragraphs(): string[] {
    return this.email.body.split('\n');
  }
}
