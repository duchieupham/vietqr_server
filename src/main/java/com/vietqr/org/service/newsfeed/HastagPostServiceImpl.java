package com.vietqr.org.service.newsfeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.newsfeed.HastagPostEntity;
import com.vietqr.org.repository.HastagPostRepo;

@Service
public class HastagPostServiceImpl implements HastagPostService {

    @Autowired
    HastagPostRepo repo;

    @Override
    public int insert(HastagPostEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedHastag(String hastag) {
        return repo.checkExistedHastag(hastag);
    }

}
