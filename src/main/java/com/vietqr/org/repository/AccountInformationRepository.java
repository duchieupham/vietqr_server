package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountSearchDTO;
import com.vietqr.org.entity.AccountInformationEntity;

@Repository
public interface AccountInformationRepository extends JpaRepository<AccountInformationEntity, Long> {

	@Query(value = "SELECT * FROM account_information WHERE user_id = :userId AND status = 1", nativeQuery = true)
	AccountInformationEntity getAccountInformation(@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET first_name = :firstName, middle_name = :middleName, last_name = :lastName, "
			+ "birth_date = :birthDate, address = :address, gender = :gender, email = :email "
			+ "WHERE user_id = :userId", nativeQuery = true)
	void updateAccountInformaiton(@Param(value = "firstName") String firstName,
			@Param(value = "middleName") String middleName, @Param(value = "lastName") String lastName,
			@Param(value = "birthDate") String birthDate, @Param(value = "address") String address,
			@Param(value = "gender") int gender, @Param(value = "email") String email,
			@Param(value = "userId") String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET img_id = :imgId WHERE user_id = :userId", nativeQuery = true)
	void updateImage(@Param(value = "imgId") String imgId, @Param(value = "userId") String userId);

	@Query(value = "SELECT phone_no FROM account_login WHERE id = :userId", nativeQuery = true)
	String getPhoneNoByUserId(@Param(value = "userId") String userId);

	@Query(value = "SELECT a.id, a.phone_no as phoneNo, b.first_name as firstName, b.middle_name as middleName, b.last_name as lastName, b.img_id as imgId "
			+ "FROM account_login a "
			+ "INNER JOIN account_information b "
			+ "ON a.id = b.user_id "
			+ "WHERE a.phone_no= :phoneNo AND a.status = 1", nativeQuery = true)
	AccountSearchDTO getAccountSearch(@Param(value = "phoneNo") String phoneNo);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET status = :status WHERE user_id = :userId", nativeQuery = true)
	void updateStatus(@Param(value = "status") int status, @Param(value = "userId") String userId);
}
