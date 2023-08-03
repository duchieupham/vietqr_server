package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountCheckDTO;
import com.vietqr.org.entity.AccountLoginEntity;

@Repository
public interface AccountLoginRepository extends JpaRepository<AccountLoginEntity, Long> {

	@Query(value = "SELECT id FROM account_login WHERE phone_no = :phoneNo AND password = :password AND status = 1", nativeQuery = true)
	String login(@Param(value = "phoneNo") String phoneNo, @Param(value = "password") String password);

	@Query(value = "SELECT password FROM account_login WHERE id = :userId AND password = :password", nativeQuery = true)
	String checkOldPassword(@Param(value = "userId") String userId, @Param(value = "password") String password);

	@Query(value = "SELECT id FROM account_login WHERE id = :userId AND password = :password", nativeQuery = true)
	String checkExistedUserByIdAndPassword(@Param(value = "userId") String userId,
			@Param(value = "password") String password);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_login SET password = :password WHERE id = :userId", nativeQuery = true)
	void updatePassword(@Param(value = "password") String password, @Param(value = "userId") String userId);

	@Query(value = "SELECT id, status FROM account_login WHERE phone_no = :phoneNo", nativeQuery = true)
	AccountCheckDTO checkExistedPhoneNo(@Param(value = "phoneNo") String phoneNo);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_login SET status = :status WHERE id = :id", nativeQuery = true)
	void updateStatus(@Param(value = "status") int status, @Param(value = "id") String id);

	@Query(value = "SELECT id FROM account_login WHERE email = :email AND password = :password AND status = 1", nativeQuery = true)
	String loginByEmail(@Param(value = "email") String email, @Param(value = "password") String password);

	@Transactional
	@Modifying
	@Query(value = "UPDATE account_login SET card_number = :cardNumber WHERE id = :userId", nativeQuery = true)
	void updateCardNumber(@Param(value = "cardNumber") String cardNumber, @Param(value = "userId") String userId);

	@Query(value = "SELECT phone_no FROM account_login WHERE id = :userId", nativeQuery = true)
	String getPhoneNoById(@Param(value = "userId") String userId);

	@Query(value = "SELECT card_number FROM account_login WHERE card_number = :cardNumber", nativeQuery = true)
	String checkExistedCardNumber(@Param(value = "cardNumber") String cardNumber);

	@Query(value = "SELECT id FROM account_login WHERE card_number = :cardNumber", nativeQuery = true)
	String loginByCardNumber(@Param(value = "cardNumber") String cardNumber);

	@Query(value = "SELECT card_number FROM account_login WHERE id = :userId", nativeQuery = true)
	String getCardNumberByUserId(@Param(value = "userId") String userId);

	@Query(value = "SELECT id FROM account_login", nativeQuery = true)
	List<String> getAllUserIds();

	@Query(value = "SELECT id FROM account_login WHERE phone_no = :phoneNo AND status = 1", nativeQuery = true)
	String getIdFromPhoneNo(@Param(value = "phoneNo") String phoneNo);

	@Query(value = "SELECT * FROM account_login", nativeQuery = true)
	List<AccountLoginEntity> getAllAccountLogin();
}
