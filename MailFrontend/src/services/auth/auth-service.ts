import { Injectable, signal } from '@angular/core';
import { UserDTO } from '../../app/models/UserDTO';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Signal to hold the current user's profile data (optional, but reactive)
  public currentUser = signal<UserDTO | null>(null);

  // --- STORAGE KEYS ---
  private readonly TOKEN_KEY = 'current_user_token';
  private readonly USER_ID_KEY = 'current_user_id';
  private readonly USER_NAME_KEY = 'current_user_name';
  private readonly USER_EMAIL_KEY = 'current_user_email';
  private readonly CURRENT_FOLDER_NAME = 'current_folder_name';

  constructor() {
    // Attempt to load user data/ID from storage on service creation
    this.loadInitialState();
    
    // Clear session when user navigates away from the site
    // This ensures they need to login again when returning
    window.addEventListener('beforeunload', () => {
      this.clearAuthData();
    });
  }

  private loadInitialState(): void {
    const userId = sessionStorage.getItem(this.USER_ID_KEY);
  }

  public saveAuthData(loginResponse: any): void {
    if (!loginResponse.id) {
      console.error('Authentication Error: User ID is missing in the successful response.');
      return;
    }
    console.log('login response: ', loginResponse.fullName);

    const userIdString = loginResponse.id.toString();
    sessionStorage.setItem(this.TOKEN_KEY, userIdString);
    sessionStorage.setItem(this.USER_ID_KEY, userIdString);
    sessionStorage.setItem(this.USER_NAME_KEY, loginResponse.fullName);
    sessionStorage.setItem(this.USER_EMAIL_KEY, loginResponse.email);
  }

  // (on error/logout)
  public clearAuthData(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.USER_ID_KEY);
    sessionStorage.removeItem(this.USER_EMAIL_KEY);
    sessionStorage.removeItem(this.USER_NAME_KEY);
    sessionStorage.removeItem(this.CURRENT_FOLDER_NAME);
  }

  // Called by other services (like MailService) to get the necessary ID
  public getCurrentUserId(): number | null {
    const id = sessionStorage.getItem(this.USER_ID_KEY);
    // Convert to number or return null
    return id ? parseInt(id, 10) : null;
  }

  public getCurrentUserName(): string | null {
    const userName = sessionStorage.getItem(this.USER_NAME_KEY);
    return userName ? userName : null;
  }

  public getCurrentEmail(): string | null {
    const userEmail = sessionStorage.getItem(this.USER_EMAIL_KEY);
    return userEmail ? userEmail : null;
  }

  public isAuthenticated(): boolean {
    return !!sessionStorage.getItem(this.TOKEN_KEY);
  }

  public setCurrentFolder(folderName: string) {
    sessionStorage.setItem(this.CURRENT_FOLDER_NAME, folderName);
  }

  public getCurrentFolderName() {
    return sessionStorage.getItem(this.CURRENT_FOLDER_NAME);
  }
}
