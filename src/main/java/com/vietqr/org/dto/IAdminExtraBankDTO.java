package com.vietqr.org.dto;

public interface IAdminExtraBankDTO {
    long getOverdueCount();        // Số lượng tài khoản đã quá hạn
    long getNearlyExpireCount();   // Số lượng tài khoản gần hết hạn (còn 7 ngày)
    long getValidCount();          // Số lượng tài khoản còn hạn
    long getNotRegisteredCount();  // Số lượng tài khoản chưa đăng ký dịch vụ
}
