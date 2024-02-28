package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.repository.AccountLoginRepository;

@Service
public class AccountLoginServiceImpl implements AccountLoginService {

	@Autowired
	AccountLoginRepository repo;

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
	public int sumOfUserByStartDateEndDate(String date) {
		StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
		return repo.sumOfUserByStartDateEndDate(startEndTimeDTO.getFromDate(), startEndTimeDTO.getToDate());
	}

	@Override
	public List<RegisterUserDTO> getAllRegisterUser(String date) {
		StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
		return repo.getAllRegisterUser(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
				startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
	}

	@Override
	public List<RegisterUserResponseDTO> getAllRegisterUserResponse(String fromDate, String toDate, int offset) {
		StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndDate(fromDate, toDate);
		return repo.getAllRegisterUserResponse(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
				startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
	}

	@Override
	public List<String> getAllDate() {
		return repo.getAllDate();
	}

	@Override
	public List<RegisterUserResponseDTO> getAllRegisterUserResponseByPhone(String fromDate, String toDate, String value, int offset) {
		StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndDate(fromDate, toDate);
		return repo.getAllRegisterUserResponseByPhone(startEndTimeDTO.getFromDate(), startEndTimeDTO.getToDate(), value, offset);
	}

	@Override
	public List<RegisterUserResponseDTO> getAllRegisterUserResponseByName(String fromDate, String toDate, String value, int offset) {
		return null;
	}

	@Override
	public List<RegisterUserResponseDTO> getAllRegisterUserResponseByPlatform(String fromDate, String toDate, String value, int offset) {
		return null;
	}

}
