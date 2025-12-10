import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // 1. Retrieve the token from Session Storage (or localStorage)
    const token = sessionStorage.getItem('current_user_token');

    // Check if the token exists and if the request is going to our backend API
    if (token) {
      // 2. Clone the request and set the Authorization header
      // The token must be sent in the format: Bearer <token>
      const clonedRequest = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + token),
      });

      // 3. Pass the cloned request (with the token) to the next handler
      return next.handle(clonedRequest);
    }

    // If no token exists, send the original request without modification
    return next.handle(request);
  }
}
