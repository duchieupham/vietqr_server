package com.vietqr.org.service.newsfeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.PostHastagEntity;
import com.vietqr.org.repository.PostHastagRepo;

@Service
public class PostHastagServiceImpl implements PostHastagService {

    @Autowired
    PostHastagRepo repo;

    @Override
    public int insert(PostHastagEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
