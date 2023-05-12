package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.NotificationCountDTO;
import com.vietqr.org.dto.NotificationInputDTO;
import com.vietqr.org.dto.NotificationStatusDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.service.NotificationService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class NotificationController {
    private static final Logger logger = Logger.getLogger(NotificationController.class);

    @Autowired
    NotificationService notificationService;

    @PostMapping("notifications")
    public ResponseEntity<List<NotificationEntity>> getNotificationsByUserId(
            @Valid @RequestBody NotificationInputDTO dto) {
        List<NotificationEntity> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = notificationService.getNotificationsByUserId(dto.getUserId(), dto.getOffset());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Notification Error: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("notification/count/{userId}")
    ResponseEntity<NotificationCountDTO> getNotificationCountByUserId(@PathVariable(value = "userId") String userId) {
        NotificationCountDTO result = null;
        HttpStatus httpStatus = null;
        try {
            int count = notificationService.getNotificationCountByUserId(userId);
            result = new NotificationCountDTO(count);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Notification Count Error: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("notification/status")
    ResponseEntity<ResponseMessageDTO> updateNotificationStatus(@Valid @RequestBody NotificationStatusDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            notificationService.updateNotificationStatus(dto.getUserId());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Notification Count Error: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
