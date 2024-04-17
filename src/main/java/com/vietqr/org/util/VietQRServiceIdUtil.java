package com.vietqr.org.util;

public class VietQRServiceIdUtil {
	//Cấu trúc dữ liệu gốc gốc của VietQR trong dịch vụ chuyển nhanh NAPAS247
	  //Phiên bản dữ liệu
	  private static final String PAYLOAD_FORMAT_INDICATOR_ID = "00";
	  //Phương thức khởi tạo
	  private static final String POINT_OF_INITIATION_METHOD_ID = "01";
	  //Thông tin định danh ĐVCNTT
	  private static final String MERCHANT_ACCOUNT_INFORMATION_ID = "38";
	  //Mã danh mục ĐVCNTT
	  private static final String MERCHANT_CATEGORY_CODE_ID = "52";
	  //Mã tiền tệ
	  private static final String TRANSACTION_CURRENCY_ID = "53";
	  //Số tiền giao dịch
	  private static final String TRANSACTION_AMOUNT_ID = "54";
	  //Chỉ thị cho Tip và phí giao dịch
	  private static final String TIP_OR_CONVENIENCE_INDICATOR_ID = "55";
	  //Giá trị phí cố định
	  private static final String VALUE_OF_CONVENIENCE_FEE_FIXED_ID = "56";
	  //Giá trị phí tỷ lệ phần trăm
	  private static final String VALUE_OF_CONVENIENCE_FEE_PERCENTAGE_ID = "57";
	  //Mã quốc gia
	  private static final String COUNTRY_CODE_ID = "58";
	  //Tên ĐVCNTT
	  private static final String MERCHANT_NAME_ID_ID = "59";
	  //Thành phố của ĐVCNTT
	  private static final String MERCHANT_CITY_ID = "60";
	  //Mã bưu điện
	  private static final String POSTAL_CODE_ID = "61";
	  //Thông tin bổ sung
	  private static final String ADDITIONAL_DATA_FIELD_TEMPLATE_ID = "62";
	  //Thông tin ĐVCNTT khuân mẫu ngôn ngữ thay thế
	  private static final String MERCHANT_INFORMATION_LANGUAGE_TEMPLATE_ID = "64";
	  //Đăng ký bởi EMVCo - RFU for EMVCo
	  //65-79
//	  private static final List<String> RFU_FOR_EMVCO = ["65", "66", "67"]; //...
//	  //Các thông tin bổ sung đăng ký dùng trong tương lai - Unreserved Templates
//	  //80-99
//	  private static final List<String> UNRESERVED_TEMPLATES_ID = ["80", "81", "82"]; //...
	  //Cyclic Redundancy Check
	  private static final String CRC_ID = "63";
	  //Mã dịch vụ
	  private static final String TRANSFER_SERVCICE_CODE = "02";

	public static String getPayloadFormatIndicatorId() {
		return PAYLOAD_FORMAT_INDICATOR_ID;
	}
	public static String getPointOfInitiationMethodId() {
		return POINT_OF_INITIATION_METHOD_ID;
	}
	public static String getMerchantAccountInformationId() {
		return MERCHANT_ACCOUNT_INFORMATION_ID;
	}
	public static String getMerchantCategoryCodeId() {
		return MERCHANT_CATEGORY_CODE_ID;
	}
	public static String getTransactionCurrencyId() {
		return TRANSACTION_CURRENCY_ID;
	}
	public static String getTransactionAmountId() {
		return TRANSACTION_AMOUNT_ID;
	}
	public static String getTipOrConvenienceIndicatorId() {
		return TIP_OR_CONVENIENCE_INDICATOR_ID;
	}
	public static String getValueOfConvenienceFeeFixedId() {
		return VALUE_OF_CONVENIENCE_FEE_FIXED_ID;
	}
	public static String getValueOfConvenienceFeePercentageId() {
		return VALUE_OF_CONVENIENCE_FEE_PERCENTAGE_ID;
	}
	public static String getCountryCodeId() {
		return COUNTRY_CODE_ID;
	}
	public static String getMerchantNameIdId() {
		return MERCHANT_NAME_ID_ID;
	}
	public static String getMerchantCityId() {
		return MERCHANT_CITY_ID;
	}
	public static String getPostalCodeId() {
		return POSTAL_CODE_ID;
	}
	public static String getAdditionalDataFieldTemplateId() {
		return ADDITIONAL_DATA_FIELD_TEMPLATE_ID;
	}
	public static String getMerchantInformationLanguageTemplateId() {
		return MERCHANT_INFORMATION_LANGUAGE_TEMPLATE_ID;
	}
	public static String getCrcId() {
		return CRC_ID;
	}
	public static String getTransferServciceCode() {
		return TRANSFER_SERVCICE_CODE;
	}
}
