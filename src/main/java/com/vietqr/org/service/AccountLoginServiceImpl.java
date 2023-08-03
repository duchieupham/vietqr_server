package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountCheckDTO;
import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.repository.AccountLoginRepository;

@Service
public class AccountLoginServiceImpl implements AccountLoginService {

	@Autowired
	AccountLoginRepository accountLoginRepository;

	@Override
	public String login(String phoneNo, String password) {
		return accountLoginRepository.login(phoneNo, password);
	}

	@Override
	public String checkOldPassword(String userId, String password) {
		return accountLoginRepository.checkOldPassword(userId, password);
	}

	@Override
	public void updatePassword(String password, String userId) {
		accountLoginRepository.updatePassword(password, userId);
	}

	@Override
	public int insertAccountLogin(AccountLoginEntity entity) {
		return accountLoginRepository.save(entity) == null ? 0 : 1;
	}

	@Override
	public AccountCheckDTO checkExistedPhoneNo(String phoneNo) {
		return accountLoginRepository.checkExistedPhoneNo(phoneNo);
	}

	@Override
	public void updateStatus(int status, String userId) {
		accountLoginRepository.updateStatus(status, userId);
	}

	@Override
	public String loginByEmail(String email, String password) {
		return accountLoginRepository.loginByEmail(email, password);
	}

	@Override
	public String getPhoneNoById(String userId) {
		return accountLoginRepository.getPhoneNoById(userId);
	}

	@Override
	public void updateCardNumber(String cardNumber, String userId) {
		accountLoginRepository.updateCardNumber(cardNumber, userId);
	}

	@Override
	public String checkExistedCardNumber(String cardNumber) {
		return accountLoginRepository.checkExistedCardNumber(cardNumber);
	}

	@Override
	public String loginByCardNumber(String cardNumber) {
		return accountLoginRepository.loginByCardNumber(cardNumber);
	}

	@Override
	public String getCardNumberByUserId(String userId) {
		return accountLoginRepository.getCardNumberByUserId(userId);
	}

	@Override
	public List<String> getAllUserIds() {
		return accountLoginRepository.getAllUserIds();
	}

	@Override
	public String getUserIdByPhoneNo(String phoneNo) {
		return accountLoginRepository.getIdFromPhoneNo(phoneNo);
	}

	@Override
	public List<AccountLoginEntity> getAllAccountLogin() {
		return accountLoginRepository.getAllAccountLogin();
	}

	@Override
	public String checkExistedUserByIdAndPassword(String userId, String password) {
		return accountLoginRepository.checkExistedUserByIdAndPassword(userId, password);
	}

}
