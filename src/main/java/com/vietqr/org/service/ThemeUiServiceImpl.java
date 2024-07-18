package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ThemeUiEntity;
import com.vietqr.org.repository.ThemeUiRepository;

@Service
public class ThemeUiServiceImpl implements ThemeUiService {

    @Autowired
    ThemeUiRepository repo;

    @Override
    public List<ThemeUiEntity> getThemes() {
        return repo.getThemes();
    }

    @Override
    public String getImgUrlByType(int type) {
        return repo.getImgUrlByType(type);
    }

}
