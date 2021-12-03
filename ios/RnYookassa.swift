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
    func pay(_ info: NSDictionary, callbacker callback: @escaping RCTResponseSenderBlock) -> Void {
        self.callback = callback
        guard let clientApplicationKey = info["api_key"] as? String,
            let amountValue = info["amount"] as? NSNumber,
            let shopName = info["shop_name"] as? String,
            let purchaseDescription = info["purchase_description"] as? String,
            let paymentTypes = info["payment_types"] as? [String]
            else {
                return
            }
        var paymentMethodTypes: PaymentMethodTypes = []
        paymentTypes.forEach { type in
            if let payType = PaymentMethodType(rawValue: type) {
                paymentMethodTypes.insert(PaymentMethodTypes(rawValue: [payType]))
            }
        }
        let testModeSettings = TestModeSettings(paymentAuthorizationPassed: false,
                                                cardsCount: 2,
                                                charge: Amount(value: 100, currency: .rub),
                                                enablePaymentError: false)

        let tokenizationSettings = TokenizationSettings(paymentMethodTypes: paymentMethodTypes)

        let amount = Amount(value: amountValue.decimalValue, currency: .rub) // rub
        let tokenizationModuleInputData =
            TokenizationModuleInputData(clientApplicationKey: clientApplicationKey,
            shopName: shopName,
            purchaseDescription: purchaseDescription,
            amount: amount,
            tokenizationSettings: tokenizationSettings,
            testModeSettings: (info["test"] != nil) ? testModeSettings : nil,
            cardScanning: CardScannerProvider(),
            applePayMerchantIdentifier: info["applePayMerchantIdentifier"] as? String,
            returnUrl: (info["returnUrl"] != nil) ? info["returnUrl"] as? String : nil,
            savePaymentMethod: .userSelects
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
        if let callback = callback {
            callback([
                token.paymentToken,
                paymentMethodType.rawValue
            ])
            self.callback = nil
        }
    }


    func didFinish(on module: TokenizationModuleInput, with error: YooKassaPaymentsError?) {
        if let callback = callback {
            callback([
                "","","error"
            ])
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
            callback(["success"])
            confirmCallback = nil
        }
        viewController?.dismiss(animated: true)
        self.dismiss()
    }
}

