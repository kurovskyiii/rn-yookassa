import * as React from 'react';
import { Alert, TouchableOpacity, StyleSheet, View, Text } from 'react-native';

import { tokenize, confirmPayment, ErrorResult } from 'rn-yookassa';

export default function App() {
  const onPayPress = () => {
    tokenize({
      clientApplicationKey: 'test_ODIzNDE0PrQiM6dYe7ypegMZcKq6b_Rk7Zok467bGao',
      shopId: '823414',
      title: 'Товар',
      subtitle: 'Описание',
      price: 100,
      // isDebug: true,
    })
      .then((result) => Alert.alert(`${result.token} ${result.type}`))
      .catch((err: ErrorResult) => Alert.alert(`${err.code} ${err.message}`));
  };

  const onConfirmPress = async () => {
    const url =
      'https://3ds-gate.yoomoney.ru/card-auth?acsUri=https%3A%2F%2Fyookassa.ru%2Fsandbox%2Fbank-card%2F3ds&MD=1638920652394-5487315384260906123&PaReq=Q1VSUkVOQ1k9UlVCJk9SREVSPTI5NDIwNThiLTAwMGYtNTAwMC1hMDAwLTE4MGNkN2Y3ZGU4NiZURVJNSU5BTD05OTk5OTgmRVhQX1lFQVI9MzAmQU1PVU5UPTIzNS4wMCZDQVJEX1RZUEU9UEFOJlRSVFlQRT0wJkVYUD0wMSZDVkMyPTEyMyZDQVJEPTU1NTU1NTU1NTU1NTQ0NzcmTkFNRT0%3D&TermUrl=https%3A%2F%2Fpaymentcard.yoomoney.ru%3A443%2F3ds%2Fchallenge%2F241%2FFWxpokXcVtu4SuHuKZSfj0pVoT8Z..001.202112';

    const confirmationResult = await confirmPayment(url);

    Alert.alert(JSON.stringify(confirmationResult));
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity style={styles.button} onPress={onConfirmPress}>
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
