package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vietqr.org.entity.newsfeed.PostHastagEntity;

public interface PostHastagRepo extends JpaRepository<PostHastagEntity, Long> {

}
