package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.dto.BranchChoiceDTO;
import com.vietqr.org.dto.BranchConnectedCheckDTO;
import com.vietqr.org.dto.BranchFilterResponseDTO;

@Repository
public interface BranchInformationRepository extends JpaRepository<BranchInformationEntity, Long> {

        @Query(value = "SELECT * FROM branch_information WHERE id = :id", nativeQuery = true)
        BranchInformationEntity getBranchById(@Param(value = "id") String id);

        // delete
        @Transactional
        @Modifying
        @Query(value = "DELETE from branch_information WHERE id = :id", nativeQuery = true)
        void deleteBranch(@Param(value = "id") String id);

        // delete all branch into business
        @Transactional
        @Modifying
        @Query(value = "DELETE from branch_information WHERE business_id = :businessId", nativeQuery = true)
        void deleteAllBranchByBusinessId(@Param(value = "businessId") String businessId);

        // get list branch by business_id
        @Query(value = "SELECT * FROM branch_information WHERE business_id = :businessId AND is_active = true", nativeQuery = true)
        List<BranchInformationEntity> getListBranchByBusinessId(@Param(value = "businessId") String businessId);

        // update active branch
        @Transactional
        @Modifying
        @Query(value = "UPDATE branch_information SET is is_active = :isActive WHERE id = :id", nativeQuery = true)
        void updateActiveBranch(@Param(value = "isActive") boolean isActive, @Param(value = "id") String id);

        @Query(value = "SELECT a.id "
                        + "FROM branch_information a "
                        + "INNER JOIN business_member b "
                        + "ON a.business_id = b.business_id "
                        + "WHERE b.user_id = :userId", nativeQuery = true)
        List<String> getBranchIdsByUserIdBusiness(@Param(value = "userId") String userId);

        @Query(value = "SELECT a.id as branchId, a.name, a.address "
                        + "FROM branch_information a "
                        + "WHERE business_id = :businessId AND a.is_active = true", nativeQuery = true)
        List<BranchChoiceDTO> getBranchsByBusinessId(@Param(value = "businessId") String businessId);

        @Query(value = "SELECT branch_id "
                        + "FROM bank_receive_branch "
                        + "WHERE bank_id = :bankId", nativeQuery = true)
        List<String> getBranchIdsByBankId(@Param(value = "bankId") String bankId);

        @Query(value = "SELECT id as branchId, name as branchName "
                        + "FROM branch_information "
                        + "WHERE business_id = :businessId", nativeQuery = true)
        List<BranchFilterResponseDTO> getBranchFilters(@Param(value = "businessId") String businessId);

        @Query(value = "SELECT id FROM branch_information WHERE business_id = :businessId", nativeQuery = true)
        List<String> getBranchIdsByBusinessId(@Param(value = "businessId") String businessId);

        @Query(value = "SELECT a.branch_id as branchId, b.name as branchName "
                        + "FROM branch_member a "
                        + "INNER JOIN branch_information b ON a.branch_id = b.id "
                        + "WHERE a.user_id = :userId AND a.role = :role "
                        + "AND b.business_id = :businessId", nativeQuery = true)
        List<BranchFilterResponseDTO> getBranchFilterByUserIdAndRole(@Param(value = "userId") String userId,
                        @Param(value = "role") int role, @Param(value = "businessId") String businessId);

        @Query(value = "SELECT branch_information.id as branchId, IF(bank_receive_branch.branch_id IS NOT NULL, true, false) AS connected "
                        + "FROM branch_information "
                        + "LEFT JOIN "
                        + "bank_receive_branch ON branch_information.id= "
                        + "bank_receive_branch.branch_id WHERE branch_information.business_id= :businessId", nativeQuery = true)
        List<BranchConnectedCheckDTO> getBranchContects(@Param(value = "businessId") String businessId);

}
