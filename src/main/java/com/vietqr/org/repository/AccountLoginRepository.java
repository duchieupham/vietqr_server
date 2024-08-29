package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.qrfeed.IUserInfoQrDTO;
import com.vietqr.org.entity.AccountLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

//import com.vietqr.org.entity.AccountLoginEntity;

@Repository
public interface AccountLoginRepository extends JpaRepository<AccountLoginEntity, Long> {

    @Query(value = "SELECT a.id AS id, b.phone_no AS phoneNo, " +
            "COALESCE(a.first_name, '') as firstName, COALESCE(a.middle_name, '') as middleName, " +
            "COALESCE(a.last_name, '') as lastName, " +
            "CONCAT(COALESCE(a.first_name, ''), ' ', COALESCE(a.middle_name, ''), ' ', COALESCE(a.last_name, '')) AS fullname, " +
            "COALESCE(a.email, '') AS email, a.gender AS gender, a.status AS status, a.national_date AS nationalDate, " +
            "a.national_id AS nationalId, a.old_national_id AS oldNationalId, " +
            "a.address AS address, c.amount AS balance, c.point AS score " +
            "FROM viet_qr.account_login b " +
            "INNER JOIN account_information a ON b.id = a.user_id " +
            "INNER JOIN account_wallet c ON b.id = c.user_id " +
            "WHERE a.user_id = :userId LIMIT 1 ", nativeQuery = true)
    IUserInfoDTO getUserInfoDetailsByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT a.id AS id, b.phone_no AS phoneNo, " +
            "COALESCE(a.first_name, '') as firstName, COALESCE(a.middle_name, '') as middleName, " +
            "COALESCE(a.last_name, '') as lastName, " +
            "CONCAT(COALESCE(a.first_name, ''), ' ', COALESCE(a.middle_name, ''), ' ', COALESCE(a.last_name, '')) AS fullname, " +
            "COALESCE(a.email, '') AS email, a.address AS address " +
            "FROM viet_qr.account_login b " +
            "INNER JOIN account_information a ON b.id = a.user_id " +
            "WHERE a.user_id = :userId LIMIT 1 ", nativeQuery = true)
    IUserInfoQrDTO getUserInfoQRByUserId(String userId);

    @Query(value = "SELECT a.id AS id, b.phone_no AS phoneNo, " +
            "COALESCE(a.first_name, '') as firstName, COALESCE(a.middle_name, '') as middleName, " +
            "COALESCE(a.last_name, '') as lastName, " +
            "CONCAT(COALESCE(a.first_name, ''), ' ', COALESCE(a.middle_name, ''), ' ', COALESCE(a.last_name, '')) AS fullName, " +
            "COALESCE(a.email, '') AS email, a.gender AS gender, a.status AS status, a.national_date AS nationalDate, " +
            "a.national_id AS nationalId, a.old_national_id AS oldNationalId, " +
            "a.address AS address " +
            "FROM viet_qr.account_login b " +
            "INNER JOIN account_information a ON b.id = a.user_id " +
            "WHERE a.user_id = :userId LIMIT 1 ", nativeQuery = true)
    IUserInfoDTO getUserInfoByUserId(String userId);

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

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_login SET is_verify = TRUE, email = :email WHERE id = :userId", nativeQuery = true)
    void updateIsVerifiedByUserId(@Param(value = "userId") String userId, @Param(value = "email") String email);

    @Query(value = "SELECT id, status FROM account_login WHERE phone_no = :phoneNo", nativeQuery = true)
    AccountCheckDTO checkExistedPhoneNo(@Param(value = "phoneNo") String phoneNo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_login SET status = :status WHERE id = :id", nativeQuery = true)
    void updateStatus(@Param(value = "status") int status, @Param(value = "id") String id);

    @Query(value = "SELECT id FROM account_login WHERE email = :email AND password = :password AND status = 1", nativeQuery = true)
    String loginByEmail(@Param(value = "email") String email, @Param(value = "password") String password);

    ////////
    // UPDATE CARD
    @Transactional
    @Modifying
    @Query(value = "UPDATE account_login SET card_number = :cardNumber WHERE id = :userId", nativeQuery = true)
    void updateCardNumber(@Param(value = "cardNumber") String cardNumber, @Param(value = "userId") String userId);

    // reset password
    @Transactional
    @Modifying
    @Query(value = "UPDATE account_login SET password = :password WHERE phone_no = :phoneNo ", nativeQuery = true)
    void resetPassword(
            @Param(value = "password") String password,
            @Param(value = "phoneNo") String phoneNo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_login SET card_nfc_number = :cardNumber WHERE id = :userId", nativeQuery = true)
    void updateCardNfcNumber(@Param(value = "cardNumber") String cardNumber, @Param(value = "userId") String userId);

    //////////
    //////////
    @Query(value = "SELECT phone_no FROM account_login WHERE id = :userId", nativeQuery = true)
    String getPhoneNoById(@Param(value = "userId") String userId);

    ///////
    // CHECK EXISTED CARD
    @Query(value = "SELECT card_number FROM account_login WHERE card_number = :cardNumber", nativeQuery = true)
    String checkExistedCardNumber(@Param(value = "cardNumber") String cardNumber);

    @Query(value = "SELECT card_nfc_number FROM account_login WHERE card_nfc_number = :cardNumber", nativeQuery = true)
    String checkExistedCardNfcNumber(@Param(value = "cardNumber") String cardNumber);

    ////////
    // LOGIN CARD
    @Query(value = "SELECT id FROM account_login WHERE card_number = :cardNumber", nativeQuery = true)
    String loginByCardNumber(@Param(value = "cardNumber") String cardNumber);

    //
    @Query(value = "SELECT id FROM account_login WHERE card_nfc_number = :cardNumber ", nativeQuery = true)
    String loginByCardNfcNumber(@Param(value = "cardNumber") String cardNumber);

    ////////
    // GET CARD INFO
    @Query(value = "SELECT card_number FROM account_login WHERE id = :userId", nativeQuery = true)
    String getCardNumberByUserId(@Param(value = "userId") String userId);

    // CardVQRInfoDTO
    @Query(value = "SELECT card_number as cardNumber, card_nfc_number as cardNfcNumber FROM account_login WHERE id = :userId ", nativeQuery = true)
    CardVQRInfoDTO getVcardInforByUserId(@Param(value = "userId") String userId);

    //////////
    //////////
    @Query(value = "SELECT id FROM account_login", nativeQuery = true)
    List<String> getAllUserIds();

    @Query(value = "SELECT id FROM account_login WHERE phone_no = :phoneNo AND status = 1", nativeQuery = true)
    String getIdFromPhoneNo(@Param(value = "phoneNo") String phoneNo);

    @Query(value = "SELECT * FROM account_login", nativeQuery = true)
    List<AccountLoginEntity> getAllAccountLogin();

    @Query(value = "SELECT id FROM account_login WHERE id = :userId AND password = :password ", nativeQuery = true)
    String checkPassword(@Param(value = "userId") String userId,
                         @Param(value = "password") String password);

    boolean existsByPhoneNo(String phoneNo);

    @Query(value = "SELECT COUNT(*) > 0 FROM account_login WHERE phone_no = :phoneNo", nativeQuery = true)
    boolean existsPhoneNo(@Param("phoneNo") String phoneNo);

    @Query(value = "SELECT COUNT(b.id) FROM account_login b WHERE b.time BETWEEN :startTime AND :endTime", nativeQuery = true)
    long countAccountsRegisteredInDay(@Param("startTime") long startTime, @Param("endTime") long endTime);

    @Query(value = "SELECT COUNT(b.id) FROM account_login b", nativeQuery = true)
    long getTotalUsers();

    @Query(value = "SELECT a.time AS timeCreate FROM account_login a where id = :userId ", nativeQuery = true)
    long getRegisterDate(String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_login SET email = :email WHERE id = :userId", nativeQuery = true)
    void updateEmailByUserId(String email, String userId);

    @Query(value = "SELECT a.id, a.phone_no, a.email, a.time FROM account_login as a WHERE a.time BETWEEN :startTime AND :endTime", nativeQuery = true)
    List<IAccountLogin> findUsersRegisteredInDay(@Param("startTime") long startTime, @Param("endTime") long endTime);

    @Query(value = "SELECT is_verify FROM account_login WHERE id = :userId", nativeQuery = true)
    boolean getVerifyEmailStatus(String userId);

    @Query(value = "SELECT COUNT(b.id) FROM account_login b WHERE b.time <= :endTime", nativeQuery = true)
    long getTotalUsersUntilDate(@Param("endTime") long endTime);

    @Query(value = "SELECT a.id, a.phone_no, a.email, a.time FROM account_login as a WHERE a.time BETWEEN :startTime AND :endTime", nativeQuery = true)
    List<IAccountLogin> findUsersRegisteredInMonth(@Param("startTime") long startTime, @Param("endTime") long endTime);

    @Query(value = "SELECT * FROM account_login WHERE phone_no = :phoneNo LIMIT 1", nativeQuery = true)
    AccountLoginEntity getAccountLoginEntityByPhoneNo(String phoneNo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_login SET otp = :randomOTP, time_expired = :timeVerified WHERE phone_no = :phoneNo AND email = :email AND is_verify = TRUE LIMIT 1", nativeQuery = true)
    void updateOtpLogin(String email, String phoneNo, long timeVerified, String randomOTP);

    @Query(value = "SELECT * FROM account_login WHERE id = :userId LIMIT 1", nativeQuery = true)
    AccountLoginEntity getAccountLoginById(String userId);
}
