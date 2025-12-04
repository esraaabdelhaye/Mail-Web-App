import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
// Angular Animations imports have been removed.

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // animations: [slideFormAnimation] <-- Removed
})
export class LoginPage {
  public activeTab = signal<'login' | 'signup'>('login');
  public isLoading = signal(false);

  public loginData = signal({ email: '', password: '' });
  public signupData = signal({ name: '', email: '', password: '', confirmPassword: '' });

  updateLoginData(key: 'email' | 'password', value: string): void {
    this.loginData.update((data) => ({ ...data, [key]: value }));
  }

  updateSignupData(key: 'name' | 'email' | 'password' | 'confirmPassword', value: string): void {
    this.signupData.update((data) => ({ ...data, [key]: value }));
  }

  switchTab(tab: 'login' | 'signup'): void {
    this.activeTab.set(tab);
  }

  handleLogin(event: Event): void {
    event.preventDefault();
    this.isLoading.set(true);
    setTimeout(() => {
      this.isLoading.set(false);
      console.log('Login attempt successful for:', this.loginData().email);
    }, 1500);
  }

  handleSignUp(event: Event): void {
    event.preventDefault();
    this.isLoading.set(true);
    const data = this.signupData();
    if (data.password !== data.confirmPassword) {
      this.isLoading.set(false);
      console.error('Passwords do not match.');
      return;
    }
    setTimeout(() => {
      this.isLoading.set(false);
      console.log('Signup attempt successful for:', data.email);
    }, 1500);
  }
}
