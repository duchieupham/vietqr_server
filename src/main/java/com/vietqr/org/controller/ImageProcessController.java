package com.vietqr.org.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.service.ImageService;

@RestController
@CrossOrigin
@RequestMapping("/images")
public class ImageProcessController {

    @Autowired
    ImageService imageService;

    @GetMapping(value = "/{name:.+}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public ResponseEntity<byte[]> getImage(@PathVariable String name) {
        byte[] result = new byte[0];
        HttpStatus httpStatus = null;
        try {
            result = imageService.getImageByName(name);
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (name.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(result, headers, httpStatus);
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result, httpStatus);
        }
    }
}
