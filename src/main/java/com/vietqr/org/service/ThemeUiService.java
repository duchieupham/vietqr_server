package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ThemeUiEntity;

@Service
public interface ThemeUiService {

    public List<ThemeUiEntity> getThemes();

    public String getImgUrlByType(int type);
}
