package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.vietqr.org.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountInformationEntity;

@Repository
public interface AccountInformationRepository extends JpaRepository<AccountInformationEntity, Long> {

	@Query(value = "SELECT * FROM account_information WHERE user_id = :userId AND status = 1", nativeQuery = true)
	AccountInformationEntity getAccountInformation(@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET first_name = :firstName, middle_name = :middleName, last_name = :lastName, "
			+ "birth_date = :birthDate, address = :address, gender = :gender, email = :email, "
			+ "national_id = :nationalId, old_national_id = :oldNationalId, national_date = :nationalDate "
			+ "WHERE user_id = :userId", nativeQuery = true)
	void updateAccountInformaiton(@Param(value = "firstName") String firstName,
			@Param(value = "middleName") String middleName, @Param(value = "lastName") String lastName,
			@Param(value = "birthDate") String birthDate, @Param(value = "address") String address,
			@Param(value = "gender") int gender, @Param(value = "email") String email,
			@Param(value = "nationalId") String nationalId,
			@Param(value = "oldNationalId") String oldNationalId,
			@Param(value = "nationalDate") String nationalDate,
			@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET img_id = :imgId WHERE user_id = :userId", nativeQuery = true)
	void updateImage(@Param(value = "imgId") String imgId, @Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET carrier_type_id = :carrierTypeId WHERE user_id = :userId", nativeQuery = true)
	void updateCarrierTypeIdByUserId(@Param(value = "carrierTypeId") String carrierTypeId,
			@Param(value = "userId") String userId);

	@Query(value = "SELECT phone_no FROM account_login WHERE id = :userId", nativeQuery = true)
	String getPhoneNoByUserId(@Param(value = "userId") String userId);

	@Query(value = "SELECT a.id, a.phone_no as phoneNo, b.first_name as firstName, b.middle_name as middleName, b.last_name as lastName, b.img_id as imgId, b.carrier_type_id as carrierTypeId "
			+ "FROM account_login a "
			+ "INNER JOIN account_information b "
			+ "ON a.id = b.user_id "
			+ "WHERE a.phone_no= :phoneNo AND a.status = 1", nativeQuery = true)
	AccountSearchDTO getAccountSearch(@Param(value = "phoneNo") String phoneNo);

	@Query(value = "SELECT a.id, a.phone_no as phoneNo, b.first_name as firstName, b.middle_name as middleName, b.last_name as lastName, b.img_id as imgId, b.carrier_type_id as carrierTypeId "
			+ "FROM account_login a "
			+ "INNER JOIN account_information b "
			+ "ON a.id = b.user_id "
			+ "WHERE CONCAT(b.last_name, '-' ,b.middle_name, '-' , b.first_name) LIKE %:fullname%", nativeQuery = true)
	List<AccountSearchDTO> getAccountSearchByFullname(@Param(value = "fullname") String fullname);

	@Query(value = "SELECT a.id, a.phone_no as phoneNo, b.first_name as firstName, b.middle_name as middleName, b.last_name as lastName, b.img_id as imgId, b.carrier_type_id as carrierTypeId "
			+ "FROM account_login a "
			+ "INNER JOIN account_information b "
			+ "ON a.id = b.user_id "
			+ "WHERE a.phone_no LIKE %:phoneNo% AND a.status = 1", nativeQuery = true)
	List<AccountSearchDTO> getAccountsSearch(@Param(value = "phoneNo") String phoneNo);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET status = :status WHERE user_id = :userId", nativeQuery = true)
	void updateStatus(@Param(value = "status") int status, @Param(value = "userId") String userId);

	@Query(value = "SELECT a.user_id as userId, a.first_name as firstName, a.middle_name as middleName, a.last_name as lastName, b.wallet_id as walletId "
			+ "FROM account_information a "
			+ "INNER JOIN account_wallet b "
			+ "ON a.user_id = b.user_id "
			+ "WHERE a.user_id = :userId", nativeQuery = true)
	UserInfoWalletDTO getUserInforWallet(@Param(value = "userId") String userId);

	@Query(value = "SELECT a.id, a.phone_no as phoneNo, b.address, b.email, b.last_name as lastName, "
			+ "b.middle_name as middleName, b.first_name as firstName, b.birth_date as birthDate "
			+ "FROM account_login a "
			+ "INNER JOIN account_information b "
			+ "ON a.id = b.user_id ", nativeQuery = true)
	List<AccountInformationSyncDTO> getUserInformationSync();

	@Query(value = "SELECT DISTINCT a.user_id AS id, a.phone_no AS phoneNo, a.img_id AS imgId, "
			+ "a.birth_date AS birthDate, a.email AS email, a.nation_id AS nationId, "
			+ "CONCAT(c.last_name, ' ', c.middle_name, ' ', c.first_name) AS fullName, "
			+ "a.gender AS gender, b.is_owner AS isOwner FROM account_information a "
			+ "INNER JOIN account_bank_receive_share b ON b.bank_id = a.user_id "
			+ "WHERE b.terminal_id = :terminalId "
			+ "AND a.status = 1 "
			+ "LIMIT :offset, 20", nativeQuery = true)
    List<IAccountTerminalMemberDTO> getMembersWebByTerminalId(String terminalId, int offset);

	@Query(value = "SELECT DISTINCT a.user_id AS id, c.phone_no AS phoneNo, a.img_id AS imgId, "
			+ "a.birth_date AS birthDate, a.email AS email, a.national_id AS nationalId, "
			+ "CONCAT(a.last_name, ' ', a.middle_name, ' ', a.first_name) AS fullName, "
			+ "a.gender AS gender, b.is_owner AS isOwner FROM account_information a "
			+ "INNER JOIN account_login c ON c.id = a.user_id "
			+ "INNER JOIN account_bank_receive_share b ON b.user_id = a.user_id "
			+ "WHERE b.terminal_id = :terminalId "
			+ "AND CONCAT(a.last_name, ' ' ,a.middle_name, ' ' , a.first_name) LIKE %:value% "
			+ "AND a.status = 1 "
			+ "LIMIT :offset, 20", nativeQuery = true)
	List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndFullName(String terminalId, String value, int offset);

	@Query(value = "SELECT DISTINCT a.user_id AS id, c.phone_no AS phoneNo, a.img_id AS imgId, "
			+ "a.birth_date AS birthDate, a.email AS email, a.national_id AS nationalId, "
			+ "CONCAT(a.last_name, ' ', a.middle_name, ' ', a.first_name) AS fullName, "
			+ "a.gender AS gender, b.is_owner AS isOwner FROM account_information a "
			+ "INNER JOIN account_login c ON c.id = a.user_id "
			+ "INNER JOIN account_bank_receive_share b ON b.user_id = a.user_id "
			+ "WHERE b.terminal_id = :terminalId "
			+ "AND c.phone_no LIKE %:value% "
			+ "AND a.status = 1 "
			+ "LIMIT :offset, 20", nativeQuery = true)
	List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndPhoneNo(String terminalId, String value, int offset);

	@Query(value = "SELECT b.id AS userId, b.phone_no as phoneNo, a.img_id as imgId, "
			+ "CONCAT(a.last_name, ' ', a.middle_name, ' ', a.first_name) AS fullName "
			+ "FROM account_information a "
			+ "INNER JOIN account_login b "
			+ "ON b.id = a.user_id "
			+ "WHERE b.phone_no = :phoneNo AND b.status = 1", nativeQuery = true)
	AccountSearchByPhoneNoDTO findAccountByPhoneNo(@Param(value = "phoneNo") String phoneNo);

	@Query(value = "SELECT b.id AS userId, b.phone_no as phoneNo, a.img_id as imgId, "
			+ "CONCAT(a.last_name, ' ', a.middle_name, ' ', a.first_name) AS fullName "
			+ "FROM account_information a "
			+ "INNER JOIN account_login b "
			+ "ON b.id = a.user_id "
			+ "WHERE b.id = :userId AND b.status = 1", nativeQuery = true)
	AccountSearchByPhoneNoDTO findAccountByUserId(@Param(value = "userId") String userId);

	@Query(value = "SELECT DISTINCT a.user_id AS id, c.phone_no AS phoneNo, a.img_id AS imgId, "
			+ "a.birth_date AS birthDate, a.email AS email, a.national_id AS nationalId, "
			+ "CONCAT(a.last_name, ' ', a.middle_name, ' ', a.first_name) AS fullName, "
			+ "a.gender AS gender, "
			+ "CASE "
			+ "WHEN terminal_id = '' THEN TRUE "
			+ "ELSE FALSE "
			+ "END AS isOwner "
			+ "FROM account_information a "
			+ "INNER JOIN account_login c ON c.id = a.user_id "
			+ "INNER JOIN merchant_member b ON b.user_id = a.user_id "
			+ "INNER JOIN terminal d ON d.id = b.terminal_id "
			+ "WHERE (b.terminal_id = :terminalId OR terminal_id = '') "
			+ "AND a.status = 1 "
			+ "ORDER BY isOwner DESC ", nativeQuery = true)
	List<IAccountTerminalMemberDTO> getMembersByTerminalId(String terminalId);
}
