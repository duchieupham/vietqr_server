package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.qrfeed.IUserInfoQrDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.repository.AccountLoginRepository;

@Service
public class AccountLoginServiceImpl implements AccountLoginService {

    @Autowired
    AccountLoginRepository repo;

    @Override
    public IUserInfoDTO getUserInfoDetailsByUserId(String userId) {
        return repo.getUserInfoDetailsByUserId(userId);
    }

    @Override
    public IUserInfoQrDTO getUserInfoQRByUserId(String userId) {
        return repo.getUserInfoQRByUserId(userId);
    }

    @Override
    public IUserInfoDTO getUserInfoByUserId(String userId) {
        return repo.getUserInfoByUserId(userId);
    }

    @Override
    public String login(String phoneNo, String password) {
        return repo.login(phoneNo, password);
    }

    @Override
    public String checkOldPassword(String userId, String password) {
        return repo.checkOldPassword(userId, password);
    }

    @Override
    public void updatePassword(String password, String userId) {
        repo.updatePassword(password, userId);
    }

    @Override
    public void updateIsVerifiedByUserId(String userId, String email) {
        repo.updateIsVerifiedByUserId(userId, email);
    }

    @Override
    public int insertAccountLogin(AccountLoginEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public AccountCheckDTO checkExistedPhoneNo(String phoneNo) {
        return repo.checkExistedPhoneNo(phoneNo);
    }

    @Override
    public void updateStatus(int status, String userId) {
        repo.updateStatus(status, userId);
    }

    @Override
    public String loginByEmail(String email, String password) {
        return repo.loginByEmail(email, password);
    }

    @Override
    public String getPhoneNoById(String userId) {
        return repo.getPhoneNoById(userId);
    }

    @Override
    public void updateCardNumber(String cardNumber, String userId) {
        repo.updateCardNumber(cardNumber, userId);
    }

    @Override
    public String checkExistedCardNumber(String cardNumber) {
        return repo.checkExistedCardNumber(cardNumber);
    }

    @Override
    public String loginByCardNumber(String cardNumber) {
        return repo.loginByCardNumber(cardNumber);
    }

    @Override
    public String getCardNumberByUserId(String userId) {
        return repo.getCardNumberByUserId(userId);
    }

    @Override
    public List<String> getAllUserIds() {
        return repo.getAllUserIds();
    }

    @Override
    public String getUserIdByPhoneNo(String phoneNo) {
        return repo.getIdFromPhoneNo(phoneNo);
    }

    @Override
    public List<AccountLoginEntity> getAllAccountLogin() {
        return repo.getAllAccountLogin();
    }

    @Override
    public String checkExistedUserByIdAndPassword(String userId, String password) {
        return repo.checkExistedUserByIdAndPassword(userId, password);
    }

    @Override
    public void updateCardNfcNumber(String cardNumber, String userId) {
        repo.updateCardNfcNumber(cardNumber, userId);
    }

    @Override
    public String checkExistedCardNfcNumber(String cardNumber) {
        return repo.checkExistedCardNfcNumber(cardNumber);
    }

    @Override
    public String loginByCardNfcNumber(String cardNumber) {
        return repo.loginByCardNfcNumber(cardNumber);
    }

    @Override
    public CardVQRInfoDTO getVcardInforByUserId(String userId) {
        return repo.getVcardInforByUserId(userId);
    }

    @Override
    public void resetPassword(String password, String phoneNo) {
        repo.resetPassword(password, phoneNo);
    }

    @Override
    public String checkPassword(String userId, String password) {
        return repo.checkPassword(userId, password);
    }

    @Override
    public boolean isPhoneNoExists(String phoneNo) {
        return repo.existsByPhoneNo(phoneNo);
    }

    @Override
    public long countAccountsRegisteredInDay(long startTime, long endTime) {
        return repo.countAccountsRegisteredInDay(startTime, endTime);
    }

    @Override
    public long getTotalUsers() {
        return repo.getTotalUsers();
    }

    @Override
    public long getRegisterDate(String userId) {
        return repo.getRegisterDate(userId);
    }

    @Override
    public void updateEmailByUserId(String email, String userId) {
        repo.updateEmailByUserId(email, userId);
    }

    @Override
    public List<IAccountLogin> findUsersRegisteredInDay(long startTime, long endTime) {
        return repo.findUsersRegisteredInDay(startTime, endTime);
    }

    @Override
    public long getTotalUsersUntilDate(long endTime) {
        return repo.getTotalUsersUntilDate(endTime);
    }

    @Override
    public boolean getVerifyEmailStatus(String userId) {
        return repo.getVerifyEmailStatus(userId);
    }
    @Override
    public List<IAccountLogin> findUsersRegisteredInMonth(long startTime, long endTime) {
        return repo.findUsersRegisteredInMonth(startTime, endTime);
    }
}
