package com.vietqr.org.service.newsfeed;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.PostEntity;

@Service
public interface PostService {

    public int insert(PostEntity entity);

}
