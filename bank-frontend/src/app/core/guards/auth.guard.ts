import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {StorageService} from "../services/storage/storage.service";

export const authGuard: CanActivateFn = (route, state) => {
  if(inject(StorageService).getAuthToken()) {
    return true;
  } else {
    return inject(Router).createUrlTree(['']);
  }
};
