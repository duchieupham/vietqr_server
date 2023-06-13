package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ImageEntity;

@Service
public interface ImageService {
	public int insertImage(ImageEntity entity);

	public byte[] getImageById(String id);

	public void updateImage(byte[] image, String name, String id);

	public String getImageNameById(String id);

	public byte[] getImageByName(String name);
}
