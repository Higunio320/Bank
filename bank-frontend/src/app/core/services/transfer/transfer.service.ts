import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {TransferList} from "../../data/transfer/transfer-list";
import {ApiUrl} from "../../enums/api-url";
import {catchError, throwError} from "rxjs";
import {TransferRequest} from "../../data/transfer/transfer-request";
import {TransferResponse} from "../../data/transfer/transfer-response";

@Injectable({
  providedIn: 'root'
})
export class TransferService {

  constructor(private http: HttpClient) { }

  getTransfersOnPage(pageNumber: number) {
    return this.http.get<TransferList>(`${ApiUrl.TRANSFERS_ON_PAGE}`, {params: {pageNumber: pageNumber}})
      .pipe(
        catchError((error: any) => {
          if(error.error?.exceptionMessage) {
            return throwError(() => new Error(error.error.exceptionMessage))
          } else {
            return throwError(() => new Error(`An unexpected error occurred: ${error.message}`))
          }})
      )
  }

  createTransfer(transferRequest: TransferRequest) {
    return this.http.post<TransferResponse>(`${ApiUrl.CREATE_TRANSFER}`, transferRequest, {observe: 'response'})
      .pipe(
        catchError((error: any) => {
          if(error.error?.exceptionMessage) {
            return throwError(() => new HttpErrorResponse({status: error.status, statusText: error.error.exceptionMessage}))
          } else {
            return throwError(() => new HttpErrorResponse({status: error.status, statusText: `An unexpected error occurred: ${error.message}`}));
          }})
      )
  }
}
