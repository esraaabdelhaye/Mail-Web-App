import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { NotificationService } from '../../services/notification/notification-service';
import { LucideAngularModule, X } from 'lucide-angular';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-pop-up',
  imports: [],
  templateUrl: './pop-up.html',
  styleUrl: './pop-up.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PopupMessageComponent {
  public notificationService = inject(NotificationService);
  readonly icons = { X };

  get notificationClass(): string {
    const type = this.notificationService.currentNotification().type;
    return `toast-${type}`;
  }
}
