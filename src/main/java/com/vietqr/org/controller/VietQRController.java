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

import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.BankDetailInputDTO;
import com.vietqr.org.dto.CaiBankDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.dto.VietQRCreateCustomerDTO;
import com.vietqr.org.dto.VietQRCreateDTO;
import com.vietqr.org.dto.VietQRCreateFromTransactionDTO;
import com.vietqr.org.dto.VietQRCreateListDTO;
import com.vietqr.org.dto.VietQRCreateUnauthenticatedDTO;
import com.vietqr.org.dto.VietQRDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.dto.VietQRMMSCreateDTO;
import com.vietqr.org.dto.VietQRMMSRequestDTO;
import com.vietqr.org.util.VietQRUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.SocketHandler;
import com.vietqr.org.util.TransactionRefIdUtil;

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
	AccountBankReceivePersonalService accountBankReceivePersonalService;

	@Autowired
	BankReceiveBranchService bankReceiveBranchService;

	@Autowired
	TerminalBankService terminalBankService;

	@Autowired
	private TextToSpeechService textToSpeechService;

	private FirebaseMessagingService firebaseMessagingService;

	@Autowired
	private ImageService imageService;

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

	@PostMapping("account-bank/qr/generate-image")
	public ResponseEntity<VietQRDTO> generateQRBankImage(@Valid @RequestBody BankDetailInputDTO dto) {
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
				// generate QR image
				byte[] centerVietQrIcon = imageService.getImageById(EnvironmentUtil.getVietQrIcon());
				byte[] headerVietQr = imageService.getImageById(EnvironmentUtil.getVietQrLogo());
				byte[] bottomLeftIcon = imageService.getImageById(EnvironmentUtil.getNapasLogo());
				byte[] qrImage = VietQRUtil.generateVietQRImg(qr, headerVietQr, centerVietQrIcon, bottomLeftIcon);
				String fileName = "vietqr_" + UUID.randomUUID() + ".jpg";
				UUID uuidImage = UUID.randomUUID();
				ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, qrImage);
				imageService.insertImage(imageEntity);
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
				vietQRDTO.setTransactionId("");
				vietQRDTO.setTerminalCode("");
				vietQRDTO.setTransactionRefId("");
				vietQRDTO.setQrLink(String.format("%s%s", EnvironmentUtil.getVietQrUrlApi(),
						uuidImage));
				httpStatus = HttpStatus.OK;
				result = vietQRDTO;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			System.out.println("generateQRBankImage: ERROR: " + e.toString());
			logger.error(e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping(value = "qr/generate-image", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> generateImage(@RequestParam String content) {
		byte[] result = new byte[0];
		HttpStatus httpStatus = null;
		try {
			String qr = content;
			byte[] centerVietQrIcon = imageService.getImageById(EnvironmentUtil.getVietQrIcon());
			byte[] headerVietQr = imageService.getImageById(EnvironmentUtil.getVietQrLogo());
			byte[] bottonLeftIcon = imageService.getImageById(EnvironmentUtil.getNapasLogo());
			byte[] qrImage = VietQRUtil.generateVietQRImg(qr, headerVietQr, centerVietQrIcon, bottonLeftIcon);
			// String fileName = "vietqr_" + UUID.randomUUID() + ".jpg";
			// UUID uuidImage = UUID.randomUUID();
			// ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName,
			// qrImage);
			// imageService.insertImage(imageEntity);
			result = qrImage;
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			// result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// generate QR into bankDetail
	@PostMapping("account-bank/qr/generate")
	public ResponseEntity<VietQRDTO> generateQRBank(@RequestBody BankDetailInputDTO dto) {
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
				vietQRDTO.setTransactionId("");
				vietQRDTO.setTerminalCode("");
				vietQRDTO.setTransactionRefId("");
				vietQRDTO.setQrLink("");
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

	@PostMapping("qr/generate/unauthenticated")
	public ResponseEntity<Object> generateQRUnauthenticated(@Valid @RequestBody VietQRCreateUnauthenticatedDTO dto) {
		Object result = null;
		HttpStatus httpStatus = null;
		VietQRDTO vietQRDTO = new VietQRDTO();
		try {
			String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
			if (bankTypeId != null) {
				String caiValue = caiBankService.getCaiValue(bankTypeId);
				BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
				VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
				vietQRGenerateDTO.setCaiValue(caiValue);
				vietQRGenerateDTO.setBankAccount(dto.getBankAccount());
				String amount = "";
				String content = "";
				if (dto.getAmount() != null && !dto.getAmount().trim().isEmpty()) {
					amount = dto.getAmount();
				} else if (dto.getAmount() != null && dto.getAmount().trim().isEmpty()) {
					amount = "0";
				}
				if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
					content = dto.getContent();
				}
				vietQRGenerateDTO.setAmount(amount);
				vietQRGenerateDTO.setContent(content);
				String qr = "";
				if (amount.trim().isEmpty() && content.trim().isEmpty()) {
					qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
				} else {
					qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
				}
				vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
				vietQRDTO.setBankName(bankTypeEntity.getBankName());
				vietQRDTO.setBankAccount(dto.getBankAccount());
				vietQRDTO.setUserBankName(dto.getUserBankName().toUpperCase());
				vietQRDTO.setAmount(amount);
				vietQRDTO.setContent(content);
				vietQRDTO.setQrCode(qr);
				vietQRDTO.setImgId(bankTypeEntity.getImgId());
				vietQRDTO.setExisting(0);
				vietQRDTO.setTransactionId("");
				vietQRDTO.setTransactionRefId("");
				vietQRDTO.setTerminalCode("");
				vietQRDTO.setQrLink("");
				result = vietQRDTO;
				httpStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("qr/generate-customer")
	public ResponseEntity<Object> generateQRCustomer(@RequestBody VietQRCreateCustomerDTO dto,
			@RequestHeader("Authorization") String token) {
		Object result = null;
		HttpStatus httpStatus = null;
		UUID transactionUUID = UUID.randomUUID();
		// for saving qr mms flow 2
		String qrMMS = "";
		// find bankAccount đã liên kết và mms = true và check transType = "C -> gọi
		// luồng 2
		String checkExistedMMSBank = accountBankService.checkMMSBankAccount(dto.getBankAccount());
		boolean checkMMS = false;
		String transType = "C";
		if (dto.getTransType() == null) {
			transType = "C";
		} else {
			transType = dto.getTransType().trim();
		}
		if (checkExistedMMSBank != null && !checkExistedMMSBank.trim().isEmpty() && transType.equals("C")) {
			checkMMS = true;
		}
		if (checkMMS == false) {
			// Luồng 1
			// UUID transcationUUID = UUID.randomUUID();
			String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
			String bankTypeId = "";
			if (dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")) {
				bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
			} else {
				bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getCustomerBankCode());
			}
			VietQRDTO vietQRDTO = new VietQRDTO();
			try {
				if (dto.getContent().length() <= 50) {
					// check if generate qr with transtype = D or C
					// if D => generate with customer information
					// if C => do normal
					// find bankTypeId by bankcode
					if (bankTypeId != null && !bankTypeId.isEmpty()) {
						// find bank by bankAccount and banktypeId

						AccountBankReceiveEntity accountBankEntity = null;
						if (dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")) {
							accountBankEntity = accountBankService
									.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
						} else {
							accountBankEntity = accountBankService
									.getAccountBankByBankAccountAndBankTypeId(dto.getCustomerBankAccount(), bankTypeId);
						}
						if (accountBankEntity != null) {
							// get cai value
							BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
							String caiValue = caiBankService.getCaiValue(bankTypeId);
							String content = "";
							if (dto.getReconciliation() == null || dto.getReconciliation()) {
								content = traceId + "." + dto.getContent();
							} else {
								content = dto.getContent();
							}
							// generate VietQRGenerateDTO
							VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
							vietQRGenerateDTO.setCaiValue(caiValue);
							vietQRGenerateDTO.setAmount(dto.getAmount() + "");
							vietQRGenerateDTO.setContent(content);
							vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
							String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
							// generate VietQRDTO
							vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
							vietQRDTO.setBankName(bankTypeEntity.getBankName());
							vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
							vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
							vietQRDTO.setAmount(dto.getAmount() + "");
							vietQRDTO.setContent(content);
							vietQRDTO.setQrCode(qr);
							vietQRDTO.setImgId(bankTypeEntity.getImgId());
							vietQRDTO.setExisting(1);
							vietQRDTO.setTransactionId("");
							vietQRDTO.setTerminalCode(dto.getTerminalCode());
							String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
							String qrLink = EnvironmentUtil.getQRLink() + refId;
							vietQRDTO.setTransactionRefId(refId);
							vietQRDTO.setQrLink(qrLink);
							//
							result = vietQRDTO;
							httpStatus = HttpStatus.OK;
						} else {
							String bankAccount = "";
							String userBankName = "";
							String content = "";
							if (dto.getTransType() == null || dto.getTransType().trim().toUpperCase().equals("C")) {
								bankAccount = dto.getBankAccount();
								userBankName = dto.getUserBankName().trim().toUpperCase();
							} else {
								bankAccount = dto.getCustomerBankAccount();
								userBankName = dto.getCustomerName().trim().toUpperCase();
							}
							// get cai value
							BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
							String caiValue = caiBankService.getCaiValue(bankTypeId);
							// generate VietQRGenerateDTO
							VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
							vietQRGenerateDTO.setCaiValue(caiValue);
							vietQRGenerateDTO.setAmount(dto.getAmount() + "");
							if (dto.getReconciliation() == null || dto.getReconciliation()) {
								content = traceId + "." + dto.getContent();
							} else {
								content = dto.getContent();
							}
							vietQRGenerateDTO.setContent(content);
							vietQRGenerateDTO.setBankAccount(bankAccount);
							String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
							//
							// generate VietQRDTO
							vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
							vietQRDTO.setBankName(bankTypeEntity.getBankName());
							vietQRDTO.setBankAccount(bankAccount);
							vietQRDTO.setUserBankName(userBankName);
							vietQRDTO.setAmount(dto.getAmount() + "");
							vietQRDTO.setContent(content);
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
				if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
					bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
				}
				AccountBankReceiveEntity accountBankEntity = accountBankService
						.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeId);
				if (accountBankEntity != null) {
					System.out.println("FINALLY accountBankEntity FOUND: " + accountBankEntity.toString());
					VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
					vietQRCreateDTO.setBankId(accountBankEntity.getId());
					vietQRCreateDTO.setAmount(dto.getAmount() + "");
					vietQRCreateDTO.setContent(dto.getContent());
					vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
					vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
					//
					if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
						vietQRCreateDTO.setTransType("D");
						vietQRCreateDTO.setCustomerBankAccount(dto.getCustomerBankAccount());
						vietQRCreateDTO.setCustomerBankCode(dto.getCustomerBankCode());
						vietQRCreateDTO.setCustomerName(dto.getCustomerName());
					} else {
						vietQRCreateDTO.setTransType("C");
					}
					if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
						vietQRCreateDTO.setUrlLink(dto.getUrlLink());
					} else {
						vietQRCreateDTO.setUrlLink("");
					}
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
		} else {
			// Luồng 2
			// String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
			LocalDateTime requestLDT = LocalDateTime.now();
			long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
			logger.info("generateVietQRMMS: start generate at: " + requestTime);
			String bankTypeMB = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
			// BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeMB);
			AccountBankReceiveEntity accountBankEntity = null;
			String qrCode = "";
			try {
				// 1. Validate input (amount, content, bankCode) => E34 if Invalid input data
				if (checkRequestBodyFlow2(dto)) {
					// 2. Find terminal bank by bank_account_raw_number
					accountBankEntity = accountBankService
							.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeMB);
					if (accountBankEntity != null) {
						TerminalBankEntity terminalBankEntity = terminalBankService
								.getTerminalBankByBankAccount(dto.getBankAccount());
						if (terminalBankEntity == null) {
							// 3.A. If not found => E35 (terminal is not existed)
							logger.error("generateVietQRMMS: ERROR: Bank account is not existed.");
							result = new ResponseMessageDTO("FAILED", "E35");
							httpStatus = HttpStatus.BAD_REQUEST;
						} else {
							// 3.B. If found => get bank token => create qr code
							TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();
							if (tokenBankDTO != null) {
								String content = "";
								if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
									content = dto.getContent();
								} else {
									String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
									content = traceId;
								}
								VietQRMMSRequestDTO requestDTO = new VietQRMMSRequestDTO();
								requestDTO.setToken(tokenBankDTO.getAccess_token());
								requestDTO.setTerminalId(terminalBankEntity.getTerminalId());
								requestDTO.setAmount(dto.getAmount() + "");
								requestDTO.setContent(content);
								requestDTO.setOrderId(dto.getOrderId());
								qrCode = requestVietQRMMS(requestDTO);
								if (qrCode != null) {
									// VietQRMMSDTO vietQRMMSDTO = new VietQRMMSDTO(qrCode);
									qrMMS = qrCode;
									String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
									if (bankTypeId != null && !bankTypeId.trim().isEmpty()) {
										VietQRDTO vietQRDTO = new VietQRDTO();
										// get cai value
										BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
										//
										String bankAccount = "";
										String userBankName = "";
										bankAccount = dto.getBankAccount();
										userBankName = dto.getUserBankName().trim().toUpperCase();
										// generate VietQRDTO
										vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
										vietQRDTO.setBankName(bankTypeEntity.getBankName());
										vietQRDTO.setBankAccount(bankAccount);
										vietQRDTO.setUserBankName(userBankName);
										vietQRDTO.setAmount(dto.getAmount() + "");
										vietQRDTO.setContent(content);
										vietQRDTO.setQrCode(qrCode);
										vietQRDTO.setImgId(bankTypeEntity.getImgId());
										vietQRDTO.setExisting(0);
										vietQRDTO.setTransactionId("");
										vietQRDTO.setTerminalCode(dto.getTerminalCode());
										String refId = TransactionRefIdUtil
												.encryptTransactionId(transactionUUID.toString());
										String qrLink = EnvironmentUtil.getQRLink() + refId;
										vietQRDTO.setTransactionRefId(refId);
										vietQRDTO.setQrLink(qrLink);
										result = vietQRDTO;
										httpStatus = HttpStatus.OK;
										// result = vietQRMMSDTO;
									} else {
										result = new ResponseMessageDTO("FAILED", "E24");
										httpStatus = HttpStatus.BAD_REQUEST;
									}
								} else {
									logger.error("generateVietQRMMS: ERROR: Invalid get QR Code");
									result = new ResponseMessageDTO("FAILED", "E05");
									httpStatus = HttpStatus.BAD_REQUEST;
								}
							} else {
								logger.error("generateVietQRMMS: ERROR: Invalid get bank token");
								result = new ResponseMessageDTO("FAILED", "E05");
								httpStatus = HttpStatus.BAD_REQUEST;
							}
						}
					} else {
						logger.error("generateVietQRMMS: ERROR: bankAccount is not existed in system");
						result = new ResponseMessageDTO("FAILED", "E36");
						httpStatus = HttpStatus.BAD_REQUEST;

					}
				} else {
					logger.error("generateVietQRMMS: ERROR: Invalid request body");
					result = new ResponseMessageDTO("FAILED", "E34");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
				return new ResponseEntity<>(result, httpStatus);
			} catch (Exception e) {
				logger.error("generateVietQRMMS: ERROR: " + e.toString());
				result = new ResponseMessageDTO("FAILED", "E05");
				httpStatus = HttpStatus.BAD_REQUEST;
				return new ResponseEntity<>(result, httpStatus);
			} finally {
				// 4. Insert transaction_receive
				// (5. Insert notification)
				if (accountBankEntity != null && qrCode != null && !qrCode.isEmpty()) {
					String content = "";
					if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
						content = dto.getContent();
					} else {
						String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
						content = traceId;
					}
					LocalDateTime currentDateTime = LocalDateTime.now();
					long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
					VietQRMMSCreateDTO vietQRMMSCreateDTO = new VietQRMMSCreateDTO();
					vietQRMMSCreateDTO.setBankAccount(dto.getBankAccount());
					vietQRMMSCreateDTO.setBankCode(dto.getBankCode());
					vietQRMMSCreateDTO.setAmount(dto.getAmount() + "");
					vietQRMMSCreateDTO.setContent(content);
					vietQRMMSCreateDTO.setOrderId(dto.getOrderId());
					vietQRMMSCreateDTO.setSign(dto.getSign());
					vietQRMMSCreateDTO.setTerminalCode(dto.getTerminalCode());
					vietQRMMSCreateDTO.setNote(dto.getNote());
					if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
						vietQRMMSCreateDTO.setUrlLink(dto.getUrlLink());
					} else {
						vietQRMMSCreateDTO.setUrlLink("");
					}
					insertNewTransactionFlow2(qrMMS, transactionUUID.toString(), accountBankEntity, vietQRMMSCreateDTO,
							time);
				}
			}
		}

	}

	private String requestVietQRMMS(VietQRMMSRequestDTO dto) {
		String result = null;
		LocalDateTime requestLDT = LocalDateTime.now();
		long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
		logger.info("requestVietQRMMS: start request QR to MB at: " + requestTime);
		try {
			UUID clientMessageId = UUID.randomUUID();
			Map<String, Object> data = new HashMap<>();
			data.put("terminalID", dto.getTerminalId());
			data.put("qrcodeType", 4);
			data.put("partnerType", 2);
			data.put("initMethod", 12);
			data.put("transactionAmount", dto.getAmount());
			data.put("billNumber", "");
			data.put("additionalAddress", 0);
			data.put("additionalMobile", 0);
			data.put("additionalEmail", 0);
			data.put("referenceLabelCode", dto.getOrderId());
			String content = "";
			if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
				content = dto.getContent();
			} else {
				String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
				content = traceId;
			}
			data.put("transactionPurpose", content);
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(EnvironmentUtil.getBankUrl()
							+ "ms/offus/public/payment-service/payment/v1.0/createqr")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(
							EnvironmentUtil.getBankUrl()
									+ "ms/offus/public/payment-service/payment/v1.0/createqr")
					.build();
			Mono<ClientResponse> responseMono = webClient.post()
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_JSON)
					.header("clientMessageId", clientMessageId.toString())
					.header("secretKey", EnvironmentUtil.getSecretKeyAPI())
					.header("username", EnvironmentUtil.getUsernameAPI())
					.header("Authorization", "Bearer " + dto.getToken())
					.body(BodyInserters.fromValue(data))
					.exchange();
			ClientResponse response = responseMono.block();
			if (response.statusCode().is2xxSuccessful()) {
				String json = response.bodyToMono(String.class).block();
				logger.info("requestVietQRMMS: RESPONSE: " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				if (rootNode.get("data") != null) {
					if (rootNode.get("data").get("qrcode") != null) {
						result = rootNode.get("data").get("qrcode").asText();
						logger.info("requestVietQRMMS: RESPONSE qrcode: " + result);
					} else {
						logger.info("requestVietQRMMS: RESPONSE qrcode is null");
					}
				} else {
					logger.info("requestVietQRMMS: RESPONSE data is null");
				}
			} else {
				String json = response.bodyToMono(String.class).block();
				logger.info("requestVietQRMMS: RESPONSE: " + response.statusCode().value() + " - " + json);
			}
		} catch (Exception e) {
			logger.error("requestVietQRMMS: ERROR: " + e.toString());
		} finally {
			LocalDateTime responseLDT = LocalDateTime.now();
			long responseTime = responseLDT.toEpochSecond(ZoneOffset.UTC);
			logger.info("requestVietQRMMS: response from MB at: " + responseTime);
		}
		return result;
	}

	private boolean checkRequestBodyFlow2(VietQRCreateCustomerDTO dto) {
		boolean result = false;
		try {
			// content up to 19
			// orderId up to 13
			String content = "";
			String orderId = "";
			if (dto.getContent() != null) {
				content = dto.getContent();
			}
			if (dto.getOrderId() != null) {
				orderId = dto.getOrderId();
			}
			if (dto != null
					&& content.length() <= 19
					&& orderId.length() <= 13
					&& dto.getAmount() != null && !dto.getBankAccount().trim().isEmpty()

					&& dto.getBankAccount() != null && !dto.getBankAccount().trim().isEmpty()
					&& dto.getBankCode() != null && dto.getBankCode().equals("MB")) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("checkRequestBody: ERROR: " + e.toString());
		}
		return result;
	}

	@Async
	private void insertNewTransactionFlow2(String qrCode, String transcationUUID,
			AccountBankReceiveEntity accountBankReceiveEntity,
			VietQRMMSCreateDTO dto,
			long time) {
		try {
			TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
			transactionEntity.setId(transcationUUID);
			transactionEntity.setBankAccount(accountBankReceiveEntity.getBankAccount());
			transactionEntity.setBankId(accountBankReceiveEntity.getId());
			transactionEntity.setContent(dto.getContent());
			transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
			transactionEntity.setTime(time);
			transactionEntity.setRefId("");
			transactionEntity.setType(0);
			transactionEntity.setStatus(0);
			transactionEntity.setTraceId("");
			transactionEntity.setTransType("C");
			transactionEntity.setReferenceNumber("");
			transactionEntity.setOrderId(dto.getOrderId());
			transactionEntity.setSign(dto.getSign());
			transactionEntity.setTimePaid(time);
			transactionEntity.setTerminalCode(dto.getTerminalCode());
			transactionEntity.setQrCode(qrCode);
			transactionEntity.setUserId(accountBankReceiveEntity.getUserId());
			transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
			transactionEntity.setTransStatus(0);
			transactionEntity.setUrlLink(dto.getUrlLink());
			transactionReceiveService.insertTransactionReceive(transactionEntity);
			LocalDateTime endTime = LocalDateTime.now();
			long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
			logger.info("insertNewTransaction - end generateVietQRMMS at: " + endTimeLong);
		} catch (Exception e) {
			logger.error("insertNewTransaction - generateVietQRMMS: ERROR: " + e.toString());
		}
	}

	// isFromBusinessSync: From customer with extra flow - customer-sync.
	@Async
	private void insertNewTransaction(UUID transcationUUID, String traceId, VietQRCreateDTO dto, VietQRDTO result,
			String orderId, String sign, boolean isFromMerchantSync) {
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
				// UUID transactionBranchUUID = UUID.randomUUID();
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
				transactionEntity.setTimePaid(0);
				transactionEntity.setTerminalCode(dto.getTerminalCode());
				transactionEntity.setQrCode("");
				transactionEntity.setUserId(accountBankEntity.getUserId());
				transactionEntity.setOrderId(orderId);
				transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
				transactionEntity.setTransStatus(0);
				transactionEntity.setUrlLink(dto.getUrlLink());
				if (dto.getTransType() != null) {
					transactionEntity.setTransType(dto.getTransType());
				} else {
					transactionEntity.setTransType("C");
				}
				transactionEntity.setReferenceNumber("");
				transactionEntity.setOrderId(orderId);
				transactionEntity.setSign(sign);
				//
				if (dto.getTransType() != null && dto.getTransType().trim().toUpperCase().equals("D")) {
					transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
					transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
					transactionEntity.setCustomerName(dto.getCustomerName());
				}
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				LocalDateTime afterInsertTransactionTime = LocalDateTime.now();
				long afterInsertTransactionTimeLong = afterInsertTransactionTime.toEpochSecond(ZoneOffset.UTC);
				logger.info("QR generate - after insertTransactionReceive at: " + afterInsertTransactionTimeLong);

				// insert transaction branch if existing branchId and businessId. Else just do
				// not map.
				// if (!dto.getBranchId().isEmpty() && !dto.getBusinessId().isEmpty()) {
				// TransactionReceiveBranchEntity transactionBranchEntity = new
				// TransactionReceiveBranchEntity();
				// transactionBranchEntity.setId(transactionBranchUUID.toString());
				// transactionBranchEntity.setTransactionReceiveId(transcationUUID.toString());
				// transactionBranchEntity.setBranchId(dto.getBranchId());
				// transactionBranchEntity.setBusinessId(dto.getBusinessId());
				// // System.out.println("branchId: " + dto.getBranchId());
				// // System.out.println("branchId after set: " +
				// // transactionBranchEntity.getBranchId());
				// transactionReceiveBranchService.insertTransactionReceiveBranch(transactionBranchEntity);
				// // // find userIds into business_member and branch_member
				// // List<String> userIds = branchMemberService
				// // .getUserIdsByBusinessIdAndBranchId(dto.getBusinessId(),
				// dto.getBranchId());
				// // // insert AND push notification to users belong to
				// // // admin business/ member of branch
				// // if (userIds != null && !userIds.isEmpty()) {
				// // for (String userId : userIds) {
				// // // insert notification
				// // UUID notificationUUID = UUID.randomUUID();
				// // NotificationEntity notiEntity = new NotificationEntity();
				// // BranchInformationEntity branchEntity = branchInformationService
				// // .getBranchById(dto.getBranchId());
				// // String message = NotificationUtil.getNotiDescNewTransPrefix()
				// // + branchEntity.getName()
				// // + NotificationUtil.getNotiDescNewTransSuffix1()
				// // + nf.format(Double.parseDouble(dto.getAmount()))
				// // + NotificationUtil
				// // .getNotiDescNewTransSuffix2();

				// // // push notification
				// // List<FcmTokenEntity> fcmTokens = new ArrayList<>();
				// // fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
				// // Map<String, String> data = new HashMap<>();
				// // data.put("notificationType",
				// NotificationUtil.getNotiTypeNewTransaction());
				// // data.put("notificationId", notificationUUID.toString());
				// // data.put("bankCode", result.getBankCode());
				// // data.put("bankName", result.getBankName());
				// // data.put("bankAccount", result.getBankAccount());
				// // data.put("userBankName", result.getUserBankName());
				// // data.put("amount", result.getAmount());
				// // data.put("content", result.getContent());
				// // data.put("qrCode", result.getQrCode());
				// // data.put("imgId", result.getImgId());
				// // firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
				// // NotificationUtil
				// // .getNotiTitleNewTransaction(),
				// // message);
				// // socketHandler.sendMessageToUser(userId, data);
				// // notiEntity.setId(notificationUUID.toString());
				// // notiEntity.setRead(false);
				// // notiEntity.setMessage(message);
				// // notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
				// // notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
				// // notiEntity.setUserId(userId);
				// // notiEntity.setData(transcationUUID.toString());
				// // notificationService.insertNotification(notiEntity);
				// // LocalDateTime afterInsertNotificationTransaction = LocalDateTime.now();
				// // long afterInsertNotificationTransactionLong =
				// // afterInsertNotificationTransaction
				// // .toEpochSecond(ZoneOffset.UTC);
				// // logger.info("QR generate - after InsertNotificationTransaction at: "
				// // + afterInsertNotificationTransactionLong);

				// // }
				// // }
				// }

				// insert notification
				UUID notificationUUID = UUID.randomUUID();
				NotificationEntity notiEntity = new NotificationEntity();
				String message = NotificationUtil.getNotiDescNewTransPrefix2()
						+ NotificationUtil.getNotiDescNewTransSuffix1()
						+ nf.format(Double.parseDouble(dto.getAmount()))
						+ NotificationUtil
								.getNotiDescNewTransSuffix2();

				if (isFromMerchantSync == false) {
					// push notification
					List<FcmTokenEntity> fcmTokens = new ArrayList<>();
					fcmTokens = fcmTokenService.getFcmTokensKiotByUserId(dto.getUserId());
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
			// }

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
				// UUID transactionBranchUUID = UUID.randomUUID();
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
				transactionEntity.setTimePaid(0);
				transactionEntity.setTerminalCode(dto.getTerminalCode());
				transactionEntity.setQrCode("");
				transactionEntity.setUserId(accountBankEntity.getUserId());
				transactionEntity.setNote("");
				transactionEntity.setTransStatus(0);
				transactionEntity.setUrlLink("");
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				// insert transaction branch if existing branchId and businessId. Else just do
				// not map.
				// find businessId and branchId
				// BankReceiveBranchEntity bankReceiveBranch = bankReceiveBranchService
				// .getBankReceiveBranchByBankId(dto.getBankId());
				// if (bankReceiveBranch != null) {
				// TransactionReceiveBranchEntity transactionBranchEntity = new
				// TransactionReceiveBranchEntity();
				// transactionBranchEntity.setId(transactionBranchUUID.toString());
				// transactionBranchEntity.setTransactionReceiveId(transcationUUID.toString());
				// transactionBranchEntity.setBranchId(bankReceiveBranch.getBranchId());
				// transactionBranchEntity.setBusinessId(bankReceiveBranch.getBusinessId());
				// transactionReceiveBranchService.insertTransactionReceiveBranch(transactionBranchEntity);
				// // find userIds into business_member and branch_member
				// List<String> userIds = branchMemberService
				// .getUserIdsByBusinessIdAndBranchId(bankReceiveBranch.getBusinessId(),
				// bankReceiveBranch.getBranchId());
				// // insert AND push notification to users belong to
				// // admin business/ member of branch
				// if (userIds != null && !userIds.isEmpty()) {
				// for (String userId : userIds) {
				// // insert notification
				// UUID notificationUUID = UUID.randomUUID();
				// NotificationEntity notiEntity = new NotificationEntity();
				// BranchInformationEntity branchEntity = branchInformationService
				// .getBranchById(bankReceiveBranch.getBranchId());
				// String message = NotificationUtil.getNotiDescNewTransPrefix()
				// + branchEntity.getName()
				// + NotificationUtil.getNotiDescNewTransSuffix1()
				// + nf.format(Double.parseDouble(dto.getAmount()))
				// + NotificationUtil
				// .getNotiDescNewTransSuffix2();

				// // push notification
				// List<FcmTokenEntity> fcmTokens = new ArrayList<>();
				// fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
				// Map<String, String> data = new HashMap<>();
				// data.put("notificationType", NotificationUtil.getNotiTypeNewTransaction());
				// data.put("notificationId", notificationUUID.toString());
				// data.put("bankCode", result.getBankCode());
				// data.put("bankName", result.getBankName());
				// data.put("bankAccount", result.getBankAccount());
				// data.put("userBankName", result.getUserBankName());
				// data.put("amount", result.getAmount());
				// data.put("content", result.getContent());
				// data.put("qrCode", result.getQrCode());
				// data.put("imgId", result.getImgId());
				// firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
				// NotificationUtil
				// .getNotiTitleNewTransaction(),
				// message);
				// socketHandler.sendMessageToUser(userId, data);
				// notiEntity.setId(notificationUUID.toString());
				// notiEntity.setRead(false);
				// notiEntity.setMessage(message);
				// notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
				// notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
				// notiEntity.setUserId(userId);
				// notiEntity.setData(transcationUUID.toString());
				// notificationService.insertNotification(notiEntity);
				// }

				// }
				// } else {
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
				fcmTokens = fcmTokenService.getFcmTokensKiotByUserId(dto.getUserId());
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
			// }
		} catch (Exception e) {
			logger.error("Error at reInsertNewTransaction: " + e.toString());
		}

	}

	@PostMapping("qr/generate")
	public ResponseEntity<VietQRDTO> generateQR(@RequestBody VietQRCreateDTO dto) {
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
				// return transactionId to upload bill image
				vietQRDTO.setTransactionId(transcationUUID.toString());
				// return terminal code of merchant
				vietQRDTO.setTerminalCode(dto.getTerminalCode());
				// return transactionRefId to present url QR Generated
				String refId = TransactionRefIdUtil.encryptTransactionId(transcationUUID.toString());
				String qrLink = EnvironmentUtil.getQRLink() + refId;
				vietQRDTO.setTransactionRefId(refId);
				vietQRDTO.setQrLink(qrLink);
				if (dto.getUrlLink() == null || dto.getUrlLink().trim().isEmpty()) {
					dto.setUrlLink("");
				}
				//
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
			if (dto.getOrderId() != null && !dto.getOrderId().isEmpty()) {
				insertNewTransaction(transcationUUID, traceId, dto, result, dto.getOrderId(), "", false);
			} else {
				insertNewTransaction(transcationUUID, traceId, dto, result, "", "", false);
			}
		}
	}

	@GetMapping("qr/list")
	public ResponseEntity<List<VietQRDTO>> generateStaticQRList(
			@RequestParam(value = "userId") String userId) {
		List<VietQRDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			List<String> bankIds = new ArrayList<>();
			// 1. getPersonalBankIdsByUserId
			List<String> personalBankIds = accountBankReceivePersonalService.getPersonalBankIdsByUserId(userId);
			if (personalBankIds != null && !personalBankIds.isEmpty()) {
				// add list bank
				for (String personalBankId : personalBankIds) {
					bankIds.add(personalBankId);
				}
			}
			// 2. getBankIdsByBusinessId
			List<String> branchIds = new ArrayList<>();
			List<String> branchIdsByUserIdBusiness = branchInformationService.getBranchIdsByUserIdBusiness(userId);
			List<String> branchIdsByUserIdBranch = branchMemberService.getBranchIdsByUserId(userId);
			if (branchIdsByUserIdBusiness != null && !branchIdsByUserIdBusiness.isEmpty()) {
				branchIds.addAll(branchIdsByUserIdBusiness);
			}
			if (branchIdsByUserIdBranch != null && !branchIdsByUserIdBranch.isEmpty()) {
				branchIds.addAll(branchIdsByUserIdBranch);
			}
			if (branchIds != null && !branchIds.isEmpty()) {
				for (String branchId : branchIds) {
					List<String> businessBankIds = bankReceiveBranchService.getBankIdsByBranchId(branchId);
					if (businessBankIds != null && !businessBankIds.isEmpty()) {
						for (String businessBankId : businessBankIds) {
							bankIds.add(businessBankId);
						}
					}
				}
			}
			// System.out.println("bankIds list: " + bankIds.size() + " - " +
			// bankIds.toString());
			if (bankIds != null && !bankIds.isEmpty()) {
				for (String bankId : bankIds) {
					AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(bankId);
					// System.out.println("bankId: " + bankId + " - bank account: " +
					// accountBankEntity.getBankAccount());
					if (accountBankEntity != null) {
						// get bank type information
						BankTypeEntity bankTypeEntity = bankTypeService
								.getBankTypeById(accountBankEntity.getBankTypeId());
						if (bankTypeEntity != null) {
							// get cai value
							String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
							if (caiValue != null && !caiValue.trim().isEmpty()) {
								// generate VietQRGenerateDTO
								VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
								vietQRGenerateDTO.setCaiValue(caiValue);
								vietQRGenerateDTO.setAmount("");
								vietQRGenerateDTO.setContent("");
								vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount()); // generate QR
								String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
								// generate VietQRDTO
								VietQRDTO vietQRDTO = new VietQRDTO();
								vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
								vietQRDTO.setBankName(bankTypeEntity.getBankName());
								vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
								vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
								vietQRDTO.setAmount("");
								vietQRDTO.setContent("");
								vietQRDTO.setQrCode(qr);
								vietQRDTO.setImgId(bankTypeEntity.getImgId());
								vietQRDTO.setTransactionId("");
								result.add(vietQRDTO);
							}

						}

					}
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			logger.error("generateStaticQRList: ERROR: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
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
						vietQRDTO.setTransactionId("");
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
				vietQRDTO.setTerminalCode(dto.getTerminalCode());
				String refId = "";
				String qrLink = "";
				if (dto.isNewTransaction() == true) {
					refId = TransactionRefIdUtil.encryptTransactionId(transcationUUID.toString());
					qrLink = EnvironmentUtil.getQRLink() + refId;
				} else {
					refId = TransactionRefIdUtil.encryptTransactionId(dto.getTransactionId());
					qrLink = EnvironmentUtil.getQRLink() + refId;
				}
				vietQRDTO.setTransactionRefId(refId);
				vietQRDTO.setQrLink(qrLink);
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
