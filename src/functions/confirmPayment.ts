import { NativeModules } from 'react-native';

import type {
  ConfirmationPaymentParams,
  ConfirmationPaymentResult,
  ErrorResult,
} from '../types';
import { YooKassaError } from '../classes';

const RnYookassa = NativeModules.RnYookassa;

export function confirmPayment(
  params: ConfirmationPaymentParams
): Promise<ConfirmationPaymentResult> {
  return new Promise((resolve, reject) => {
    RnYookassa.confirmPayment(
      params,
      (result?: ConfirmationPaymentResult, error?: ErrorResult) => {
        if (result) {
          resolve(result);
        } else {
          if (error) {
            reject(new YooKassaError(error.code, error.message));
          } else {
            reject();
          }
        }
      }
    );
  });
}
