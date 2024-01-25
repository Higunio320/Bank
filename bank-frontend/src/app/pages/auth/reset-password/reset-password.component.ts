import {Component, OnInit} from '@angular/core';
import {StorageService} from "../../../core/services/storage/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {CustomValidators} from "../../../core/validators/custom-validators";
import {NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {MatSnackBar} from "@angular/material/snack-bar";
import {AuthService} from "../../../core/services/auth/auth.service";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    MatLabel,
    MatInput,
    MatFormFieldModule,
    ReactiveFormsModule,
    FormsModule,
    NgIf,
    MatButton,
    MatProgressSpinner
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent implements OnInit{

  constructor(private storage: StorageService,
              private route: ActivatedRoute,
              private router: Router,
              private snackBar: MatSnackBar,
              private authService: AuthService) {}

  isWaiting = false;

  passwordsForm = new FormGroup({
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(15),
      Validators.maxLength(30),
      CustomValidators.passwordEntropyValidator(3.5),
      CustomValidators.passwordRegexValidator(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\-_+={[\]}:;"'\\|<,>.?/]).*$/)
    ]),
    repeatPassword: new FormControl('')
  }, CustomValidators.matchValidator('password', 'repeatPassword'));

  ngOnInit(): void {
    let token = this.route.snapshot.queryParamMap.get('token');
    if(token) {
      this.storage.saveResetPasswordToken(token);
    } else {
      this.router.navigate(['/auth/login'])
    }
  }

  submitForm() {
    if(this.passwordsForm.invalid) {
      return;
    }

    const password = this.passwordsForm.value.password!;

    this.isWaiting = true;

    this.authService.resetPassword(password).subscribe({
      next: () => {
        this.snackBar.open('Password reset successful', 'Close', {duration: 5000});
        this.isWaiting = false;
        this.router.navigate(['/auth/login'])
      },
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 10000});
        this.isWaiting = false;
        this.router.navigate(['/auth/login'])
      }
    })

  }
}
