import type { PaymentTypesEnum } from '.';

export interface TokenizationResult {
  token: string;
  type: PaymentTypesEnum;
}
