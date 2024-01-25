import {Component, OnInit} from '@angular/core';
import {BasicAccountInfo} from "../../../../core/data/account/basic-account-info";
import {SensitiveAccountInfo} from "../../../../core/data/account/sensitive-account-info";
import {AccountService} from "../../../../core/services/account/account.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {AuthService} from "../../../../core/services/auth/auth.service";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatSuffix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-account-details',
  standalone: true,
  imports: [
    MatIconButton,
    MatSuffix,
    MatIcon,
    MatButton
  ],
  templateUrl: './account-details.component.html',
  styleUrl: './account-details.component.scss'
})
export class AccountDetailsComponent implements OnInit {

  constructor(private accountService: AccountService,
              private snackBar: MatSnackBar,
              private router: Router,
              private authService: AuthService) {}

  basicAccountInfo: BasicAccountInfo = {accountNumber: "", balance: 0.0};
  sensitiveAccountInfo: SensitiveAccountInfo = {idNumber: "********", cardNumber: "********"};

  showSensitiveInfo = false;

  ngOnInit(): void {
    this.accountService.getAccountInfo().subscribe({
      next: (next) => this.basicAccountInfo = next,
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 5000});
        this.authService.deleteAll();
        this.router.navigate(['/auth/login']);
      }
    })
  }

  updateSensitiveInfo() {
    if(this.showSensitiveInfo) {
      this.revealSensitiveInfo();
    } else {
      this.restoreDefaults();
    }
  }

  revealSensitiveInfo() {
    this.accountService.getSensitiveAccountInfo().subscribe({
      next: (next) => this.sensitiveAccountInfo = next,
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 5000})
        this.authService.deleteAll();
        this.router.navigate(['/auth/login'])
      }
    })
  }

  restoreDefaults() {
    this.sensitiveAccountInfo = {idNumber: "********", cardNumber: "********"};
  }

  goBack() {
    this.restoreDefaults();
    this.router.navigate(['/home/account'])
  }

}
