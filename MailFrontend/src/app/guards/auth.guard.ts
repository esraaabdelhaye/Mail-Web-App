import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../../services/auth/auth-service';

/*
  - Guards are functions that determine whether we can access a specific route or not
  - Here we are controlling access to the homepage and to the login/signup page
*/



export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Can only access /home if user is authenticated
  if (authService.isAuthenticated()) {
    return true;
  }

  // Redirect to login if not authenticated
  router.navigate(['/']);
  return false;
};

export const loginGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);


  // Can't access /home if user is not authenticated
  if (!authService.isAuthenticated()) {
    return true;
  }

  // Redirect to home if already authenticated
  router.navigate(['/home']);
  return false;
};
