package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.repository.ImageRepository;

@Service
public class ImageServiceImpl implements ImageService{

	@Autowired
	ImageRepository imageRepo;

	@Override
	public int insertImage(ImageEntity entity) {
		return imageRepo.save(entity) == null ? 0: 1;
	}

	@Override
	public byte[] getImageById(String id) {
		return imageRepo.getImageById(id);
	}

	@Override
	public void updateImage(byte[] image, String id) {
		 imageRepo.updateImage(image, id);
	}

}
