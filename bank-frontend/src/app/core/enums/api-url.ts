export enum ApiUrl {

  LOGIN = '/api/auth/login',
  AUTHENTICATE = '/api/auth/authenticate',
  PASSWORD_RESET_REQUEST = '/api/auth/reset-password-mail',
  PASSWORD_RESET = '/api/auth/reset-password',
  LOGOUT = '/api/auth/logout',

  BASIC_ACCOUNT_INFO = '/api/account/account-info',
  SENSITIVE_ACCOUNT_INFO = '/api/account/sensitive-account-info',

  TRANSFERS_ON_PAGE = '/api/transfer/get-transfer-on-page',
  CREATE_TRANSFER = '/api/transfer/create-transfer'
}
