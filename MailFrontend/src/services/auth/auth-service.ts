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

  constructor() {
    // Attempt to load user data/ID from storage on service creation
    this.loadInitialState();
  }

  private loadInitialState(): void {
    const userId = sessionStorage.getItem(this.USER_ID_KEY);
  }

  public saveAuthData(userId: Number): void {
    if (!userId) {
      console.error('Authentication Error: User ID is missing in the successful response.');
      return;
    }

    const userIdString = userId.toString();
    sessionStorage.setItem(this.TOKEN_KEY, userIdString);
    sessionStorage.setItem(this.USER_ID_KEY, userIdString);
  }

  // (on error/logout)
  public clearAuthData(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.USER_ID_KEY);
  }

  // Called by other services (like MailService) to get the necessary ID
  public getCurrentUserId(): number | null {
    const id = sessionStorage.getItem(this.USER_ID_KEY);
    // Convert to number or return null
    return id ? parseInt(id, 10) : null;
  }

  public isAuthenticated(): boolean {
    return !!sessionStorage.getItem(this.TOKEN_KEY);
  }
}
