import { Component } from '@angular/core';
import { EmailSidebarComponent } from '../../features/email-sidebar/email-sidebar';
import { EmailListComponent } from '../../features/email-list/email-list';
import { Email, Folder } from '../models/email.model';
import { BackendController } from '../../services/backend-controller/backend-controller';
import { inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmailHandler } from '../../services/emails-handler/email-handler';
import { EmailDetailComponent } from '../../features/email-detail/email-detail';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [CommonModule, EmailSidebarComponent, EmailListComponent, EmailDetailComponent],
  templateUrl: './main-page.html',
  styleUrls: ['./main-page.css'],
})
export class MainPage {
  protected emailHandler = inject(EmailHandler);
}
