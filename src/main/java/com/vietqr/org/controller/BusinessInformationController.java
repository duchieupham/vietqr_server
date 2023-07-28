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
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.vietqr.org.dto.AccountBankBranchDTO;
import com.vietqr.org.dto.BranchConnectedCheckDTO;
import com.vietqr.org.dto.BranchFilterInsertDTO;
import com.vietqr.org.dto.BranchFilterResponseDTO;
import com.vietqr.org.dto.BusinessBankDTO;
import com.vietqr.org.dto.BusinessBranchDTO;
import com.vietqr.org.dto.BusinessBranchInsertDTO;
import com.vietqr.org.dto.BusinessInformationInsertDTO;
import com.vietqr.org.dto.BusinessStatusDTO;
import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.BusinessItemResponseDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;
import com.vietqr.org.dto.BusinessItemDTO;
import com.vietqr.org.dto.BusinessCounterDTO;
import com.vietqr.org.dto.BusinessInformationDetailDTO;
import com.vietqr.org.entity.BankReceivePersonalEntity;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.BusinessInformationEntity;
import com.vietqr.org.entity.BusinessMemberEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.service.AccountBankReceivePersonalService;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankReceiveBranchService;
import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.BusinessInformationService;
import com.vietqr.org.service.BusinessMemberService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.util.RandomCodeUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BusinessInformationController {
	private static final Logger logger = Logger.getLogger(BusinessInformationController.class);

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
	AccountBankReceiveService accountBankReceiveService;

	@Autowired
	TransactionReceiveService transactionReceiveService;

	@Autowired
	AccountBankReceivePersonalService accountBankReceivePersonalService;

	@Autowired
	BankReceiveBranchService bankReceiveBranchService;

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

	// get business detail

	@GetMapping("business-information/detail/{businessId}/{userId}")
	public ResponseEntity<BusinessInformationDetailDTO> getBusinessDetail(
			@PathVariable("businessId") String businessId, @PathVariable("userId") String userId) {
		BusinessInformationDetailDTO result = null;
		HttpStatus httpStatus = null;
		try {
			BusinessInformationEntity bEntity = businessInfoService.getBusinessById(businessId);
			if (bEntity != null) {
				result = new BusinessInformationDetailDTO();
				result.setId(bEntity.getId());
				result.setName(bEntity.getName());
				result.setAddress(bEntity.getAddress());
				result.setCode(bEntity.getCode());
				result.setImgId(bEntity.getImgId());
				result.setCoverImgId(bEntity.getCoverImgId());
				result.setTaxCode(bEntity.getTaxCode());
				result.setActive(bEntity.isActive());
				int role = 0;
				int businessRole = businessMemberService.getRoleFromBusiness(userId,
						businessId);
				if (businessRole != 0) {
					role = businessRole;
				} else {
					List<String> branchIds = branchInformationService.getBranchIdsByBusinessId(businessId);
					if (branchIds != null && !branchIds.isEmpty()) {
						for (String branchId : branchIds) {
							int branchRole = branchMemberService.getRoleFromBranch(userId, branchId);
							System.out.println("branchId: " + branchId + "- role: " + branchRole);
							if (branchRole != 0) {
								role = branchRole;
								break;
							}

						}
					}
				}
				result.setUserRole(role);
				// get list manager
				List<MemberDTO> managers = new ArrayList<>();
				managers = businessMemberService.getBusinessMembersByBusinessId(businessId);
				if (managers != null && !managers.isEmpty()) {
					result.setManagers(managers);
				}
				// get list branch
				List<BusinessBranchDTO> branchs = new ArrayList<>();
				List<BranchInformationEntity> branchEntities = branchInformationService
						.getListBranchByBusinessId(businessId);
				if (branchEntities != null && !branchEntities.isEmpty()) {
					for (BranchInformationEntity branchEntity : branchEntities) {
						BusinessBranchDTO businessBranchDTO = new BusinessBranchDTO();
						businessBranchDTO.setId(branchEntity.getId());
						businessBranchDTO.setCode(branchEntity.getCode());
						businessBranchDTO.setName(branchEntity.getName());
						businessBranchDTO.setAddress(branchEntity.getAddress());
						// set total member
						int totalMemberBranch = branchMemberService.getTotalMemberInBranch(branchEntity.getId());
						businessBranchDTO.setTotalMember(totalMemberBranch);
						// set manager
						MemberDTO manager = branchMemberService.getManagerByBranchId(branchEntity.getId());
						businessBranchDTO.setManager(manager);
						// set bankAccount linked
						List<BusinessBankDTO> bank = accountBankReceiveService.getBankByBranchId(branchEntity.getId());
						businessBranchDTO.setBanks(bank);
						branchs.add(businessBranchDTO);
					}
				}
				result.setBranchs(branchs);
				// get list transaction
				List<TransactionRelatedDTO> transactions = new ArrayList<>();
				transactions = transactionReceiveService.getRelatedTransactionReceives(businessId);
				result.setTransactions(transactions);
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// get list branch filter search by businessId, check is manager or not

	@PostMapping("business-information/branch/filter")
	public ResponseEntity<List<BranchFilterResponseDTO>> getBranchFilterList(
			@Valid @RequestBody BranchFilterInsertDTO dto) {
		List<BranchFilterResponseDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			if (dto.getRole() == 5 || dto.getRole() == 1) {
				// role = 5 && role = 1
				result = branchInformationService.getBranchFilters(dto.getBusinessId());
				httpStatus = HttpStatus.OK;
			} else {
				// find BranchFilterResponseDTO
				result = branchInformationService.getBranchFilterByUserIdAndRole(dto.getUserId(), dto.getRole(),
						dto.getBusinessId());
				httpStatus = HttpStatus.OK;
			}

		} catch (Exception e) {
			logger.error(e.toString());
			System.out.println("Error: " + e.toString());
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
			List<BusinessItemDTO> businessFromMemberItems = branchMemberService.getBusinessItemByUserId(userId);
			if (businessItems != null && !businessItems.isEmpty()) {
				for (BusinessItemDTO item : businessItems) {
					List<BranchConnectedCheckDTO> branchs = new ArrayList<>();
					List<TransactionRelatedDTO> transactions = new ArrayList<>();
					List<AccountBankBranchDTO> bankAccounts = new ArrayList<>();
					BusinessItemResponseDTO dto = new BusinessItemResponseDTO();
					BusinessCounterDTO counterDTO = businessInfoService.getBusinessCounter(item.getBusinessId());
					dto.setBusinessId(item.getBusinessId());
					// dto.setCode(item.getCode());
					dto.setRole(item.getRole());
					dto.setImgId(item.getImgId());
					dto.setCoverImgId(item.getCoverImgId());
					dto.setName(item.getName());
					// dto.setAddress(item.getAddress());
					// dto.setTaxCode(item.getTaxCode());
					transactions = transactionReceiveService.getRelatedTransactionReceives(item.getBusinessId());
					branchs = branchInformationService.getBranchContects(item.getBusinessId());
					bankAccounts = bankReceiveBranchService.getBanksByBusinessId(item.getBusinessId());
					dto.setBranchs(branchs);
					dto.setTransactions(transactions);
					dto.setTotalMember(counterDTO.getTotalAdmin() + counterDTO.getTotalMember());
					dto.setTotalBranch(counterDTO.getTotalBranch());
					dto.setBankAccounts(bankAccounts);
					result.add(dto);
				}
			}
			if (businessFromMemberItems != null && !businessFromMemberItems.isEmpty()) {
				for (BusinessItemDTO item : businessFromMemberItems) {
					List<BranchConnectedCheckDTO> branchs = new ArrayList<>();
					List<TransactionRelatedDTO> transactions = new ArrayList<>();
					List<AccountBankBranchDTO> bankAccounts = new ArrayList<>();
					BusinessItemResponseDTO dto = new BusinessItemResponseDTO();
					BusinessCounterDTO counterDTO = businessInfoService.getBusinessCounter(item.getBusinessId());
					dto.setBusinessId(item.getBusinessId());
					// dto.setCode(item.getCode());
					dto.setRole(item.getRole());
					dto.setImgId(item.getImgId());
					dto.setCoverImgId(item.getCoverImgId());
					dto.setName(item.getName());
					// dto.setAddress(item.getAddress());
					// dto.setTaxCode(item.getTaxCode());
					transactions = transactionReceiveService.getRelatedTransactionReceives(item.getBusinessId());
					branchs = branchInformationService.getBranchContects(item.getBusinessId());
					bankAccounts = bankReceiveBranchService.getBanksByBusinessId(item.getBusinessId());
					dto.setBranchs(branchs);
					dto.setTransactions(transactions);
					dto.setTotalMember(counterDTO.getTotalAdmin() + counterDTO.getTotalMember());
					dto.setTotalBranch(counterDTO.getTotalBranch());
					dto.setBankAccounts(bankAccounts);
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

	// Delete business information
	// 1. get business information to delete image and cover
	// 2. delete business member
	// 3. delete branch member
	// 4. delete branch bank
	// 4. delete branchs
	// 5. update bank type
	// 6. insert bank_account_personal
	// 7. delete bank_account_branch
	// 8. delete business
	//
	@DeleteMapping("business/remove/{businessId}")
	public ResponseEntity<ResponseMessageDTO> deleteBusiness(@PathVariable(value = "businessId") String businessId) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (businessId != null && !businessId.trim().isEmpty()) {
				BusinessInformationEntity entity = businessInfoService.getBusinessById(businessId);
				if (entity != null) {
					// 1.
					imageService.deleteImage(entity.getImgId());
					imageService.deleteImage(entity.getCoverImgId());
					// 2.
					businessMemberService.deleteAllMemberFromBusiness(businessId);
					// 3.
					branchMemberService.deleteAllMemberFromBusiness(businessId);
					// 4.
					branchInformationService.deleteAllBranchByBusinessId(businessId);
					// before 5&6. get all bankId by businessId
					List<String> bankIds = bankReceiveBranchService.getBankIdsByBusinessId(businessId);
					if (bankIds != null && !bankIds.isEmpty()) {
						for (String bankId : bankIds) {
							String userId = accountBankReceiveService.getUserIdByBankId(bankId);
							if (userId != null && !userId.trim().isEmpty()) {
								// 5.
								accountBankReceiveService.updateBankType(bankId, 0);
								// 6.
								UUID uuid = UUID.randomUUID();
								BankReceivePersonalEntity bankPersonalEntity = new BankReceivePersonalEntity();
								bankPersonalEntity.setId(uuid.toString());
								bankPersonalEntity.setBankId(bankId);
								bankPersonalEntity.setUserId(userId);
								accountBankReceivePersonalService.insertAccountBankReceivePersonal(bankPersonalEntity);

							}
						}
					}
					// 7.
					bankReceiveBranchService.deleteBankReceiveBranchByBusinessId(businessId);
					// 8.
					businessInfoService.deleteBusinessInformation(businessId);
					//
					result = new ResponseMessageDTO("SUCCESS", "");
					httpStatus = HttpStatus.OK;
				} else {
					logger.error("deleteBusiness: RECORD NOT FOUND");
					result = new ResponseMessageDTO("FAILED", "E05");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				logger.error("deleteBusiness: INVALID BUSINESS ID");
				result = new ResponseMessageDTO("FAILED", "E05");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error("deleteBusiness: ERROR: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}
}
