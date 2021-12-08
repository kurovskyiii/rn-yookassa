import { NativeModules } from 'react-native';

const RnYookassa = NativeModules.RnYookassa;

export function dismiss(): void {
  RnYookassa.dismiss();
}
