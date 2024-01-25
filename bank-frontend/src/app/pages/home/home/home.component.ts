import {Component, OnInit} from '@angular/core';
import {MatToolbar} from "@angular/material/toolbar";
import {MatButton, MatFabButton} from "@angular/material/button";
import {NgIcon, provideIcons} from "@ng-icons/core";
import {ionLogOutOutline} from "@ng-icons/ionicons";
import {Router, RouterLink, RouterOutlet} from "@angular/router";
import {AuthService} from "../../../core/services/auth/auth.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {AccountService} from "../../../core/services/account/account.service";
import {BasicAccountInfo} from "../../../core/data/account/basic-account-info";
import {TransferService} from "../../../core/services/transfer/transfer.service";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    MatToolbar,
    MatButton,
    MatFabButton,
    NgIcon,
    RouterOutlet,
    RouterLink
  ],
  providers: [provideIcons({ionLogOutOutline})],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(private authService: AuthService,
              private snackBar: MatSnackBar,
              private router: Router) {

  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.snackBar.open('Logged out successfully', 'Close', {duration: 5000});
        this.router.navigate(['/auth/login']);
      },
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 10000});
        this.authService.deleteAll();
        this.router.navigate(['/auth/login']);
      }
    })
  }

}
