import Foundation
import YooKassaPayments

@objc(RnYookassa)
class RnYookassa: RCTViewManager, TokenizationModuleOutput {

    var callback: RCTResponseSenderBlock?
    var confirmCallback: RCTResponseSenderBlock?
    var viewController: UIViewController?

    func didSuccessfullyConfirmation(paymentMethodType: PaymentMethodType) {

    }

    @objc(cancel)
    func dismiss() {
        DispatchQueue.main.async {
            self.viewController?.dismiss(animated: true)
        }
    }

    @objc
    func confirmPayment(_ url: String, callbacker callback: @escaping RCTResponseSenderBlock) -> Void {
        guard let viewController = viewController as? TokenizationModuleInput else { return }
        confirmCallback = callback
        viewController.start3dsProcess(requestUrl: url)
    }

    @objc
    func tokenize(_ info: NSDictionary, callbacker callback: @escaping RCTResponseSenderBlock) -> Void {
        self.callback = callback
        guard let clientApplicationKey = info["clientApplicationKey"] as? String,
            let shopId = info["shopId"] as? String,
            let title = info["title"] as? String,
            let subtitle = info["subtitle"] as? String,
            let amountValue = info["price"] as? NSNumber
        else {
            return
        }
        
            // Optional:
            let paymentTypes = info["paymentTypes"] as? [String]
            let authCenterClientId = info["authCenterClientId"] as? String
            let userPhoneNumber = info["userPhoneNumber"] as? String
            let gatewayId = info["gatewayId"] as? String
            let applePayMerchantId = info["applePayMerchantId"] as? String
            let returnUrl = info["returnUrl"] as? String
            let isDebug = info["isDebug"] as? Bool
  
        var paymentMethodTypes: PaymentMethodTypes = []
        
        if (paymentTypes != nil) {
            paymentTypes!.forEach { type in
                if let payType = PaymentMethodType(rawValue: type) {
                    paymentMethodTypes.insert(PaymentMethodTypes(rawValue: [payType]))
                }
            }
        } else {
            paymentMethodTypes.insert(.bankCard)
            paymentMethodTypes.insert(.sberbank)
            paymentMethodTypes.insert(.yooMoney)
            
            if (applePayMerchantId != nil) {
                paymentMethodTypes.insert(.applePay)
            }
        }
        
        let testModeSettings = TestModeSettings(paymentAuthorizationPassed: false,
                                                cardsCount: 2,
                                                charge: Amount(value: 10, currency: .rub),
                                                enablePaymentError: false)

        let tokenizationSettings = TokenizationSettings(paymentMethodTypes: paymentMethodTypes)

        let amount = Amount(value: amountValue.decimalValue, currency: .rub) // rub
        let tokenizationModuleInputData =
            TokenizationModuleInputData(clientApplicationKey: clientApplicationKey,
            shopName: title,
            purchaseDescription: subtitle,
            amount: amount,
            gatewayId: gatewayId,
            tokenizationSettings: tokenizationSettings,
            testModeSettings: (isDebug != nil) ? testModeSettings : nil,
            cardScanning: CardScannerProvider(),
            applePayMerchantIdentifier: applePayMerchantId,
            returnUrl: returnUrl,
            isLoggingEnabled: (isDebug != nil) ? true : false,
            userPhoneNumber: userPhoneNumber,
            savePaymentMethod: .userSelects,
            moneyAuthClientId: authCenterClientId,
            customerId: userPhoneNumber
        )

        DispatchQueue.main.async {
            let inputData: TokenizationFlow = .tokenization(tokenizationModuleInputData)
            self.viewController = TokenizationAssembly.makeModule(inputData: inputData, moduleOutput: self)
            let rootViewController = UIApplication.shared.keyWindow!.rootViewController!
            rootViewController.present(self.viewController!, animated: true, completion: nil)
        }
    }

    func tokenizationModule(_ module: TokenizationModuleInput,
                            didTokenize token: Tokens,
                            paymentMethodType: PaymentMethodType) {
        let result: NSDictionary = [
            "token" : token.paymentToken,
            "type" : paymentMethodType.rawValue.uppercased()
        ]
        
        if let callback = callback {
            callback([result])
            self.callback = nil
        }
        
        DispatchQueue.main.async {
            self.viewController?.dismiss(animated: true)
        }
    }


    func didFinish(on module: TokenizationModuleInput, with error: YooKassaPaymentsError?) {
        let error: NSDictionary = [
            "code" : "E_PAYMENT_CANCELLED",
            "message" : "Payment cancelled."
        ]
        
        if let callback = callback {
            callback([NSNull(), error])
            self.callback = nil
        }
        DispatchQueue.main.async {
            self.viewController?.dismiss(animated: true)
        }
    }

    override class func requiresMainQueueSetup() -> Bool {
        return false
    }

    func didSuccessfullyPassedCardSec(on module: TokenizationModuleInput) {
        if let callback = self.confirmCallback {
            callback([true])
            confirmCallback = nil
        }
        viewController?.dismiss(animated: true)
        self.dismiss()
    }
}

