package com.vietqr.org.service.newsfeed;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.PostImageEntity;

@Service
public interface PostImageService {

    public int insert(PostImageEntity entity);

}
