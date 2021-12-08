import { NativeModules } from 'react-native';

import type {
  TokenizationParams,
  TokenizationResult,
  ErrorResult,
} from '../types';
import { YooKassaError } from '../classes';

const RnYookassa = NativeModules.RnYookassa;

export function tokenize(
  params: TokenizationParams
): Promise<TokenizationResult> {
  return new Promise((resolve, reject) => {
    RnYookassa.tokenize(
      params,
      (result?: TokenizationResult, error?: ErrorResult) => {
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
