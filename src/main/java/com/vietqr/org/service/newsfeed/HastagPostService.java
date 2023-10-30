package com.vietqr.org.service.newsfeed;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.HastagPostEntity;

@Service
public interface HastagPostService {

    public int insert(HastagPostEntity entity);

    public String checkExistedHastag(String hastag);
}
