import { Component } from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {KeyValuePipe, NgForOf, NgIf} from "@angular/common";
import {CustomValidators} from "../../../core/validators/custom-validators";
import {AuthService} from "../../../core/services/auth/auth.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatFormFieldModule,
    MatButton,
    NgIf,
    KeyValuePipe,
    NgForOf,
    MatProgressSpinner
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  indexes : number[] = [];

  passwordTime = false;

  isWaiting = false;

  constructor(private authService: AuthService,
              private snackBar: MatSnackBar,
              private router: Router) {
  }

  loginForm = new FormGroup({
    login: new FormControl('', [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(20),
      CustomValidators.alphanumericValidator()
    ])
  });

  passwordForm = new FormGroup({});

  submitLoginForm() {
    if(this.loginForm.invalid) {
      return;
    }

    let login = this.loginForm.value.login!;

    this.authService.getPasswordCombination(login).subscribe({
      next: (data) => {
        this.indexes = data.indexes.split(' ').map(Number);
        this.createPasswordForm();
        this.passwordTime=true;
        this.loginForm.reset();
      },
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 10000});
        this.loginForm.reset();
        this.reset();
      }
    })

  }

  createPasswordForm() {

    for(let i of this.indexes) {
      this.passwordForm.addControl(`char${i}`, new FormControl({value: '', disabled: false}, Validators.required))
    }
  }

  submitPasswordForm() {
    if(this.passwordForm.invalid) {
      return;
    }

    this.isWaiting = true;

    let password = Object.values(this.passwordForm.controls).map(control => (control as FormControl).value)
      .join('');

    this.authService.login(password).subscribe({
      next: () => this.router.navigate(['/home']),
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 10000});
        this.reset();
      }
    })
  }

  requestForPasswordReset() {
    if(this.loginForm.invalid) {
      return;
    }

    let username = this.loginForm.value.login!;

    this.authService.requestForPasswordReset(username).subscribe({
      next: () => {
        this.snackBar.open('If username is correct, email has been sent', 'Close', {duration: 5000});
        this.reset();
      },
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 10000});
        this.reset();
    }
    })
  }

  reset() {
    this.passwordForm = new FormGroup({});
    this.passwordTime = false;
    this.isWaiting = false;
  }
}
