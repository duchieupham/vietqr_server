package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.RegisterUserDTO;
import com.vietqr.org.dto.RegisterUserResponseDTO;
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

	public int sumOfUserByStartDateEndDate(String date);

    List<RegisterUserDTO> getAllRegisterUser(String dateString);

	List<RegisterUserResponseDTO> getAllRegisterUserResponse(String fromDate, String toDate, int offset);

	List<String> getAllDate();

    List<RegisterUserResponseDTO> getAllRegisterUserResponseByPhone(String fromDate, String toDate, String value, int offset);

	List<RegisterUserResponseDTO> getAllRegisterUserResponseByName(String fromDate, String toDate, String value, int offset);

	List<RegisterUserResponseDTO> getAllRegisterUserResponseByPlatform(String fromDate, String toDate, String value, int offset);
}
