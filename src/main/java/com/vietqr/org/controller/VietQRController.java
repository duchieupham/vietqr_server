package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.text.NumberFormat;
import java.util.Locale;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.BankDetailInputDTO;
import com.vietqr.org.dto.CaiBankDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.VietQRCreateCustomerDTO;
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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.RandomCodeUtil;

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

	@PostMapping("account-bank/qr/generate")
	public ResponseEntity<VietQRDTO> generateQRBank(@Valid @RequestBody BankDetailInputDTO dto) {
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
				vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
				String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
				// generate VietQRDTO
				VietQRDTO vietQRDTO = new VietQRDTO();
				vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
				vietQRDTO.setBankName(bankTypeEntity.getBankName());
				vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
				vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
				vietQRDTO.setQrCode(qr);
				vietQRDTO.setImgId(bankTypeEntity.getImgId());
				vietQRDTO.setAmount("");
				vietQRDTO.setContent("");
				httpStatus = HttpStatus.OK;
				result = vietQRDTO;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("qr/generate-customer")
	public ResponseEntity<Object> generateQRCustomer(@Valid @RequestBody VietQRCreateCustomerDTO dto,
			@RequestHeader("Authorization") String token) {
		Object result = null;
		HttpStatus httpStatus = null;
		// UUID transcationUUID = UUID.randomUUID();
		String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
		try {
			if (dto.getContent().length() <= 50) {
				// find bankTypeId by bankcode
				String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
				if (bankTypeId != null && !bankTypeId.isEmpty()) {
					// find bank by bankAccount and banktypeId
					AccountBankReceiveEntity accountBankEntity = accountBankService
							.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
					if (accountBankEntity != null) {
						// get cai value
						BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
						String caiValue = caiBankService.getCaiValue(bankTypeId);
						// generate VietQRGenerateDTO
						VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
						vietQRGenerateDTO.setCaiValue(caiValue);
						vietQRGenerateDTO.setAmount(dto.getAmount() + "");
						vietQRGenerateDTO.setContent(traceId + "." + dto.getContent());
						vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
						String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
						//
						// generate VietQRDTO
						VietQRDTO vietQRDTO = new VietQRDTO();
						vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
						vietQRDTO.setBankName(bankTypeEntity.getBankName());
						vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
						vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
						vietQRDTO.setAmount(dto.getAmount() + "");
						vietQRDTO.setContent(traceId + "." + dto.getContent());
						vietQRDTO.setQrCode(qr);
						vietQRDTO.setImgId(bankTypeEntity.getImgId());
						vietQRDTO.setExisting(1);
						result = vietQRDTO;
						httpStatus = HttpStatus.OK;
					} else {
						// get cai value
						BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
						String caiValue = caiBankService.getCaiValue(bankTypeId);
						// generate VietQRGenerateDTO
						VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
						vietQRGenerateDTO.setCaiValue(caiValue);
						vietQRGenerateDTO.setAmount(dto.getAmount() + "");
						vietQRGenerateDTO.setContent(traceId + "." + dto.getContent());
						vietQRGenerateDTO.setBankAccount(dto.getBankAccount());
						String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
						//
						// generate VietQRDTO
						VietQRDTO vietQRDTO = new VietQRDTO();
						vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
						vietQRDTO.setBankName(bankTypeEntity.getBankName());
						vietQRDTO.setBankAccount(dto.getBankAccount());
						vietQRDTO.setUserBankName(dto.getUserBankName().toUpperCase());
						vietQRDTO.setAmount(dto.getAmount() + "");
						vietQRDTO.setContent(traceId + "." + dto.getContent());
						vietQRDTO.setQrCode(qr);
						vietQRDTO.setImgId(bankTypeEntity.getImgId());
						vietQRDTO.setExisting(0);
						result = vietQRDTO;
						httpStatus = HttpStatus.OK;
						// result = new ResponseMessageDTO("FAILED", "E25");
						// httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					result = new ResponseMessageDTO("FAILED", "E24");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				result = new ResponseMessageDTO("FAILED", "E26");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			return new ResponseEntity<>(result, httpStatus);
			//
		} catch (Exception e) {
			logger.error(e.toString());
			System.out.println(e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			System.out.println(token);
			String secretKey = "mySecretKey";
			String jwtToken = token.substring(7); // remove "Bearer " from the beginning
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
			String user = (String) claims.get("user");
			if (user != null) {
				System.out.println(user);
			} else {
				System.out.println("undetected user");
			}
		}

	}

	@PostMapping("qr/generate")
	public ResponseEntity<VietQRDTO> generateQR(@Valid @RequestBody VietQRCreateDTO dto) {
		VietQRDTO result = null;
		HttpStatus httpStatus = null;
		UUID transcationUUID = UUID.randomUUID();
		String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
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
				vietQRGenerateDTO.setContent(traceId + "." + dto.getContent());
				vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
				String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
				// generate VietQRDTO
				VietQRDTO vietQRDTO = new VietQRDTO();
				vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
				vietQRDTO.setBankName(bankTypeEntity.getBankName());
				vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
				vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
				vietQRDTO.setAmount(dto.getAmount());
				vietQRDTO.setContent(traceId + "." + dto.getContent());
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
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
			if (accountBankEntity != null) {
				UUID transactionBranchUUID = UUID.randomUUID();
				LocalDateTime currentDateTime = LocalDateTime.now();
				TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
				transactionEntity.setId(transcationUUID.toString());
				transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
				transactionEntity.setBankId(dto.getBankId());
				transactionEntity.setContent(traceId + "." + dto.getContent());
				transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
				transactionEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
				transactionEntity.setRefId("");
				transactionEntity.setType(0);
				transactionEntity.setStatus(0);
				transactionEntity.setTraceId(traceId);
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				// insert transaction branch if existing branchId and businessId. Else just do
				// not map.
				if (!dto.getBranchId().isEmpty() && !dto.getBusinessId().isEmpty()) {
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
									+ NotificationUtil.getNotiDescNewTransSuffix1()
									+ nf.format(Double.parseDouble(dto.getAmount()))
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
				} else {
					// insert notification
					UUID notificationUUID = UUID.randomUUID();
					NotificationEntity notiEntity = new NotificationEntity();
					String message = NotificationUtil.getNotiDescNewTransPrefix2()
							+ NotificationUtil.getNotiDescNewTransSuffix1()
							+ nf.format(Double.parseDouble(dto.getAmount()))
							+ NotificationUtil
									.getNotiDescNewTransSuffix2();
					// String title = NotificationUtil.getNotiTitleNewTransaction();
					notiEntity.setId(notificationUUID.toString());
					notiEntity.setRead(false);
					notiEntity.setMessage(message);
					notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
					notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
					notiEntity.setUserId(dto.getUserId());
					notiEntity.setData(transcationUUID.toString());
					notificationService.insertNotification(notiEntity);
					// push notification
					List<FcmTokenEntity> fcmTokens = new ArrayList<>();
					fcmTokens = fcmTokenService.getFcmTokensByUserId(dto.getUserId());
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
