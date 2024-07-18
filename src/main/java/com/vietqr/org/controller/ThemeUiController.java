package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.entity.ThemeUiEntity;
import com.vietqr.org.service.ThemeUiService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ThemeUiController {
    private static final Logger logger = Logger.getLogger(ThemeUiController.class);

    @Autowired
    ThemeUiService themeUiService;

    @GetMapping("theme/list")
    public ResponseEntity<List<ThemeUiEntity>> getThemes() {
        List<ThemeUiEntity> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = themeUiService.getThemes();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getThemes: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
