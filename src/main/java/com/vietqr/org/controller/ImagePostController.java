package com.vietqr.org.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.service.newsfeed.ImagePostService;

@RestController
@CrossOrigin
@RequestMapping("/api/post/images")
public class ImagePostController {

    @Autowired
    ImagePostService imagePostService;

}
