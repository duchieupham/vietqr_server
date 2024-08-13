package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * BankReceiveFeePackageInsertDTO
 */
public class BankReceiveFeePackageInsertDTO implements Serializable {
    @NotBlank
    private String fee_package_id;
    @NotBlank
    private String bank_id;
    @NotBlank
    private String mid;
    @NotBlank
    private String user_id;

    public BankReceiveFeePackageInsertDTO(String bank_id, String fee_package_id, String mid, String user_id) {
        this.bank_id = bank_id;
        this.fee_package_id = fee_package_id;
        this.mid = mid;
        this.user_id = user_id;
    }

    public BankReceiveFeePackageInsertDTO() {
        super();
    }


    public @NotBlank String getFeePackageId() {
        return fee_package_id;
    }

    public void setFeePackageId(@NotBlank String fee_package_id) {
        this.fee_package_id = fee_package_id;
    }

    public @NotBlank String getBankId() {
        return bank_id;
    }

    public void setBankId(@NotBlank String bank_id) {
        this.bank_id = bank_id;
    }

    public @NotBlank String getMid() {
        return mid;
    }

    public void setMid(@NotBlank String mid) {
        this.mid = mid;
    }

    public @NotBlank String getUserId() {
        return user_id;
    }

    public void setUserId(@NotBlank String user_id) {
        this.user_id = user_id;
    }
}