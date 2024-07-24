package com.vietqr.org.dto;

public class AccountInformationBackUpDTO extends AccountInformationDTO{
    private boolean isVerify;
    private long balance;
    private long score;

    public AccountInformationBackUpDTO(boolean isVerify, long balance, long score) {
        this.isVerify = isVerify;
        this.balance = balance;
        this.score = score;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String userId, boolean isVerify, long balance, long score) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, userId);
        this.isVerify = isVerify;
        this.balance = balance;
        this.score = score;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String nationalId, String oldNationalId, String nationalDate, String userId, boolean isVerify, long balance, long score) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, nationalId, oldNationalId, nationalDate, userId);
        this.isVerify = isVerify;
        this.balance = balance;
        this.score = score;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String userId, String nationalId, String oldNationalId, String nationalDate, String imgId, boolean isVerify, long balance, long score) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, userId, nationalId, oldNationalId, nationalDate, imgId);
        this.isVerify = isVerify;
        this.balance = balance;
        this.score = score;
    }

    public AccountInformationBackUpDTO(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String userId, String nationalId, String oldNationalId, String nationalDate, String imgId, String carrierTypeId, boolean isVerify, long balance, long score) {
        super(firstName, middleName, lastName, birthDate, address, gender, email, userId, nationalId, oldNationalId, nationalDate, imgId, carrierTypeId);
        this.isVerify = isVerify;
        this.balance = balance;
        this.score = score;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

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
