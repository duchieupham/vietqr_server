package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.IAccountTerminalMemberDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.dto.AccountInformationSyncDTO;
import com.vietqr.org.dto.AccountSearchDTO;
import com.vietqr.org.dto.UserInfoWalletDTO;

@Service
public interface AccountInformationService {

	public AccountInformationEntity getAccountInformation(String userId);

	public void updateAccountInformation(String firstName, String middleName, String lastName, String birthDate,
			String address, int gender, String email, String nationalId, String oldNationalId, String nationalDate,
			String userId);

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
}
