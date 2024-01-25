import { Routes } from '@angular/router';
import {LoginComponent} from "./pages/auth/login/login.component";
import {ResetPasswordComponent} from "./pages/auth/reset-password/reset-password.component";
import {authGuard} from "./core/guards/auth.guard";
import {HomeComponent} from "./pages/home/home/home.component";
import {AccountComponent} from "./pages/home/home/account/account/account.component";
import {AccountDetailsComponent} from "./pages/home/home/account-details/account-details.component";
import {NewTransferComponent} from "./pages/home/home/new-transfer/new-transfer.component";

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full'},
  { path: 'auth', children: [
      { path: '', redirectTo: 'login', pathMatch: 'full'},
      { path: 'login', component: LoginComponent},
      { path: 'reset-password', component: ResetPasswordComponent},
      { path: '**', redirectTo:'login'}
    ]},
  { path: 'home', canActivate: [authGuard], component: HomeComponent, children:[
      { path: '', redirectTo: 'account', pathMatch: 'full'},
      { path: 'account', component: AccountComponent},
      { path: 'account-details', component: AccountDetailsComponent},
      { path: 'new-transfer', component: NewTransferComponent},
      { path: '**', redirectTo: 'account'}
    ]},
  { path: '**', redirectTo: 'auth/login'}
];
