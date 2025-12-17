import {Component, signal, ViewChild} from '@angular/core';
import { EmailSidebarComponent } from '../../features/email-sidebar/email-sidebar';
import { EmailListComponent } from '../../features/email-list/email-list';
import { CommonModule } from '@angular/common';
import { EmailDetailComponent } from '../../features/email-detail/email-detail';
import {ContactsComponent} from '../../features/contacts-view/contacts-view';

@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [CommonModule, EmailSidebarComponent, EmailListComponent, EmailDetailComponent, ContactsComponent],
  templateUrl: './main-page.html',
  styleUrls: ['./main-page.css'],
})
export class MainPage {
  @ViewChild(EmailListComponent) emailList!: EmailListComponent;
  isContactsOpen = signal<boolean>(false);


}
