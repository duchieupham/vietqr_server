package com.vietqr.org.repository;

import com.vietqr.org.dto.RoleMemberDTO;
import com.vietqr.org.entity.TransactionReceiveRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionReceiveRoleRepository extends JpaRepository<TransactionReceiveRoleEntity, String> {
    @Query(value = "SELECT id, name, description, color, role "
            + "FROM transaction_receive_role WHERE id IN :roleIds", nativeQuery = true)
    List<RoleMemberDTO> getRoleByIds(@Param(value = "roleIds") List<String> receiveRoles);
}
