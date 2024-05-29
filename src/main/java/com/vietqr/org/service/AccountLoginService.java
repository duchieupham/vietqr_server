package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.AccountLoginSyncDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountCheckDTO;
import com.vietqr.org.dto.CardVQRInfoDTO;
import com.vietqr.org.entity.AccountLoginEntity;

@Service
public interface AccountLoginService {

	public String login(String phoneNo, String password);

	public String checkOldPassword(String userId, String password);

	public void updatePassword(String password, String userId);

	public int insertAccountLogin(AccountLoginEntity entity);

	public AccountCheckDTO checkExistedPhoneNo(String phoneNo);

	public void updateStatus(int status, String userId);

	public String loginByEmail(String email, String password);

	public String getPhoneNoById(String userId);

	public void updateCardNumber(String cardNumber, String userId);

	public String checkExistedCardNumber(String cardNumber);

	public String loginByCardNumber(String cardNumber);

	public String getCardNumberByUserId(String userId);

	public List<String> getAllUserIds();

	public String getUserIdByPhoneNo(String phoneNo);

	public List<AccountLoginEntity> getAllAccountLogin();

	public String checkExistedUserByIdAndPassword(String userId, String password);

	public void updateCardNfcNumber(String cardNumber, String userId);

	public String checkExistedCardNfcNumber(String cardNumber);

	public String loginByCardNfcNumber(String cardNumber);

	public CardVQRInfoDTO getVcardInforByUserId(String userId);

	public void resetPassword(String password, String phoneNo);

	String checkPassword(String userId, String password);

	AccountLoginSyncDTO countAccounts();
}
