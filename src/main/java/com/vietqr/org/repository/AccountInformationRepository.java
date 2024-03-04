package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.vietqr.org.dto.IAccountTerminalMemberDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountInformationSyncDTO;
import com.vietqr.org.dto.AccountSearchDTO;
import com.vietqr.org.dto.UserInfoWalletDTO;
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

	@Query(value = "SELECT DISTINCT a.user_id AS id, a.phone_no AS phoneNo, a.img_id AS imgId, "
			+ "a.birth_date AS birthDate, a.email AS email, a.nation_id AS nationId, "
			+ "CONCAT(c.last_name, ' ', c.middle_name, ' ', c.first_name) AS fullName, "
			+ "a.gender AS gender, b.is_owner AS isOwner FROM account_information a "
			+ "INNER JOIN account_bank_receive_share b ON b.bank_id = a.user_id "
			+ "WHERE b.terminal_id = :terminalId "
			+ "AND CONCAT(b.last_name, ' ' ,b.middle_name, ' ' , b.first_name) LIKE %:value% "
			+ "AND a.status = 1 "
			+ "LIMIT :offset, 20", nativeQuery = true)
	List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndFullName(String terminalId, String value, int offset);

	@Query(value = "SELECT DISTINCT a.user_id AS id, a.phone_no AS phoneNo, a.img_id AS imgId, "
			+ "a.birth_date AS birthDate, a.email AS email, a.nation_id AS nationId, "
			+ "CONCAT(c.last_name, ' ', c.middle_name, ' ', c.first_name) AS fullName, "
			+ "a.gender AS gender, b.is_owner AS isOwner FROM account_information a "
			+ "INNER JOIN account_bank_receive_share b ON b.bank_id = a.user_id "
			+ "WHERE b.terminal_id = :terminalId "
			+ "AND a.phone_no = :value "
			+ "AND a.status = 1 "
			+ "LIMIT :offset, 20", nativeQuery = true)
	List<IAccountTerminalMemberDTO> getMembersWebByTerminalIdAndPhoneNo(String terminalId, String value, int offset);
}
