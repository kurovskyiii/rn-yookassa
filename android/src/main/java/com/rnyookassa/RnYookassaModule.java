package com.rnyookassa;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import com.facebook.react.bridge.WritableMap;
import com.rnyookassa.settings.Settings;
import com.rnyookassa.callbackError.CallbackError;
import com.rnyookassa.callbackError.CallbackErrorTypes;
import com.rnyookassa.callbackSuccess.CallbackTokenizeSuccess;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount;
import ru.yoomoney.sdk.kassa.payments.Checkout;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.GooglePayCardNetwork;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.GooglePayParameters;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.MockConfiguration;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters;
import ru.yoomoney.sdk.kassa.payments.TokenizationResult;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters;

public class RnYookassaModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  private static final int REQUEST_CODE_TOKENIZE = 33;
  private static final int REQUEST_CODE_3DSECURE = 35;
  private Callback paymentCallback;

  public RnYookassaModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addActivityEventListener(mActivityEventListener);
  }

  @Override
  public String getName() {
    return "RnYookassa";
  }

  @ReactMethod
  public void tokenize(ReadableMap obj, Callback callback) {

    final Settings settings = new Settings(this.reactContext);
    this.paymentCallback = callback;

    String clientApplicationKey = obj.getString("clientApplicationKey");
    String shopId = String.valueOf(obj.getString("shopId"));
    String title = obj.getString("title");
    String subtitle = obj.getString("subtitle");
    String amount = String.valueOf(obj.getDouble("price"));

    ReadableArray paymentTypes = obj.hasKey("paymentMethodTypes") ? obj.getArray("paymentMethodTypes") : null;
    String authCenterClientId = obj.hasKey("authCenterClientId") ? obj.getString("authCenterClientId") : null;
    String userPhoneNumber = obj.hasKey("userPhoneNumber") ? obj.getString("userPhoneNumber") : null;
    String gatewayId = obj.hasKey("gatewayId") ? obj.getString("gatewayId") : null;
    String returnUrl = obj.hasKey("returnUrl") ? obj.getString("returnUrl") : null;
    ReadableArray googlePaymentTypes = obj.hasKey("googlePaymentMethodTypes") ? obj.getArray("googlePaymentMethodTypes") : null;

    Boolean isDebug = obj.hasKey("isDebug") ? Boolean.valueOf(obj.getBoolean("isDebug")) : false;

    final Set<PaymentMethodType> paymentMethodTypes = getPaymentMethodTypes(paymentTypes, authCenterClientId != null);
    final Set<GooglePayCardNetwork> googlePaymentMethodTypes = getGooglePaymentMethodTypes(googlePaymentTypes);

    PaymentParameters paymentParameters = new PaymentParameters(
      new Amount(new BigDecimal(amount), Currency.getInstance("RUB")),
      title,
      subtitle,
      clientApplicationKey,
      shopId,
      settings.getSavePaymentMethod(),
      // optional:
      paymentMethodTypes,
      gatewayId,
      returnUrl,
      userPhoneNumber,
      new GooglePayParameters(googlePaymentMethodTypes),
      authCenterClientId
    );

    TestParameters testParameters = new TestParameters(true, true,
      new MockConfiguration(false, true, 5, new Amount(BigDecimal.TEN, Currency.getInstance("RUB"))));

    Intent intent = isDebug ?
      Checkout.createTokenizeIntent(this.reactContext, paymentParameters, testParameters) :
      Checkout.createTokenizeIntent(this.reactContext, paymentParameters);
    getCurrentActivity().startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
  }

  @ReactMethod
  public void confirmPayment(ReadableMap obj, Callback callback) {
    this.paymentCallback = callback;

    String confirmationUrl = obj.getString("confirmationUrl");
    String _paymentMethodType = String.valueOf(obj.getString("paymentMethodType"));
    PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(_paymentMethodType);

    Intent intent = Checkout.createConfirmationIntent(this.reactContext, confirmationUrl, paymentMethodType);
    Activity activity = getCurrentActivity();

    if (activity == null) {
      final CallbackError callbackErrorResult = new CallbackError(CallbackErrorTypes.E_UNKNOWN, "Payment confirmation error.");
      final WritableMap errorResultMap = generateErrorMapCallback(callbackErrorResult);

      paymentCallback.invoke(false, errorResultMap);
      return;
    }

    activity.startActivityForResult(intent, REQUEST_CODE_3DSECURE);
  }

  @ReactMethod
  public void dismiss() {
    return;
  }

  @NonNull
  private static Set<PaymentMethodType> getPaymentMethodTypes(ReadableArray paymentTypes, Boolean authCenterClientIdProvided) {
    final Set<PaymentMethodType> paymentMethodTypes = new HashSet<>();

    if (paymentTypes == null) {
      paymentMethodTypes.add(PaymentMethodType.BANK_CARD);
      paymentMethodTypes.add(PaymentMethodType.SBERBANK);
      paymentMethodTypes.add(PaymentMethodType.GOOGLE_PAY);

      if (authCenterClientIdProvided) {
        paymentMethodTypes.add(PaymentMethodType.YOO_MONEY);
      }
    } else {
      for (int i = 0; i < paymentTypes.size(); i++) {
        String upperType = paymentTypes.getString(i).toUpperCase();
        switch (upperType) {
          case "BANK_CARD":
            paymentMethodTypes.add(PaymentMethodType.BANK_CARD);
            break;
          case "SBERBANK":
            paymentMethodTypes.add(PaymentMethodType.SBERBANK);
            break;
          case "GOOGLE_PAY":
            paymentMethodTypes.add(PaymentMethodType.GOOGLE_PAY);
            break;
          case "YOO_MONEY":
            if (authCenterClientIdProvided) {
              paymentMethodTypes.add(PaymentMethodType.YOO_MONEY);
            }
            break;
        }
      }
    }

    return paymentMethodTypes;
  }

  @NonNull
  private static Set<GooglePayCardNetwork> getGooglePaymentMethodTypes(ReadableArray googlePaymentTypes) {
    final Set<GooglePayCardNetwork> googlePaymentMethodTypes = new HashSet<>();

    if (googlePaymentTypes == null) {
      googlePaymentMethodTypes.add(GooglePayCardNetwork.AMEX);
      googlePaymentMethodTypes.add(GooglePayCardNetwork.DISCOVER);
      googlePaymentMethodTypes.add(GooglePayCardNetwork.JCB);
      googlePaymentMethodTypes.add(GooglePayCardNetwork.MASTERCARD);
      googlePaymentMethodTypes.add(GooglePayCardNetwork.VISA);
      googlePaymentMethodTypes.add(GooglePayCardNetwork.INTERAC);
      googlePaymentMethodTypes.add(GooglePayCardNetwork.OTHER);
    } else {
      for (int i = 0; i < googlePaymentTypes.size(); i++) {
        String upperType = googlePaymentTypes.getString(i).toUpperCase();
        switch (upperType) {
          case "AMEX":
            googlePaymentMethodTypes.add(GooglePayCardNetwork.AMEX);
            break;
          case "DISCOVER":
            googlePaymentMethodTypes.add(GooglePayCardNetwork.DISCOVER);
            break;
          case "JCB":
            googlePaymentMethodTypes.add(GooglePayCardNetwork.JCB);
            break;
          case "MASTERCARD":
            googlePaymentMethodTypes.add(GooglePayCardNetwork.MASTERCARD);
            break;
          case "VISA":
            googlePaymentMethodTypes.add(GooglePayCardNetwork.VISA);
            break;
          case "INTERAC":
            googlePaymentMethodTypes.add(GooglePayCardNetwork.INTERAC);
            break;
          case "OTHER":
            googlePaymentMethodTypes.add(GooglePayCardNetwork.OTHER);
            break;
        }
      }
    }

    return googlePaymentMethodTypes;
  }

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

      if (requestCode == REQUEST_CODE_TOKENIZE) {

        switch (resultCode) {
          case Activity.RESULT_OK:
            final TokenizationResult result = Checkout.createTokenizationResult(data);
            String token = result.getPaymentToken();
            String type = result.getPaymentMethodType().name().toUpperCase();

            final CallbackTokenizeSuccess callbackReadyResult = new CallbackTokenizeSuccess(token, type);
            final WritableMap resultMap = generateTokenizeSuccessCallback(callbackReadyResult);

            paymentCallback.invoke(resultMap);
            break;

          case Activity.RESULT_CANCELED:
            final CallbackError callbackErrorResult = new CallbackError(CallbackErrorTypes.E_PAYMENT_CANCELLED, "Payment cancelled.");
            final WritableMap cancelledResultMap = generateErrorMapCallback(callbackErrorResult);

            paymentCallback.invoke(null, cancelledResultMap);
            break;
        }
      }

      if (requestCode == REQUEST_CODE_3DSECURE) {
        switch (resultCode) {
          case Activity.RESULT_OK:
            paymentCallback.invoke(true);
            break;

          case Activity.RESULT_CANCELED:
          case Checkout.RESULT_ERROR:
            final CallbackError callbackErrorResult = new CallbackError(CallbackErrorTypes.E_PAYMENT_CANCELLED, "Payment cancelled.");
            final WritableMap errorResultMap = generateErrorMapCallback(callbackErrorResult);

            paymentCallback.invoke(null, errorResultMap);
            break;
        }
      }

    }
  };

  private final WritableMap generateTokenizeSuccessCallback(CallbackTokenizeSuccess result) {
    final WritableMap resultMap = Arguments.createMap();
    resultMap.putString("paymentToken", result.getPaymentToken());
    resultMap.putString("paymentMethodType", result.getPaymentMethodType());

    return resultMap;
  }

  private final WritableMap generateErrorMapCallback(CallbackError error) {
    final WritableMap resultMap = Arguments.createMap();
    resultMap.putString("code", error.getCode().toString());
    resultMap.putString("message", error.getMessage());

    return resultMap;
  }
}
