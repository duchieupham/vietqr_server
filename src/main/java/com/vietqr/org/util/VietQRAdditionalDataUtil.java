package com.vietqr.org.util;

public class VietQRAdditionalDataUtil {
	 //Cấu trúc dữ liệu cho Thông tin bổ sung (ID “62”) cho VietQR trong Dịch vụ Chuyển nhanh NAPAS247
	  //
	  //Số hóa đơn
	  private static final String BILL_NUMBER_ID = "01";
	  //Số điện thoại di động
	  private static final String MOBILE_NUMBER_ID = "02";
	  //Mã cửa hàng
	  private static final String STORE_LABEL_ID = "03";
	  //Mã khách hàng thân thiết
	  private static final String LOYALTY_NUMBER_ID = "04";
	  //Mã tham chiếu
	  private static final String REFERENCE_LABEL_ID = "05";
	  //Mã khách hàng
	  private static final String CUSTOMER_LABEL_ID = "06";
	  //Mã số điểm bán hàng
	  private static final String TERMINAL_LABEL_ID = "07";
	  //Mục đích giao dịch
	  private static final String PURPOSE_OF_TRANSACTION_ID = "08";
	  //Yêu cầu dữ liệu KH bổ sung
	  private static final String ADDITIONAL_CONSUMER_DATA_REQUEST_ID = "09";
	  //Đăng ký bởi EMVCo
	  //10-49
	  //
	  //Hệ thống thanh toán cụ thể
	  //50-99
	  //
	public static String getBillNumberId() {
		return BILL_NUMBER_ID;
	}
	public static String getMobileNumberId() {
		return MOBILE_NUMBER_ID;
	}
	public static String getStoreLabelId() {
		return STORE_LABEL_ID;
	}
	public static String getLoyaltyNumberId() {
		return LOYALTY_NUMBER_ID;
	}
	public static String getReferenceLabelId() {
		return REFERENCE_LABEL_ID;
	}
	public static String getCustomerLabelId() {
		return CUSTOMER_LABEL_ID;
	}
	public static String getTerminalLabelId() {
		return TERMINAL_LABEL_ID;
	}
	public static String getPurposeOfTransactionId() {
		return PURPOSE_OF_TRANSACTION_ID;
	}
	public static String getAdditionalConsumerDataRequestId() {
		return ADDITIONAL_CONSUMER_DATA_REQUEST_ID;
	}

}
