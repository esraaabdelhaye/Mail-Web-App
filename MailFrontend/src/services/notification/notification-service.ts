import { Injectable, signal } from '@angular/core';

export interface Notification {
  message: string;
  type: 'success' | 'error' | 'info';
  isVisible: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private notificationSignal = signal<Notification>({
    message: '',
    type: 'info',
    isVisible: false,
  });

  readonly currentNotification = this.notificationSignal.asReadonly();

  private timeoutId: any;
  private readonly DISPLAY_DURATION = 3000;

  constructor() {}

  show(message: string, type: Notification['type']): void {
    this.clearTimer();
    this.notificationSignal.set({
      message: message,
      type: type,
      isVisible: true,
    });

    this.timeoutId = setTimeout(() => {
      this.hide();
    }, this.DISPLAY_DURATION);
  }

  showError(message: string): void {
    this.show(message, 'error');
  }

  showSuccess(message: string): void {
    this.show(message, 'success');
  }

  hide(): void {
    this.clearTimer();
    this.notificationSignal.update((n) => ({ ...n, isVisible: false }));
  }

  private clearTimer(): void {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
  }
}
