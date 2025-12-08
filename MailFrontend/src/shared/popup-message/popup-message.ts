import { Component, ChangeDetectionStrategy, signal } from '@angular/core';

@Component({
  selector: 'app-popup-message',
  imports: [],
  templateUrl: './popup-message.html',
  styleUrl: './popup-message.css',
})
export class PopupMessage {
  public successMessage = signal<string | null>(null);
  public errorMessage = signal<string | null>(null);
  private messageTimeout: any = null;

  closeMessage(): void {
    this.successMessage.set(null);
    this.errorMessage.set(null);
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
      this.messageTimeout = null;
    }
  }

  setMessageAndTimeout(message: string, success: boolean): void {
    if (success) {
      this.errorMessage.set(null);
      this.successMessage.set(message);
    } else {
      this.errorMessage.set(message);
      this.successMessage.set(null);
    }

    if (this.messageTimeout) clearTimeout(this.messageTimeout);
    this.messageTimeout = setTimeout(() => {
      this.errorMessage.set(null);
      this.successMessage.set(null);
    }, 5000);
  }
}
