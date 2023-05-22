package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.AccountCheckDTO;
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

}
