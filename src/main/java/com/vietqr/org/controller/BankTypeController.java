package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.vietqr.org.dto.BankTypeDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.CaiBankEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.ImageService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BankTypeController {
	private static final Logger logger = Logger.getLogger(BankTypeController.class);

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	ImageService imageService;

	@Autowired
	CaiBankService caiBankService;

	@PostMapping(value = "bank-type", produces = "application/json;charset=UTF-8")
	public ResponseEntity<ResponseMessageDTO> insertBankType(@Valid @RequestParam String bankCode,
			@Valid @RequestParam String bankName, @Valid @RequestParam MultipartFile imgUrl) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID uuid = UUID.randomUUID();
			UUID uuidImage = UUID.randomUUID();
			String fileName = StringUtils.cleanPath(imgUrl.getOriginalFilename());
			BankTypeEntity entity = new BankTypeEntity(uuid.toString(), bankCode, bankName, uuidImage.toString(), 0);
			bankTypeService.insertBankType(entity);
			// insert image
			ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, imgUrl.getBytes());
			imageService.insertImage(imageEntity);
			/// old process, save image to spring boot project
			// FileUploadUtil.saveFile("src/main/webapp/WEB-INF/images", fileName, imgUrl);
			result = new ResponseMessageDTO("SUCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at insertBankType: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("bank-type")
	public ResponseEntity<List<BankTypeDTO>> getBankTypes() {
		List<BankTypeDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			List<BankTypeEntity> entities = bankTypeService.getBankTypes();
			if (!entities.isEmpty()) {
				for (BankTypeEntity entity : entities) {
					BankTypeDTO dto = new BankTypeDTO();
					dto.setId(entity.getId());
					dto.setBankCode(entity.getBankCode());
					dto.setBankName(entity.getBankName());
					dto.setImageId(entity.getImgId());
					dto.setStatus(entity.getStatus());
					String caiValue = caiBankService.getCaiValue(dto.getId());
					dto.setCaiValue(caiValue);
					result.add(dto);
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBankTypes: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("bank-type/cai/{caiValue}")
	public ResponseEntity<BankTypeDTO> getBankTypeByCaiValue(@PathVariable("caiValue") String caiValue) {
		BankTypeDTO result = null;
		HttpStatus httpStatus = null;
		try {
			CaiBankEntity caiBankEntity = caiBankService.getCaiBankByCaiValue(caiValue);
			if (caiBankEntity != null) {
				BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(caiBankEntity.getBankTypeId());
				if (bankTypeEntity != null) {
					BankTypeDTO dto = new BankTypeDTO();
					dto.setId(bankTypeEntity.getId());
					dto.setBankCode(bankTypeEntity.getBankCode());
					dto.setBankName(bankTypeEntity.getBankName());
					dto.setImageId(bankTypeEntity.getImgId());
					dto.setStatus(bankTypeEntity.getStatus());
					dto.setCaiValue(caiValue);
					result = dto;
					httpStatus = HttpStatus.OK;
				} else {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}
}