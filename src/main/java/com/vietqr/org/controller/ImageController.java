package com.vietqr.org.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/images")
public class ImageController {

	@Autowired
	ImageService imageService;

	@GetMapping(value = "/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
		byte[] result = new byte[0];
		HttpStatus httpStatus = null;
		try {
			result = imageService.getImageById(id);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getImage: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

}
