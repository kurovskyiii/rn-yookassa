import type { ErrorCodesEnum } from '../types';

export class YooKassaError extends Error {
  code: ErrorCodesEnum;

  constructor(code: ErrorCodesEnum, message: string) {
    super(message);
    this.code = code;
  }
}
