import { NativeModules, Platform } from 'react-native';

export * from './types';

const LINKING_ERROR =
  `The package 'rn-yookassa' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

NativeModules.RnYookassa
  ? NativeModules.RnYookassa
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export * from './functions';
