package com.rnyookassa.callbackSuccess;

import androidx.annotation.NonNull;

public final class CallbackTokenizeSuccess {
  private String token;

  public String getToken() {
    return token;
  }

  private String type;

  public String getType() {
    return type;
  }

  public CallbackTokenizeSuccess(@NonNull String token, @NonNull String type) {
    this.token = token;
    this.type = type;
  }
}
