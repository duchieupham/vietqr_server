package com.vietqr.org.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.entity.SystemSettingEntity;
import com.vietqr.org.service.SystemSettingService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class SystemSettingController {
    private static final Logger logger = Logger.getLogger(SystemSettingController.class);

    @Autowired
    public SystemSettingService systemSettingService;

    @GetMapping("system-setting")
    public ResponseEntity<SystemSettingEntity> getSystemSetting() {
        SystemSettingEntity result = null;
        HttpStatus httpStatus = null;
        try {
            result = systemSettingService.getSystemSetting();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("System setting: Error: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
