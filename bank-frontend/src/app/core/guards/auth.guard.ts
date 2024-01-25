import { CanActivateFn } from '@angular/router';
import {inject} from "@angular/core";
import {StorageService} from "../services/storage/storage.service";

export const authGuard: CanActivateFn = (route, state) => {
  return !!inject(StorageService).getAuthToken();
};
