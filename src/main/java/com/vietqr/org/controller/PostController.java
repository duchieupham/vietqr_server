package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.dto.ImagePostConvertDTO;
import com.vietqr.org.dto.PostTypeNewsInputDTO;
import com.vietqr.org.dto.PostTypeNewsfeedInputDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.newsfeed.HastagPostEntity;
import com.vietqr.org.entity.newsfeed.ImagePostEntity;
import com.vietqr.org.entity.newsfeed.PostEntity;
import com.vietqr.org.entity.newsfeed.PostHastagEntity;
import com.vietqr.org.entity.newsfeed.PostImageEntity;
import com.vietqr.org.service.newsfeed.HastagPostService;
import com.vietqr.org.service.newsfeed.ImagePostService;
import com.vietqr.org.service.newsfeed.PostHastagService;
import com.vietqr.org.service.newsfeed.PostImageService;
import com.vietqr.org.service.newsfeed.PostService;
import com.vietqr.org.util.ImageUtil;
import com.vietqr.org.util.StringUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class PostController {

    private static final Logger logger = Logger.getLogger(PostController.class);

    @Autowired
    PostService postService;

    @Autowired
    ImagePostService imagePostService;

    @Autowired
    PostImageService postImageService;

    @Autowired
    HastagPostService hastagPostService;

    @Autowired
    PostHastagService postHastagService;

    // api insert post type newsfeed
    @PostMapping("post/newsfeed")
    public ResponseEntity<ResponseMessageDTO> insertNewsfeed(@ModelAttribute PostTypeNewsfeedInputDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // 1. check input
            if (dto != null) {
                // insert post
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                UUID postUUID = UUID.randomUUID();
                PostEntity postEntity = new PostEntity();
                postEntity.setId(postUUID.toString());
                postEntity.setType(1);
                postEntity.setTitle("");
                postEntity.setContent(dto.getContent());
                postEntity.setTimeCreated(time);
                postService.insert(postEntity);
                // image
                if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                    int indexImage = 0;
                    // insert image post
                    for (MultipartFile image : dto.getImages()) {
                        UUID imageUUID = UUID.randomUUID();
                        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                        ImagePostEntity imagePostEntity = new ImagePostEntity(imageUUID.toString(), fileName,
                                image.getBytes());
                        imagePostService.insert(imagePostEntity);
                        // link image post
                        UUID postImageUUID = UUID.randomUUID();
                        PostImageEntity postImageEntity = new PostImageEntity(postImageUUID.toString(),
                                postUUID.toString(), indexImage, imageUUID.toString());
                        postImageService.insert(postImageEntity);
                        indexImage++;
                    }
                }

                // hastag
                if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                    List<String> hastags = StringUtil.findHashtags(dto.getContent());
                    if (hastags != null && !hastags.isEmpty()) {
                        for (String hastag : hastags) {
                            String checkExistedHastag = hastagPostService.checkExistedHastag(hastag);
                            if (checkExistedHastag != null && !checkExistedHastag.trim().isEmpty()) {
                                // link hastag post
                                UUID postHastagUUID = UUID.randomUUID();
                                PostHastagEntity postHastagEntity = new PostHastagEntity(postHastagUUID.toString(),
                                        postUUID.toString(), checkExistedHastag);
                                postHastagService.insert(postHastagEntity);
                            } else {
                                // insert hastag
                                UUID hastagPostUUID = UUID.randomUUID();
                                HastagPostEntity hastagPostEntity = new HastagPostEntity(hastagPostUUID.toString(),
                                        hastag, time);
                                hastagPostService.insert(hastagPostEntity);
                                // link hastag post
                                UUID postHastagUUID = UUID.randomUUID();
                                PostHastagEntity postHastagEntity = new PostHastagEntity(postHastagUUID.toString(),
                                        postUUID.toString(), hastagPostUUID.toString());
                                postHastagService.insert(postHastagEntity);
                            }
                        }
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertNewsfeed: ERROR:  INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertNewsfeed: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // api insert post type news
    @PostMapping("post/news")
    public ResponseEntity<ResponseMessageDTO> insertNews(@RequestBody Object dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
        } catch (Exception e) {
            logger.error("insertNews: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    public String formattedContent(String html, String postId) {
        String result = "";
        try {
            if (html != null && !html.trim().isEmpty()) {

                // get list base64 images
                List<ImagePostConvertDTO> imageConverts = ImageUtil.parseImages(html);
                if (imageConverts != null && !imageConverts.isEmpty()) {
                    int index = 0;
                    for (ImagePostConvertDTO imageConvert : imageConverts) {
                        UUID imagePostUUID = UUID.randomUUID();
                        if (imageConvert != null && !imageConvert.getBase64Image().trim().isEmpty()) {
                            // convert to byte and insert image
                            byte[] imageConverted = ImageUtil.convertBase64ToByteArray(imageConvert.getBase64Image());
                            if (imageConverted != null) {
                                // insert image post
                                ImagePostEntity imagePostEntity = new ImagePostEntity(
                                        imagePostUUID.toString(),
                                        imageConvert.getName(),
                                        imageConverted);
                                imagePostService.insert(imagePostEntity);
                                // map post image
                                UUID postImageUUID = UUID.randomUUID();
                                PostImageEntity postImageEntity = new PostImageEntity(postImageUUID.toString(), postId,
                                        index, imagePostUUID.toString());
                                postImageService.insert(postImageEntity);
                                index++;
                            }
                            // replace content
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error("formattedContent: ERROR: " + e.toString());
        }
        return result;
    }

    @PostMapping("post/test/news")
    public ResponseEntity<ResponseMessageDTO> insertTestNews(@RequestBody PostTypeNewsInputDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        int a = 112;
        try {
            // List<String> images = ImageUtil.parseImages(dto.getContent());
            // int index = 0;
            // if (images != null && !images.isEmpty()) {
            // for (String image : images) {
            // index++;
            // }
            // }
            LocalDateTime currentDateTime = LocalDateTime.now();
            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            result = new ResponseMessageDTO("SUCCESS", "end try at: " + time);
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(result, httpStatus);
        } catch (Exception e) {
            // logger.error("insertNews: ERROR: " + e.toString());
            LocalDateTime currentDateTime = LocalDateTime.now();
            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            result = new ResponseMessageDTO("FAILED", "E05" + " at: " + time);
            httpStatus = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result, httpStatus);
        } finally {
            // Thực hiện công việc trong một luồng riêng

            Thread thread = new Thread(() -> {
                // Các công việc trong khối finally
                // ...
                try {
                    Thread.sleep(5000); // Tạm dừng luồng trong 1 giây
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                } catch (InterruptedException e) {
                    // Xử lý ngoại lệ nếu có
                }

            });
            thread.start();
        }
    }
    // api get list post pagging, sort by date
    // get list by category
    // get list by hastag
    // get list all
    // get list by title

    // api delete post

    // api count like

    // api like/unlike

    // api get list comment

    // api comment

    // api delete comment

}
