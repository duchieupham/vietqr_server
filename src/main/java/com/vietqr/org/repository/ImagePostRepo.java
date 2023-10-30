package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.newsfeed.ImagePostEntity;

@Repository
public interface ImagePostRepo extends JpaRepository<ImagePostEntity, Long> {

}
