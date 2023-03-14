package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.CaiBankDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.VietQRCreateDTO;
import com.vietqr.org.dto.VietQRCreateListDTO;
import com.vietqr.org.dto.VietQRDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.dto.FcmRequestDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.CaiBankEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.util.VietQRUtil;
import com.vietqr.org.util.NotificationUtil;

@RestController
@RequestMapping("/api")
public class VietQRController {
	private static final Logger logger = Logger.getLogger(AccountController.class);

	@Autowired
	CaiBankService caiBankService;

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	AccountBankReceiveService accountBankService;

	@Autowired
	TransactionReceiveService transactionReceiveService;

	@Autowired
	TransactionReceiveBranchService transactionReceiveBranchService;

	@Autowired
	BranchMemberService branchMemberService;

	@Autowired
	BranchInformationService branchInformationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	FcmTokenService fcmTokenService;

	private FirebaseMessagingService firebaseMessagingService;

	public VietQRController(FirebaseMessagingService firebaseMessagingService) {
		this.firebaseMessagingService = firebaseMessagingService;
	}

	@PostMapping("qr/cai-bank")
	public ResponseEntity<ResponseMessageDTO> insertCaiBank(@Valid @RequestBody CaiBankDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
			UUID uuid = UUID.randomUUID();
			CaiBankEntity entity = new CaiBankEntity();
			entity.setId(uuid.toString());
			entity.setBankTypeId(bankTypeId);
			entity.setCaiValue(dto.getCaiValue());
			int check = caiBankService.insertCaiBank(entity);
			if (check == 1) {
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			} else {
				result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			System.out.println("Error at insertCaiBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("qr/generate")
	public ResponseEntity<VietQRDTO> generateQR(@Valid @RequestBody VietQRCreateDTO dto) {
		VietQRDTO result = null;
		HttpStatus httpStatus = null;
		try {
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
			if (accountBankEntity != null) {

				// 1.Generate VietQR
				// get bank information
				// get bank type information
				BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
				// get cai value
				String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
				// generate VietQRGenerateDTO
				VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
				vietQRGenerateDTO.setCaiValue(caiValue);
				vietQRGenerateDTO.setAmount(dto.getAmount());
				vietQRGenerateDTO.setContent(dto.getContent());
				vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
				String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
				// generate VietQRDTO
				VietQRDTO vietQRDTO = new VietQRDTO();
				vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
				vietQRDTO.setBankName(bankTypeEntity.getBankName());
				vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
				vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
				vietQRDTO.setAmount(dto.getAmount());
				vietQRDTO.setContent(dto.getContent());
				vietQRDTO.setQrCode(qr);
				vietQRDTO.setImgId(bankTypeEntity.getImgId());
				result = vietQRDTO;
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			return new ResponseEntity<>(result, httpStatus);
		} catch (Exception e) {
			System.out.println("Error at generateQR: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			// 2. Insert transaction_receive if branch_id and business_id != null
			// 3. Insert transaction_receive_branch if branch_id and business_id != null
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
			if (accountBankEntity != null) {
				if (dto != null && dto.getBusinessId() != null && dto.getBranchId() != null) {
					if (!dto.getBranchId().isEmpty() && !dto.getBusinessId().isEmpty()) {
						UUID transcationUUID = UUID.randomUUID();
						UUID transactionBranchUUID = UUID.randomUUID();
						LocalDateTime currentDateTime = LocalDateTime.now();
						TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
						transactionEntity.setId(transcationUUID.toString());
						transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
						transactionEntity.setBankId(dto.getBankId());
						transactionEntity.setContent(dto.getContent());
						transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
						transactionEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
						transactionEntity.setRefId("");
						transactionEntity.setType(0);
						transactionEntity.setStatus(0);
						transactionReceiveService.insertTransactionReceive(transactionEntity);
						TransactionReceiveBranchEntity transactionBranchEntity = new TransactionReceiveBranchEntity();
						transactionBranchEntity.setId(transactionBranchUUID.toString());
						transactionBranchEntity.setTransactionReceiveId(transcationUUID.toString());
						transactionBranchEntity.setBranchId(dto.getBranchId());
						transactionBranchEntity.setBusinessId(dto.getBusinessId());
						transactionReceiveBranchService.insertTransactionReceiveBranch(transactionBranchEntity);
						// find userIds into business_member and branch_member
						List<String> userIds = branchMemberService
								.getUserIdsByBusinessIdAndBranchId(dto.getBusinessId(), dto.getBranchId());
						// insert AND push notification to users belong to
						// admin business/ member of branch
						if (userIds != null && !userIds.isEmpty()) {
							for (String userId : userIds) {
								// insert notification
								UUID notificationUUID = UUID.randomUUID();
								NotificationEntity notiEntity = new NotificationEntity();
								BranchInformationEntity branchEntity = branchInformationService
										.getBranchById(dto.getBranchId());
								String message = NotificationUtil.getNotiDescNewTransPrefix() + branchEntity.getName()
										+ NotificationUtil.getNotiDescNewTransSuffix1() + dto.getAmount()
										+ NotificationUtil
												.getNotiDescNewTransSuffix2();
								// String title = NotificationUtil.getNotiTitleNewTransaction();
								notiEntity.setId(notificationUUID.toString());
								notiEntity.setRead(false);
								notiEntity.setMessage(message);
								notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
								notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
								notiEntity.setUserId(userId);
								notiEntity.setData(transcationUUID.toString());
								notificationService.insertNotification(notiEntity);
								// push notification
								List<FcmTokenEntity> fcmTokens = new ArrayList<>();
								fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
								if (fcmTokens != null && !fcmTokens.isEmpty()) {
									for (FcmTokenEntity fcmToken : fcmTokens) {
										try {
											FcmRequestDTO fcmDTO = new FcmRequestDTO();
											fcmDTO.setTitle(NotificationUtil.getNotiTitleNewTransaction());
											fcmDTO.setMessage(message);
											fcmDTO.setToken(fcmToken.getToken());
											firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
											logger.info("Send notification to device " + fcmToken.getToken());
										} catch (Exception e) {
											System.out.println("Error at send noti" + e.toString());
											logger.error("Error when Send Notification using FCM " + e.toString());
											if (e.toString()
													.contains(
															"The registration token is not a valid FCM registration token")) {
												fcmTokenService.deleteFcmToken(fcmToken.getToken());
											}

										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	@PostMapping("qr/generate-list")
	public ResponseEntity<List<VietQRDTO>> generateQRList(@Valid @RequestBody VietQRCreateListDTO list) {
		List<VietQRDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			if (!list.getDtos().isEmpty()) {
				for (VietQRCreateDTO dto : list.getDtos()) {
					// get bank information
					AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
					if (accountBankEntity != null) {
						// get bank type information
						BankTypeEntity bankTypeEntity = bankTypeService
								.getBankTypeById(accountBankEntity.getBankTypeId());
						// get cai value
						String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
						// generate VietQRGenerateDTO
						VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
						vietQRGenerateDTO.setCaiValue(caiValue);
						vietQRGenerateDTO.setAmount(dto.getAmount());
						vietQRGenerateDTO.setContent(dto.getContent());
						vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount()); // generate QR
						String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
						// generate VietQRDTO
						VietQRDTO vietQRDTO = new VietQRDTO();
						vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
						vietQRDTO.setBankName(bankTypeEntity.getBankName());
						vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
						vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
						vietQRDTO.setAmount(dto.getAmount());
						vietQRDTO.setContent(dto.getContent());
						vietQRDTO.setQrCode(qr);
						vietQRDTO.setImgId(bankTypeEntity.getImgId());
						result.add(vietQRDTO);
					}
				}
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}

		} catch (Exception e) {
			System.out.println("Error at generateQRList: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

}
