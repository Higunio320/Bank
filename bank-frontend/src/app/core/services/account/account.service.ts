import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BasicAccountInfo} from "../../data/account/basic-account-info";
import {ApiUrl} from "../../enums/api-url";
import {catchError, throwError} from "rxjs";
import {SensitiveAccountInfo} from "../../data/account/sensitive-account-info";

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private http: HttpClient) { }

  getAccountInfo() {
    return this.http.get<BasicAccountInfo>(`${ApiUrl.BASIC_ACCOUNT_INFO}`)
      .pipe(
        catchError((error: any) => {
          if(error.error?.exceptionMessage) {
            return throwError(() => new Error(error.error.exceptionMessage))
          } else {
            return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
          }})
      )
  }

  getSensitiveAccountInfo() {
    return this.http.get<SensitiveAccountInfo>(`${ApiUrl.SENSITIVE_ACCOUNT_INFO}`)
      .pipe(
        catchError((error: any) => {
          if(error.error?.exceptionMessage) {
            return throwError(() => new Error(error.error.exceptionMessage))
          } else {
            return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
          }})
      )
  }
}
