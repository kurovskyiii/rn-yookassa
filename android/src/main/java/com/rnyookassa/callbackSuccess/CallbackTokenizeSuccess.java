package com.rnyookassa.callbackSuccess;

import androidx.annotation.NonNull;

public final class CallbackTokenizeSuccess {
  private String paymentToken;

  public String getPaymentToken() {
    return paymentToken;
  }

  private String paymentMethodType;

  public String getPaymentMethodType() {
    return paymentMethodType;
  }

  public CallbackTokenizeSuccess(@NonNull String paymentToken, @NonNull String paymentMethodType) {
    this.paymentToken = paymentToken;
    this.paymentMethodType = paymentMethodType;
  }
}
