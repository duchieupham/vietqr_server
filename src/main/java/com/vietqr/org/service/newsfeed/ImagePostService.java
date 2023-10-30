package com.vietqr.org.service.newsfeed;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.ImagePostEntity;

@Service
public interface ImagePostService {

    public int insert(ImagePostEntity entity);

}
