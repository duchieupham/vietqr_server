package com.vietqr.org.util;

public class NotificationUtil {

	// Type
	private static final String NOTI_TYPE_CONNECT_SUCCESS = "N00";
	private static final String NOTI_TYPE_LOGIN = "N02";
	private static final String NOTI_TYPE_TRANSACTION = "N01";
	private static final String NOTI_TYPE_NEW_MEMBER = "N03";
	private static final String NOTI_TYPE_NEW_TRANSACTION = "N04";
	private static final String NOTI_TYPE_UPDATE_TRANSACTION = "N05";
	private static final String NOTI_TYPE_VOICE_TRANSACTION = "N06";
	private static final String NOTI_TYPE_ADD_MEMBER = "N07";
	private static final String NOTI_TYPE_REMOVE_MEMBER = "N08";
	private static final String NOTI_ADD_VIETQR_ID = "N09";
	private static final String NOTI_RECHARGE = "N10";
	private static final String NOTI_MOBILE_TOPUP = "N11";
	private static final String NOTI_TYPE_CANCEL_TRANSACTION = "N12";
	private static final String NOTI_TYPE_ACTIVE_KEY = "N13";
	private static final String NOTI_TYPE_PAYMENT_SUCCESS_VA_INVOICE = "N14";
	private static final String NOTI_TYPE_PAYMENT_INVOICE_SUCCESS = "N15";
	private static final String NOTI_CONNECT_QR_SUCCESS = "N16";
	private static final String NOTI_SEND_DYNAMIC_QR = "N17";
	private static final String NOTI_SEND_INVOICE = "N18";
	private static final String NOTI_SEND_INVOICE_CREATE = "N19";

	// Title
	private static final String NOTI_TITLE_TRANSACTION = "Thông báo biến động số dư";
	private static final String NOTI_TITLE_LOGIN_WARNING = "Đăng nhập vào thiết bị mới";
	private static final String NOTI_TITLE_NEW_MEMBER = "Thành viên mới";
	private static final String NOTI_TITLE_NEW_TRANSACTION = "Giao dịch mới";
	private static final String NOTI_TITLE_INVOICE_UNPAID = "Thông báo hoá đơn chưa thanh toán";
	private static final String NOTI_TITLE_INVOICE = "Thông báo thanh toán hoá đơn";
	private static final String NOTI_TITLE_INVOICE_TOTAL_AMOUNT = " với số tiền: ";
	private static final String NOTI_TITLE_UPDATE_TRANSACTION = "Biến động số dư";
	private static final String NOTI_TITLE_ADD_MEMBER = "Thông báo chia sẻ giao dịch cho cửa hàng";
	private static final String NOTI_TITLE_REMOVE_MEMBER = "Thông báo chia sẻ biến động số dư";
	private static final String NOTI_TITLE_ADD_VIETQR_ID = "Thêm bạn bè";
	private static final String NOTI_DESC_PAYMENT_INVOICE = "Quý khách đã thanh toán thành công hoá đơn có mã HĐ: ";
	private static final String NOTI_TITLE_INVOICE_UNFAID = "Thông bán. Vui lòng trên hệ thống VietQR VN.";
	private static final String NOTI_TITLE_RECHARGE = "Thanh toán thành công";
	private static final String NOTI_TITLE_MOBILE_TOPUP = "Nạp điện thoại thành công";
	private static final String NOTI_TITLE_MOBILE_TOPUP_FAILED = "Nạp điện thoại thất bại. Số tiền được hoàn lại vào tài khoản của bạn trên hệ thống VietQR VN.";
	private static final String NOTI_TITLE_PAYMENT_SUCCESS_VA_INVOICE = "Thanh toán hoá đơn thành công";

	// Description
	private static final String NOTI_DESC_LOGIN_WARNING_PREFIX = "Chúng tôi nhận thấy có lượt đăng nhập từ thiết bị ";
	private static final String NOTI_DESC_NEW_MEMBER_PREFIX = "Bạn vừa được thêm vào tài khoản ";
	private static final String NOTI_DESC_NEW_MEMBER_SUFFIX = " để quản lý đối soát thanh toán";
	private static final String NOTI_DESC_NEW_TRANS_PREFIX = "Giao dịch mới từ chi nhánh ";
	private static final String NOTI_DESC_NEW_TRANS_PREFIX2 = "Giao dịch mới";
	private static final String NOTI_DESC_NEW_TRANS_SUFFIX_1 = " được tạo. Số tiền ";
	private static final String NOTI_DESC_NEW_TRANS_SUFFIX_2 = " VND";
	private static final String NOTI_DESC_UPDATE_TRANS_SUFFIX_1 = "TK ";
	private static final String NOTI_DESC_UPDATE_TRANS_SUFFIX_2 = "|GD: ";
	private static final String NOTI_DESC_UPDATE_TRANS_SUFFIX_3 = "|CN: ";
	private static final String NOTI_DESC_UPDATE_TRANS_SUFFIX_4 = "|ND: ";
	private static final String NOTI_DESC_ADD_MEMBER = "Bạn đã được quản lí cửa hàng %s chia sẻ thông báo giao dịch.";
	private static final String NOTI_DESC_REMOVE_MEMBER = "Bạn vừa được quản lý xoá khỏi Doanh nghiệp ";
	private static final String NOTI_DESC_ADD_VIETQR_ID = " vừa thêm bạn vào danh bạ của họ. Hãy trở thành bạn bè với nhau trên VietQR VN";
	private static final String NOTI_DESC_RECHARGE_1 = "Quý khách đã nạp thành công số tiền ";
	private static final String NOTI_DESC_RECHARGE_2 = " VQR. Cảm ơn quý khách đã sử dụng dịch vụ của VietQR VN";
	private static final String NOTI_DESC_MOBILE_TOPUP_1 = "Quý khách đã nạp thành công số tiền ";
	private static final String NOTI_DESC_MOBILE_TOPUP_2 = " VND cho số điện thoại ";
	private static final String NOTI_DESC_MOBILE_TOPUP_3 = ". Cảm ơn quý khách đã sử dụng dịch vụ của VietQR VN";
	private static final String NOTI_DESC_ACTIVE_KEY_1 = "Quý khách đã thanh toán thành công số tiền ";
	private static final String NOTI_DESC_ACTIVE_KEY_2 = " VND cho dịch vụ nhận BĐSD";
	private static final String NOTI_DESC_ACTIVE_KEY_3 = ". Cảm ơn quý khách đã sử dụng dịch vụ của VietQR VN";
	private static final String NOTI_DESC_MOBILE_TOPUP_FAILED = "Có vấn đề xảy ra trong quá trình nạp tiền trong hệ thống VietQR. Chúng tôi sẽ xem xét và xử lý sớm, mong quý khách thông cảm về vấn đề này.";
	private static final String NOTI_DESC_PAYMENT_SUCCESS_VA_INVOICE_1 = "Quý khách đã thanh toán thành công ";
	private static final String NOTI_DESC_PAYMENT_SUCCESS_VA_INVOICE_2 = " VND cho hoá đơn ";
	private static final String NOTI_PAYMENT_ANNUAL_FEE_VIETQR_NAME = "Thanh toán dịch vụ phần mềm VietQR ";
	private static final String NOTI_PAYMENT_BDSD_VIETQR_1 = "Thanh toán dịch vụ nhận BĐSD ";
	private static final String NOTI_PAYMENT_BDSD_VIETQR_2 = "cho số TK: ";

	public static final String getNotiConnectQrSuccess() {
		return NOTI_CONNECT_QR_SUCCESS;
	}

	public static final String getNotiSendDynamicQr() {
		return NOTI_SEND_DYNAMIC_QR;
	}
	public static String getNotiTypeConnectSuccess() {
		return NOTI_TYPE_CONNECT_SUCCESS;
	}
	public static String getNotiPaymentAnnualFeeVietqrName() {
		return NOTI_PAYMENT_ANNUAL_FEE_VIETQR_NAME;
	}

	public static String getNotiPaymentBdsdVietqr1() {
		return NOTI_PAYMENT_BDSD_VIETQR_1;
	}

	public static String getNotiPaymentBdsdVietqr2() {
		return NOTI_PAYMENT_BDSD_VIETQR_2;
	}

	public static String getNotiTypePaymentSuccessVaInvoice() {
		return NOTI_TYPE_PAYMENT_SUCCESS_VA_INVOICE;
	}

	public static String getNotiTitlePaymentSuccessVaInvoice() {
		return NOTI_TITLE_PAYMENT_SUCCESS_VA_INVOICE;
	}

	public static String getNotiDescPaymentSuccessVaInvoice1() {
		return NOTI_DESC_PAYMENT_SUCCESS_VA_INVOICE_1;
	}

	public static String getNotiDescPaymentSuccessVaInvoice2() {
		return NOTI_DESC_PAYMENT_SUCCESS_VA_INVOICE_2;
	}

	public static String getNotiTitleMobileTopupFailed() {
		return NOTI_TITLE_MOBILE_TOPUP_FAILED;
	}
	public static String getNOTI_TITLE_INVOICE_UNPAID() {
		return NOTI_TITLE_INVOICE_UNPAID;
	}

	public static String getNOTI_TITLE_INVOICE_SUCESS_FINAL() {
		return NOTI_TITLE_INVOICE;
	}

	public static String getNotiDescMobileTopupFailed() {
		return NOTI_DESC_MOBILE_TOPUP_FAILED;
	}

	public static String getNotiTitleInvoiceTotalAmount() {
		return NOTI_TITLE_INVOICE_TOTAL_AMOUNT;
	}

	public static String getNotiMobileTopup() {
		return NOTI_MOBILE_TOPUP;
	}

	public static String getNotiMobileTopupCreate() {
		return NOTI_SEND_INVOICE_CREATE;
	}

	public static String getNotiTitleMobileTopup() {
		return NOTI_TITLE_MOBILE_TOPUP;
	}

	public static String getNotiDescMobileTopup1() {
		return NOTI_DESC_MOBILE_TOPUP_1;
	}

	public static String getNotiDescMobileTopup2() {
		return NOTI_DESC_MOBILE_TOPUP_2;
	}

	public static String getNotiDescMobileTopup3() {
		return NOTI_DESC_MOBILE_TOPUP_3;
	}

	public static String getNotiRecharge() {
		return NOTI_RECHARGE;
	}

	public static String getNotiInvoice() {
		return NOTI_SEND_INVOICE;
	}

	public static String getNotiTitleRecharge() {
		return NOTI_TITLE_RECHARGE;
	}

	public static String getNotiDescRecharge1() {
		return NOTI_DESC_RECHARGE_1;
	}

	public static String getNotiDescRecharge2() {
		return NOTI_DESC_RECHARGE_2;
	}

	public static String getNotiAddVietqrId() {
		return NOTI_ADD_VIETQR_ID;
	}

	public static String getNotiTitleAddVietqrId() {
		return NOTI_TITLE_ADD_VIETQR_ID;
	}

	public static String getNotiDescAddVietqrId() {
		return NOTI_DESC_ADD_VIETQR_ID;
	}

	public static String getNotiTitleTransaction() {
		return NOTI_TITLE_TRANSACTION;
	}

	public static String getNotiTitleLoginWarning() {
		return NOTI_TITLE_LOGIN_WARNING;
	}

	public static String getNotiDescLoginWarningPrefix() {
		return NOTI_DESC_LOGIN_WARNING_PREFIX;
	}

	public static String getNotiTitleNewMember() {
		return NOTI_TITLE_NEW_MEMBER;
	}

	public static String getNotiDescNewMemberPrefix() {
		return NOTI_DESC_NEW_MEMBER_PREFIX;
	}

	public static String getNotiDescNewMemberSuffix() {
		return NOTI_DESC_NEW_MEMBER_SUFFIX;
	}

	public static String getNotiTypeLogin() {
		return NOTI_TYPE_LOGIN;
	}

	public static String getNotiTypeTransaction() {
		return NOTI_TYPE_TRANSACTION;
	}

	public static String getNotiTypeNewMember() {
		return NOTI_TYPE_NEW_MEMBER;
	}

	public static String getNotiTypeNewTransaction() {
		return NOTI_TYPE_NEW_TRANSACTION;
	}

	public static String getNotiTitleNewTransaction() {
		return NOTI_TITLE_NEW_TRANSACTION;
	}

	public static String getNotiDescNewTransPrefix() {
		return NOTI_DESC_NEW_TRANS_PREFIX;
	}

	public static String getNotiDescNewTransSuffix1() {
		return NOTI_DESC_NEW_TRANS_SUFFIX_1;
	}

	public static String getNotiDescNewTransSuffix2() {
		return NOTI_DESC_NEW_TRANS_SUFFIX_2;
	}

	public static String getNotiTitleUpdateTransaction() {
		return NOTI_TITLE_UPDATE_TRANSACTION;
	}

	public static String getNotiDescUpdateTransSuffix1() {
		return NOTI_DESC_UPDATE_TRANS_SUFFIX_1;
	}

	public static String getNotiDescUpdateTransSuffix2() {
		return NOTI_DESC_UPDATE_TRANS_SUFFIX_2;
	}

	public static String getNotiDescUpdateTransSuffix3() {
		return NOTI_DESC_UPDATE_TRANS_SUFFIX_3;
	}

	public static String getNotiDescUpdateTransSuffix4() {
		return NOTI_DESC_UPDATE_TRANS_SUFFIX_4;
	}

	public static String getNotiTypeUpdateTransaction() {
		return NOTI_TYPE_UPDATE_TRANSACTION;
	}

	public static String getNotiDescNewTransPrefix2() {
		return NOTI_DESC_NEW_TRANS_PREFIX2;
	}

	public static String getNotiTypeVoiceTransaction() {
		return NOTI_TYPE_VOICE_TRANSACTION;
	}

	public static String getNotiTypeAddMember() {
		return NOTI_TYPE_ADD_MEMBER;
	}

	public static String getNotiTypeRemoveMember() {
		return NOTI_TYPE_REMOVE_MEMBER;
	}

	public static String getNotiTitleAddMember() {
		return NOTI_TITLE_ADD_MEMBER;
	}

	public static String getNotiTitleRemoveMember() {
		return NOTI_TITLE_REMOVE_MEMBER;
	}

	public static String getNotiDescAddMember() {
		return NOTI_DESC_ADD_MEMBER;
	}

	public static String getNotiDescRemoveMember() {
		return NOTI_DESC_REMOVE_MEMBER;
	}

	public static String getNotiTypeCancelTransaction() {
		return NOTI_TYPE_CANCEL_TRANSACTION;
	}

	public static String getNotiAnnualFee() {
		return NOTI_TYPE_ACTIVE_KEY;
	}

	public static String getNotiInvoiceFinal() {
		return NOTI_SEND_INVOICE;
	}
	public static String getNotiInvoiceSuccess() {
		return NOTI_TITLE_INVOICE;
	}
	public static String getNotiDescActiveKey1() {
		return NOTI_DESC_PAYMENT_INVOICE;
	}


	public static String getNotiDescActiveKey2() {
		return NOTI_DESC_ACTIVE_KEY_2;
	}

	public static String getNotiDescActiveKey3() {
		return NOTI_DESC_ACTIVE_KEY_3;
	}
	public static String getNotiTypePaymentInvoiceSuccess() {
		return NOTI_TYPE_PAYMENT_INVOICE_SUCCESS;
	}
}
