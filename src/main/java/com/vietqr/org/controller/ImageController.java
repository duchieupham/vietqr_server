package com.vietqr.org.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.ImageEntity;
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

	@PostMapping("save")
	public ResponseEntity<ResponseMessageDTO> insertImage(
			@RequestParam MultipartFile image) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID uuid = UUID.randomUUID();
			String fileName = StringUtils.cleanPath(image.getOriginalFilename());
			ImageEntity entity = new ImageEntity(uuid.toString(), fileName, image.getBytes());
			imageService.insertImage(entity);
			result = new ResponseMessageDTO("SUCCESS", uuid.toString());
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at updainsertImageteImage: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

}
