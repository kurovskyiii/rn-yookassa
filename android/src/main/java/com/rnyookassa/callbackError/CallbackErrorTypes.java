package com.rnyookassa.callbackError;

import androidx.annotation.NonNull;

public enum CallbackErrorTypes {
  E_UNKNOWN {
    @NonNull
    @Override
    public String toString() {
      return "E_UNKNOWN";
    }
  },
  E_PAYMENT_CANCELLED {
    @NonNull
    @Override
    public String toString() {
      return "E_PAYMENT_CANCELLED";
    }
  },
}
