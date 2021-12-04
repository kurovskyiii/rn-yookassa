import { NativeModules } from 'react-native';

const RnYookassa = NativeModules.RnYookassa;

export function cancel(): void {
  RnYookassa.cancel();
}
