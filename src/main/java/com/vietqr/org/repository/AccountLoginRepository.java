package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountLoginEntity;

@Repository
public interface AccountLoginRepository extends JpaRepository<AccountLoginEntity, Long>{

	@Query(value = "SELECT id FROM account_login WHERE phone_no = :phoneNo AND password = :password", nativeQuery = true)
	String login(@Param(value="phoneNo") String phoneNo, @Param(value="password") String password);

	@Query(value = "SELECT password FROM account_login WHERE id = :userId AND password = :password", nativeQuery = true)
	String checkOldPassword(@Param(value = "userId") String userId, @Param(value = "password")String password);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_login SET password = :password WHERE id = :userId", nativeQuery = true)
	void updatePassword(@Param(value = "password")String password, @Param(value = "userId") String userId);

	@Query(value = "SELECT phone_no FROM account_login WHERE phone_no = :phoneNo", nativeQuery = true)
	String checkExistedPhoneNo(@Param(value = "phoneNo") String phoneNo);

	@Query(value = "SELECT id FROM account_login WHERE phone_no = :phoneNo", nativeQuery = true)
	String checkExistedAccountByPhoneNo(@Param(value = "phoneNo") String phoneNo);

	@Query(value = "SELECT id FROM account_login WHERE id = :userId", nativeQuery = true)
	String checkExistedAccount(@Param(value = "userId")String userId);
}
