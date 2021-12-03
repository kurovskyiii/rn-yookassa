package com.rnyookassa.callbackError;

import androidx.annotation.NonNull;

public final class CallbackError {
  private CallbackErrorTypes code;

  public CallbackErrorTypes getCode() {
    return code;
  }

  private String message;

  public String getMessage() {
    return message;
  }

  public CallbackError(@NonNull CallbackErrorTypes code, @NonNull String message) {
    this.code = code;
    this.message = message;
  }
}
