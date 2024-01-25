import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  saveAuthToken(token: string) {
    localStorage.setItem('token', token);
  }

  deleteAuthToken() {
    localStorage.removeItem('token');
  }

  getAuthToken() {
    return localStorage.getItem('token')
  }

  saveLoginToken(token: string) {
    localStorage.setItem('loginToken', token);
  }

  deleteLoginToken() {
    localStorage.removeItem('loginToken')
  }

  getLoginToken() {
    return localStorage.getItem('loginToken')
  }

  saveResetPasswordToken(token: string) {
    localStorage.setItem('password-reset', token);
  }

  getResetPasswordToken() {
    return localStorage.getItem('password-reset');
  }

  deleteResetPasswordToken() {
    localStorage.removeItem('password-reset');
  }

}
