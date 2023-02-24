package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountInformationEntity;

@Repository
public interface AccountInformationRepository extends JpaRepository<AccountInformationEntity, Long>{

	@Query(value = "SELECT * FROM account_information WHERE user_id = :userId", nativeQuery = true)
	AccountInformationEntity getAccountInformation(@Param(value="userId")String userId);

	@Transactional
	@Modifying
	@Query(value ="UPDATE account_information SET first_name = :firstName, middle_name = :middleName, last_name = :lastName, "
	+ "birth_date = :birthDate, address = :address, gender = :gender, email = :email "
	+ "WHERE user_id = :userId", nativeQuery = true)
	void updateAccountInformaiton(@Param(value= "firstName")String firstName, @Param(value= "middleName")String middleName, @Param(value= "lastName")String lastName, @Param(value= "birthDate")String birthDate, @Param(value= "address")String address, @Param(value= "gender")int gender, @Param(value= "email")String email, @Param(value= "userId")String userId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_information SET img_id = :imgId WHERE user_id = :userId", nativeQuery = true)
	void updateImage(@Param(value = "imgId")String imgId, @Param(value = "userId") String userId);

	@Query(value = "SELECT phone_no FROM account_login WHERE id = :userId", nativeQuery = true)
	String getPhoneNoByUserId(@Param(value = "userId") String userId);
}
