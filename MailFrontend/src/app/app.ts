import { Component, signal } from '@angular/core';

import { RouterOutlet } from '@angular/router';
import { EmailListComponent } from '../features/email-list/email-list';
import { MainPage } from './main-page/main-page';
import { LoginPage } from './login-page/login-page';

@Component({
  selector: 'app-root',

  imports: [RouterOutlet, EmailListComponent, MainPage, LoginPage],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('MailFrontend');
}
