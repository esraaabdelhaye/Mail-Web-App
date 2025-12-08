import {
  Component,
  ChangeDetectionStrategy,
  signal,
  ViewChild,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { inject } from '@angular/core';
import { UserDTO } from '../models/UserDTO';
import { UserService } from '../../services/User/user-service';
import {
  FormsModule,
  FormGroup,
  FormControl,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { PopupMessage } from '../../shared/popup-message/popup-message';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth-service';

interface LoginFormGroup {
  email: FormControl<string>;
  password: FormControl<string>;
}

interface SignupFormGroup {
  name: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
  confirmPassword: FormControl<string>;
}

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, PopupMessage],
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginPage {
  public activeTab = signal<'login' | 'signup'>('login');
  public isLoading = signal(false);
  public userService: UserService = inject(UserService);
  public authService: AuthService = inject(AuthService);
  public validLogin: boolean = false;
  public validSignUp: boolean = false;

  constructor(private router: Router, private cdr: ChangeDetectorRef) {}

  @ViewChild(PopupMessage) popupMessage!: PopupMessage;

  public loginForm = new FormGroup<LoginFormGroup>({
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(8)],
    }),
  });

  public signupForm = new FormGroup<SignupFormGroup>({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(8)],
    }),
    confirmPassword: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  ngOnInit(): void {
    // Use a small timeout to ensure the browser has completed the asynchronous autofill process.
    setTimeout(() => {
      // Update validity based on autofilled values
      this.loginForm.updateValueAndValidity();
      console.log('timeout');

      if (this.loginForm.valid) {
        this.loginForm.markAllAsTouched();
        this.loginForm.markAsDirty();
      }
      this.cdr.detectChanges();
    }, 100);
  }

  switchTab(tab: 'login' | 'signup'): void {
    this.activeTab.set(tab);
    if (tab === 'login') {
      this.signupForm.reset();
    } else {
      this.loginForm.reset();
    }
  }

  private displayError(message: string): void {
    this.popupMessage.setMessageAndTimeout(message, false);
  }

  private displaySuccess(message: string): void {
    this.popupMessage.setMessageAndTimeout(message, true);
  }

  handleLogin(event: Event): void {
    event.preventDefault();

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      console.error('Login form is invalid.');
      return;
    }

    this.isLoading.set(true);
    const loginPayload = this.loginForm.getRawValue();

    console.log('Attempting login with:', loginPayload);

    const oldUser: UserDTO = {
      id: null,
      email: loginPayload.email,
      password: loginPayload.password,
    };

    this.userService.login(oldUser).subscribe({
      next: (response) => {
        console.log(response);
        if (response.reqState) this.loginVerified(response.reqMessage, response.id);
        else this.loginFailed(response.reqMessage);
      },
      error: (err) => {
        this.loginFailed(err);
      },
    });
  }

  handleSignUp(event: Event): void {
    event.preventDefault();

    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();
      console.error('Signup form is invalid.');
      return;
    }

    let data = this.signupForm.getRawValue();

    if (data.password !== data.confirmPassword) {
      this.signupForm.controls.confirmPassword.setErrors({ mismatch: true });
      this.isLoading.set(false);
      console.error('Passwords do not match.');
      return;
    }

    this.isLoading.set(true);

    // Construct DTO from form data
    const newUser: UserDTO = {
      id: null,
      fullName: data.name,
      email: data.email,
      password: data.password,
    };

    this.userService.registerUser(newUser).subscribe({
      next: (response) => {
        if (response.reqState) this.signUpVerified(response.reqMessage);
        else this.signUpFailed(response.reqMessage);
        console.log(response);
      },
      error: (err) => {
        this.signUpFailed(err);
      },
    });
  }

  private signUpVerified(mess: any | '') {
    this.isLoading.set(false);
    this.validSignUp = true;
    const message = 'User Registered Successfully: ' + mess;
    this.displaySuccess(message);
    this.switchTab('login');
  }

  private signUpFailed(mess: any | 'SignUp failed. Please check your credentials.') {
    this.isLoading.set(false);
    const message = 'Registration failed: ' + mess;
    this.displayError(message);
    console.error(message);
    this.validSignUp = false;
  }

  private loginVerified(mess: any | '', id: any) {
    this.isLoading.set(false);
    this.validLogin = true;
    const message = 'User Logged In Successfully: ' + mess;
    this.displaySuccess(message);

    this.authService.saveAuthData(id);

    // 2. Navigate away from the login page to the application dashboard
    this.router.navigate(['/home']);
  }

  private loginFailed(mess: any | 'Login failed. Please check your credentials.') {
    this.isLoading.set(false);
    this.validLogin = false;
    const message = 'Login failed: ' + mess;
    this.displayError(message);
    console.error(message);
  }
}
