package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.dto.BusinessListItemDTO;
import com.vietqr.org.dto.BusinessMemberInsertDTO;
import com.vietqr.org.dto.BusinessBranchInsertDTO;
import com.vietqr.org.dto.BusinessInformationInsertDTO;
import com.vietqr.org.dto.BusinessStatusDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.BusinessItemResponseDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.dto.BusinessItemDTO;
import com.vietqr.org.dto.BusinessCounterDTO;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.BusinessInformationEntity;
import com.vietqr.org.entity.BusinessMemberEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.BusinessInformationService;
import com.vietqr.org.service.BusinessMemberService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.util.RandomCodeUtil;

@RestController
@RequestMapping("/api")
public class BusinessInformationController {

	@Autowired
	BusinessInformationService businessInfoService;

	@Autowired
	BusinessMemberService businessMemberService;

	@Autowired
	BranchInformationService branchInformationService;

	@Autowired
	BranchMemberService branchMemberService;

	@Autowired
	ImageService imageService;

	@Autowired
	TransactionReceiveService transactionReceiveService;

	// insert business information
	@PostMapping("business-information")
	public ResponseEntity<ResponseMessageDTO> insertBusinessInformation(
			@ModelAttribute BusinessInformationInsertDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// initial BussinessInfor. entity
			UUID businessUUID = UUID.randomUUID();
			BusinessInformationEntity entity = new BusinessInformationEntity();
			String businessCode = RandomCodeUtil.generateRandomCode(1);
			entity.setId(businessUUID.toString());
			entity.setAddress(dto.getAddress());
			entity.setActive(true);
			entity.setName(dto.getName());
			entity.setCode(businessCode);
			if (dto.getTaxCode() == null) {
				entity.setTaxCode("");
			} else {
				entity.setTaxCode(dto.getTaxCode());
			}
			// insert image != null
			if (dto.getImage() != null) {
				UUID uuidImage = UUID.randomUUID();
				String fileName = StringUtils.cleanPath(dto.getImage().getOriginalFilename());
				ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, dto.getImage().getBytes());
				imageService.insertImage(imageEntity);
				entity.setImgId(uuidImage.toString());
			} else {
				entity.setImgId("");
			}
			if (dto.getCoverImage() != null) {
				UUID uuidCoverImage = UUID.randomUUID();
				String fileName = StringUtils.cleanPath(dto.getCoverImage().getOriginalFilename());
				ImageEntity imageEntity = new ImageEntity(uuidCoverImage.toString(), fileName,
						dto.getCoverImage().getBytes());
				imageService.insertImage(imageEntity);
				entity.setCoverImgId(uuidCoverImage.toString());
			} else {
				entity.setCoverImgId("");
			}
			// insert business information
			int check = businessInfoService.insertBusinessInformation(entity);
			if (check == 1) {
				// insert business member
				for (BusinessMemberInsertDTO memberDTO : dto.getMembers()) {
					BusinessMemberEntity businessMemberEntity = new BusinessMemberEntity();
					UUID uuidMember = UUID.randomUUID();
					businessMemberEntity.setId(uuidMember.toString());
					businessMemberEntity.setUserId(memberDTO.getUserId());
					businessMemberEntity.setRole(memberDTO.getRole());
					businessMemberEntity.setBusinessId(businessUUID.toString());
					businessMemberService.insertBusinessMember(businessMemberEntity);
				}
				///
				// insert business branch
				//
				// if business branch = null OR is empty => create default branch
				if (dto.getBranchs() == null || dto.getBranchs().isEmpty()) {
					BranchInformationEntity branchEntity = new BranchInformationEntity();
					UUID uuidBranch = UUID.randomUUID();
					String branchCode = RandomCodeUtil.generateRandomCode(2);
					branchEntity.setId(uuidBranch.toString());
					branchEntity.setName(dto.getName());
					branchEntity.setAddress(dto.getAddress());
					branchEntity.setBusinessId(businessUUID.toString());
					branchEntity.setActive(true);
					branchEntity.setCode(branchCode);
					branchInformationService.insertBranchInformation(branchEntity);
				} else {
					for (BusinessBranchInsertDTO branchDTO : dto.getBranchs()) {
						BranchInformationEntity branchEntity = new BranchInformationEntity();
						UUID uuidBranch = UUID.randomUUID();
						String branchCode = RandomCodeUtil.generateRandomCode(2);
						branchEntity.setId(uuidBranch.toString());
						branchEntity.setName(branchDTO.getName());
						branchEntity.setAddress(branchDTO.getAddress());
						branchEntity.setBusinessId(businessUUID.toString());
						branchEntity.setActive(true);
						branchEntity.setCode(branchCode);
						branchInformationService.insertBranchInformation(branchEntity);
					}
				}
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			} else {
				result = new ResponseMessageDTO("FAILED", "E14");
				httpStatus = HttpStatus.BAD_REQUEST;
			}

		} catch (Exception e) {
			System.out.println("Error at insertBusinessInformation - BusinessInformationController: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// get business list dashboard
	@GetMapping("business-informations/{userId}")
	public ResponseEntity<List<BusinessItemResponseDTO>> getBusinessItems(@PathVariable("userId") String userId) {
		List<BusinessItemResponseDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			List<BusinessItemDTO> businessItems = businessMemberService.getBusinessItemByUserId(userId);
			List<BusinessItemDTO> branchItems = branchMemberService.getBusinessItemByUserId(userId);
			if (businessItems != null && !businessItems.isEmpty()) {
				for (BusinessItemDTO item : businessItems) {
					List<TransactionRelatedDTO> transactions = new ArrayList<>();
					BusinessItemResponseDTO dto = new BusinessItemResponseDTO();
					BusinessCounterDTO counterDTO = businessInfoService.getBusinessCounter(item.getBusinessId());
					dto.setBusinessId(item.getBusinessId());
					dto.setCode(item.getCode());
					dto.setRole(item.getRole());
					dto.setImgId(item.getImgId());
					dto.setCoverImgId(item.getCoverImgId());
					dto.setName(item.getName());
					dto.setAddress(item.getAddress());
					dto.setTaxCode(item.getTaxCode());
					transactions = transactionReceiveService.getRelatedTransactionReceives(item.getBusinessId());
					dto.setTransactions(transactions);
					dto.setTotalMember(counterDTO.getTotalAdmin() + counterDTO.getTotalMember());
					dto.setTotalBranch(counterDTO.getTotalBranch());
					result.add(dto);
				}
			}
			if (branchItems != null && !branchItems.isEmpty()) {
				for (BusinessItemDTO item : branchItems) {
					List<TransactionRelatedDTO> transactions = new ArrayList<>();
					BusinessItemResponseDTO dto = new BusinessItemResponseDTO();
					BusinessCounterDTO counterDTO = businessInfoService.getBusinessCounter(item.getBusinessId());
					dto.setBusinessId(item.getBusinessId());
					dto.setCode(item.getCode());
					dto.setRole(item.getRole());
					dto.setImgId(item.getImgId());
					dto.setCoverImgId(item.getCoverImgId());
					dto.setName(item.getName());
					dto.setAddress(item.getAddress());
					dto.setTaxCode(item.getTaxCode());
					transactions = transactionReceiveService.getRelatedTransactionReceives(item.getBusinessId());
					dto.setTransactions(transactions);
					dto.setTotalMember(counterDTO.getTotalAdmin() + counterDTO.getTotalMember());
					dto.setTotalBranch(counterDTO.getTotalBranch());
					result.add(dto);
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBusinessItems " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<BusinessItemResponseDTO>>(result, httpStatus);
	}

	// get business list by userId
	@GetMapping("business-information/list/{userId}")
	public ResponseEntity<List<BusinessListItemDTO>> getBusinessListItem(@PathVariable("userId") String userId) {
		List<BusinessListItemDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			result = businessMemberService.getBusinessListItem(userId);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBusinessListItem - BusinessInformationController: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<BusinessListItemDTO>>(result, httpStatus);
	}

	// get business information
	@GetMapping("business-information/{id}")
	public ResponseEntity<BusinessInformationEntity> getBusinessInformation(@PathVariable("id") String id) {
		BusinessInformationEntity result = null;
		HttpStatus httpStatus = null;
		try {
			result = businessInfoService.getBusinessById(id);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBusinessListItem - BusinessInformationController: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<BusinessInformationEntity>(result, httpStatus);
	}

	// update cover image
	@PutMapping("business-information/cover")
	public ResponseEntity<ResponseMessageDTO> updateBusinessCover(@RequestParam String id,
			@RequestParam String coverImgId, @RequestParam MultipartFile coverImage) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (coverImgId.isEmpty()) {
				UUID uuidImage = UUID.randomUUID();
				String fileName = StringUtils.cleanPath(coverImage.getOriginalFilename());
				ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, coverImage.getBytes());
				imageService.insertImage(imageEntity);
				businessInfoService.updateBusinessCover(coverImgId, id);
				result = new ResponseMessageDTO("SUCCESS", uuidImage.toString());
				httpStatus = HttpStatus.OK;
			} else {
				String fileName = StringUtils.cleanPath(coverImage.getOriginalFilename());
				imageService.updateImage(coverImage.getBytes(), fileName, coverImgId);
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
	}

	// update image
	@PutMapping("business-information/image")
	public ResponseEntity<ResponseMessageDTO> updateBusinessImage(@RequestParam String id, @RequestParam String imgId,
			@RequestParam MultipartFile image) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (imgId.isEmpty()) {
				UUID uuidImage = UUID.randomUUID();
				String fileName = StringUtils.cleanPath(image.getOriginalFilename());
				ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, image.getBytes());
				imageService.insertImage(imageEntity);
				businessInfoService.updateBusinessCover(imgId, id);
				result = new ResponseMessageDTO("SUCCESS", uuidImage.toString());
				httpStatus = HttpStatus.OK;
			} else {
				String fileName = StringUtils.cleanPath(image.getOriginalFilename());
				imageService.updateImage(image.getBytes(), fileName, imgId);
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
	}

	// update business status
	@PutMapping("business-information/status")
	public ResponseEntity<ResponseMessageDTO> updateBusinessStatus(@Valid @RequestBody BusinessStatusDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			businessInfoService.updateActiveBusinessInformation(dto.isStatus(), dto.getId());
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
	}

}
