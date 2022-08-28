# rn-yookassa – React Native YooKassa SDK Integration

Inspired by: [react-native-yookassa-payments](https://www.npmjs.com/package/react-native-yookassa-payments)

#### Android NATIVE SDK - 6.4.0

#### iOS NATIVE SDK - 6.2.0

<br/>

## Quick Navigation

- [Running Example App](#running-example-app)
- [STEP 1: Package Installation](#step-1-package-installation)
- [STEP 2: Native Installation](#step-2-native-installation)
  - [Android Installation](#android-installation)
  - [iOS Installation](#ios-installation)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)

<br/>

## Running Example App

```bash
git clone https://github.com/kurovskyi/rn-yookassa.git
```

Open project folder and install dependencies:

```bash
yarn
```

Run Example App on device:

```bash
# Android:
yarn example android
# iOS:
yarn example ios
```

<br/>

## STEP 1: Package Installation

```bash
yarn add rn-yookassa
```

– OR –

```bash
npm install rn-yookassa --save
```

<br/>

## STEP 2: Native Installation

<br/>

### Android Installation:

1.  Create `libs` folder in `android/app` directory and put there `ThreatMetrix Android SDK X.X-XX.aar` file (this file will given you by YooKassa manager by support request).<br/><br/>
    ❗️ Required TMX Android SDK version 5.4-XX because of issues with app icon on Android ❗️<br/><br/>
2.  Make sure your `android/build.gradle` looks like that. Pay attention for `minSdkVersion` and version of `com.android.tools.build:gradle`. This is the minimal requirements, so if you have higher versions it's ok.

```gradle
buildscript {
    ext {
        minSdkVersion = 21 // <- CHECK YOU HAVE THIS MINIMAL VERSION
        ...
    }

    ...

    dependencies {
        classpath("com.android.tools.build:gradle:3.5.4") // <- CHECK YOU HAVE THIS MINIMAL VERSION

        ...
    }

    ...
}

...
```

```gradle
...

allprojects {
    repositories {
        mavenCentral() // <- CHECK YOU HAVE THIS
        ...
    }

    ...
}
```

3.  Add following lines in `android/app/build.gradle` dependencies:

```gradle
...

dependencies {
    ...

    implementation fileTree(dir: "libs", include: ["*.aar"]) // <- ADD THIS LINE
}
```

4.  Add following lines in `android/app/src/main/res/values/strings.xml` to set scheme `your_unique_app_shceme` for processing **deep links**. It's required for payments via **SberPay**.

```xml
<resources>
    ...

    <string name="ym_app_scheme" translatable="false">your_unique_app_shceme</string> <!-- ADD THIS LINE -->
</resources>
```

6. Add this in AndroidManifest.xml for card scanning work:

```xml
<manifest ...>
    ...
    <uses-feature android:name="android.hardware.camera.autofocus" android:required="false" /> <!-- ADD THIS LINE -->
    <uses-feature android:name="android.hardware.camera.flash" android:required="false" /> <!-- ADD THIS LINE -->

    ...
</manifest>
```

<br/>

### iOS Installation:

1.  Change `ios/Podfile` like this:

```ruby
source 'https://github.com/CocoaPods/Specs.git' # <- ADD THIS LINE
source 'https://git.yoomoney.ru/scm/sdk/cocoa-pod-specs.git' # <- ADD THIS LINE

plugin 'cocoapods-user-defined-build-types' # <- ADD THIS LINE
enable_user_defined_build_types! # <- ADD THIS LINE

target 'ExampleApp' do
  ...

  # ADD THIS POD:
  pod 'YooKassaPayments',
    :build_type => :dynamic_framework,
    :git => 'https://git.yoomoney.ru/scm/sdk/yookassa-payments-swift.git',
    :tag => '6.6.0'

...

```

2. Create `Frameworks` folder in `ios` directory and put there `TMXProfiling.xcframework` and `TMXProfilingConnections.xcframework` (this frameworks will also given you by YooKassa manager by support request).

3. Add `TMXProfiling.xcframework` and `TMXProfilingConnections.xcframework` in **Frameworks, Libraries, and Embedded Content** in Xcode for the main target of the project under the **General** section.

4. Run `pod install`.<br/><br/> \* If you get `[!] Invalid Podfile file: undefined method 'enable_user_defined_build_types!'` error<br/>
   run command:

   ```bash
   sudo gem install cocoapods-user-defined-build-types
   ```

<br/>

## Usage

#### 1) Import functions and types from package

```typescript
import {
  tokenize,
  confirmPayment,
  dismiss,
  YooKassaError,
  ErrorCodesEnum,
  PaymentTypesEnum,
  GooglePaymentTypesEnum,
} from 'rn-yookassa';
```

#### 2) Create payment trigger

```typescript
const onPayPress = async () => {
  try {
    // Our next code will be here.
  } catch (err) {
    // Process errors from YooKassa module:
    if (err instanceof YooKassaError) {
      switch (err.code) {
        case ErrorCodesEnum.E_PAYMENT_CANCELLED:
          console.log('User cancelled YooKassa module.');
          break;
      }
    }
  }
};
```

You can check error is instance of `YooKassaError` and process error codes.
To process errors use `ErrorCodesEnum`.

#### 3) Run tokenization

You will get `paymentToken` for payment validation via your back-end and `paymentMethodType` for the next payment confirmation process.

❗️ Do not specify `returnUrl` if you want to confirm payment in-app.

```typescript
const { paymentToken, paymentMethodType } = await tokenize({
  clientApplicationKey: 'test_ABCDEF',
  shopId: '123456',
  title: 'iPhone 7',
  subtitle: 'Best device!',
  price: 1000,
  // OPTIONAL:
  paymentTypes: [PaymentTypesEnum.BANK_CARD, PaymentTypesEnum.APPLE_PAY],
  authCenterClientId: '123abc',
  userPhoneNumber: '+79301234567',
  gatewayId: '123abc',
  returnUrl: 'http://google.com',
  googlePaymentTypes: [
    GooglePaymentTypesEnum.VISA,
    GooglePaymentTypesEnum.MASTERCARD,
  ],
  applePayMerchantId: 'merchant.com.example',
  isDebug: false,
});
```

#### 4) Validate `paymentToken` with your API and return `confirmationUrl`

You also can check payments error on that stage (for example, `card_expired`). All error list available [here](https://yookassa.ru/en/developers/payments/declined-payments).

```typescript
const { confirmationUrl, paymentError } = await YOUR_API_REQUEST({
  confirmationUrl,
});

// Error validation here...
// Call dismiss() if you get an error and notify user.
```

#### 5) Confirm payment in-app (3-D Secure or another method)

If you get `confirmation_url` from the Payment object returned by API's response so you should confirm that payment.
Use here `paymentMethodType` from `tokenize()` result.

```typescript
await confirmPayment({ confirmationUrl, paymentMethodType });
```

#### 6) Close YooKassa window and show Success notification

```typescript
dismiss();
```

<br/>

### `tokenize({...PROPS})`

Open YooKassa window and create payment token.

#### PROPS Object

| Name                         | Type                            | Default                               | Description                                                                                                                                                                                                 |
| ---------------------------- | ------------------------------- | ------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **clientApplicationKey**     | string                          | ❗️ **REQUIRED**                      | Key for client apps from the YooMoney Merchant Profile ([Settings section — API keys](https://yookassa.ru/my/api-keys-settings)).                                                                           |
| **shopId**                   | string                          | ❗️ **REQUIRED**                      | Store's ID in YooMoney.                                                                                                                                                                                     |
| **title**                    | string                          | ❗️ **REQUIRED**                      | Product name.                                                                                                                                                                                               |
| **subtitle**                 | string                          | ❗️ **REQUIRED**                      | Product description.                                                                                                                                                                                        |
| **price**                    | number                          | ❗️ **REQUIRED**                      | Product price. Available payment methods can change depending on this parameter.                                                                                                                            |
| **paymentMethodTypes**       | PaymentMethodTypesEnum[]?       | All of `PaymentMethodTypesEnum`       | Array of needed payment methods. If you leave the field empty, the library will use all available payment methods.                                                                                          |
| **authCenterClientId**       | string?                         | undefined                             | App's ID for SDK authorization ru.yoomoney.sdk.auth, see [Registering an app for payments via the wallet](https://github.com/yoomoney/yookassa-android-sdk#registering-an-app-for-payments-via-the-wallet). |
| **userPhoneNumber**          | string?                         | undefined                             | User's phone number. It's used for autofilling fields for payments via SberPay. Supported format: "+7XXXXXXXXXX".                                                                                           |
| **gatewayId**                | string?                         | undefined                             | Gateway ID for the store.                                                                                                                                                                                   |
| **returnUrl**                | string?                         | undefined                             | Url of the page (only https supported) where you need to return after completing 3DS. If `confirmPayment()` is used, don't specify this parameter!                                                          |
| **googlePaymentMethodTypes** | GooglePaymentMethodTypesEnum[]? | All of `GooglePaymentMethodTypesEnum` | Array of needed payment methods for Google Pay (❗️ required for payments via Google Pay).                                                                                                                  |
| **applePayMerchantId**       | string?                         | undefined                             | Apple Pay merchant ID (❗️ required for payments via Apple Pay).                                                                                                                                            |
| **isDebug**                  | boolean?                        | false                                 | Enter to the Debug mode to test payments. In this mode you will receive fake tokens. If you want to test real tokens try to set up Test Shop in the YooKassa Panel.                                         |

#### Result Object

| Name                  | Type                   | Description                                                                  |
| --------------------- | ---------------------- | ---------------------------------------------------------------------------- |
| **paymentToken**      | string                 | Payment token that you need to pass to your API.                             |
| **paymentMethodType** | PaymentMethodTypesEnum | Payment method that was used. This will be used in the confirmation process. |

#### Error Object (can be `undefined`)

| Name        | Type           | Description                              |
| ----------- | -------------- | ---------------------------------------- |
| **code**    | ErrorCodesEnum | Error code that describes what happened. |
| **message** | string         | Something about the error.               |

<br/>

### `confirmPayment({...PROPS})`

Call this after you get `confirmation_url` from your API. Make sure you aren't specify `returnUrl` in the `tokenize()` function and didn't dismiss YooKassa window yet.

#### PROPS Object

| Name                  | Type                   | Default          | Description                                               |
| --------------------- | ---------------------- | ---------------- | --------------------------------------------------------- |
| **confirmationUrl**   | string                 | ❗️ **REQUIRED** | Confirmation url that you have got from your API.         |
| **paymentMethodType** | PaymentMethodTypesEnum | ❗️ **REQUIRED** | Payment method that was used in the tokenization process. |

#### Result Object

| Name                  | Type                   | Description                   |
| --------------------- | ---------------------- | ----------------------------- |
| **paymentMethodType** | PaymentMethodTypesEnum | Payment method that was used. |

#### Error Object (can be `undefined`)

| Name        | Type           | Description                              |
| ----------- | -------------- | ---------------------------------------- |
| **code**    | ErrorCodesEnum | Error code that describes what happened. |
| **message** | string         | Something about the error.               |

<br/>

### `dismiss()`

Close YooKassa window. Call it after successful payment confirmation.

<br/>

## Troubleshooting

### 1) If you see errors during `pod install`:

```
[!] Invalid Podfile file: undefined method `enable_user_defined_build_types!'
```

You need to install CocoaPods additional package:

```bash
sudo gem install cocoapods-user-defined-build-types
```

Then run `pod install` again.

### 2) If you see errors in Xcode Project like this:

```
Failed to build module 'MoneyAuth' from its module interface...
Compipiling for iOS 10.0, but module 'FunctiionalSwift' has a minimum deployment target iOS 11.0...
Typedef redefinition with different types ('uint8_t' (aka 'unsigned char'))...
```

You can resolve it by adding post_install in your Podfile:

```ruby
post_install do |installer|
  installer.pods_project.targets.each do |target|
  target.build_configurations.each do |config|
  if target.name == 'FunctionalSwift' || target.name == 'YooMoneyCoreApi'
    config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '10.0'
  else
  if target.name == 'RCT-Folly'
    config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '9.0'
  else
    config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '12.0'
                    end
                end
            end
        end
    end
end
```

https://github.com/yoomoney/yookassa-payments-swift/issues/93

For using your custom realization of **3DSecure confirmation**, specify `returnUrl`: string for redirect to your link. Not use `confirmPayment()` method with `returnUrl`.

<br/>

## TODO

- [ ] iOS Russian localization
- [ ] UI customization
- [ ] Refactoring native bridges
- [ ] Implementing bridges for YooMoney and SberPay
- [ ] Writing instructions for all payment methods
- [ ] Writing CHANGELOG.MD
- [ ] Writing MIGRATION.MD

<br/>

## License

MIT
