import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PasswordCombinationResponse} from "../../data/auth/password-combination-response";
import {ApiUrl} from "../../enums/api-url";
import {catchError, tap, throwError} from "rxjs";
import {StorageService} from "../storage/storage.service";
import {TokenResponse} from "../../data/auth/token-response";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient,
              private storage: StorageService) {
  }

  getPasswordCombination(login: string) {
    return this.http.post<PasswordCombinationResponse>(`${ApiUrl.LOGIN}`, {username: login})
      .pipe(tap({
        next: (response) => {
          this.storage.saveLoginToken(response.token)
        }
      }
        ),
        catchError((error: any) => {
          if(error.error?.exceptionMessage) {
            return throwError(() => new Error(error.error.exceptionMessage))
          } else {
            return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
          }
        })
      )
  }

  login(password: string) {
    let token = this.storage.getLoginToken();
    this.storage.deleteLoginToken();

    if(token) {
      return this.http.post<TokenResponse>(`${ApiUrl.AUTHENTICATE}`, {token: token, password: password})
        .pipe(tap({
          next: (response) => this.storage.saveAuthToken(response.token),
        }),
          catchError((error: any) => {
              if(error.error?.exceptionMessage) {
                return throwError(() => new Error(error.error.exceptionMessage))
              } else {
                return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
              }
          }))
    } else {
      return throwError(() => new Error('No token'));
    }
  }

  requestForPasswordReset(username: string) {
    return this.http.post(`${ApiUrl.PASSWORD_RESET_REQUEST}`, {username: username})
      .pipe(
        catchError((error: any) => {
          if(error.error?.exceptionMessage) {
            return throwError(() => new Error(error.error.exceptionMessage))
          } else {
            return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
          }
        })
      )
  }

  resetPassword(password: string) {
    const token = this.storage.getResetPasswordToken();
    this.storage.deleteResetPasswordToken();

    if(token) {
      return this.http.post(`${ApiUrl.PASSWORD_RESET}`, {token: token, password: password})
        .pipe(
          catchError((error: any) => {
            if(error.error?.exceptionMessage) {
              return throwError(() => new Error(error.error.exceptionMessage))
            } else {
              return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
            }
          })
        )
    } else {
      return throwError(() => new Error('No token'));
    }
  }

  logout() {
    const token = this.storage.getAuthToken();
    this.storage.deleteAuthToken();

    if(token) {
      return this.http.post(`${ApiUrl.LOGOUT}`, {token: token})
        .pipe(
          catchError((error: any) => {
            if(error.error?.exceptionMessage) {
              return throwError(() => new Error(error.error.exceptionMessage))
            } else {
              return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
            }
          })
        )
    } else {
      return throwError(() => new Error('No token'));
    }
  }

  deleteAll() {
    this.storage.deleteAuthToken();
    this.storage.deleteLoginToken();
    this.storage.deleteResetPasswordToken();
  }
}
