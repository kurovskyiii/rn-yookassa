import { NativeModules } from 'react-native';

import type {
  TokenizationConfig,
  TokenizationResult,
  ErrorResult,
} from '../types';

const RnYookassa = NativeModules.RnYookassa;

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
