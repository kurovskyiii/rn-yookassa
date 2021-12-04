# rn-yookassa – YooKassa React Native Integration

Inspired by: [react-native-yookassa-payments](https://www.npmjs.com/package/react-native-yookassa-payments)

#### Android NATIVE SDK - 6.4.4
#### iOS NATIVE SDK - 6.4.0

## STEP 1: Package Installation

```bash
yarn add rn-yookassa
```
– OR –
```bash
npm install rn-yookassa --save
```

## STEP 2: Native Installation
### Android Installation:

1.  Create `libs` folder in `android/app` directory and put there `ThreatMetrix Android SDK X.X-XX.aar` file (this file will given you by YooKassa manager by support request).<br/><br/>
‼️  Required TMX Android SDK version > 6.2-XX ‼️<br/><br/>
2.  Make sure your `android/build.gradle` looks like that. Pay attention for `minSdkVersion`, `kotlinVersion`, version of `com.android.tools.build:gradle`. This is the minimal requirements, so if you have higher versions it's ok.

```gradle
buildscript {
    ext {
        minSdkVersion = 21 // <- CHECK YOU HAVE THIS MINIMAL VERSION
        kotlinVersion = "1.5.20" // <- CHECK YOU HAVE THIS
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
    
    implementation 'ru.yoomoney.sdk.kassa.payments:yookassa-android-sdk:6.4.4' // <- ADD THIS LINE
    implementation fileTree(dir: "libs", include: ["*.aar"]) // <- ADD THIS LINE
}
```

4.  Add following lines in `android/app/src/main/res/values/strings.xml` to set scheme `SCHEME` for processing **deep links**. It's required for payments via **SberPay**.

```xml
<resources>
    ...
  
    <string name="ym_app_scheme" translatable="false">SCHEME</string> <!-- ADD THIS LINE -->
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

### iOS Installation:

1.  Change `ios/Podfile` like this:

```ruby
source 'https://github.com/CocoaPods/Specs.git' # <- ADD THIS LINE
source 'https://github.com/yoomoney-tech/cocoa-pod-specs.git' # <- ADD THIS LINE

plugin 'cocoapods-user-defined-build-types' # <- ADD THIS LINE
enable_user_defined_build_types! # <- ADD THIS LINE

target 'ExampleApp' do
  ...

  # ADD THIS POD:
  pod 'YooKassaPayments',
    :build_type => :dynamic_framework,
    :git => 'https://github.com/yoomoney/yookassa-payments-swift.git',
    :tag => '6.4.0' 

...

```

2. Create `Frameworks` folder in `ios` directory and put there `TMXProfiling.xcframework` and `TMXProfilingConnections.xcframework` (this frameworks will also given you by YooKassa manager by support request).

3.  Add `TMXProfiling.xcframework` and `TMXProfilingConnections.xcframework` in **Frameworks, Libraries, and Embedded Content** in Xcode for the main target of the project under the **General** section.

4. Run `pod install`.

5. (Optional) Russian Localization
    - In your Xcode project => Info => Localization => Click "+" => Add Russian language
    - Copy everything from ios/yookassa-payments-swift-6.0.0/YooKassaPayments/Public/Resources/ru.lproj/Localizable.strings
    - In your Xcode project => File => New File => Strings File => Localizable.strings => Open new created Localizable.strings and paste all copy strings
    - After pasting strings look at Xcode right side and find a Localization menu => Choose Russian language

## STEP 3: Usage

```typescript
import { tokenize, PaymentTypesEnum, GooglePaymentTypesEnum } from 'rn-yookassa';

const onPayPress = () => {
    tokenize({
      clientApplicationKey: 'test_ABCDE',
      shopId: '123456',
      title: 'iPhone 7',
      subtitle: 'Best device!',
      price: 1000,
      // OPTIONAL:
      paymentTypes: [PaymentTypesEnum.BANK_CARD, PaymentTypesEnum.APPLE_PAY],
      authCenterClientId: '123abc',
      userPhoneNumber: '79301234567',
      gatewayId: '123abc',
      returnUrl: 'http://google.com',
      googlePaymentTypes: [GooglePaymentTypesEnum.VISA, GooglePaymentTypesEnum.MASTERCARD],
      applePayMerchantId: 'merchantABC',
      isDebug: false
    })
      .then(
        (result) => 
          console.log(`${result.token} ${result.type}`)
       )
      .catch(
        (err: ErrorResult) => 
          console.log(`${err.code} ${err.message}`)
       );
}
```
### `tokenize` props

| Name                     |            Type           |              Default            | Description                                                                                                                                                            |
| ------------------------ | ------------------------- | ------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **clientApplicationKey** | string                    | ❗️ **REQUIRED**                 | Key for client apps from the YooMoney Merchant Profile ([Settings section — API keys](https://yookassa.ru/my/api-keys-settings)). |
| **shopId**               | string                    | ❗️ **REQUIRED**                 | Store's ID in YooMoney. |
| **title**                | string                    | ❗️ **REQUIRED**                 | Product name. |
| **subtitle**             | string                    | ❗️ **REQUIRED**                 | Product description. |
| **price**                | number                    | ❗️ **REQUIRED**                 | Product price. Available payment methods can change depending on this parameter. |
| **paymentTypes**         | PaymentTypesEnum[]?       | All of `PaymentTypesEnum`       | Array of needed payment methods. If you leave the field empty, the library will use all available payment methods. |
| **authCenterClientId**   | string?                   | undefined                       | App's ID for sdk authorization ru.yoomoney.sdk.auth, see [Registering an app for payments via the wallet](https://github.com/yoomoney/yookassa-android-sdk#registering-an-app-for-payments-via-the-wallet). |
| **userPhoneNumber**      | string?                   | undefined                       | User's phone number. It's used for autofilling fields for payments via SberPay. Supported format: "+7XXXXXXXXXX". |
| **gatewayId**            | string?                   | undefined                       | Gateway ID for the store. |
| **returnUrl**            | string?                   | undefined                       | Url of the page (only https supported) where you need to return after completing 3DS. If `confirmPaymen()` is used, don't specify this parameter! |
| **googlePaymentTypes**   | GooglePaymentTypesEnum[]? | All of `GooglePaymentTypesEnum` | Array of needed payment methods for Google Pay (❗️ required for payments via Google Pay). |
| **applePayMerchantId**   | string?                   | undefined                       | Apple Pay merchant ID (❗️ required for payments via Apple Pay) |
| **isDebug**              | boolean?                  | false                           | Enter to the Debug mode to test payments. In this mode you will receive fake tokens. If you want to test real tokens try to set up Test Shop in the YooKassa Panel. |

### Troubleshooting

If you see errors in Xcode Project like this:

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

## License

MIT
