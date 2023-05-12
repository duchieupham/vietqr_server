package com.vietqr.org.util;

public class NotificationUtil {

	// Type
	private static final String NOTI_TYPE_LOGIN = "N02";
	private static final String NOTI_TYPE_TRANSACTION = "N01";
	private static final String NOTI_TYPE_NEW_MEMBER = "N03";
	private static final String NOTI_TYPE_NEW_TRANSACTION = "N04";
	private static final String NOTI_TYPE_UPDATE_TRANSACTION = "N05";
	private static final String NOTI_TYPE_VOICE_TRANSACTION = "N06";

	// Title
	private static final String NOTI_TITLE_TRANSACTION = "Thông báo biến động số dư";
	private static final String NOTI_TITLE_LOGIN_WARNING = "Đăng nhập vào thiết bị mới";
	private static final String NOTI_TITLE_NEW_MEMBER = "Thành viên mới";
	private static final String NOTI_TITLE_NEW_TRANSACTION = "Giao dịch mới";
	private static final String NOTI_TITLE_UPDATE_TRANSACTION = "Biến động số dư";

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

}
