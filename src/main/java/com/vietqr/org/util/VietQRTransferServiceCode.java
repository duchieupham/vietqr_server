package com.vietqr.org.util;

public class VietQRTransferServiceCode {
	private static final String QUICK_TRANSFER_FROM_QR_TO_CARD = "QRIBFTTC";
	private static final String QUICK_TRANSFER_FROM_QR_TO_BANK_ACCOUNT = "QRIBFTTA";

	public static String getQuickTransferFromQrToCard() {
		return QUICK_TRANSFER_FROM_QR_TO_CARD;
	}

	public static String getQuickTransferFromQrToBankAccount() {
		return QUICK_TRANSFER_FROM_QR_TO_BANK_ACCOUNT;
	}


}
