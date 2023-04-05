package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.BranchMemberEntity;
import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.BusinessItemDTO;

@Repository
public interface BranchMemberRepository extends JpaRepository<BranchMemberEntity, Long> {

        @Query(value = "SELECT branch_id FROM branch_member WHERE user_id = :userId", nativeQuery = true)
        List<String> getBranchIdsByUserId(@Param(value = "userId") String userId);

        @Query(value = "SELECT a.id, a.user_id as userId, a.role, b.phone_no as phoneNo, c.first_name as firstName, c.middle_name as middleName, c.last_name as lastName, c.address, c.gender, c.birth_date as birthDate, c.email, c.img_id as imgId "
                        + "FROM branch_member a "
                        + "INNER JOIN account_login b ON a.user_id = b.id "
                        + "INNER JOIN account_information c ON a.user_id = c.user_id "
                        + "WHERE a.branch_id = :branchId", nativeQuery = true)
        List<MemberDTO> getBranchMembersByBranchId(@Param(value = "branchId") String branchId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM branch_member WHERE id = :id", nativeQuery = true)
        void deleteMemberFromBranch(@Param(value = "id") String id);

        @Query(value = "SELECT b.business_id as businessId, a.role, c.code, c.img_id as imgId, c.cover_img_id as coverImgId, c.name, c.address, c.tax_code as taxCode "
                        + "FROM branch_member a "
                        + "INNER JOIN branch_information b "
                        + "ON a.branch_id = b.id "
                        + "INNER JOIN business_information c "
                        + "ON b.business_id = c.id "
                        + "WHERE a.user_id = :userId AND b.is_active = true AND c.is_active = true", nativeQuery = true)
        List<BusinessItemDTO> getBusinessItemByUserId(@Param(value = "userId") String userId);

        @Query(value = "SELECT user_id as userId "
                        + "FROM business_member "
                        + "WHERE business_id = :businessId "
                        + "UNION "
                        + "SELECT user_id as userId "
                        + "FROM branch_member "
                        + "WHERE branch_id = :branchId", nativeQuery = true)
        List<String> getUserIdsByBusinessIdAndBranchId(@Param(value = "businessId") String businessId,
                        @Param(value = "branchId") String branchId);

        @Query(value = "SELECT COUNT(*) FROM branch_member  WHERE branch_id = :branchId", nativeQuery = true)
        int getTotalMemberInBranch(@Param(value = "branchId") String branchId);

        @Query(value = "SELECT a.id, c.id as userId, a.role, c.phone_no as phoneNo, b.first_name as firstName, b.middle_name as middleName, b.last_name as lastName, b.address, b.gender, b.birth_date as birthDate, b.email, b.img_id as imgId "
                        + "FROM branch_member a "
                        + "INNER JOIN account_information b "
                        + "ON a.user_id = b.user_id "
                        + "INNER JOIN account_login c "
                        + "ON a.user_id = c.id "
                        + "WHERE a.branch_id = :branchId AND a.role = 3", nativeQuery = true)
        MemberDTO getManagerByBranchId(@Param(value = "branchId") String branchId);

        @Query(value = "SELECT role FROM branch_member WHERE user_id = :userId AND branch_id = :branchId", nativeQuery = true)
        int getRoleFromBranch(@Param(value = "userId") String userId, @Param(value = "branchId") String branchId);
}
