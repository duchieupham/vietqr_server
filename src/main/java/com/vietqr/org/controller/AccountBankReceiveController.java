package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankReceiveDTO;
import com.vietqr.org.dto.AccountBankReceiveDetailDTO;
import com.vietqr.org.dto.AccountBankReceiveDetailWT;
import com.vietqr.org.dto.AccountBankResponseDTO;
import com.vietqr.org.dto.AccountBankSyncWpDTO;
import com.vietqr.org.dto.AccountBankUnauthenticatedDTO;
import com.vietqr.org.dto.AccountBankWpDTO;
import com.vietqr.org.dto.BankAccountRemoveDTO;
import com.vietqr.org.dto.RegisterAuthenticationDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.dto.AccountBankReceiveDetailDTO.BranchBankDetailDTO;
import com.vietqr.org.dto.AccountBankReceiveDetailDTO.BusinessBankDetailDTO;
import com.vietqr.org.dto.AccountBankReceiveDetailDTO.TransactionBankListDTO;
import com.vietqr.org.dto.AccountBankReceivePersonalDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.AccountCustomerBankEntity;
import com.vietqr.org.entity.BankReceivePersonalEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.BusinessInformationEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.service.AccountBankReceivePersonalService;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountCustomerBankService;
import com.vietqr.org.service.BankReceiveBranchService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.BusinessInformationService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.CustomerSyncService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.util.VietQRUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import com.vietqr.org.service.BranchInformationService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountBankReceiveController {
	private static final Logger logger = Logger.getLogger(AccountBankReceiveController.class);

	@Autowired
	AccountBankReceiveService accountBankService;

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	AccountBankReceivePersonalService bankReceivePersonalService;

	@Autowired
	BankReceiveBranchService bankReceiveBranchService;

	@Autowired
	BranchInformationService branchInformationService;

	@Autowired
	BranchMemberService branchMemberService;

	@Autowired
	CaiBankService caiBankService;

	@Autowired
	BusinessInformationService businessInformationService;

	@Autowired
	TransactionReceiveService transactionReceiveService;

	@Autowired
	CustomerSyncService customerSyncService;

	@Autowired
	AccountCustomerBankService accountCustomerBankService;

	// @GetMapping("account-bank/check/{bankAccount}/{bankTypeId}/{userId}")
	@GetMapping("account-bank/check/{bankAccount}/{bankTypeId}")
	public ResponseEntity<ResponseMessageDTO> checkExistedBankAccount(
			@PathVariable(value = "bankAccount") String bankAccount,
			@PathVariable(value = "bankTypeId") String bankTypeId
	// @PathVariable(value = "userId") String userId
	) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// check existed if bank account is authenticated
			String check = accountBankService.checkExistedBank(bankAccount, bankTypeId);
			if (check == null || check.isEmpty()) {
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			} else {
				result = new ResponseMessageDTO("CHECK", "C03");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("account-bank/unauthenticated")
	public ResponseEntity<ResponseMessageDTO> insertAccountBankWithouthAuthenticate(
			@Valid @RequestBody AccountBankUnauthenticatedDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// insert bankAccount receive
			UUID uuid = UUID.randomUUID();
			String qr = getStaticQR(dto.getBankAccount(), dto.getBankTypeId());
			AccountBankReceiveEntity entity = new AccountBankReceiveEntity();
			entity.setId(uuid.toString());
			entity.setBankTypeId(dto.getBankTypeId());
			entity.setBankAccount(dto.getBankAccount());
			entity.setBankAccountName(dto.getUserBankName());
			entity.setType(0);
			entity.setUserId(dto.getUserId());
			entity.setNationalId("");
			entity.setPhoneAuthenticated("");
			entity.setAuthenticated(false);
			entity.setSync(false);
			entity.setWpSync(false);
			entity.setStatus(true);
			entity.setMmsActive(false);
			accountBankService.insertAccountBank(entity);
			// insert bank-receive-personal
			UUID uuidPersonal = UUID.randomUUID();
			BankReceivePersonalEntity personalEntity = new BankReceivePersonalEntity();
			personalEntity.setId(uuidPersonal.toString());
			personalEntity.setBankId(uuid.toString());
			personalEntity.setUserId(dto.getUserId());
			bankReceivePersonalService.insertAccountBankReceivePersonal(personalEntity);
			result = new ResponseMessageDTO("SUCCESS", uuid.toString() + "*" + qr);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			logger.error(e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("account-bank/wp")
	public ResponseEntity<List<AccountBankWpDTO>> getAccountBankReceiveWps(
			@RequestHeader("Authorization") String token) {
		List<AccountBankWpDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			String userId = getUserIdFromToken(token);
			result = accountBankService.getAccountBankReceiveWps(userId);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			logger.error("getAccountBankReceiveWps: ERROR: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("account-bank/wp/sync")
	public ResponseEntity<ResponseMessageDTO> updateSyncWp(@RequestBody AccountBankSyncWpDTO dto,
			@RequestHeader("Authorization") String token) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (dto != null) {
				if (dto.getBankId() != null && !dto.getBankId().trim().isEmpty()) {
					String userId = getUserIdFromToken(token);
					// 1. find customer_sync_id by hosting (information)
					// - if not found => do nothing
					// - if found => step 2
					String hosting = getHostingFromToken(token);
					if (hosting != null && !hosting.trim().isEmpty()) {
						String customerSyncId = customerSyncService.checkExistedCustomerSyncByInformation(hosting);
						if (customerSyncId != null && !customerSyncId.isEmpty()) {
							// 2. Check account_customer_bank is existed by cusomer_sync_id and bank_id
							// - if not existed => insert account_customer_bank
							// - if existed => do nothing
							String checkExistedAccountCustomerBank = accountCustomerBankService
									.checkExistedAccountCustomerBank(dto.getBankId(), customerSyncId);
							if (checkExistedAccountCustomerBank == null
									|| checkExistedAccountCustomerBank.trim().isEmpty()) {
								UUID uuid = UUID.randomUUID();
								AccountCustomerBankEntity entity = new AccountCustomerBankEntity();
								entity.setId(uuid.toString());
								entity.setAccountCustomerId("");
								entity.setBankId(dto.getBankId());
								String bankAccount = accountBankService.getBankAccountById(dto.getBankId());
								entity.setBankAccount(bankAccount);
								entity.setCustomerSyncId(customerSyncId);
								accountCustomerBankService.insert(entity);
							} else {
								logger.info("updateSyncWp: EXISTED account_customer_bank - id: "
										+ checkExistedAccountCustomerBank);
							}
						} else {
							logger.info("updateSyncWp: NOT FOUND customer_sync_id - user_id: " + userId);
						}
					} else {
						logger.info("updateSyncWp: NOT FOUND HOSTING - user_id: " + userId);
					}
					accountBankService.updateSyncWp(userId, dto.getBankId());
					result = new ResponseMessageDTO("SUCCESS", "");
					httpStatus = HttpStatus.OK;
				} else {
					logger.error("updateSyncWp: ERROR: BankId is Invalid");
					result = new ResponseMessageDTO("FAILED", "E31");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				logger.error("updateSyncWp: ERROR: NULL Request Body");
				result = new ResponseMessageDTO("FAILED", "E30");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error("updateSyncWp: ERROR: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private String getUserIdFromToken(String token) {
		String result = "";
		if (token != null && !token.trim().isEmpty()) {
			String secretKey = "mySecretKey";
			String jwtToken = token.substring(7); // remove "Bearer " from the beginning
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
			String userId = (String) claims.get("userId");
			result = userId;
		}
		return result;
	}

	private String getHostingFromToken(String token) {
		String result = "";
		if (token != null && !token.trim().isEmpty()) {
			String secretKey = "mySecretKey";
			String jwtToken = token.substring(7); // remove "Bearer " from the beginning
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
			String hosting = (String) claims.get("hosting");
			if (hosting != null && !hosting.trim().isEmpty()) {
				result = hosting;
			}
		}
		return result;
	}

	// register authentication
	// for case user created bank before and then register authentication
	@PostMapping("account-bank/register-authentication")
	public ResponseEntity<ResponseMessageDTO> registerAuthentication(
			@Valid @RequestBody RegisterAuthenticationDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// update bank-receive
			accountBankService.updateRegisterAuthenticationBank(dto.getNationalId(), dto.getPhoneAuthenticated(),
					dto.getBankAccountName(), dto.getBankAccount(),
					dto.getBankId());
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			logger.error(e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// map bank with business-branch (update is linked with business)
	//

	// register bank account with authenticated

	@PostMapping("account-bank")
	public ResponseEntity<ResponseMessageDTO> insertPersonalAccountBank(@Valid @RequestBody AccountBankReceiveDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID uuid = UUID.randomUUID();
			String qr = getStaticQR(dto.getBankAccount(), dto.getBankTypeId());
			AccountBankReceiveEntity entity = new AccountBankReceiveEntity();
			entity.setId(uuid.toString());
			entity.setBankTypeId(dto.getBankTypeId());
			entity.setBankAccount(dto.getBankAccount());
			entity.setBankAccountName(dto.getUserBankName());
			entity.setType(dto.getType());
			entity.setUserId(dto.getUserId());
			entity.setNationalId(dto.getNationalId());
			entity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
			entity.setAuthenticated(true);
			entity.setSync(false);
			entity.setWpSync(false);
			entity.setStatus(true);
			entity.setMmsActive(false);
			accountBankService.insertAccountBank(entity);
			// if (dto.getType() == 0) {
			// insert bank receive personal
			UUID uuidPersonal = UUID.randomUUID();
			BankReceivePersonalEntity personalEntity = new BankReceivePersonalEntity();
			personalEntity.setId(uuidPersonal.toString());
			personalEntity.setBankId(uuid.toString());
			personalEntity.setUserId(dto.getUserId());
			bankReceivePersonalService.insertAccountBankReceivePersonal(personalEntity);
			// } else if (dto.getType() == 1) {
			// // insert bank_receive_branch
			// UUID uuidBankReceiveBranch = UUID.randomUUID();
			// BankReceiveBranchEntity bankReceiveBranchEntity = new
			// BankReceiveBranchEntity();
			// bankReceiveBranchEntity.setId(uuidBankReceiveBranch.toString());
			// bankReceiveBranchEntity.setBranchId(dto.getBranchId());
			// bankReceiveBranchEntity.setBankId(uuid.toString());
			// bankReceiveBranchEntity.setBusinessId(dto.getBusinessId());
			// bankReceiveBranchService.insertBankReceiveBranch(bankReceiveBranchEntity);
			// }
			result = new ResponseMessageDTO("SUCCESS", uuid.toString() + "*" + qr);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at insertAccountBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@Async
	private String getStaticQR(String bankAccount, String bankTypeId) {
		String result = "";
		String caiValue = caiBankService.getCaiValue(bankTypeId);
		VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
		vietQRGenerateDTO.setCaiValue(caiValue);
		vietQRGenerateDTO.setBankAccount(bankAccount);
		result = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
		return result;
	}

	@GetMapping("account-bank/detail/web/{bankId}")
	public ResponseEntity<AccountBankReceiveDetailWT> getBankDetailWithoutTransaction(
			@PathVariable("bankId") String bankId) {
		AccountBankReceiveDetailWT result = null;
		HttpStatus httpStatus = null;
		try {
			// get
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(bankId);
			if (accountBankEntity != null) {
				BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
				// get cai value
				String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
				// generate VietQRGenerateDTO
				VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
				vietQRGenerateDTO.setCaiValue(caiValue);
				vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
				String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
				// set values
				result = new AccountBankReceiveDetailWT();
				result.setId(bankId);
				result.setBankAccount(accountBankEntity.getBankAccount());
				result.setUserBankName(accountBankEntity.getBankAccountName());
				result.setBankCode(bankTypeEntity.getBankCode());
				result.setBankName(bankTypeEntity.getBankName());
				result.setImgId(bankTypeEntity.getImgId());
				result.setType(accountBankEntity.getType());
				result.setBankTypeId(bankTypeEntity.getId());
				result.setBankTypeStatus(bankTypeEntity.getStatus());
				result.setUserId(accountBankEntity.getUserId());
				result.setAuthenticated(accountBankEntity.isAuthenticated());
				result.setNationalId(accountBankEntity.getNationalId());
				result.setQrCode(qr);
				result.setPhoneAuthenticated(accountBankEntity.getPhoneAuthenticated());
				List<String> branchIds = new ArrayList<>();
				branchIds = branchInformationService.getBranchIdsByBankId(bankId);
				// get list branch linked
				List<BranchInformationEntity> branchEntities = new ArrayList<>();
				if (branchIds != null && !branchIds.isEmpty()) {
					for (String branchId : branchIds) {
						BranchInformationEntity branchEntity = branchInformationService.getBranchById(branchId);
						branchEntities.add(branchEntity);
					}
				}
				// get list business linked
				List<BusinessInformationEntity> businessEntities = new ArrayList<>();
				if (branchEntities != null && !branchEntities.isEmpty()) {
					for (BranchInformationEntity branch : branchEntities) {
						BusinessInformationEntity businessEntity = businessInformationService
								.getBusinessById(branch.getBusinessId());
						businessEntities.add(businessEntity);
					}
				}
				// map business and branch
				List<BusinessBankDetailDTO> businessBankDetailDTOs = new ArrayList<>();
				if (businessEntities != null && !businessEntities.isEmpty()) {
					//
					for (BusinessInformationEntity business : businessEntities) {
						BusinessBankDetailDTO businessBankDTO = new BusinessBankDetailDTO();
						businessBankDTO.setBusinessId(business.getId());
						businessBankDTO.setBusinessName(business.getName());
						businessBankDTO.setImgId(business.getImgId());
						businessBankDTO.setCoverImgId(business.getCoverImgId());
						List<BranchBankDetailDTO> branchBanks = new ArrayList<>();
						if (branchEntities != null && !branchEntities.isEmpty()) {
							for (BranchInformationEntity branch : branchEntities) {
								if (branch.getBusinessId().equals(business.getId())) {
									BranchBankDetailDTO branchBank = new BranchBankDetailDTO();
									branchBank.setBranchId(branch.getId());
									branchBank.setBranchName(branch.getName());
									branchBank.setCode(branch.getCode());
									branchBank.setAddress(branch.getAddress());
									branchBanks.add(branchBank);
								}
							}
						}
						businessBankDTO.setBranchDetails(branchBanks);
						businessBankDetailDTOs.add(businessBankDTO);
					}
				}
				result.setBusinessDetails(businessBankDetailDTOs);

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

	@GetMapping("account-bank/detail/{bankId}")
	public ResponseEntity<AccountBankReceiveDetailDTO> getBankDetail(@PathVariable("bankId") String bankId) {
		AccountBankReceiveDetailDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// get
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(bankId);
			if (accountBankEntity != null) {
				BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
				// get cai value
				String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
				// generate VietQRGenerateDTO
				VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
				vietQRGenerateDTO.setCaiValue(caiValue);
				vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
				String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
				// set values
				result = new AccountBankReceiveDetailDTO();
				result.setId(bankId);
				result.setBankAccount(accountBankEntity.getBankAccount());
				result.setUserBankName(accountBankEntity.getBankAccountName());
				result.setBankCode(bankTypeEntity.getBankCode());
				result.setBankName(bankTypeEntity.getBankName());
				result.setImgId(bankTypeEntity.getImgId());
				result.setType(accountBankEntity.getType());
				result.setBankTypeId(bankTypeEntity.getId());
				result.setBankTypeStatus(bankTypeEntity.getStatus());
				result.setUserId(accountBankEntity.getUserId());
				result.setAuthenticated(accountBankEntity.isAuthenticated());
				result.setNationalId(accountBankEntity.getNationalId());
				result.setQrCode(qr);
				result.setCaiValue(caiValue);
				result.setPhoneAuthenticated(accountBankEntity.getPhoneAuthenticated());
				List<String> branchIds = new ArrayList<>();
				branchIds = branchInformationService.getBranchIdsByBankId(bankId);
				// get list branch linked
				List<BranchInformationEntity> branchEntities = new ArrayList<>();
				if (branchIds != null && !branchIds.isEmpty()) {
					for (String branchId : branchIds) {
						BranchInformationEntity branchEntity = branchInformationService.getBranchById(branchId);
						branchEntities.add(branchEntity);
					}
				}
				// get list business linked
				List<BusinessInformationEntity> businessEntities = new ArrayList<>();
				if (branchEntities != null && !branchEntities.isEmpty()) {
					for (BranchInformationEntity branch : branchEntities) {
						BusinessInformationEntity businessEntity = businessInformationService
								.getBusinessById(branch.getBusinessId());
						businessEntities.add(businessEntity);
					}
				}
				// map business and branch
				List<BusinessBankDetailDTO> businessBankDetailDTOs = new ArrayList<>();
				if (businessEntities != null && !businessEntities.isEmpty()) {
					//
					for (BusinessInformationEntity business : businessEntities) {
						BusinessBankDetailDTO businessBankDTO = new BusinessBankDetailDTO();
						businessBankDTO.setBusinessId(business.getId());
						businessBankDTO.setBusinessName(business.getName());
						businessBankDTO.setImgId(business.getImgId());
						businessBankDTO.setCoverImgId(business.getCoverImgId());
						List<BranchBankDetailDTO> branchBanks = new ArrayList<>();
						if (branchEntities != null && !branchEntities.isEmpty()) {
							for (BranchInformationEntity branch : branchEntities) {
								if (branch.getBusinessId().equals(business.getId())) {
									BranchBankDetailDTO branchBank = new BranchBankDetailDTO();
									branchBank.setBranchId(branch.getId());
									branchBank.setBranchName(branch.getName());
									branchBank.setCode(branch.getCode());
									branchBank.setAddress(branch.getAddress());
									branchBanks.add(branchBank);
								}
							}
						}
						businessBankDTO.setBranchDetails(branchBanks);
						businessBankDetailDTOs.add(businessBankDTO);
					}
				}
				result.setBusinessDetails(businessBankDetailDTOs);
				// get related transaction
				List<TransactionBankListDTO> transactions = new ArrayList<>();
				List<TransactionReceiveEntity> transactionEntities = transactionReceiveService
						.getTransactionByBankId(bankId);
				if (transactionEntities != null && !transactionEntities.isEmpty()) {
					for (TransactionReceiveEntity transactionEntity : transactionEntities) {
						TransactionBankListDTO transaction = new TransactionBankListDTO();
						transaction.setTransactionId(transactionEntity.getId());
						transaction.setBankAccount(transactionEntity.getBankAccount());
						transaction.setBankId(transactionEntity.getBankId());
						transaction.setAmount(transactionEntity.getAmount() + "");
						transaction.setContent(transactionEntity.getContent());
						transaction.setStatus(transactionEntity.getStatus());
						transaction.setTime(transactionEntity.getTime());
						transaction.setType(transactionEntity.getType());
						transaction.setTransType(transactionEntity.getTransType());
						transactions.add(transaction);
					}
				}
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

	@GetMapping("account-bank/{userId}")
	public ResponseEntity<List<AccountBankResponseDTO>> getAccountBanks(@PathVariable("userId") String userId) {
		List<AccountBankResponseDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		System.out.println("userId: " + userId);
		try {
			// get list personal bank
			//
			List<AccountBankReceivePersonalDTO> personalBanks = bankReceivePersonalService
					.getBankReceivePersonals(userId);
			System.out.println("personalBanks size: " + personalBanks.size());
			if (personalBanks != null && !personalBanks.isEmpty()) {
				for (AccountBankReceivePersonalDTO personalBank : personalBanks) {
					AccountBankResponseDTO dto = new AccountBankResponseDTO();
					dto.setId(personalBank.getBankId());
					dto.setBankAccount(personalBank.getBankAccount());
					dto.setUserBankName(personalBank.getUserBankName());
					dto.setBankCode(personalBank.getBankCode());
					dto.setBankName(personalBank.getBankName());
					dto.setImgId(personalBank.getImgId());
					dto.setType(personalBank.getBankType());
					// dto.setNationalId(personalBank.getNationalId());
					// dto.setPhoneAuthenticated(personalBank.getPhoneAuthenticated());
					dto.setBranchId("");
					dto.setBusinessId("");
					dto.setBranchName("");
					// dto.setBranchCode("");
					dto.setBusinessName("");
					dto.setAuthenticated(personalBank.getAuthenticated());
					dto.setUserId(personalBank.getUserId());
					// dto.setBusinessCode("");
					result.add(dto);
				}
			}
			// get list business bank (with branch)
			List<String> branchIds = new ArrayList<>();
			// 1. check user is admin/manager of business or not.
			List<String> branchIdsByUserIdBusiness = branchInformationService.getBranchIdsByUserIdBusiness(userId);
			// 2. get list branch id of user
			// 3. get list business bank by userId
			List<String> branchIdsByUserIdBranch = branchMemberService.getBranchIdsByUserId(userId);
			// add all branchIds
			if (branchIdsByUserIdBusiness != null && !branchIdsByUserIdBusiness.isEmpty()) {
				branchIds.addAll(branchIdsByUserIdBusiness);
			}
			if (branchIdsByUserIdBranch != null && !branchIdsByUserIdBranch.isEmpty()) {
				branchIds.addAll(branchIdsByUserIdBranch);
			}
			List<AccountBankReceivePersonalDTO> businessBanks = new ArrayList<>();
			if (branchIds != null && !branchIds.isEmpty()) {
				for (String branchId : branchIds) {
					List<AccountBankReceivePersonalDTO> banks = bankReceiveBranchService
							.getBankReceiveBranchs(branchId);
					if (banks != null && !banks.isEmpty()) {
						for (AccountBankReceivePersonalDTO bank : banks) {
							businessBanks.add(bank);
						}
					}
				}
			}
			if (businessBanks != null && !businessBanks.isEmpty()) {
				for (AccountBankReceivePersonalDTO bank : businessBanks) {
					AccountBankResponseDTO dto = new AccountBankResponseDTO();
					dto.setId(bank.getBankId());
					dto.setBankAccount(bank.getBankAccount());
					dto.setUserBankName(bank.getUserBankName());
					dto.setBankCode(bank.getBankCode());
					dto.setBankName(bank.getBankName());
					dto.setImgId(bank.getImgId());
					dto.setType(bank.getBankType());
					// dto.setNationalId(bank.getNationalId());
					// dto.setPhoneAuthenticated(bank.getPhoneAuthenticated());
					dto.setBranchId(bank.getBranchId());
					dto.setBusinessId(bank.getBusinessId());
					dto.setBranchName(bank.getBranchName());
					dto.setBusinessName(bank.getBusinessName());
					dto.setAuthenticated(bank.getAuthenticated());
					dto.setUserId(bank.getUserId());
					// dto.setBranchCode(bank.getBranchCode());
					// dto.setBusinessCode(bank.getBusinessCode());
					result.add(dto);
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getAccountBantks: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@DeleteMapping("account-bank")
	public ResponseEntity<ResponseMessageDTO> deleteAccountBank(@Valid @RequestBody BankAccountRemoveDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (dto.isAuthenticated() == true) {
				result = new ResponseMessageDTO("CHECK", "C04");
				httpStatus = HttpStatus.OK;
			} else {
				// 1.check type = 0 => personal; type = 1 => business
				// 2.a remove bank_receive_branch by bank_id
				// 2.b remvoe bank_receive_personal by bank_id
				// 3. remove bank_receive
				//
				if (dto.getType() == 0) {
					bankReceivePersonalService.deleteBankReceivePersonalByBankId(dto.getBankId());
				} else if (dto.getType() == 1) {
					bankReceiveBranchService.deleteBankReceiveBranchByBankId(dto.getBankId());
				}
				accountBankService.deleteAccountBank(dto.getBankId());
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
			System.out.println("Error at deleteAccountBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

}
