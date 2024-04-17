package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountSystemEntity;

@Repository
public interface AccountSystemRepository extends JpaRepository<AccountSystemEntity, Long> {

    @Query(value = "SELECT * FROM account_system WHERE username = :username AND password = :password", nativeQuery = true)
    AccountSystemEntity loginAdmin(@Param(value = "username") String username,
            @Param(value = "password") String password);

    @Query(value = "SELECT id FROM account_system WHERE id = :id", nativeQuery = true)
    String checkExistedAdmin(@Param(value = "id") String id);
}
