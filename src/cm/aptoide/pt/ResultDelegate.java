package cm.aptoide.pt;

import com.paypal.android.MEP.PayPalResultDelegate;

import java.io.Serializable;


public class ResultDelegate implements PayPalResultDelegate, Serializable {

	private static final long serialVersionUID = 10001L;

	public void onPaymentSucceeded(String payKey, String paymentStatus) {
		Buy.resultTitle = "SUCCESS";
		Buy.resultInfo = "You have successfully completed your transaction.";
		Buy.resultExtra = "Key: " + payKey;
	}

	public void onPaymentFailed(String paymentStatus, String correlationID, String payKey, String errorID, String errorMessage) {
		Buy.resultTitle = "FAILURE";
		Buy.resultInfo = errorMessage;
		Buy.resultExtra = "Error ID: " + errorID + "\nCorrelation ID: "+ correlationID + "\nPay Key: " + payKey;
	}

	public void onPaymentCanceled(String paymentStatus) {
		Buy.resultTitle = "CANCELED";
		Buy.resultInfo = "The transaction has been cancelled.";
		Buy.resultExtra = "";
	}
}
