package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountInformationEntity;

@Service
public interface AccountInformationService {

	public AccountInformationEntity getAccountInformation(String userId);

	public void updateAccountInformation(String firstName, String middleName, String lastName, String birthDate, String address, int gender, String email, String userId);

	public void updateImageId(String imgId, String userId);

	public String getPhoneNoByUserId(String userId);

	public int insertAccountInformation(AccountInformationEntity entity);
}
