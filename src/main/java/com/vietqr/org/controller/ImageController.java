package com.vietqr.org.controller;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import com.vietqr.org.service.AmazonS3Service;
import org.apache.log4j.Logger;
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
import software.amazon.awssdk.core.ResponseInputStream;

@RestController
@CrossOrigin
@RequestMapping("/api/images")
public class ImageController {

	private static final Logger logger = Logger.getLogger(ImageController.class);

	@Autowired
	ImageService imageService;

	@Autowired
	AmazonS3Service amazonS3Service;

	@GetMapping(value = "/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
		byte[] result = new byte[0];
		HttpStatus httpStatus = null;
		try {
			ResponseInputStream<?> responseInputStream = amazonS3Service.downloadFile(id);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = responseInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			result = outputStream.toByteArray();
			if (!(result.length > 0)) {
				result = imageService.getImageById(id);
			}
//			result = imageService.getImageById(id);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getImage: " + e.toString());
			logger.error("getImage: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
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
			// Amazon S3
			try {
				Thread thread = new Thread(() -> {
					amazonS3Service.uploadFile(uuid.toString(), image);
				});
				thread.start();
			} catch (Exception e) {
				logger.error("insertImage: AmazonS3 ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
			}

			imageService.insertImage(entity);
			result = new ResponseMessageDTO("SUCCESS", uuid.toString());
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			logger.error("insertImage: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

}
