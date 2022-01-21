import * as React from 'react';
import { TouchableOpacity, StyleSheet, View, Text } from 'react-native';

import {
  tokenize,
  confirmPayment,
  dismiss,
  YooKassaError,
  ErrorCodesEnum,
  PaymentMethodTypesEnum,
} from 'rn-yookassa';

const CONFIRMATION_URL =
  'https://3ds-gate.yoomoney.ru/card-auth?acsUri=https%3A%2F%2Fyookassa.ru%2Fsandbox%2Fbank-card%2F3ds&MD=1638920652394-5487315384260906123&PaReq=Q1VSUkVOQ1k9UlVCJk9SREVSPTI5NDIwNThiLTAwMGYtNTAwMC1hMDAwLTE4MGNkN2Y3ZGU4NiZURVJNSU5BTD05OTk5OTgmRVhQX1lFQVI9MzAmQU1PVU5UPTIzNS4wMCZDQVJEX1RZUEU9UEFOJlRSVFlQRT0wJkVYUD0wMSZDVkMyPTEyMyZDQVJEPTU1NTU1NTU1NTU1NTQ0NzcmTkFNRT0%3D&TermUrl=https%3A%2F%2Fpaymentcard.yoomoney.ru%3A443%2F3ds%2Fchallenge%2F241%2FFWxpokXcVtu4SuHuKZSfj0pVoT8Z..001.202112';

export default function App() {
  const emulateApiRequest = async (_paymentToken: string): Promise<string> => {
    return new Promise<string>((res) => {
      setTimeout(() => {
        res(CONFIRMATION_URL);
      }, 5000);
    });
  };

  const onPayPress = async () => {
    try {
      // ! PROCCESS PAYMENT AND GET `paymentToken` & `paymentMethodType`
      // ! DONT'T DISMISS YOOKASSA WINDOW HERE
      const { paymentToken, paymentMethodType } = await tokenize({
        clientApplicationKey:
          'test_ODIzNDE0PrQiM6dYe7ypegMZcKq6b_Rk7Zok467bGao',
        shopId: '823414',
        title: 'Товар',
        subtitle: 'Описание',
        price: 100,
        paymentMethodTypes: [
          PaymentMethodTypesEnum.BANK_CARD,
          PaymentMethodTypesEnum.GOOGLE_PAY,
        ],
        // isDebug: true,
      });

      console.log(
        `Tokenization was successful. paymentToken: ${paymentToken}, paymentMethodType: ${paymentMethodType}`
      );

      // ! VALIDATE TOKEN WITH YOUR API AND GET confirmationUrl
      const confirmationUrl = await emulateApiRequest(paymentToken);

      console.log(`Got confirmationUrl from your API: ${confirmationUrl}`);

      // ! RETURNING AGAIN TO YOOKASSA MODULE AND CONFIRM PAYMENT
      // ! BY PROVIDING `confirmationUrl` FROM YOUR API and `paymentMethodType`
      // ! FROM PREVIOUS PAYMENT PROCESSING
      await confirmPayment({ confirmationUrl, paymentMethodType });

      console.log('Payment was confirmed!');

      // ! CONFIRMATION WAS SUCCESSFUL, NOW YOOKASSA CAN BE CLOSED
      dismiss();

      // ! CONFIGURE YOUR API TO HANDLE YOOKASSA WEBHOOKS TO GET PAYMENT STATUS
    } catch (err) {
      if (err instanceof YooKassaError) {
        switch (err.code) {
          case ErrorCodesEnum.E_PAYMENT_CANCELLED:
            console.log('User cancelled YooKassa module.');
        }
      }
    }
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity style={styles.button} onPress={onPayPress}>
        <Text style={styles.buttonTitle}>Pay</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  button: {
    backgroundColor: 'black',
    width: 300,
    height: 60,
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonTitle: {
    color: 'white',
  },
});
