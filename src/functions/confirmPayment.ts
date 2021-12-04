import { NativeModules } from 'react-native';

import type { ErrorResult } from '../types';

const RnYookassa = NativeModules.RnYookassa;

export function confirmPayment(url: string): Promise<void> {
  return new Promise((resolve, reject) => {
    RnYookassa.confirmPayment(url, (result?: boolean, error?: ErrorResult) => {
      if (result) {
        resolve();
      } else {
        reject(error);
      }
    });
  });
}
