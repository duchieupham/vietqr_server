package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.BankDetailInputDTO;
import com.vietqr.org.dto.CaiBankDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.VietQRCreateCustomerDTO;
import com.vietqr.org.dto.VietQRCreateDTO;
import com.vietqr.org.dto.VietQRCreateFromTransactionDTO;
import com.vietqr.org.dto.VietQRCreateListDTO;
import com.vietqr.org.dto.VietQRDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankReceiveBranchEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.CaiBankEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankReceiveBranchService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.TextToSpeechService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.util.VietQRUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.SocketHandler;

@RestController
@CrossOrigin
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

	@Autowired
	private SocketHandler socketHandler;

	@Autowired
	private TextToSpeechService textToSpeechService;

	@Autowired
	private BankReceiveBranchService bankReceiveBranchService;

	private FirebaseMessagingService firebaseMessagingService;

	public VietQRController(FirebaseMessagingService firebaseMessagingService) {
		this.firebaseMessagingService = firebaseMessagingService;
	}

	@PostMapping("/transaction/voice/{userId}")
	public ResponseEntity<Void> handleWebhookEvent(@PathVariable String userId, @RequestBody Object payload) {
		String requestId = "";
		String audioLink = "";
		try {
			logger.info("TTS-WH: payload: " + payload.toString());
			// Create an ObjectMapper instance
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.valueToTree(payload);

			// Extract the values of request_id and audio_link
			if (rootNode.get("request_id") != null) {
				requestId = rootNode.get("request_id").asText();
				logger.info("TTS-WH: request_id: " + requestId);
			} else {
				logger.info("TTS-WH: empty request_id");
			}
			if (rootNode.get("audio_link") != null) {
				audioLink = rootNode.get("audio_link").asText();
				logger.info("TTS-WH: audio_link: " + audioLink);
			} else {
				logger.info("TTS-WH: empty audio_link");
			}
			if (!requestId.trim().isEmpty() && !audioLink.trim().isEmpty()) {
				logger.info("TTS-WH: update tts");
				textToSpeechService.update(requestId, audioLink);
			}
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			logger.info("TTS-WH: Error: " + e.toString());
			return ResponseEntity.ok().build();
		} finally {
			if (!requestId.trim().isEmpty() && !audioLink.trim().isEmpty()) {
				Map<String, String> notiData = textToSpeechService.findData(requestId);
				notiData.put("audioLink", audioLink);
				try {
					socketHandler.sendMessageToUser(userId, notiData);
				} catch (Exception e) {
					logger.error("TTS-WS: Error: " + e.toString());
				}
			}
		}
	}

	@GetMapping("qr/websocket/test")
	public ResponseEntity<ResponseMessageDTO> testWebSocket() {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			Map<String, String> data = new HashMap<>();
			data.put("key1", "xinchao");
			data.put("key2", "test");
			socketHandler.sendMessageToUser("36a242cd-5c83-4fb8-830a-cf30a193a99d", data);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at insertCaiBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
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

	// generate QR into bankDetail
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
		String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
		VietQRDTO vietQRDTO = new VietQRDTO();
		try {
			if (dto.getContent().length() <= 50) {
				// find bankTypeId by bankcode

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
						// generate VietQRDTO
						vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
						vietQRDTO.setBankName(bankTypeEntity.getBankName());
						vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
						vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
						vietQRDTO.setAmount(dto.getAmount() + "");
						vietQRDTO.setContent(traceId + "." + dto.getContent());
						vietQRDTO.setQrCode(qr);
						vietQRDTO.setImgId(bankTypeEntity.getImgId());
						vietQRDTO.setExisting(1);
						//
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
			// insert new transaction with orderId and sign
			AccountBankReceiveEntity accountBankEntity = accountBankService
					.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
			if (accountBankEntity != null) {
				VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
				vietQRCreateDTO.setBankId(accountBankEntity.getId());
				vietQRCreateDTO.setAmount(dto.getAmount() + "");
				vietQRCreateDTO.setContent(dto.getContent());
				vietQRCreateDTO.setBusinessId("");
				vietQRCreateDTO.setBranchId("");
				vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
				UUID transactionUUID = UUID.randomUUID();
				insertNewTransaction(transactionUUID, traceId, vietQRCreateDTO, vietQRDTO, dto.getOrderId(),
						dto.getSign(), true);
			}
			//
			LocalDateTime currentDateTime = LocalDateTime.now();
			long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
			String secretKey = "mySecretKey";
			String jwtToken = token.substring(7); // remove "Bearer " from the beginning
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
			String user = (String) claims.get("user");
			if (user != null) {
				String decodedUser = new String(Base64.getDecoder().decode(user));
				logger.info("qr/generate-customer - user " + decodedUser + " call at " + time);
				System.out.println("qr/generate-customer - user " + decodedUser + " call at " + time);
			} else {
				logger.info("qr/generate-customer - Sytem User call at " + time);
			}
		}
	}

	@Async
	private void insertNewTransaction(UUID transcationUUID, String traceId, VietQRCreateDTO dto, VietQRDTO result,
			String orderId, String sign, boolean isFromBusinessSync) {
		LocalDateTime startTime = LocalDateTime.now();
		long startTimeLong = startTime.toEpochSecond(ZoneOffset.UTC);
		logger.info("QR generate - start insertNewTransaction at: " + startTimeLong);
		logger.info("QR generate - insertNewTransaction data: " + result.toString());
		try {
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			// 2. Insert transaction_receive if branch_id and business_id != null
			// 3. Insert transaction_receive_branch if branch_id and business_id != null
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
				transactionEntity.setTransType("C");
				transactionEntity.setReferenceNumber("");
				transactionEntity.setOrderId(orderId);
				transactionEntity.setSign(sign);
				//
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				LocalDateTime afterInsertTransactionTime = LocalDateTime.now();
				long afterInsertTransactionTimeLong = afterInsertTransactionTime.toEpochSecond(ZoneOffset.UTC);
				logger.info("QR generate - after insertTransactionReceive at: " + afterInsertTransactionTimeLong);

				// insert transaction branch if existing branchId and businessId. Else just do
				// not map.
				if (!dto.getBranchId().isEmpty() && !dto.getBusinessId().isEmpty()) {
					TransactionReceiveBranchEntity transactionBranchEntity = new TransactionReceiveBranchEntity();
					transactionBranchEntity.setId(transactionBranchUUID.toString());
					transactionBranchEntity.setTransactionReceiveId(transcationUUID.toString());
					transactionBranchEntity.setBranchId(dto.getBranchId());
					transactionBranchEntity.setBusinessId(dto.getBusinessId());
					System.out.println("branchId: " + dto.getBranchId());
					System.out.println("branchId after set: " + transactionBranchEntity.getBranchId());
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
							String message = NotificationUtil.getNotiDescNewTransPrefix()
									+ branchEntity.getName()
									+ NotificationUtil.getNotiDescNewTransSuffix1()
									+ nf.format(Double.parseDouble(dto.getAmount()))
									+ NotificationUtil
											.getNotiDescNewTransSuffix2();

							// push notification
							List<FcmTokenEntity> fcmTokens = new ArrayList<>();
							fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
							Map<String, String> data = new HashMap<>();
							data.put("notificationType", NotificationUtil.getNotiTypeNewTransaction());
							data.put("notificationId", notificationUUID.toString());
							data.put("bankCode", result.getBankCode());
							data.put("bankName", result.getBankName());
							data.put("bankAccount", result.getBankAccount());
							data.put("userBankName", result.getUserBankName());
							data.put("amount", result.getAmount());
							data.put("content", result.getContent());
							data.put("qrCode", result.getQrCode());
							data.put("imgId", result.getImgId());
							firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
									NotificationUtil
											.getNotiTitleNewTransaction(),
									message);
							socketHandler.sendMessageToUser(userId, data);
							notiEntity.setId(notificationUUID.toString());
							notiEntity.setRead(false);
							notiEntity.setMessage(message);
							notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
							notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
							notiEntity.setUserId(userId);
							notiEntity.setData(transcationUUID.toString());
							notificationService.insertNotification(notiEntity);
							LocalDateTime afterInsertNotificationTransaction = LocalDateTime.now();
							long afterInsertNotificationTransactionLong = afterInsertNotificationTransaction
									.toEpochSecond(ZoneOffset.UTC);
							logger.info("QR generate - after InsertNotificationTransaction at: "
									+ afterInsertNotificationTransactionLong);

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

					if (isFromBusinessSync == false) {
						// push notification
						List<FcmTokenEntity> fcmTokens = new ArrayList<>();
						fcmTokens = fcmTokenService.getFcmTokensByUserId(dto.getUserId());
						Map<String, String> data = new HashMap<>();
						data.put("notificationType", NotificationUtil.getNotiTypeNewTransaction());
						data.put("notificationId", notificationUUID.toString());
						data.put("bankCode", result.getBankCode());
						data.put("bankName", result.getBankName());
						data.put("bankAccount", result.getBankAccount());
						data.put("userBankName", result.getUserBankName());
						data.put("amount", result.getAmount());
						data.put("content", result.getContent());
						data.put("qrCode", result.getQrCode());
						data.put("imgId", result.getImgId());
						firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
								NotificationUtil
										.getNotiTitleNewTransaction(),
								message);
						socketHandler.sendMessageToUser(dto.getUserId(), data);
					}

					notiEntity.setId(notificationUUID.toString());
					notiEntity.setRead(false);
					notiEntity.setMessage(message);
					notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
					notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
					notiEntity.setUserId(dto.getUserId());
					notiEntity.setData(transcationUUID.toString());
					notificationService.insertNotification(notiEntity);
					LocalDateTime afterInsertNotificationTransaction = LocalDateTime.now();
					long afterInsertNotificationTransactionLong = afterInsertNotificationTransaction
							.toEpochSecond(ZoneOffset.UTC);
					logger.info("QR generate - after InsertNotificationTransaction at: "
							+ afterInsertNotificationTransactionLong);
				}
			}

		} catch (Exception e) {
			logger.error("Error at insertNewTransaction: " + e.toString());
		} finally {
			LocalDateTime endTime = LocalDateTime.now();
			long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
			logger.info("QR generate - end insertNewTransaction at: " + endTimeLong);
		}

	}

	@Async
	private void reInsertNewTransaction(UUID transcationUUID, String traceId,
			VietQRCreateFromTransactionDTO dto, VietQRDTO result) {
		try {
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			// 2. Insert transaction_receive if branch_id and business_id != null
			// 3. Insert transaction_receive_branch if branch_id and business_id != null
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
			if (accountBankEntity != null) {
				UUID transactionBranchUUID = UUID.randomUUID();
				LocalDateTime currentDateTime = LocalDateTime.now();
				TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
				transactionEntity.setId(transcationUUID.toString());
				transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
				transactionEntity.setBankId(dto.getBankId());
				transactionEntity.setContent(result.getContent());
				transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
				transactionEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
				transactionEntity.setRefId("");
				transactionEntity.setType(0);
				transactionEntity.setStatus(0);
				transactionEntity.setTraceId(traceId);
				transactionEntity.setTransType("C");
				transactionEntity.setReferenceNumber("");
				transactionEntity.setOrderId("");
				transactionEntity.setSign("");
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				// insert transaction branch if existing branchId and businessId. Else just do
				// not map.
				// find businessId and branchId
				BankReceiveBranchEntity bankReceiveBranch = bankReceiveBranchService
						.getBankReceiveBranchByBankId(dto.getBankId());
				if (bankReceiveBranch != null) {
					TransactionReceiveBranchEntity transactionBranchEntity = new TransactionReceiveBranchEntity();
					transactionBranchEntity.setId(transactionBranchUUID.toString());
					transactionBranchEntity.setTransactionReceiveId(transcationUUID.toString());
					transactionBranchEntity.setBranchId(bankReceiveBranch.getBranchId());
					transactionBranchEntity.setBusinessId(bankReceiveBranch.getBusinessId());
					transactionReceiveBranchService.insertTransactionReceiveBranch(transactionBranchEntity);
					// find userIds into business_member and branch_member
					List<String> userIds = branchMemberService
							.getUserIdsByBusinessIdAndBranchId(bankReceiveBranch.getBusinessId(),
									bankReceiveBranch.getBranchId());
					// insert AND push notification to users belong to
					// admin business/ member of branch
					if (userIds != null && !userIds.isEmpty()) {
						for (String userId : userIds) {
							// insert notification
							UUID notificationUUID = UUID.randomUUID();
							NotificationEntity notiEntity = new NotificationEntity();
							BranchInformationEntity branchEntity = branchInformationService
									.getBranchById(bankReceiveBranch.getBranchId());
							String message = NotificationUtil.getNotiDescNewTransPrefix()
									+ branchEntity.getName()
									+ NotificationUtil.getNotiDescNewTransSuffix1()
									+ nf.format(Double.parseDouble(dto.getAmount()))
									+ NotificationUtil
											.getNotiDescNewTransSuffix2();

							// push notification
							List<FcmTokenEntity> fcmTokens = new ArrayList<>();
							fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
							Map<String, String> data = new HashMap<>();
							data.put("notificationType", NotificationUtil.getNotiTypeNewTransaction());
							data.put("notificationId", notificationUUID.toString());
							data.put("bankCode", result.getBankCode());
							data.put("bankName", result.getBankName());
							data.put("bankAccount", result.getBankAccount());
							data.put("userBankName", result.getUserBankName());
							data.put("amount", result.getAmount());
							data.put("content", result.getContent());
							data.put("qrCode", result.getQrCode());
							data.put("imgId", result.getImgId());
							firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
									NotificationUtil
											.getNotiTitleNewTransaction(),
									message);
							socketHandler.sendMessageToUser(userId, data);
							notiEntity.setId(notificationUUID.toString());
							notiEntity.setRead(false);
							notiEntity.setMessage(message);
							notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
							notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
							notiEntity.setUserId(userId);
							notiEntity.setData(transcationUUID.toString());
							notificationService.insertNotification(notiEntity);
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

					// push notification
					List<FcmTokenEntity> fcmTokens = new ArrayList<>();
					fcmTokens = fcmTokenService.getFcmTokensByUserId(dto.getUserId());
					Map<String, String> data = new HashMap<>();
					data.put("notificationType", NotificationUtil.getNotiTypeNewTransaction());
					data.put("notificationId", notificationUUID.toString());
					data.put("bankCode", result.getBankCode());
					data.put("bankName", result.getBankName());
					data.put("bankAccount", result.getBankAccount());
					data.put("userBankName", result.getUserBankName());
					data.put("amount", result.getAmount());
					data.put("content", result.getContent());
					data.put("qrCode", result.getQrCode());
					data.put("imgId", result.getImgId());
					firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
							NotificationUtil
									.getNotiTitleNewTransaction(),
							message);
					socketHandler.sendMessageToUser(dto.getUserId(), data);
					notiEntity.setId(notificationUUID.toString());
					notiEntity.setRead(false);
					notiEntity.setMessage(message);
					notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
					notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
					notiEntity.setUserId(dto.getUserId());
					notiEntity.setData(transcationUUID.toString());
					notificationService.insertNotification(notiEntity);
				}
			}
		} catch (Exception e) {
			logger.error("Error at reInsertNewTransaction: " + e.toString());
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
			insertNewTransaction(transcationUUID, traceId, dto, result, "", "", false);
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
			// System.out.println("Error at generateQRList: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// API to show QR code
	// newTransaction = true => create new Transaction and generate QR
	// newTransaction = false => generate QR

	@PostMapping("qr/re-generate")
	public ResponseEntity<VietQRDTO> reGenerateQR(@Valid @RequestBody VietQRCreateFromTransactionDTO dto) {
		VietQRDTO result = null;
		HttpStatus httpStatus = null;
		UUID transcationUUID = UUID.randomUUID();
		String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
		try {
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
			if (accountBankEntity != null) {
				String content = "";
				if (dto.isNewTransaction() == true) {
					String suffixContent = "";
					String regex = "VQR\\w{10}\\.?";
					suffixContent = dto.getContent().replaceAll(regex, "");
					content = traceId + "." + suffixContent;
				} else {
					content = dto.getContent();
				}
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
				vietQRGenerateDTO.setContent(content);
				vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
				String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
				// generate VietQRDTO
				VietQRDTO vietQRDTO = new VietQRDTO();
				vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
				vietQRDTO.setBankName(bankTypeEntity.getBankName());
				vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
				vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
				vietQRDTO.setAmount(dto.getAmount());
				vietQRDTO.setContent(content);
				vietQRDTO.setQrCode(qr);
				vietQRDTO.setImgId(bankTypeEntity.getImgId());
				result = vietQRDTO;
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			return new ResponseEntity<>(result, httpStatus);
		} catch (Exception e) {
			// System.out.println("Error at reGenerateQR: " + e.toString());
			logger.error("Re-generateQR: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			if (dto.isNewTransaction() == true) {
				reInsertNewTransaction(transcationUUID, traceId, dto, result);
			}
		}
	}
}
