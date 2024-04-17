package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.newsfeed.ImagePostEntity;

@Repository
public interface ImagePostRepo extends JpaRepository<ImagePostEntity, Long> {

    @Query(value = "SELECT * FROM image_post WHERE id = :id ", nativeQuery = true)
    ImagePostEntity getImagePostById(@Param(value = "id") String id);

}
