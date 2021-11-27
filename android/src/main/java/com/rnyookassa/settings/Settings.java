package com.rnyookassa.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod;

public final class Settings {

  static final String KEY_SAVE_PAYMENT_METHOD = "save_payment_method";

  private SharedPreferences sp;

  public Settings(@NonNull Context context) {
    sp = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public SavePaymentMethod getSavePaymentMethod() {
    return getSavePaymentMethod(getSavePaymentMethodId());
  }

  int getSavePaymentMethodId() {
    return sp.getInt(KEY_SAVE_PAYMENT_METHOD, 0);
  }

  private static SavePaymentMethod getSavePaymentMethod(int value) {
    SavePaymentMethod savePaymentMethod;
    switch (value) {
      case 0:
        savePaymentMethod = SavePaymentMethod.USER_SELECTS;
        break;
      case 1:
        savePaymentMethod = SavePaymentMethod.ON;
        break;
      case 2:
        savePaymentMethod = SavePaymentMethod.OFF;
        break;
      default:
        savePaymentMethod = SavePaymentMethod.USER_SELECTS;
    }
    return savePaymentMethod;
  }
}
