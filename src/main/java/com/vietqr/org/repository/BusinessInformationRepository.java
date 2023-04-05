package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.BusinessInformationEntity;
import com.vietqr.org.dto.BusinessCounterDTO;

@Repository
public interface BusinessInformationRepository extends JpaRepository<BusinessInformationEntity, Long> {

	@Transactional
	@Modifying
	@Query(value = "UPDATE business_information SET is_active = :value WHERE id = :id", nativeQuery = true)
	void updateActiveBussiness(@Param(value = "value") boolean value, @Param(value = "id") String id);

	@Query(value = "SELECT * FROM business_information WHERE id = :id", nativeQuery = true)
	BusinessInformationEntity getBusinessInformationById(@Param(value = "id") String id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE business_information SET img_id = :imgId WHERE id = :id", nativeQuery = true)
	void updateBusinessImage(@Param(value = "imgId") String imgId, @Param(value = "id") String id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE business_information SET cover_img_id = :coverImgId WHERE id = :id", nativeQuery = true)
	void updateBusinessCover(@Param(value = "coverImgId") String coverImgId, @Param(value = "id") String id);

	@Query(value = "SELECT "
			+ "(SELECT COUNT(*) FROM business_member WHERE business_id = :businessId) as totalAdmin, "
			+ "(SELECT COUNT(*) FROM branch_information a INNER JOIN branch_member b ON a.id = b.branch_id WHERE a.business_id = :businessId) as totalMember, "
			+ "(SELECT COUNT(*) FROM branch_information WHERE business_id = :businessId) as totalBranch; ", nativeQuery = true)
	BusinessCounterDTO getBusinessCounter(@Param(value = "businessId") String businessId);

	// @Query(value = "", nativeQuery = true)

}
