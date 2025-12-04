import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {EmailSidebarComponent} from '../../features/email-sidebar/email-sidebar';
import {EmailListComponent} from '../../features/email-list/email-list';


@Component({
  selector: 'app-main-page',
  standalone: true,
  imports: [CommonModule, EmailSidebarComponent, EmailListComponent],
  templateUrl: './main-page.html',
  styleUrls: ['./main-page.css']
})
export class MainPage {



}
