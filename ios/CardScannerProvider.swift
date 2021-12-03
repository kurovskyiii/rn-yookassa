import YooKassaPayments

class CardScannerProvider: NSObject, CardScanning {
    weak var cardScanningDelegate: CardScanningDelegate?
    var cardScanningViewController: UIViewController? {
        let cardIOPaymentViewController = CardIOPaymentViewController(paymentDelegate: self)
        cardIOPaymentViewController?.hideCardIOLogo = true
        cardIOPaymentViewController?.suppressScanConfirmation = true
        cardIOPaymentViewController?.disableManualEntryButtons = true
        cardIOPaymentViewController?.guideColor = .white
        return cardIOPaymentViewController
    }
}

extension CardScannerProvider: CardIOPaymentViewControllerDelegate {

    public func userDidProvide(_ cardInfo: CardIOCreditCardInfo!,
        in paymentViewController: CardIOPaymentViewController!) {
        let scannedCardInfo = ScannedCardInfo(number: cardInfo.cardNumber,
            expiryMonth: "\(cardInfo.expiryMonth)",
            expiryYear: "\(cardInfo.expiryYear)")
        cardScanningDelegate?.cardScannerDidFinish(scannedCardInfo)
    }

    public func userDidCancel(_ paymentViewController: CardIOPaymentViewController!) {
        cardScanningDelegate?.cardScannerDidFinish(nil)
    }
}
