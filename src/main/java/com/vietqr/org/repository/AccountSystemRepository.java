package com.vietqr.org.repository;

import com.vietqr.org.dto.IAccountSystemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountSystemEntity;

import javax.transaction.Transactional;

@Repository
public interface AccountSystemRepository extends JpaRepository<AccountSystemEntity, Long> {

    @Query(value = "SELECT * FROM account_system WHERE username = :username AND password = :password", nativeQuery = true)
    AccountSystemEntity loginAdmin(@Param(value = "username") String username,
            @Param(value = "password") String password);

    @Query(value = "SELECT id FROM account_system WHERE id = :id", nativeQuery = true)
    String checkExistedAdmin(@Param(value = "id") String id);

    @Query(value="SELECT id, username, role, name  FROM account_system WHERE id = :adminId ", nativeQuery = true )
    IAccountSystemDTO findAdminById(@Param("adminId") String adminId);

    //reset pass word
    @Modifying
    @Transactional
    @Query(value = "UPDATE account_login SET password = :newPassword WHERE phone_no = :phoneNo", nativeQuery = true)
    int updateUserPassword(@Param("phoneNo") String phoneNo,@Param("newPassword") String newPassword);
}
