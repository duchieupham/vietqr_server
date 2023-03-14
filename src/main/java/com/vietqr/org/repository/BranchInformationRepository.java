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

@Repository
public interface BranchInformationRepository extends JpaRepository<BranchInformationEntity, Long> {

        @Query(value = "SELECT * FROM branch_information WHERE id = :id", nativeQuery = true)
        BranchInformationEntity getBranchById(@Param(value = "id") String id);

        // delete
        @Transactional
        @Modifying
        @Query(value = "DELETE from branch_information WHERE id = :id", nativeQuery = true)
        void deleteBranch(@Param(value = "id") String id);

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
}
