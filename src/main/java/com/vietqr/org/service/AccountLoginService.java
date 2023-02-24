package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountLoginEntity;

@Service
public interface AccountLoginService {

	public String login(String phoneNo, String password);

	public String checkOldPassword(String userId, String password);

	public void updatePassword(String password, String userId);

	public int insertAccountLogin(AccountLoginEntity entity);

	public String checkExistedPhoneNo(String phoneNo);

	public String checkExistedAccount(String userId);

	public String checkExistedAccountByPhoneNo(String phoneNo);
}
