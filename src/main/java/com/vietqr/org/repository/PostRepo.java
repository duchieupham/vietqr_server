package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.newsfeed.PostEntity;

@Repository
public interface PostRepo extends JpaRepository<PostEntity, Long> {

}
