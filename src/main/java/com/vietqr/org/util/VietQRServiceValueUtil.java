package com.vietqr.org.util;

public class VietQRServiceValueUtil {
	  //Payload Format Indicator value
	  private static final String PAYLOAD_FORMAT_INDICATOR_VALUE = "01";
	  //Point of Initiation Method value
	  //phương thức khởi tạo dùng cho QR tĩnh - sử dụng QR được nhiều liền
	  private static final String POINT_OF_INITIATION_METHOD_VALUE_STATIC = "11";
	  //phương thức khởi tạo dùng cho QR động - sử dụng QR chỉ được 1 lần duy nhất
	  private static final String POINT_OF_INITIATION_METHOD_VALUE = "12";
	  //Transaction Currency value
	  private static final String TRANSACTION_CURRENCY_VALUE = "704";
	  //Country Code value
	  private static final String COUNTRY_CODE_VALUE = "VN";
	  //CRC (Cyclic Redundancy Check) default length
	  private static final String CRC_LENGTH = "04";

	public static String getPayloadFormatIndicatorValue() {
		return PAYLOAD_FORMAT_INDICATOR_VALUE;
	}
	public static String getPointOfInitiationMethodValueStatic() {
		return POINT_OF_INITIATION_METHOD_VALUE_STATIC;
	}
	public static String getPointOfInitiationMethodValue() {
		return POINT_OF_INITIATION_METHOD_VALUE;
	}
	public static String getTransactionCurrencyValue() {
		return TRANSACTION_CURRENCY_VALUE;
	}
	public static String getCountryCodeValue() {
		return COUNTRY_CODE_VALUE;
	}
	public static String getCrcLength() {
		return CRC_LENGTH;
	}


}
