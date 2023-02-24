package com.vietqr.org.util;

public class NotificationUtil {

	//Type
	private static final String NOTI_TYPE_LOGIN = "N02";
	private static final String NOTI_TYPE_TRANSACTION = "N01";
	private static final String NOTI_TYPE_NEW_MEMBER = "N03";

	//Title
	private static final String NOTI_TITLE_TRANSACTION = "Thông báo biến động số dư";
	private static final String NOTI_TITLE_LOGIN_WARNING = "Đăng nhập vào thiết bị mới";
	private static final String NOTI_TITLE_NEW_MEMBER = "Thành viên mới";

	//Description
	private static final String NOTI_DESC_LOGIN_WARNING_PREFIX = "Chúng tôi nhận thấy có lượt đăng nhập từ thiết bị ";
	private static final String NOTI_DESC_NEW_MEMBER_PREFIX = "Bạn vừa được thêm vào tài khoản ";
	private static final String NOTI_DESC_NEW_MEMBER_SUFFIX = " để quản lý đối soát thanh toán";


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
}
