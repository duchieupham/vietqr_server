package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.repository.AccountInformationRepository;
import com.vietqr.org.dto.AccountSearchDTO;;

@Service
public class AccountInformationServiceImpl implements AccountInformationService {

	@Autowired
	AccountInformationRepository accountInformationRepo;

	@Override
	public AccountInformationEntity getAccountInformation(String userId) {
		return accountInformationRepo.getAccountInformation(userId);
	}

	@Override
	public void updateAccountInformation(String firstName, String middleName, String lastName, String birthDate,
			String address, int gender, String email, String userId) {
		accountInformationRepo.updateAccountInformaiton(firstName, middleName, lastName, birthDate, address, gender,
				email, userId);
	}

	@Override
	public void updateImageId(String imgId, String userId) {
		accountInformationRepo.updateImage(imgId, userId);
	}

	@Override
	public String getPhoneNoByUserId(String userId) {
		return accountInformationRepo.getPhoneNoByUserId(userId);
	}

	@Override
	public int insertAccountInformation(AccountInformationEntity entity) {
		return accountInformationRepo.save(entity) == null ? 0 : 1;
	}

	@Override
	public AccountSearchDTO getAccountSearch(String phoneNo) {
		return accountInformationRepo.getAccountSearch(phoneNo);
	}

	@Override
	public void udpateStatus(int status, String userId) {
		accountInformationRepo.updateStatus(status, userId);
	}

}
