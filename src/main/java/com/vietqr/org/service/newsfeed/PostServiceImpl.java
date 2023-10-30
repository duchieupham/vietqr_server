package com.vietqr.org.service.newsfeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.PostEntity;
import com.vietqr.org.repository.PostRepo;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    PostRepo repo;

    @Override
    public int insert(PostEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
