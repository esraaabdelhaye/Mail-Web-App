import { Routes } from '@angular/router';
import { LoginPage } from './login-page/login-page';
import { MainPage } from './main-page/main-page';
import { authGuard, loginGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LoginPage, canActivate: [loginGuard] },
  { path: 'home', component: MainPage, canActivate: [authGuard] },
];
