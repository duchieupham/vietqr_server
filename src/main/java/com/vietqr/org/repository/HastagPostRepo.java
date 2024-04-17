package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.newsfeed.HastagPostEntity;

@Repository
public interface HastagPostRepo extends JpaRepository<HastagPostEntity, Long> {

    // check existed hastag
    @Query(value = "SELECT id FROM hastag_post WHERE hastag = :hastag ", nativeQuery = true)
    String checkExistedHastag(@Param(value = "hastag") String hastag);

}
