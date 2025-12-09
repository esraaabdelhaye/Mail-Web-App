import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {routes} from './app/app.routes';
import {provideHttpClient} from '@angular/common/http';

bootstrapApplication(App, {
  providers: [
    // Enables routing
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient()
  ]
}).catch((err) => console.error(err));
