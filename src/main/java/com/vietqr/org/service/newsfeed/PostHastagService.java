package com.vietqr.org.service.newsfeed;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.PostHastagEntity;

@Service
public interface PostHastagService {

    public int insert(PostHastagEntity entity);

}
