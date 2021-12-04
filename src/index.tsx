import { NativeModules, Platform } from 'react-native';

import type {
  TokenizationConfig,
  TokenizationResult,
  ErrorResult,
} from './types';

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

export function tokenize(
  info: TokenizationConfig
): Promise<TokenizationResult> {
  //TODO: Maybe create mapper
  const paymentConfig = info;

  return new Promise((resolve, reject) => {
    RnYookassa.tokenize(
      paymentConfig,
      (result?: TokenizationResult, error?: ErrorResult) => {
        if (result) {
          resolve(result);
        } else {
          reject(error);
        }
      }
    );
  });
}

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

export function cancel(): void {
  RnYookassa.cancel();
}
