package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.BusinessListItemDTO;
import com.vietqr.org.dto.BusinessChoiceDTO;
import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.BusinessItemDTO;
import com.vietqr.org.entity.BusinessMemberEntity;

@Repository
public interface BusinessMemberRepository extends JpaRepository<BusinessMemberEntity, Long> {

	@Query(value = "SELECT a.id, a.user_id as userId, a.role, b.phone_no as phoneNo, c.first_name as firstName, c.middle_name as middleName, c.last_name as lastName, c.address, c.gender, c.birth_date as birthDate, c.email, c.img_id as imgId "
			+ "FROM business_member a "
			+ "INNER JOIN account_login b ON a.user_id = b.id "
			+ "INNER JOIN account_information c ON a.user_id = c.user_id "
			+ "WHERE a.business_id = :businessId", nativeQuery = true)
	List<MemberDTO> getBusinessMembersByBusinessId(@Param(value = "businessId") String businessId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM business_member WHERE id = :id", nativeQuery = true)
	void deleteMemberFromBusinessInformation(@Param(value = "id") String id);

	@Query(value = "SELECT a.business_id, b.name, b.img_id "
			+ "FROM business_member a "
			+ "INNER JOIN business_information b ON a.business_id = b.id "
			+ "WHERE a.user_id = :userId", nativeQuery = true)
	List<BusinessListItemDTO> getBusinessListItem(@Param(value = "userId") String userId);

	@Query(value = "SELECT a.business_id as businessId, b.name, b.img_id "
			+ "as imgId, b.cover_img_id as coverImgId "
			+ "FROM business_member a "
			+ "INNER JOIN business_information b "
			+ "ON a.business_id = b.id "
			+ "WHERE a.user_id = :userId "
			+ "AND b.is_active = true", nativeQuery = true)
	List<BusinessChoiceDTO> getBusinessChoiceByUserId(@Param(value = "userId") String userId);

	@Query(value = "SELECT a.business_id as businessId, a.role, b.code, b.img_id as imgId, "
			+ "b.cover_img_id as coverImgId, b.name, b.address, b.tax_code as taxCode "
			+ "FROM business_member a "
			+ "INNER JOIN business_information b "
			+ "ON a.business_id=b.id "
			+ "WHERE a.user_id = :userId "
			+ "AND b.is_active=true", nativeQuery = true)
	List<BusinessItemDTO> getBusinessItemByUserId(@Param(value = "userId") String userId);
}
