package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountInformationEntity;

@Service
public interface AccountInformationService {

	public List<IAdminListUserAccountResponseDTO> getAdminListUsersAccount(String value, int offset, int size);
	public List<IAdminListUserAccountResponseDTO> getAdminListUsersAccountByPhone(String value, int offset, int size);

	public int countAdminListUsersAccountByName(String value);
	public int countAdminListUsersAccountByPhone(String value);

	public AccountInformationEntity getAccountInformation(String userId);

	public void updateAccountInformation(String firstName, String middleName, String lastName, String birthDate,
			String address, int gender, String email, String nationalId, String oldNationalId, String nationalDate,
			String userId);

	public void updateEmailAccountInformation(String userId, String email);

	public void updateImageId(String imgId, String userId);

	public String getPhoneNoByUserId(String userId);

	public int insertAccountInformation(AccountInformationEntity entity);

	public AccountSearchDTO getAccountSearch(String phoneNo);

	public List<AccountSearchDTO> getAccountSearchByFullname(String fullname);

	public void udpateStatus(int status, String userId);

	public List<AccountSearchDTO> getAccountsSearch(String phoneNo);

	public UserInfoWalletDTO getUserInforWallet(String userId);

	public void updateCarrierTypeIdByUserId(String carrierTypeId, String userId);

	public List<AccountInformationSyncDTO> getUserInformationSync();

	List<IAccountTerminalMemberDTO> getMembersWebByTerminalId(String terminalId, int offset);

	List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndPhoneNo(String terminalId, String value, int offset);

	List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndFullName(String terminalId, String value, int offset);

    AccountSearchByPhoneNoDTO findAccountByPhoneNo(String phoneNo);

    AccountSearchByPhoneNoDTO findAccountByUserId(String userId);

	List<IAccountTerminalMemberDTO> getMembersByTerminalId(String terminalId);
}
