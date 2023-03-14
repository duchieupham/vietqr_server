package com.vietqr.org.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long>{

	@Query(value ="SELECT image FROM image WHERE id = :id", nativeQuery  = true)
	byte[] getImageById(@Param(value = "id")String id);

	@Query(value = "DELETE FROM image WHERE id = :id", nativeQuery = true)
	int deleteImage(@Param(value = "id")String id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE image SET image = :image, name = :name WHERE id = :id", nativeQuery = true)
	void updateImage(@Param(value = "image") byte[] image, @Param(value = "name") String name, @Param(value = "id") String id);

}
