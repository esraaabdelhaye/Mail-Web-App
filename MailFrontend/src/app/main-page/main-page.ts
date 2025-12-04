import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {EmailSidebarComponent} from '../../features/email-sidebar/email-sidebar';
import {EmailListComponent} from '../../features/email-list/email-list';
import {Email, Folder} from '../models/email.model';
import {BackendController} from '../../services/backend-controller/backend-controller';
import {EmailHandler} from '../../services/emails-handler/email-handler';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [CommonModule, EmailSidebarComponent, EmailListComponent],
  templateUrl: './main-page.html',
  styleUrls: ['./main-page.css']
})
export class MainPage {
  protected emailHandler = inject(EmailHandler);


}
