package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.repository.AccountInformationRepository;
;

@Service
public class AccountInformationServiceImpl implements AccountInformationService {

	@Autowired
	AccountInformationRepository accountInformationRepo;

	@Override
	public List<IAdminListUserAccountResponseDTO> getAdminListUsersAccount(String value, int offset, int size) {
		return accountInformationRepo.getAdminListUsersAccount(value, offset, size);
	}

	@Override
	public List<IAdminListUserAccountResponseDTO> getAdminListUsersAccountByPhone(String value, int offset, int size) {
		return accountInformationRepo.getAdminListUsersAccountByPhone(value, offset, size);
	}
	@Override
	public int countAdminListUsersAccountByName(String value) {
		return accountInformationRepo.countAdminListUsersAccountByName(value);
	}
	@Override
	public int countAdminListUsersAccountByPhone(String value) {
		return accountInformationRepo.countAdminListUsersAccountByPhone(value);
	}

	@Override
	public AccountInformationEntity getAccountInformation(String userId) {
		return accountInformationRepo.getAccountInformation(userId);
	}

	@Override
	public void updateAccountInformation(String firstName, String middleName, String lastName, String birthDate,
			String address, int gender, String email, String nationalId, String oldNationalId, String nationalDate,
			String userId) {
		accountInformationRepo.updateAccountInformaiton(firstName, middleName, lastName, birthDate, address, gender,
				email, nationalId, oldNationalId, nationalDate, userId);
	}

	@Override
	public void updateEmailAccountInformation(String email, String userId) {
		accountInformationRepo.updateEmailAccountInformation(email, userId);
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

	@Override
	public UserInfoWalletDTO getUserInforWallet(String userId) {
		return accountInformationRepo.getUserInforWallet(userId);
	}

	@Override
	public void updateCarrierTypeIdByUserId(String carrierTypeId, String userId) {
		accountInformationRepo.updateCarrierTypeIdByUserId(carrierTypeId, userId);
	}

	@Override
	public List<AccountSearchDTO> getAccountsSearch(String phoneNo) {
		return accountInformationRepo.getAccountsSearch(phoneNo);
	}

	@Override
	public List<AccountSearchDTO> getAccountSearchByFullname(String fullname) {
		return accountInformationRepo.getAccountSearchByFullname(fullname);
	}

	@Override
	public List<AccountInformationSyncDTO> getUserInformationSync() {
		return accountInformationRepo.getUserInformationSync();
	}

	@Override
	public List<IAccountTerminalMemberDTO> getMembersWebByTerminalId(String terminalId, int offset) {
		return accountInformationRepo.getMembersWebByTerminalId(terminalId, offset);
	}

	@Override
	public List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndPhoneNo(String terminalId, String value, int offset) {
		return accountInformationRepo.getMembersWebByTerminalIdAndPhoneNo(terminalId, value, offset);
	}

	@Override
	public List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndFullName(String terminalId, String value, int offset) {
		return accountInformationRepo.getMembersWebByTerminalIdAndFullName(terminalId, value, offset);
	}

	@Override
	public AccountSearchByPhoneNoDTO findAccountByPhoneNo(String phoneNo) {
		return accountInformationRepo.findAccountByPhoneNo(phoneNo);
	}

	@Override
	public AccountSearchByPhoneNoDTO findAccountByUserId(String userId) {
		return accountInformationRepo.findAccountByUserId(userId);
	}

	@Override
	public List<IAccountTerminalMemberDTO> getMembersByTerminalId(String terminalId) {
		return accountInformationRepo.getMembersByTerminalId(terminalId);
	}
}
