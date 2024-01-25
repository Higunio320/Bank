import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {BasicAccountInfo} from "../../../../../core/data/account/basic-account-info";
import {AccountService} from "../../../../../core/services/account/account.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {AuthService} from "../../../../../core/services/auth/auth.service";
import {Router} from "@angular/router";
import {MatPaginator} from "@angular/material/paginator";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";
import {TransferResponse} from "../../../../../core/data/transfer/transfer-response";
import {DatePipe} from "@angular/common";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {catchError, map, merge, of, startWith, switchMap} from "rxjs";
import {TransferService} from "../../../../../core/services/transfer/transfer.service";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    DatePipe,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatPaginator,
    MatButton
  ],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss'
})
export class AccountComponent implements OnInit, AfterViewInit{

  constructor(private accountService: AccountService,
              private snackBar: MatSnackBar,
              private authService: AuthService,
              private router: Router,
              private transferService: TransferService) {
  }

  ngOnInit(): void {
    this.getBasicUserInfo();
  }

  accountInfo : BasicAccountInfo = {accountNumber: "", balance: 0.0};

  transfers : TransferResponse[] = [];

  numOfElements = 0;
  displayedColumns = ['senderAccountNumber', 'receiverAccountNumber', 'title', 'receiverName', 'transferDate', 'amount'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  getBasicUserInfo() {
    this.accountService.getAccountInfo().subscribe({
      next: (value) => {
        this.accountInfo = value;
      },
      error: (error) => {
        this.snackBar.open(error.message, 'Close', {duration: 10000});
        this.authService.deleteAll();
        this.router.navigate(['/auth/login'])
      }
    })
  }

  getTransfers(pageNumber: number) {
    return this.transferService.getTransfersOnPage(pageNumber);
  }

  ngAfterViewInit(): void {
    merge(this.paginator.page).pipe(
      startWith({}),
      switchMap(() => {
        return this.getTransfers(this.paginator.pageIndex);
      })
    ).pipe(catchError((error) => {
      this.snackBar.open(error.message, 'Close', {duration: 5000});
      return of(null);
    }),
      map(data => {
        if(data == null) {
          return [];
        }

        this.numOfElements = data.totalTransfers;
        return data.transferList;
      })
    ).subscribe(data => {this.transfers = data});
  }

  goToAccountDetails() {
    this.router.navigate(['/home/account-details'])
  }

}
