import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'rn-yookassa' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const RnYookassa = NativeModules.RnYookassa
  ? NativeModules.RnYookassa
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export type PaymentType =
  | 'BANK_CARD'
  | 'APPLE_PAY'
  | 'GOOGLE_PAY'
  | 'YOO_MONEY'
  | 'SBERBANK';

export type GooglePaymentType =
  | 'AMEX'
  | 'DISCOVER'
  | 'JCB'
  | 'MASTERCARD'
  | 'VISA'
  | 'INTERAC'
  | 'OTHER';
export interface PaymentConfig {
  clientApplicationKey: string;
  shopId: string;
  title: string;
  subtitle: string;
  // TODO: currency
  price: number;
  paymentTypes?: PaymentType[];
  authCenterClientId?: string; // ! If YooMoney method selected
  userPhoneNumber?: string;
  gatewayId?: string;
  googlePaymentTypes?: GooglePaymentType[];
  isDebug?: boolean;
}

export interface PaymentResult {
  token: string;
  type: PaymentType;
}

export function tokenize(info: PaymentConfig): Promise<PaymentResult> {
  //TODO: Maybe create mapper
  const paymentConfig = info;

  return new Promise((resolve, reject) => {
    RnYookassa.tokenize(
      paymentConfig,
      (token: string, type: PaymentType, error: string) => {
        if (!!token && !!type) {
          resolve({
            token: token,
            type: type,
          });
        } else if (error) {
          reject(error);
        }
      }
    );
  });
}

export function confirmPayment(url: string): Promise<void> {
  return new Promise((resolve, reject) => {
    RnYookassa.confirmPayment(url, (result: string) => {
      if (result === 'success') {
        resolve();
      } else {
        reject();
      }
    });
  });
}

export function cancel(): void {
  RnYookassa.cancel();
}
