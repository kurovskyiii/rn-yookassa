import * as React from 'react';
import { Alert, TouchableOpacity, StyleSheet, View, Text } from 'react-native';

import { tokenize, ErrorResult } from 'rn-yookassa';

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
