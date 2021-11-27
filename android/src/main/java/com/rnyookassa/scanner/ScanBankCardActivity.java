package com.rnyookassa.scanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.graphics.Color;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import ru.yoomoney.sdk.kassa.payments.Checkout;

import static android.Manifest.permission.CAMERA;

public class ScanBankCardActivity extends AppCompatActivity {

  public static final int REQUEST_CODE = 1;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CODE);
  }

  @Override
  public void onRequestPermissionsResult(
    int requestCode,
    @NonNull String[] permissions,
    @NonNull int[] grantResults
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == REQUEST_CODE) {
      Intent scanIntent = new Intent(this, CardIOActivity.class);
      scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true);
      scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
      scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, Color.WHITE);
      scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
      scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
      startActivityForResult(scanIntent, REQUEST_CODE);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE) {
      if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
        CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
        final String cardNumber = scanResult.getFormattedCardNumber();

        if (scanResult.isExpiryValid() && scanResult.getRedactedCardNumber() != null &&
          !scanResult.getRedactedCardNumber().isEmpty()) {
          final Intent scanBankCardResult = Checkout.createScanBankCardIntent(
            cardNumber,
            scanResult.expiryMonth,
            scanResult.expiryYear % 100
          );
          setResult(RESULT_OK, scanBankCardResult);
        } else {
          setResult(RESULT_CANCELED);
        }
      } else {
        setResult(RESULT_CANCELED);
      }
      finish();
    }
  }
}
