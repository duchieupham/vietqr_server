package com.vietqr.org.dto;

public class AccountInformationBackUpDTO extends AccountInformationDTO{
    private boolean isVerify;

    public boolean isVerify() {
        return isVerify;
    }

    public AccountInformationBackUpDTO() {
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    public AccountInformationBackUpDTO(boolean isVerify) {
        this.isVerify = isVerify;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String userId, boolean isVerify) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, userId);
        this.isVerify = isVerify;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String nationalId, String oldNationalId, String nationalDate, String userId, boolean isVerify) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, nationalId, oldNationalId, nationalDate, userId);
        this.isVerify = isVerify;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String userId, String nationalId, String oldNationalId, String nationalDate, String imgId, boolean isVerify) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, userId, nationalId, oldNationalId, nationalDate, imgId);
        this.isVerify = isVerify;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String userId, String nationalId, String oldNationalId, String nationalDate, String imgId, String carrierTypeId, boolean isVerify) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, userId, nationalId, oldNationalId, nationalDate, imgId, carrierTypeId);
        this.isVerify = isVerify;
    }


}
