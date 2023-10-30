package com.vietqr.org.service.newsfeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.ImagePostEntity;
import com.vietqr.org.repository.ImagePostRepo;

@Service
public class ImagePostServiceImpl implements ImagePostService {

    @Autowired
    ImagePostRepo repo;

    @Override
    public int insert(ImagePostEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
