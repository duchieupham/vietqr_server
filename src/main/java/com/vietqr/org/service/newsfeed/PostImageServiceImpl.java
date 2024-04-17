package com.vietqr.org.service.newsfeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.PostImageEntity;
import com.vietqr.org.repository.PostImageRepo;

@Service
public class PostImageServiceImpl implements PostImageService {

    @Autowired
    PostImageRepo repo;

    @Override
    public int insert(PostImageEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
