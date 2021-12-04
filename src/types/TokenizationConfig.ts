import type { PaymentTypesEnum, GooglePaymentTypesEnum } from '.';

export interface TokenizationConfig {
  clientApplicationKey: string;
  shopId: string;
  title: string;
  subtitle: string;
  // TODO: currency
  price: number;
  paymentTypes?: PaymentTypesEnum[];
  authCenterClientId?: string; // ! If YooMoney method selected
  userPhoneNumber?: string;
  gatewayId?: string;
  returnUrl?: string;
  googlePaymentTypes?: GooglePaymentTypesEnum[];
  applePayMerchantId?: string;
  isDebug?: boolean;
}
