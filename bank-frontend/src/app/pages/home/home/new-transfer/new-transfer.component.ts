import {Component, OnInit} from '@angular/core';
import {TransferService} from "../../../../core/services/transfer/transfer.service";
import {AccountService} from "../../../../core/services/account/account.service";
import {BasicAccountInfo} from "../../../../core/data/account/basic-account-info";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {AuthService} from "../../../../core/services/auth/auth.service";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {CustomValidators} from "../../../../core/validators/custom-validators";
import {MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {TransferRequest} from "../../../../core/data/transfer/transfer-request";

@Component({
  selector: 'app-new-transfer',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatFormFieldModule,
    MatButton,
    NgIf
  ],
  templateUrl: './new-transfer.component.html',
  styleUrl: './new-transfer.component.scss'
})
export class NewTransferComponent implements OnInit{

  constructor(private transferService: TransferService,
              private accountService: AccountService,
              private snackBar: MatSnackBar,
              private router: Router,
              private authService: AuthService) {
  }

  accountInfo: BasicAccountInfo = {accountNumber: "", balance: 0.0};

  ngOnInit(): void {
    this.getAccountInfo();
  }

  getAccountInfo() {
    this.accountService.getAccountInfo().subscribe({
      next: (next) => this.accountInfo = next,
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 5000});
        this.authService.deleteAll();
        this.router.navigate(['/auth/login'])
      }
    })
  }

  transferForm = new FormGroup({
    receiverAccountNumber: new FormControl('', [
      Validators.required,
      CustomValidators.fixedLengthValidator(16),
      CustomValidators.onlyNumbersValidator()]
    ),
    receiverName: new FormControl('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50),
      CustomValidators.alphanumericValidator()
    ]),
    title: new FormControl('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50),
      CustomValidators.alphanumericValidator()
    ]),
    amount: new FormControl('',[
      Validators.required,
      CustomValidators.doubleNumberValidator()
    ])
  });

  submitForm() {
    if(this.transferForm.invalid) {
      return;
    }

    const receiverAccountNumber = this.transferForm.value.receiverAccountNumber!;
    const receiverName = this.transferForm.value.receiverName!;
    const title = this.transferForm.value.title!;
    const amount = Number(this.transferForm.value.amount!);

    if(receiverAccountNumber === this.accountInfo.accountNumber) {
      this.snackBar.open('You cannot send a transfer to yourself', 'Close', {duration: 5000});
      this.transferForm.reset();
      return;
    }

    if(amount > this.accountInfo.balance) {
      this.snackBar.open('You don\'t have enough money', 'Close', {duration: 5000});
      this.transferForm.reset();
      return;
    }

    const request: TransferRequest = {
      receiverAccountNumber: receiverAccountNumber,
      receiverName: receiverName,
      title: title,
      amount: amount
    };

    this.transferService.createTransfer(request).subscribe({
      next: () => this.router.navigate(['/home/account']),
      error: (error) => {
        this.snackBar.open(error.statusText, 'Close', {duration: 5000});
        this.transferForm.reset();
        if(error.status !== 404) {
          this.authService.deleteAll();
          this.router.navigate(['/auth/login'])
        }
      }
    })
  }

}
