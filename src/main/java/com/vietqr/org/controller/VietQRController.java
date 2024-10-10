package com.vietqr.org.controller;

import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.text.NumberFormat;

import javax.validation.Valid;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.service.mqtt.MqttMessagingService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import com.vietqr.org.util.bank.mb.MBVietQRUtil;
import org.apache.commons.lang3.ObjectUtils;
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
import com.vietqr.org.util.bank.mb.MBTokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

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
	MqttMessagingService mqttMessagingService;

	@Autowired
	AccountCustomerBankService accountCustomerBankService;

	@Autowired
	AccountBankReceiveService accountBankReceiveService;

	@Autowired
	CustomerVaService customerVaService;

	@Autowired
	TerminalBankReceiveService terminalBankReceiveService;

	@Autowired
	TerminalItemService terminalItemService;

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
	CustomerInvoiceService customerInvoiceService;

	@Autowired
	private TextToSpeechService textToSpeechService;

	@Autowired
	private TerminalService terminalService;

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
			AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
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
			AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
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
				vietQRDTO.setAmount(StringUtil.formatNumberAsString(amount));
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
		int qrType = 0;
		ResponseEntity<Object> response = null;
		if (Objects.nonNull(dto.getQrType())) {
			qrType = dto.getQrType();
		}
		try {
			if (StringUtil.containsOnlyDigits(dto.getBankCode())) {
				String bankCode = bankTypeService.getBankCodeByCaiValue(dto.getBankCode());
				if (!StringUtil.isNullOrEmpty(bankCode)) {
					dto.setBankCode(bankCode);
				}
			}
			switch (qrType) {
				case 0:
					response = generateDynamicQrCustomer(dto, token);
					result = response.getBody();
					httpStatus = response.getStatusCode();
					break;
				case 1:
					response = generateStaticQrCustomer(dto, token);
					result = response.getBody();
					httpStatus = response.getStatusCode();
					break;
				case 3:
					response = generateSemiDynamicQrCustomer(dto, token);
					result = response.getBody();
					httpStatus = response.getStatusCode();
					break;
				default:
					// Invalid QR type
					result = new ResponseMessageDTO("FAILED", "E46");
					httpStatus = HttpStatus.BAD_REQUEST;
					break;
			}
		} catch (Exception e) {
			logger.error("VietQRController: generateQRCustomer: ERROR: " + e.getMessage() + " at: "
					+ System.currentTimeMillis());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("qr/generate-bidv")
	public ResponseEntity<Object> generateQRBIDV(@RequestBody BIDVGenerateQrDTO dto) {
		Object result = null;
		HttpStatus httpStatus = null;
		ResponseEntity<Object> response = null;
		try {
			AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
			VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
			if ("0".equals(dto.getAmount())) {
				vietQRVaRequestDTO.setAmount("");
			} else {
				vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
			}
			vietQRVaRequestDTO.setBillId(dto.getBillId());
			vietQRVaRequestDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
			vietQRVaRequestDTO.setDescription(dto.getContent());
			String vaNumber = customerVaService.getVaNumberByBankId(dto.getBankId());
			ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankReceiveEntity.getCustomerId());
			VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
			vietQRGenerateDTO.setCaiValue("970418");
			vietQRGenerateDTO.setBankAccount(vaNumber);
			vietQRGenerateDTO.setAmount(dto.getAmount());
			vietQRGenerateDTO.setContent(dto.getContent());
			String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
			result = new ResponseMessageDTO(qr, generateVaInvoiceVietQR.getMessage());
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			logger.error("VietQRController: generateQRCustomer: ERROR: " + e.getMessage() + " at: "
					+ System.currentTimeMillis());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private String getUsernameFromToken(String token) {
		String result = "";
		if (token != null && !token.trim().isEmpty()) {
			String secretKey = "mySecretKey";
			String jwtToken = token.substring(7); // remove "Bearer " from the beginning
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
			String userId = (String) claims.get("user");
			if (userId != null) {
				result = new String(Base64.getDecoder().decode(userId));
			}
		}
		return result;
	}

	private ResponseEntity<Object> generateDynamicQrCustomer(VietQRCreateCustomerDTO dto, String token) {
		Object result = null;
		HttpStatus httpStatus = null;
		UUID transactionUUID = UUID.randomUUID();
		String serviceCode = !StringUtil.isNullOrEmpty(dto.getServiceCode()) ? dto.getServiceCode() : "";
		String subRawCode = StringUtil.getValueNullChecker(dto.getSubTerminalCode());
        ITerminalBankReceiveQR terminalBankReceiveEntity = null;
		if (!StringUtil.isNullOrEmpty(subRawCode) && !"3991031291095".equals(dto.getBankAccount())) {
			terminalBankReceiveEntity =
					terminalBankReceiveService.getTerminalBankReceiveQR(subRawCode);
			if (terminalBankReceiveEntity != null) {
				dto.setTerminalCode(terminalBankReceiveEntity.getTerminalCode());
			}
		}
		VietQRDTO vietQRDTO = null;
		if (dto.getReconciliation() == null || dto.getReconciliation() == true) {
			switch (dto.getBankCode().toUpperCase()) {
				case "MB":
					// for saving qr mms flow 2
					String qrMMS = "";
					// find bankAccount đã liên kết và mms = true và check transType = "C -> gọi
					// luồng 2
					String checkExistedMMSBank = accountBankReceiveService.checkMMSBankAccount(dto.getBankAccount());
					boolean checkMMS = false;
					String transType = "C";
					if (dto.getTransType() != null) {
						transType = dto.getTransType().trim();
					}
					if (checkExistedMMSBank != null && !checkExistedMMSBank.trim().isEmpty() && transType.equals("C")) {
						checkMMS = true;
					}
					if (!checkMMS) {
						// Luồng 1
						String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
						String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
//					if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
//						bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
//					} else {
//						bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getCustomerBankCode());
//					}
						vietQRDTO = new VietQRDTO();
						try {
							if (dto.getContent().length() <= 50) {
								// check if generate qr with transtype = D or C
								// if D => generate with customer information
								// if C => do normal
								// find bankTypeId by bankcode
								if (bankTypeId != null && !bankTypeId.isEmpty()) {
									// get cai value
									ICaiBankTypeQR caiBankTypeQR = bankTypeService.getCaiBankTypeById(bankTypeId);
									// find bank by bankAccount and banktypeId
									IAccountBankInfoQR accountBankEntity = null;
									if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
										accountBankEntity = accountBankReceiveService
												.getAccountBankQRByAccountAndId(dto.getBankAccount(), bankTypeId);
									} else {
										accountBankEntity = accountBankReceiveService
												.getAccountBankQRByAccountAndId(dto.getCustomerBankAccount(), bankTypeId);
									}
									if (accountBankEntity != null) {
										String content = dto.getContent();
										if (dto.getReconciliation() == null || dto.getReconciliation()) {
											content = traceId + " " + dto.getContent();
										}
										// generate VietQRGenerateDTO
										VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
										vietQRGenerateDTO.setCaiValue(caiBankTypeQR.getCaiValue());
										vietQRGenerateDTO.setAmount(dto.getAmount() + "");
										vietQRGenerateDTO.setContent(content);
										vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
										// generate VietQRDTO
										String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
										vietQRDTO.setBankCode(caiBankTypeQR.getBankCode());
										vietQRDTO.setBankName(caiBankTypeQR.getBankName());
										vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
										vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
										vietQRDTO.setAmount(dto.getAmount() + "");
										vietQRDTO.setContent(content);
										vietQRDTO.setQrCode(VietQRUtil.generateTransactionQR(vietQRGenerateDTO));
										vietQRDTO.setImgId(caiBankTypeQR.getImgId());
										vietQRDTO.setExisting(1);
										vietQRDTO.setTransactionId("");
										vietQRDTO.setTerminalCode(dto.getTerminalCode());
										vietQRDTO.setTransactionRefId(refId);
										vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + refId);
										vietQRDTO.setOrderId(dto.getOrderId());
										vietQRDTO.setAdditionalData(new ArrayList<>());
										if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
											vietQRDTO.setAdditionalData(dto.getAdditionalData());
										}
										vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
										vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));

										result = vietQRDTO;
										httpStatus = HttpStatus.OK;
									} else {
										String bankAccount = dto.getCustomerBankAccount();
										String userBankName = dto.getCustomerName().trim().toUpperCase();
										String content = dto.getContent();
										if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
											bankAccount = dto.getBankAccount();
											userBankName = dto.getUserBankName().trim().toUpperCase();
										}
										// generate VietQRGenerateDTO
										VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
										vietQRGenerateDTO.setCaiValue(caiBankTypeQR.getCaiValue());
										vietQRGenerateDTO.setAmount(dto.getAmount() + "");
										if (dto.getReconciliation() == null || dto.getReconciliation()) {
											content = traceId + " " + dto.getContent();
										}
										vietQRGenerateDTO.setContent(content);
										vietQRGenerateDTO.setBankAccount(bankAccount);
										// generate VietQRDTO
										vietQRDTO.setBankCode(caiBankTypeQR.getBankCode());
										vietQRDTO.setBankName(caiBankTypeQR.getBankName());
										vietQRDTO.setBankAccount(bankAccount);
										vietQRDTO.setUserBankName(userBankName);
										vietQRDTO.setAmount(dto.getAmount() + "");
										vietQRDTO.setContent(content);
										vietQRDTO.setQrCode(VietQRUtil.generateTransactionQR(vietQRGenerateDTO));
										vietQRDTO.setImgId(caiBankTypeQR.getImgId());
										vietQRDTO.setExisting(1);
										vietQRDTO.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
										vietQRDTO.setAdditionalData(new ArrayList<>());
										if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
											vietQRDTO.setAdditionalData(dto.getAdditionalData());
										}
										vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
										vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
										result = vietQRDTO;
										httpStatus = HttpStatus.OK;
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
							result = new ResponseMessageDTO("FAILED", "Unexpected Error");
							httpStatus = HttpStatus.BAD_REQUEST;
							return new ResponseEntity<>(result, httpStatus);
						} finally {
							// insert new transaction with orderId and sign
							if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
								bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
							}
							IAccountBankQR accountBankQR = accountBankReceiveService.getAccountBankQR(dto.getBankAccount(), bankTypeId);
							if (accountBankQR != null) {
								VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
								vietQRCreateDTO.setBankId(accountBankQR.getId());
								vietQRCreateDTO.setAmount(dto.getAmount() + "");
								vietQRCreateDTO.setContent(dto.getContent());
								vietQRCreateDTO.setUserId(accountBankQR.getUserId());
								vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
								vietQRCreateDTO.setServiceCode(serviceCode);

								if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
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
								VietQRDTO finalVietQRDTO = vietQRDTO;

								Thread thread1 = new Thread(()->
										insertNewTransaction(transactionUUID, traceId, vietQRCreateDTO, finalVietQRDTO, dto.getOrderId(),
												dto.getSign(), true)
								);
								thread1.start();
							}
							Thread thread2 = new Thread(()-> logUserInfo(token));
							thread2.start();
						}
					} else {
						// Luồng 2
						LocalDateTime requestLDT = LocalDateTime.now();
						logger.info("generateVietQRMMS: start generate at: " + requestLDT.toEpochSecond(ZoneOffset.UTC));
						String bankTypeMB = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
						AccountBankReceiveEntity accountBankEntity = null;
						String qrCode = "";
						try {
							// 1. Validate input (amount, content, bankCode) => E34 if Invalid input data
							if (checkRequestBodyFlow2(dto)) {
								// 2. Find terminal bank by bank_account_raw_number
								accountBankEntity = accountBankReceiveService
										.getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeMB);
								if (accountBankEntity != null) {
									String terminalId = terminalBankService
											.getTerminalBankQRByBankAccount(dto.getBankAccount());
									if (terminalId == null) {
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
												content = "VQR" + RandomCodeUtil.generateRandomUUID();
											}
											if (accountBankEntity.getBankAccount().equals("4144898989")) {
												content = !StringUtil.isNullOrEmpty(dto.getContent()) ?
														(dto.getContent() + " " + "Ghe Massage AeonBT")
														: "Ghe Massage AeonBT" ;
											}
											VietQRMMSRequestDTO requestDTO = new VietQRMMSRequestDTO();
											requestDTO.setToken(tokenBankDTO.getAccess_token());
											requestDTO.setTerminalId(terminalId);
											requestDTO.setAmount(dto.getAmount() + "");
											requestDTO.setContent(content);
											requestDTO.setOrderId(dto.getOrderId());
											ResponseMessageDTO responseMessageDTO = requestVietQRMMS(requestDTO);
											if (Objects.nonNull(responseMessageDTO)
													&& "SUCCESS".equals(responseMessageDTO.getStatus())) {
												qrCode = responseMessageDTO.getMessage();
												qrMMS = qrCode;
												// "MB Bank"
												String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
												if (bankTypeId != null && !bankTypeId.trim().isEmpty()) {
													vietQRDTO = new VietQRDTO();
													// get cai value
													IBankTypeQR bankTypeEntity = bankTypeService.getBankTypeQRById(bankTypeId);
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
													vietQRDTO.setExisting(1);
													vietQRDTO.setTransactionId("");
													vietQRDTO.setTerminalCode(dto.getTerminalCode());
													String refId = TransactionRefIdUtil
															.encryptTransactionId(transactionUUID.toString());
													vietQRDTO.setTransactionRefId(refId);
													vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + refId);
													vietQRDTO.setOrderId(dto.getOrderId());
													vietQRDTO.setAdditionalData(new ArrayList<>());
													if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
														vietQRDTO.setAdditionalData(dto.getAdditionalData());
													}
													vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
													vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
													result = vietQRDTO;
													httpStatus = HttpStatus.OK;
												} else {
													result = new ResponseMessageDTO("FAILED", "E24");
													httpStatus = HttpStatus.BAD_REQUEST;
												}
											} else {
												logger.error("generateVietQRMMS: ERROR: Invalid get QR Code");
												if (responseMessageDTO != null) {
													result = new ResponseMessageDTO("FAILED", responseMessageDTO.getMessage());
												} else {
													result = new ResponseMessageDTO("FAILED", "E05");
												}
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
						} catch (Exception e) {
							logger.error("generateVietQRMMS: ERROR: " + e.toString());
							result = new ResponseMessageDTO("FAILED", "E05");
							httpStatus = HttpStatus.BAD_REQUEST;
						} finally {
							// 4. Insert transaction_receive
							// (5. Insert notification)
							if (accountBankEntity != null && qrCode != null && !qrCode.isEmpty()) {
								String content = "";
								if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
									content = dto.getContent();
								} else {
									content = "VQR" + RandomCodeUtil.generateRandomUUID();
								}

								if (accountBankEntity.getBankAccount().equals("4144898989")) {
									content = !StringUtil.isNullOrEmpty(dto.getContent()) ?
											(dto.getContent() + " " + "Ghe Massage AeonBT")
											: "Ghe Massage AeonBT" ;
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
								vietQRMMSCreateDTO.setServiceCode(serviceCode);
								vietQRMMSCreateDTO.setSubTerminalCode(dto.getSubTerminalCode());
								if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
									vietQRMMSCreateDTO.setUrlLink(dto.getUrlLink());
								} else {
									vietQRMMSCreateDTO.setUrlLink("");
								}
								String finalQrMMS = qrMMS;
								AccountBankReceiveEntity finalAccountBankEntity = accountBankEntity;
								Thread thread3 = new Thread(()->
										insertNewTransactionFlow2(finalQrMMS, transactionUUID.toString(), finalAccountBankEntity, vietQRMMSCreateDTO, time)
								);
								thread3.start();
							}
						}
					}
					break;
				case "BIDV":
//					String traceBIDVId = "VQR" + RandomCodeUtil.generateRandomUUID();
					String qr = "";
					String billId = "";
					String content = dto.getContent();
					BankCaiTypeDTO bankCaiTypeDTOBIDV = null;
					AccountBankGenerateBIDVDTO accountBankBIDV = null;
					if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
						bankCaiTypeDTOBIDV = bankTypeService.getBankCaiByBankCode(dto.getBankCode());
					} else {
						bankCaiTypeDTOBIDV = bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
					}
					vietQRDTO = new VietQRDTO();
					try {
						if (dto.getContent().length() <= 50) {
							// check if generate qr with transtype = D or C
							// if D => generate with customer information
							// if C => do normal
							// find bankTypeId by bankcode
							if (Objects.nonNull(bankCaiTypeDTOBIDV)) {
								// find bank by bankAccount and banktypeId

								if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
									accountBankBIDV = accountBankReceiveService
											.getAccountBankBIDVByBankAccountAndBankTypeId(dto.getBankAccount(),
													bankCaiTypeDTOBIDV.getId());
								} else {
									accountBankBIDV = accountBankReceiveService
											.getAccountBankBIDVByBankAccountAndBankTypeId(dto.getCustomerBankAccount(),
													bankCaiTypeDTOBIDV.getId());
								}
								if (Objects.nonNull(accountBankBIDV)) {
									// get cai value
									billId = getRandomBillId();
									content = billId + " " + StringUtil.getValueNullChecker(dto.getContent());
									// generate qr BIDV
									VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
									vietQRCreateDTO.setBankId(accountBankBIDV.getId());
									vietQRCreateDTO.setAmount(dto.getAmount() + "");
									vietQRCreateDTO.setContent(content);
									vietQRCreateDTO.setUserId(accountBankBIDV.getUserId());
									vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
									//
									if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
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
									ResponseMessageDTO responseMessageDTO =
											insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankBIDV, billId);

									// insert success transaction_receive
									if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
										VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
										if ("0".equals(dto.getAmount())) {
											vietQRVaRequestDTO.setAmount("");
										} else {
											vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
										}
										vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
										vietQRVaRequestDTO.setBillId(billId);
										vietQRVaRequestDTO.setUserBankName(accountBankBIDV.getBankAccountName());
										vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(content));

										ResponseMessageDTO generateVaInvoiceVietQR = new ResponseMessageDTO("SUCCESS", "");
										if (!EnvironmentUtil.isProduction()) {
											String bankAccountRequest= "";
											if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
												bankAccountRequest = dto.getBankAccount();
											} else {
												bankAccountRequest = dto.getCustomerBankAccount();
											}
											VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
											vietQRGenerateDTO.setCaiValue(bankCaiTypeDTOBIDV.getCaiValue());
											vietQRGenerateDTO.setAmount(dto.getAmount() + "");
											vietQRGenerateDTO.setContent(content);
											vietQRGenerateDTO.setBankAccount(bankAccountRequest);
											qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
											generateVaInvoiceVietQR = new ResponseMessageDTO("SUCCESS", qr);
										} else {
											if ("0".equals(dto.getAmount())) {
												vietQRVaRequestDTO.setAmount("");
											} else {
												vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
											}
											generateVaInvoiceVietQR = CustomerVaUtil.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankBIDV.getCustomerId());
										}
										if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
											qr = generateVaInvoiceVietQR.getMessage();

											// generate VietQRDTO
											vietQRDTO.setBankCode(bankCaiTypeDTOBIDV.getBankCode());
											vietQRDTO.setBankName(bankCaiTypeDTOBIDV.getBankName());
											vietQRDTO.setBankAccount(accountBankBIDV.getBankAccount());
											vietQRDTO.setUserBankName(accountBankBIDV.getBankAccountName().toUpperCase());
											vietQRDTO.setAmount(dto.getAmount() + "");
											vietQRDTO.setContent(content);
											vietQRDTO.setQrCode(qr);
											vietQRDTO.setImgId(bankCaiTypeDTOBIDV.getImgId());
											vietQRDTO.setExisting(1);
											vietQRDTO.setTransactionId("");
											vietQRDTO.setTerminalCode(dto.getTerminalCode());
											String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
											String qrLink = EnvironmentUtil.getQRLink() + refId;
											vietQRDTO.setTransactionRefId(refId);
											vietQRDTO.setQrLink(qrLink);
											vietQRDTO.setOrderId(dto.getOrderId());
											vietQRDTO.setAdditionalData(new ArrayList<>());
											if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
												vietQRDTO.setAdditionalData(dto.getAdditionalData());
											}
											vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
											vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
											//
											result = vietQRDTO;
											httpStatus = HttpStatus.OK;
										} else {
											result = new ResponseMessageDTO("FAILED", "E05");
											httpStatus = HttpStatus.BAD_REQUEST;
										}
									} else {
										httpStatus = HttpStatus.BAD_REQUEST;
									}
								} else {
									String bankAccount = "";
									String userBankName = "";
									content = "";
									if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
										bankAccount = dto.getBankAccount();
										userBankName = dto.getUserBankName().trim().toUpperCase();
									} else {
										bankAccount = dto.getCustomerBankAccount();
										userBankName = dto.getCustomerName().trim().toUpperCase();
									}
									// generate VietQRGenerateDTO
									VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
									vietQRGenerateDTO.setCaiValue(bankCaiTypeDTOBIDV.getCaiValue());
									vietQRGenerateDTO.setAmount(dto.getAmount() + "");
									content = billId + " " + dto.getContent();
									vietQRGenerateDTO.setContent(content);
									vietQRGenerateDTO.setBankAccount(bankAccount);
									qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
									//
									// generate VietQRDTO
									vietQRDTO.setBankCode(bankCaiTypeDTOBIDV.getBankCode());
									vietQRDTO.setBankName(bankCaiTypeDTOBIDV.getBankName());
									vietQRDTO.setBankAccount(bankAccount);
									vietQRDTO.setUserBankName(userBankName);
									vietQRDTO.setAmount(dto.getAmount() + "");
									vietQRDTO.setContent(content);
									vietQRDTO.setQrCode(qr);
									vietQRDTO.setImgId(bankCaiTypeDTOBIDV.getImgId());
									vietQRDTO.setExisting(0);
									vietQRDTO.setOrderId(dto.getOrderId());
									vietQRDTO.setAdditionalData(new ArrayList<>());
									if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
										vietQRDTO.setAdditionalData(dto.getAdditionalData());
									}
									vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
									vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
									result = vietQRDTO;
									httpStatus = HttpStatus.OK;
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
					} catch (Exception e) {
						logger.error(e.toString());
						System.out.println(e.toString());
						result = new ResponseMessageDTO("FAILED", "Unexpected Error");
						httpStatus = HttpStatus.BAD_REQUEST;
					} finally {
						if (Objects.nonNull(accountBankBIDV) && !StringUtil.isNullOrEmpty(qr)) {
							VietQRBIDVCreateDTO dto1 = new VietQRBIDVCreateDTO();
							dto1.setContent(content);
							dto1.setAmount(dto.getAmount() + "");
							dto1.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
							dto1.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
							dto1.setNote(StringUtil.getValueNullChecker(dto.getNote()));
							dto1.setUrlLink(StringUtil.getValueNullChecker(dto.getUrlLink()));
							dto1.setTransType(StringUtil.getValueNullChecker(dto.getTransType()));
							dto1.setSign(StringUtil.getValueNullChecker(dto.getSign()));
							dto1.setBillId(billId);
							dto1.setCustomerBankAccount(StringUtil.getValueNullChecker(dto.getCustomerBankAccount()));
							dto1.setCustomerBankCode(StringUtil.getValueNullChecker(dto.getCustomerBankCode()));
							dto1.setCustomerName(StringUtil.getValueNullChecker(dto.getCustomerName()));
							dto1.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
							dto1.setQr(qr);
							AccountBankGenerateBIDVDTO finalAccountBankBIDV = accountBankBIDV;
							Thread thread = new Thread(() -> {
								insertNewTransactionBIDV(transactionUUID, dto1, false, "",
										finalAccountBankBIDV);
							});
							thread.start();
						}
					}
					break;
				default:
					String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
					BankCaiTypeDTO bankCaiTypeDTO = null;
					if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
						bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getBankCode());
					} else {
						bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
					}
					try {
						String bankAccount = "";
						String userBankName = "";
						content = "";
						if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
							bankAccount = dto.getBankAccount();
							userBankName = dto.getUserBankName().trim().toUpperCase();
						} else {
							bankAccount = dto.getCustomerBankAccount();
							userBankName = dto.getCustomerName().trim().toUpperCase();
						}
						if (dto.getContent().length() <= 50) {
							// generate VietQRGenerateDTO
							VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
							vietQRGenerateDTO.setCaiValue(bankCaiTypeDTO.getCaiValue());
							vietQRGenerateDTO.setAmount(dto.getAmount() + "");
							if (dto.getReconciliation() == null || dto.getReconciliation()) {
								content = traceId + " " + dto.getContent();
							} else {
								content = dto.getContent();
							}
							vietQRGenerateDTO.setContent(content);
							vietQRGenerateDTO.setBankAccount(bankAccount);
							qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
							//
							vietQRDTO = new VietQRDTO();
							// generate VietQRDTO
							vietQRDTO.setBankCode(bankCaiTypeDTO.getBankCode());
							vietQRDTO.setBankName(bankCaiTypeDTO.getBankName());
							vietQRDTO.setBankAccount(bankAccount);
							vietQRDTO.setUserBankName(userBankName);
							vietQRDTO.setAmount(dto.getAmount() + "");
							vietQRDTO.setContent(content);
							vietQRDTO.setQrCode(qr);
							vietQRDTO.setImgId(bankCaiTypeDTO.getImgId());
							vietQRDTO.setExisting(0);
							vietQRDTO.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
							vietQRDTO.setAdditionalData(new ArrayList<>());
							if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
								vietQRDTO.setAdditionalData(dto.getAdditionalData());
							}
							vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
							vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
							result = vietQRDTO;
							httpStatus = HttpStatus.OK;
						} else {
							result = new ResponseMessageDTO("FAILED", "E26");
							httpStatus = HttpStatus.BAD_REQUEST;
						}
					} catch (Exception e) {
						httpStatus = HttpStatus.BAD_REQUEST;
						logger.error("VietQRController: ERROR: generateQRCustomer: " + e.getMessage() + " at: " + System.currentTimeMillis());
					}
					break;
			}
		} else {
			String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
			BankCaiTypeDTO bankCaiTypeDTO = null;
			if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
				bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getBankCode());
			} else {
				bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
			}
			try {
				String bankAccount = "";
				String userBankName = "";
				String content = "";
				if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
					bankAccount = dto.getBankAccount();
					userBankName = dto.getUserBankName().trim().toUpperCase();
				} else {
					bankAccount = dto.getCustomerBankAccount();
					userBankName = dto.getCustomerName().trim().toUpperCase();
				}
				if (dto.getContent().length() <= 50) {
					// generate VietQRGenerateDTO
					VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
					vietQRGenerateDTO.setCaiValue(bankCaiTypeDTO.getCaiValue());
					vietQRGenerateDTO.setAmount(dto.getAmount() + "");
					if (dto.getReconciliation() == null || dto.getReconciliation()) {
						content = traceId + " " + dto.getContent();
					} else {
						content = dto.getContent();
					}
					vietQRGenerateDTO.setContent(content);
					vietQRGenerateDTO.setBankAccount(bankAccount);
					String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
					//
					vietQRDTO = new VietQRDTO();
					// generate VietQRDTO
					vietQRDTO.setBankCode(bankCaiTypeDTO.getBankCode());
					vietQRDTO.setBankName(bankCaiTypeDTO.getBankName());
					vietQRDTO.setBankAccount(bankAccount);
					vietQRDTO.setUserBankName(userBankName);
					vietQRDTO.setAmount(dto.getAmount() + "");
					vietQRDTO.setContent(content);
					vietQRDTO.setQrCode(qr);
					vietQRDTO.setImgId(bankCaiTypeDTO.getImgId());
					vietQRDTO.setExisting(0);
					vietQRDTO.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
					vietQRDTO.setAdditionalData(new ArrayList<>());
					if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
						vietQRDTO.setAdditionalData(dto.getAdditionalData());
					}
					vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
					vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
					result = vietQRDTO;
					httpStatus = HttpStatus.OK;
				} else {
					result = new ResponseMessageDTO("FAILED", "E26");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} catch (Exception e) {
				httpStatus = HttpStatus.BAD_REQUEST;
				logger.error("VietQRController: ERROR: generateQRCustomer: " + e.getMessage() + " at: " + System.currentTimeMillis());
			}
		}

		if (Objects.nonNull(terminalBankReceiveEntity)) {
			sendMessageDynamicQrToQrBox("",
					terminalBankReceiveEntity.getRawTerminalCode() != null ?
							terminalBankReceiveEntity.getRawTerminalCode() : "",
					vietQRDTO, "", vietQRDTO.getQrCode(), dto.getNote()
			);
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private ResponseEntity<Object> generateStaticQrCustomer(VietQRCreateCustomerDTO dto, String token) {
		Object result = null;
		HttpStatus httpStatus = null;
		IBankTypeQR bankTypeEntity = null;
		String content = "";
		String qr = "";
		try {
			if (dto.getContent() == null) {
				dto.setContent("");
			}
			if (dto.getContent().length() <= 20) {
				if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
					bankTypeEntity = bankTypeService.getBankTypeQRByCode(dto.getBankCode());
				} else {
					bankTypeEntity = bankTypeService.getBankTypeQRByCode(dto.getCustomerBankCode());
				}
				switch (dto.getBankCode()) {
					case "MB":
						IAccountBankReceiveMMS accountBankReceiveEntity =
								accountBankReceiveService
										.getAccountBankReceiveQRByBankAccountAndBankCode(dto.getBankAccount(),
												dto.getBankCode());

						if (Objects.nonNull(accountBankReceiveEntity)) {
							TerminalBankSyncDTO terminalBankSyncDTO = terminalBankReceiveService
									.getTerminalBankReceive(dto.getTerminalCode(), dto.getBankAccount(),
											dto.getBankCode());
							if (Objects.nonNull(terminalBankSyncDTO)) {
								if (StringUtil.isNullOrEmpty(terminalBankSyncDTO.getData1())
										&& StringUtil.isNullOrEmpty(terminalBankSyncDTO.getData2())) {
									if (accountBankReceiveEntity.getMmsActive()) {
										String terminalId = terminalBankService.getTerminalBankQRByBankAccount(accountBankReceiveEntity.getBankAccount());
										if (terminalId != null) {
											// luồng uu tien
											if (StringUtil.isNullOrEmpty(dto.getContent())) {
												content = terminalBankSyncDTO.getRawTerminalCode();
											} else {
												content = dto.getContent();
											}
											qr = MBVietQRUtil.generateStaticVietQRMMS(
													new VietQRStaticMMSRequestDTO(
															MBTokenUtil.getMBBankToken().getAccess_token(),
															terminalId, content
													)
											);
											String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
											terminalBankReceiveService.updateQrCodeTerminalSync("", qr, traceTransfer,
													terminalBankSyncDTO.getTerminalBankReceiveId());
										} else {
											System.out.println("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
										}
									} else {
										// luồng thuong
										if (StringUtil.isNullOrEmpty(dto.getContent())) {
											content = "SQR" + terminalBankSyncDTO.getTerminalCode();
										} else {
											content = "SQR" + terminalBankSyncDTO.getTerminalCode() + " " + dto.getContent();
										}
										String bankAccount = accountBankReceiveEntity.getBankAccount();
										String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
										VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", content, bankAccount);
										qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
										terminalBankReceiveService.updateQrCodeTerminalSync(qr, "", "",
												terminalBankSyncDTO.getTerminalBankReceiveId());
									}
									VietQRDTO vietQRDTO = new VietQRDTO();
									vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
									vietQRDTO.setBankName(bankTypeEntity.getBankName());
									vietQRDTO.setBankAccount(accountBankReceiveEntity.getBankAccount());
									vietQRDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
									vietQRDTO.setAmount(StringUtil.getValueNullChecker(dto.getAmount()) + "");
									vietQRDTO.setContent(content);
									vietQRDTO.setQrCode(qr);
									vietQRDTO.setImgId(bankTypeEntity.getImgId());
									vietQRDTO.setExisting(1);
									vietQRDTO.setTransactionId("");
									vietQRDTO.setTerminalCode(dto.getTerminalCode());
									vietQRDTO.setTransactionRefId("");
									vietQRDTO.setQrLink("");
									result = vietQRDTO;
									httpStatus = HttpStatus.OK;
								} else {
									String terminalCode = getRandomUniqueCodeInTerminalCode();
									TerminalBankReceiveEntity terminalBankReceiveEntity =
											new TerminalBankReceiveEntity();
									terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
									terminalBankReceiveEntity.setRawTerminalCode(terminalBankSyncDTO.getRawTerminalCode());
									terminalBankReceiveEntity.setTerminalCode(terminalCode);
									terminalBankReceiveEntity.setSubTerminalAddress("");
									terminalBankReceiveEntity.setBankId(terminalBankSyncDTO.getBankId());
									terminalBankReceiveEntity.setTerminalId(terminalBankSyncDTO.getTerminalId());
									terminalBankReceiveEntity.setTypeOfQR(1);
									if (accountBankReceiveEntity.getMmsActive()) {
										String terminalId =
												terminalBankService.getTerminalBankQRByBankAccount(accountBankReceiveEntity.getBankAccount());
										if (terminalId != null) {
											// luồng uu tien
											if (StringUtil.isNullOrEmpty(dto.getContent())) {
												content = terminalBankSyncDTO.getRawTerminalCode();
											} else {
												content = dto.getContent();
											}
											qr = MBVietQRUtil.generateStaticVietQRMMS(
													new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
															terminalId, content));
											String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
											terminalBankReceiveEntity.setData2(qr);
											terminalBankReceiveEntity.setData1("");
											terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
										} else {
											System.out.println("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
										}
									} else {
										// luồng thuong
										if (StringUtil.isNullOrEmpty(dto.getContent())) {
											content = "SQR" + terminalCode;
										} else {
											content = "SQR" + terminalCode + " " + dto.getContent();
										}
										String bankAccount = accountBankReceiveEntity.getBankAccount();
										String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
										VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", content, bankAccount);
										qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);

										terminalBankReceiveEntity.setData2("");
										terminalBankReceiveEntity.setData1(qr);
										terminalBankReceiveEntity.setTraceTransfer("");
									}

									terminalBankReceiveService.insert(terminalBankReceiveEntity);
									VietQRDTO vietQRDTO = new VietQRDTO();
									vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
									vietQRDTO.setBankName(bankTypeEntity.getBankName());
									vietQRDTO.setBankAccount(accountBankReceiveEntity.getBankAccount());
									vietQRDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
									vietQRDTO.setAmount(StringUtil.getValueNullChecker(dto.getAmount()) + "");
									vietQRDTO.setContent(content);
									vietQRDTO.setQrCode(qr);
									vietQRDTO.setImgId(bankTypeEntity.getImgId());
									vietQRDTO.setExisting(1);
									vietQRDTO.setTransactionId("");
									vietQRDTO.setTerminalCode(dto.getTerminalCode());
									vietQRDTO.setSubTerminalCode("");
									vietQRDTO.setServiceCode("");
									vietQRDTO.setOrderId("");
									vietQRDTO.setAdditionalData(new ArrayList<>());
									if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
										vietQRDTO.setAdditionalData(dto.getAdditionalData());
									}
									String qrLink = "";
									vietQRDTO.setTransactionRefId("");
									vietQRDTO.setQrLink(qrLink);
									result = vietQRDTO;
									httpStatus = HttpStatus.OK;
								}
							} else {
								result = new ResponseMessageDTO("FAILED", "E152");
								httpStatus = HttpStatus.BAD_REQUEST;
							}
						} else {
							result = new ResponseMessageDTO("FAILED", "E25");
							httpStatus = HttpStatus.BAD_REQUEST;
						}
						break;
					case "BIDV":
						// TODO: implement BIDV static qr
					default:
						result = new ResponseMessageDTO("FAILED", "E151");
						httpStatus = HttpStatus.BAD_REQUEST;
						break;
				}
			} else {
				result = new ResponseMessageDTO("FAILED", "E26");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			logger.error("VietQRController: ERROR: generateQRCustomer: " + e.getMessage() + " at: " + System.currentTimeMillis());
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private ResponseEntity<Object> generateSemiDynamicQrCustomer(VietQRCreateCustomerDTO dto, String token) {
		Object result = null;
		HttpStatus httpStatus = null;
		IBankTypeQR bankTypeEntity = null;
		IAccountBankReceiveQR accountBankReceiveEntity = null;
		String qr = "";
		String traceTransfer = "";
		String serviceCode = "";
		try {
			String bankTypeId = "";
			String content = dto.getContent();
			if (checkRequestBodySemiDynamicFlow2(dto)) {
				switch (dto.getBankCode().toUpperCase()) {
					case "MB":
						// for saving qr mms flow 2
						// find bankAccount đã liên kết và mms = true và check transType = "C -> gọi
						// luồng 2
						String checkExistedMMSBank = accountBankReceiveService.checkMMSBankAccount(dto.getBankAccount());
						boolean checkMMS = false;
						if (StringUtil.isNullOrEmpty(dto.getContent())) {
							content = dto.getServiceCode();
						}
						serviceCode = getTerminalCode();
						String transType = "C";
						if (dto.getTransType() != null) {
							transType = dto.getTransType().trim();
						}
						if (checkExistedMMSBank != null && !checkExistedMMSBank.trim().isEmpty() && transType.equals("C")) {
							checkMMS = true;
						}
						bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
						bankTypeEntity = bankTypeService.getBankTypeQRById(bankTypeId);
						if (checkMMS) {
							accountBankReceiveEntity = accountBankReceiveService
									.getAccountBankReceiveQRByAccountAndId(
											dto.getBankAccount(),
											bankTypeId
									);
							if (Objects.nonNull(accountBankReceiveEntity)) {
								String terminalId = terminalBankService
										.getTerminalBankQRByBankAccount(dto.getBankAccount());
								if (Objects.nonNull(terminalId)) {
									String MBToken = MBTokenUtil.getMBBankToken().getAccess_token();
									VietQRMMSRequestDTO vietQRMMSRequestDTO = new VietQRMMSRequestDTO();
									vietQRMMSRequestDTO.setAmount(dto.getAmount() + "");
									vietQRMMSRequestDTO.setContent(content);
									vietQRMMSRequestDTO.setOrderId(serviceCode);
									vietQRMMSRequestDTO.setTerminalId(terminalId);
									vietQRMMSRequestDTO.setToken(MBToken);
									qr = generateSemiDynamicQrMMS(vietQRMMSRequestDTO);
									if (!StringUtil.isNullOrEmpty(qr)) {
										traceTransfer = MBVietQRUtil.getTraceTransfer(qr);

										VietQRDTO vietQRDTO = new VietQRDTO();
										vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
										vietQRDTO.setBankName(bankTypeEntity.getBankName());
										vietQRDTO.setBankAccount(accountBankReceiveEntity.getBankAccount());
										vietQRDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
										vietQRDTO.setAmount(StringUtil.getValueNullChecker(dto.getAmount()) + "");
										vietQRDTO.setContent(content);
										vietQRDTO.setQrCode(qr);
										vietQRDTO.setImgId(bankTypeEntity.getImgId());
										vietQRDTO.setExisting(1);
										vietQRDTO.setTransactionId("");
										vietQRDTO.setTerminalCode(dto.getTerminalCode());
										vietQRDTO.setTransactionRefId("");
										vietQRDTO.setQrLink("");
										vietQRDTO.setAdditionalData(new ArrayList<>());
										vietQRDTO.setSubTerminalCode("");
										vietQRDTO.setOrderId("");
										vietQRDTO.setServiceCode(dto.getServiceCode());

										result = vietQRDTO;
										httpStatus = HttpStatus.OK;
									} else {
										logger.error("generateVietQRMMS: ERROR: generateSemiDynamicQrCustomer FAILED at: "
												+ DateTimeUtil.getCurrentDateTimeUTC());
										result = new ResponseMessageDTO("FAILED", "E05");
										httpStatus = HttpStatus.BAD_REQUEST;
									}
								} else {
									// 3.A. If not found => E35 (terminal is not existed)
									logger.error("generateVietQRMMS: ERROR: Bank account is not existed.");
									result = new ResponseMessageDTO("FAILED", "E35");
									httpStatus = HttpStatus.BAD_REQUEST;
								}
							} else {
								logger.error("generateVietQRMMS: ERROR: bankAccount is not existed in system");
								result = new ResponseMessageDTO("FAILED", "E36");
								httpStatus = HttpStatus.BAD_REQUEST;
							}
						} else {
							String qrCodeContent = "SMQR" + serviceCode + " " + StringUtil.getValueNullChecker(dto.getContent());
							String bankAccount = accountBankReceiveEntity.getBankAccount();
							String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
							VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, dto.getAmount() + "", qrCodeContent, bankAccount);
							qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);

							if (!StringUtil.isNullOrEmpty(qr)) {
								traceTransfer = "";

								VietQRDTO vietQRDTO = new VietQRDTO();
								vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
								vietQRDTO.setBankName(bankTypeEntity.getBankName());
								vietQRDTO.setBankAccount(accountBankReceiveEntity.getBankAccount());
								vietQRDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
								vietQRDTO.setAmount(StringUtil.getValueNullChecker(dto.getAmount()) + "");
								vietQRDTO.setContent(content);
								vietQRDTO.setQrCode(qr);
								vietQRDTO.setImgId(bankTypeEntity.getImgId());
								vietQRDTO.setExisting(1);
								vietQRDTO.setTransactionId("");
								vietQRDTO.setTerminalCode(dto.getTerminalCode());
								vietQRDTO.setTransactionRefId("");
								vietQRDTO.setQrLink("");
								vietQRDTO.setAdditionalData(new ArrayList<>());
								vietQRDTO.setSubTerminalCode("");
								vietQRDTO.setOrderId("");
								vietQRDTO.setServiceCode(dto.getServiceCode());
								result = vietQRDTO;
								httpStatus = HttpStatus.OK;
							} else {
								logger.error("generateVietQRMMS: ERROR: generateSemiDynamicQrCustomer FAILED at: "
										+ DateTimeUtil.getCurrentDateTimeUTC());
								result = new ResponseMessageDTO("FAILED", "E05");
								httpStatus = HttpStatus.BAD_REQUEST;
							}
							result = new ResponseMessageDTO("FAILED", "E46");
							httpStatus = HttpStatus.BAD_REQUEST;
						}
						break;
					case "BIDV":
						qr = "";
						serviceCode = getTerminalCode();
						String billId = RandomCodeUtil.getRandomBillId();
						VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
						vietQRCreateDTO.setBankId(accountBankReceiveEntity.getId());
						vietQRCreateDTO.setAmount(dto.getAmount() + "");
						vietQRCreateDTO.setContent(billId);
						vietQRCreateDTO.setUserId(accountBankReceiveEntity.getUserId());
						vietQRCreateDTO.setServiceCode(serviceCode);
						vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());

						ResponseMessageDTO responseMessageDTO =
								insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankReceiveEntity, billId);
						if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
							VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
							vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
							vietQRVaRequestDTO.setBillId(billId);
							vietQRVaRequestDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
							vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(billId)
									+ " " + StringUtil.getValueNullChecker(dto.getContent()));
							ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil
									.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankReceiveEntity.getCustomerId());
							if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
								qr = generateVaInvoiceVietQR.getMessage();
								traceTransfer = "";

								if (!StringUtil.isNullOrEmpty(qr)) {
									traceTransfer = "";

									VietQRDTO vietQRDTO = new VietQRDTO();
									vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
									vietQRDTO.setBankName(bankTypeEntity.getBankName());
									vietQRDTO.setBankAccount(accountBankReceiveEntity.getBankAccount());
									vietQRDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
									vietQRDTO.setAmount(StringUtil.getValueNullChecker(dto.getAmount()) + "");
									vietQRDTO.setContent(content);
									vietQRDTO.setQrCode(qr);
									vietQRDTO.setImgId(bankTypeEntity.getImgId());
									vietQRDTO.setExisting(1);
									vietQRDTO.setTransactionId("");
									vietQRDTO.setTerminalCode(dto.getTerminalCode());
									vietQRDTO.setTransactionRefId("");
									vietQRDTO.setQrLink("");
									vietQRDTO.setAdditionalData(new ArrayList<>());
									vietQRDTO.setSubTerminalCode("");
									vietQRDTO.setOrderId("");
									vietQRDTO.setServiceCode(dto.getServiceCode());
									result = vietQRDTO;
									httpStatus = HttpStatus.OK;
								} else {
									logger.error("generateVietQRMMS: ERROR: generateSemiDynamicQrCustomer FAILED at: "
											+ DateTimeUtil.getCurrentDateTimeUTC());
									result = new ResponseMessageDTO("FAILED", "E05");
									httpStatus = HttpStatus.BAD_REQUEST;
								}
							} else {
								logger.error("generateVietQRMMS: ERROR: generateSemiDynamicQrCustomer FAILED at: "
										+ DateTimeUtil.getCurrentDateTimeUTC());
								result = new ResponseMessageDTO("FAILED", "E05");
								httpStatus = HttpStatus.BAD_REQUEST;
							}
						} else {
							logger.error("generateVietQRMMS: ERROR: generateSemiDynamicQrCustomer FAILED at: "
									+ DateTimeUtil.getCurrentDateTimeUTC());
							result = new ResponseMessageDTO("FAILED", "E05");
							httpStatus = HttpStatus.BAD_REQUEST;
						}

					default:
						result = new ResponseMessageDTO("FAILED", "E46");
						httpStatus = HttpStatus.BAD_REQUEST;
						break;
				}
			} else {
				result = new ResponseMessageDTO("FAILED", "E46");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error("generateSemiDynamicQrCustomer: ERROR: " + e.getMessage()
					+ " at: " + System.currentTimeMillis());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		} finally {
			if (ObjectUtils.allNotNull(qr, accountBankReceiveEntity, traceTransfer, result)) {
				if (result instanceof VietQRDTO) {
					String finalQr = qr;
					IAccountBankReceiveQR finalAccountBankReceiveEntity = accountBankReceiveEntity;
					String finalTraceTransfer = traceTransfer;
					VietQRDTO finalResult = (VietQRDTO) result;
					String finalServiceCode = serviceCode;
					Thread thread = new Thread(() -> {
						String terminalItemId = terminalItemService
								.existsByIdServiceCodeTerminalCode(finalAccountBankReceiveEntity.getId(),
										dto.getServiceCode(), dto.getTerminalCode()
										);
						if (Objects.nonNull(terminalItemId)) {
							terminalItemService.removeById(terminalItemId);
						}
						TerminalItemEntity entity = new TerminalItemEntity();
						entity.setId(UUID.randomUUID().toString());
						if (StringUtil.isNullOrEmpty(finalTraceTransfer)) {
							entity.setData1(finalQr);
							entity.setTraceTransfer("");
						} else {
							entity.setData2(finalQr);
							entity.setTraceTransfer(finalTraceTransfer);
						}
						entity.setServiceCode(finalServiceCode);
						entity.setRawServiceCode(dto.getServiceCode());
						entity.setTerminalCode(dto.getTerminalCode());
						entity.setBankId(finalAccountBankReceiveEntity.getId());
						entity.setBankAccount(finalAccountBankReceiveEntity.getBankAccount());
						entity.setTerminalId("");
						entity.setContent(finalResult.getContent());
						entity.setAmount(Long.parseLong(finalResult.getAmount()));

						terminalItemService.insert(entity);
					});
					thread.start();
				}
			}
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private boolean checkRequestBodySemiDynamicFlow2(VietQRCreateCustomerDTO dto) {
		boolean result = false;
		try {
			// content up to 19
			// orderId up to 13
			String content = "";
			String serviceCode = "";
			if (dto.getContent() != null) {
				content = dto.getContent();
			}
			if (dto.getOrderId() != null) {
				serviceCode = dto.getServiceCode();
			}
			if (content.length() <= 19
					&& serviceCode.length() <= 19
					&& dto.getAmount() != null && !dto.getBankAccount().trim().isEmpty()
					&& dto.getBankAccount() != null
					&& dto.getBankCode() != null && dto.getBankCode().equals("MB")
					&& !StringUtil.isNullOrEmpty(dto.getTerminalCode())
					&& !StringUtil.isNullOrEmpty(dto.getServiceCode())) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("checkRequestBody: ERROR: " + e.toString());
		}
		return result;
	}

	private String generateSemiDynamicQrMMS(VietQRMMSRequestDTO dto) {
		String result = "";
			try {
				UUID clientMessageId = UUID.randomUUID();
				Map<String, Object> data = new HashMap<>();
				data.put("terminalID", dto.getTerminalId());
				data.put("qrcodeType", 3);
				data.put("partnerType", 2);
				data.put("initMethod", 11);
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
					content = dto.getOrderId();
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
					logger.info("generateSemiDynamicQrMMS: RESPONSE: " + json);
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode rootNode = objectMapper.readTree(json);
					if (rootNode.get("data") != null) {
						if (rootNode.get("data").get("qrcode") != null) {
							result = rootNode.get("data").get("qrcode").asText();
							logger.info("generateSemiDynamicQrMMS: RESPONSE qrcode: " + result);
						} else {
							logger.info("generateSemiDynamicQrMMS: RESPONSE qrcode is null");
						}
					} else {
						logger.info("generateSemiDynamicQrMMS: RESPONSE data is null");
					}
				} else {
					String json = response.bodyToMono(String.class).block();
					logger.info("generateSemiDynamicQrMMS: RESPONSE: " + response.statusCode().value() + " - " + json);
				}
			} catch (Exception e) {
				logger.error("generateSemiDynamicQrMMS: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
			} finally {
				logger.info("generateSemiDynamicQrMMS: response from MB at: " + DateTimeUtil.getCurrentDateTimeUTC());
			}

		return result;
	}

	private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
																 AccountBankReceiveEntity accountBankReceiveEntity, String billId) {
		ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
		logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		try {
			long amount = 0;
			if (Objects.nonNull(accountBankReceiveEntity) && !StringUtil.isNullOrEmpty(billId)) {
				if (!StringUtil.isNullOrEmpty(accountBankReceiveEntity.getCustomerId())) {
					CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
					entity.setId(UUID.randomUUID().toString());
					entity.setCustomerId(accountBankReceiveEntity.getCustomerId());
					try {
						amount = Long.parseLong(dto.getAmount());
					} catch (Exception e) {
						logger.error("VietQRController: ERROR: insertNewCustomerInvoiceTransBIDV: " + e.getMessage());
					}
					entity.setAmount(amount);
					entity.setBillId(billId);
					entity.setStatus(0);
					entity.setType(1);
					entity.setName("");
					entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
					entity.setTimePaid(0L);
					entity.setInquire(0);
					entity.setQrType(1);
					customerInvoiceService.insert(entity);
					responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
				} else {
					responseMessageDTO = new ResponseMessageDTO("FAILED", "");
				}
			} else {
				responseMessageDTO = new ResponseMessageDTO("FAILED", "");
			}
		} catch (Exception e) {
			logger.error("Error at insertNewCustomerInvoiceTransBIDV: " + e.toString());
		} finally {
			logger.info("QR generate - end insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		}
		return responseMessageDTO;
	}

	private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
																 IAccountBankReceiveQR accountBankReceiveEntity, String billId) {
		ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
		logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		try {
			long amount = 0;
			if (Objects.nonNull(accountBankReceiveEntity) && !StringUtil.isNullOrEmpty(billId)) {
				if (!StringUtil.isNullOrEmpty(accountBankReceiveEntity.getCustomerId())) {
					CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
					entity.setId(UUID.randomUUID().toString());
					entity.setCustomerId(accountBankReceiveEntity.getCustomerId());
					try {
						amount = Long.parseLong(dto.getAmount());
					} catch (Exception e) {
						logger.error("VietQRController: ERROR: insertNewCustomerInvoiceTransBIDV: " + e.getMessage());
					}
					entity.setAmount(amount);
					entity.setBillId(billId);
					entity.setStatus(0);
					entity.setType(1);
					entity.setName(dto.getServiceCode());
					entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
					entity.setTimePaid(0L);
					entity.setInquire(0);
					entity.setQrType(3);
					customerInvoiceService.insert(entity);
					responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
				} else {
					responseMessageDTO = new ResponseMessageDTO("FAILED", "");
				}
			} else {
				responseMessageDTO = new ResponseMessageDTO("FAILED", "");
			}
		} catch (Exception e) {
			logger.error("Error at insertNewCustomerInvoiceTransBIDV: " + e.toString());
		} finally {
			logger.info("QR generate - end insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		}
		return responseMessageDTO;
	}

	private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
																 AccountBankGenerateBIDVDTO bidvdto, String billId) {
		ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
		logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		try {
			long amount = 0;
			if (Objects.nonNull(bidvdto) && !StringUtil.isNullOrEmpty(billId)) {
				if (!StringUtil.isNullOrEmpty(bidvdto.getCustomerId())) {
					CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
					entity.setId(UUID.randomUUID().toString());
					entity.setCustomerId(bidvdto.getCustomerId());
					try {
						amount = Long.parseLong(dto.getAmount());
					} catch (Exception e) {
						logger.error("VietQRController: ERROR: insertNewCustomerInvoiceTransBIDV: " + e.getMessage());
					}
					entity.setAmount(amount);
					entity.setBillId(billId);
					entity.setStatus(0);
					entity.setType(1);
					entity.setName("");
					entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
					entity.setTimePaid(0L);
					entity.setInquire(0);
					entity.setQrType(1);
					customerInvoiceService.insert(entity);
					responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
				} else {
					responseMessageDTO = new ResponseMessageDTO("FAILED", "");
				}
			} else {
				responseMessageDTO = new ResponseMessageDTO("FAILED", "");
			}
		} catch (Exception e) {
			logger.error("Error at insertNewCustomerInvoiceTransBIDV: " + e.toString());
		} finally {
			logger.info("QR generate - end insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		}
		return responseMessageDTO;
	}

	private ResponseMessageDTO sendMessageDynamicQrToQrBox(String transactionUUID, String boxCode, VietQRDTO result,
														   String terminalName, String qr, String note) {
		ResponseMessageDTO responseMessageDTO = null;
		try {
			String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(boxCode);
			Map<String, String> data = new HashMap<>();
			data.put("notificationType", NotificationUtil.getNotiSendDynamicQr());
			data.put("amount", StringUtil.formatNumberAsString(result.getAmount()));
			data.put("qrCode", qr);
			socketHandler.sendMessageToBoxId(boxId, data);

			try {
				ObjectMapper mapper = new ObjectMapper();
				DynamicQRBoxDTO dynamicQRBoxDTO = new DynamicQRBoxDTO();
				dynamicQRBoxDTO.setNotificationType(NotificationUtil.getNotiSendDynamicQr());
				dynamicQRBoxDTO.setAmount(StringUtil.formatNumberAsString(result.getAmount()));
				dynamicQRBoxDTO.setQrCode(qr);
				mqttMessagingService
						.sendMessageToBoxId(boxId, mapper.writeValueAsString(dynamicQRBoxDTO));
			} catch (Exception e) {

			}
			responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
			return responseMessageDTO;
		} catch (Exception e) {
			logger.error("insertNewTransaction - sendMessageDynamicQrToQrBox: ERROR: " + e.toString()
			+ " at: " + System.currentTimeMillis());
			responseMessageDTO = new ResponseMessageDTO("FAILED", "E05");
		}
		return responseMessageDTO;
	}

	private ResponseMessageDTO requestVietQRMMS(VietQRMMSRequestDTO dto) {
		ResponseMessageDTO result = null;
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
						String qrCode = rootNode.get("data").get("qrcode").asText();
						logger.info("requestVietQRMMS: RESPONSE qrcode: " + qrCode);
						result = new ResponseMessageDTO("SUCCESS", qrCode);
					} else {
						logger.info("requestVietQRMMS: RESPONSE qrcode is null");
					}
				} else {
					logger.info("requestVietQRMMS: RESPONSE data is null");
				}
			} else {
				String json = response.bodyToMono(String.class).block();
				logger.error("requestVietQRMMS: RESPONSE: ERROR " + response.statusCode().value() + " - " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				if (rootNode.get("errorCode") != null) {
					String getMessageBankCode = getMessageBankCode(rootNode.get("errorCode").asText());
					result = new ResponseMessageDTO("FAILED", getMessageBankCode);
				} else {
					logger.info("requestVietQRMMS: RESPONSE data is null");
				}
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

	String getMessageBankCode(String errBankCode) {
		switch (errBankCode) {
			case "404":
				return "E165";
			case "203":
				return "E165";
			case "205":
				return "E166";
			default:
				return "E05";
		}
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
					&& dto.getBankCode() != null && dto.getBankCode().equals("MB")
					&& StringUtil.isLatinAndNumeric(content)) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("checkRequestBody: ERROR: " + e.toString());
		}
		return result;
	}

	private boolean checkRequestBodyFlow1(VietQRCreateCustomerDTO dto) {
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
					&& !StringUtil.isNullOrEmpty(dto.getBankCode())
					&& StringUtil.isLatinAndNumeric(content)) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("checkRequestBody: ERROR: " + e.toString());
		}
		return result;
	}

	@Async
	protected void insertNewTransactionFlow2(String qrCode, String transcationUUID,
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
			transactionEntity.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
			transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
			transactionEntity.setQrCode(qrCode);
			transactionEntity.setUserId(accountBankReceiveEntity.getUserId());
			transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
			transactionEntity.setTransStatus(0);
			transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
			transactionEntity.setServiceCode(dto.getServiceCode());
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
	protected void insertNewTransaction(UUID transcationUUID, String traceId, VietQRCreateDTO dto, VietQRDTO result,
										String orderId, String sign, boolean isFromMerchantSync) {
		LocalDateTime startTime = LocalDateTime.now();
		long startTimeLong = startTime.toEpochSecond(ZoneOffset.UTC);
		logger.info("QR generate - start insertNewTransaction at: " + startTimeLong);
		logger.info("QR generate - insertNewTransaction data: " + result.toString());
		try {
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			// 2. Insert transaction_receive if branch_id and business_id != null
			// 3. Insert transaction_receive_branch if branch_id and business_id != null
			IAccountBankUserQR accountBankEntity = accountBankReceiveService.getAccountBankUserQRById(dto.getBankId());
			if (accountBankEntity != null) {
				LocalDateTime currentDateTime = LocalDateTime.now();
				TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
				transactionEntity.setId(transcationUUID.toString());
				transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
				transactionEntity.setBankId(dto.getBankId());
				transactionEntity.setContent(traceId + " " + dto.getContent());
				transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
				transactionEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
				transactionEntity.setRefId("");
				transactionEntity.setType(0);
				transactionEntity.setStatus(0);
				transactionEntity.setTraceId(traceId);
				transactionEntity.setTimePaid(0);
				transactionEntity.setTerminalCode(result.getTerminalCode() != null ? result.getTerminalCode() : "");
				transactionEntity.setSubCode(StringUtil.getValueNullChecker(result.getSubTerminalCode()));
				transactionEntity.setQrCode("");
				transactionEntity.setUserId(accountBankEntity.getUserId());
				transactionEntity.setOrderId(orderId);
				transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
				transactionEntity.setTransStatus(0);
				transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
				if (dto.getTransType() != null) {
					transactionEntity.setTransType(dto.getTransType());
				} else {
					transactionEntity.setTransType("C");
				}
				transactionEntity.setReferenceNumber("");
				transactionEntity.setOrderId(orderId);
				transactionEntity.setServiceCode(dto.getServiceCode());
				transactionEntity.setSign(sign);
				//
				if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
					transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
					transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
					transactionEntity.setCustomerName(dto.getCustomerName());
				}
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				LocalDateTime afterInsertTransactionTime = LocalDateTime.now();
				long afterInsertTransactionTimeLong = afterInsertTransactionTime.toEpochSecond(ZoneOffset.UTC);
				logger.info("QR generate - after insertTransactionReceive at: " + afterInsertTransactionTimeLong);

				// insert notification
				UUID notificationUUID = UUID.randomUUID();
				NotificationEntity notiEntity = new NotificationEntity();
				String message = NotificationUtil.getNotiDescNewTransPrefix2()
						+ NotificationUtil.getNotiDescNewTransSuffix1()
						+ nf.format(Double.parseDouble(dto.getAmount()))
						+ NotificationUtil
								.getNotiDescNewTransSuffix2();

				if (!isFromMerchantSync) {
					// push notification
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
		} catch (Exception e) {
			logger.error("Error at insertNewTransaction: " + e.toString());
		} finally {
			LocalDateTime endTime = LocalDateTime.now();
			long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
			logger.info("QR generate - end insertNewTransaction at: " + endTimeLong);
		}
	}

	@Async
	protected void reInsertNewTransaction(UUID transcationUUID, String traceId,
										  VietQRCreateFromTransactionDTO dto, VietQRDTO result) {
		try {
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			// 2. Insert transaction_receive if branch_id and business_id != null
			// 3. Insert transaction_receive_branch if branch_id and business_id != null
			AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
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
				transactionEntity.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
				transactionEntity.setSign("");
				transactionEntity.setTimePaid(0);
				transactionEntity.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
				transactionEntity.setSubCode(StringUtil.getValueNullChecker(result.getSubTerminalCode()));
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
//				firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
//						NotificationUtil
//								.getNotiTitleNewTransaction(),
//						message);
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
		String qr = "";
		String subRawCode = dto.getSubTerminalCode() != null ? dto.getSubTerminalCode() : "";
		boolean checkFlow2 = false;
		try {
			AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
			if (accountBankEntity != null) {
				if (!StringUtil.isNullOrEmpty(subRawCode) && accountBankEntity.isMmsActive()) {
					// insert QR Box flow 2:
					checkFlow2 = true;
					// Luồng 2
					BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
					// get cai value
					LocalDateTime requestLDT = LocalDateTime.now();
					long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
					logger.info("generateVietQRMMS: start generate at: " + requestTime);
					String qrCode = "";
					String orderIdMMs = EnvironmentUtil.getMmsPrefixOrderId() + RandomCodeUtil.generateRandomId(8);
					if (!StringUtil.isNullOrEmpty(dto.getOrderId())) {
						orderIdMMs = dto.getOrderId();
					}
					// 1. Validate input (amount, content, bankCode) => E34 if Invalid input data
					VietQRCreateCustomerDTO vietQrFlow2 = new VietQRCreateCustomerDTO();
					Long amount = Long.parseLong(dto.getAmount());
					vietQrFlow2.setAmount(amount);
					vietQrFlow2.setContent(dto.getContent());
					vietQrFlow2.setBankAccount(accountBankEntity.getBankAccount());
					vietQrFlow2.setBankCode(bankTypeEntity.getBankCode());
					vietQrFlow2.setUserBankName(accountBankEntity.getBankAccountName());
					vietQrFlow2.setTransType(dto.getTransType());
					vietQrFlow2.setCustomerBankAccount("");
					vietQrFlow2.setCustomerBankCode("");
					vietQrFlow2.setCustomerName("");
					vietQrFlow2.setOrderId(orderIdMMs);
					vietQrFlow2.setSign("");
					vietQrFlow2.setTerminalCode(subRawCode);
					vietQrFlow2.setNote("");
					vietQrFlow2.setUrlLink("");
					vietQrFlow2.setReconciliation(true);
					try {
						if (checkRequestBodyFlow2(vietQrFlow2)) {
							// 2. Find terminal bank by bank_account_raw_number
							TerminalBankEntity terminalBankEntity = terminalBankService
									.getTerminalBankByBankAccount(vietQrFlow2.getBankAccount());
							if (terminalBankEntity == null) {
								// 3.A. If not found => E35 (terminal is not existed)
								logger.error("generateVietQRMMS: ERROR: Bank account is not existed.");
								httpStatus = HttpStatus.BAD_REQUEST;
							} else {
								// 3.B. If found => get bank token => create qr code
								TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();
								if (tokenBankDTO != null) {
									String content = "";
									if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
										content = dto.getContent();
									} else {
										content = traceId;
									}
									VietQRMMSRequestDTO requestDTO = new VietQRMMSRequestDTO();
									requestDTO.setToken(tokenBankDTO.getAccess_token());
									requestDTO.setTerminalId(terminalBankEntity.getTerminalId());
									requestDTO.setAmount(dto.getAmount() + "");
									requestDTO.setContent(content);
									requestDTO.setOrderId(vietQrFlow2.getOrderId());
//									qrCode = requestVietQRMMS(requestDTO);
									ResponseMessageDTO responseMessageDTO = requestVietQRMMS(requestDTO);
//									if (qrCode != null) {
									if (Objects.nonNull(responseMessageDTO)
											&& "SUCCESS".equals(responseMessageDTO.getStatus())) {
										// VietQRMMSDTO vietQRMMSDTO = new VietQRMMSDTO(qrCode);
										qrCode = responseMessageDTO.getMessage();
										qr = qrCode;
										if (bankTypeEntity.getId() != null && !bankTypeEntity.getId().trim().isEmpty()) {
											VietQRDTO vietQRDTO = new VietQRDTO();
											//
											String bankAccount = "";
											String userBankName = "";
											bankAccount = accountBankEntity.getBankAccount();
											userBankName = accountBankEntity.getBankAccountName().trim().toUpperCase();
											// generate VietQRDTO
											vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
											vietQRDTO.setBankName(bankTypeEntity.getBankName());
											vietQRDTO.setBankAccount(bankAccount);
											vietQRDTO.setUserBankName(userBankName);
											vietQRDTO.setAmount(StringUtil.formatNumberAsString(dto.getAmount()));
											vietQRDTO.setContent(content);
											vietQRDTO.setQrCode(qrCode);
											vietQRDTO.setImgId(bankTypeEntity.getImgId());
											vietQRDTO.setExisting(1);
											vietQRDTO.setTransactionId("");
											vietQRDTO.setTerminalCode(subRawCode);
											String refId = TransactionRefIdUtil
													.encryptTransactionId(transcationUUID.toString());
											String qrLink = EnvironmentUtil.getQRLink() + refId;
											vietQRDTO.setTransactionRefId(refId);
											vietQRDTO.setQrLink(qrLink);
											result = vietQRDTO;
											httpStatus = HttpStatus.OK;
										} else {
											httpStatus = HttpStatus.BAD_REQUEST;
										}
									} else {
										logger.error("generateVietQRMMS: ERROR: Invalid get QR Code");
										httpStatus = HttpStatus.BAD_REQUEST;
									}
								} else {
									logger.error("generateVietQRMMS: ERROR: Invalid get bank token");
									httpStatus = HttpStatus.BAD_REQUEST;
								}
							}
						} else {
							logger.error("generateVietQRMMS: ERROR: Invalid request body");
							httpStatus = HttpStatus.BAD_REQUEST;
						}
					} catch (Exception e) {
						logger.error("generateVietQRMMS: ERROR: " + e.toString());
						httpStatus = HttpStatus.BAD_REQUEST;
					} finally {
						// 4. Insert transaction_receive
						// (5. Insert notification)
						if (qrCode != null && !qrCode.isEmpty()) {
							String content = "";
							if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
								content = dto.getContent();
							} else {
								content = traceId;
							}
							LocalDateTime currentDateTime = LocalDateTime.now();
							long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
							VietQRMMSCreateDTO vietQRMMSCreateDTO = new VietQRMMSCreateDTO();
							vietQRMMSCreateDTO.setBankAccount(vietQrFlow2.getBankAccount());
							vietQRMMSCreateDTO.setBankCode(vietQrFlow2.getBankCode());
							vietQRMMSCreateDTO.setAmount(vietQrFlow2.getAmount() + "");
							vietQRMMSCreateDTO.setContent(content);
							vietQRMMSCreateDTO.setOrderId(vietQrFlow2.getOrderId());
							vietQRMMSCreateDTO.setSign(vietQrFlow2.getSign());
							vietQRMMSCreateDTO.setTerminalCode(subRawCode);
							vietQRMMSCreateDTO.setNote(vietQrFlow2.getNote());
							vietQRMMSCreateDTO.setSubTerminalCode(dto.getSubTerminalCode());
							if (vietQrFlow2.getUrlLink() != null && !vietQrFlow2.getUrlLink().trim().isEmpty()) {
								vietQRMMSCreateDTO.setUrlLink(vietQrFlow2.getUrlLink());
							} else {
								vietQRMMSCreateDTO.setUrlLink("");
							}
							insertNewTransactionFlow2(qr, transcationUUID.toString(), accountBankEntity, vietQRMMSCreateDTO,
									time);
						}
					}
				} else {
					// 1.Generate VietQR
					// get bank information
					// get bank type information
					BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
					VietQRDTO vietQRDTO = new VietQRDTO();
					if ("BIDV".equals(bankTypeEntity.getBankCode()) && accountBankEntity.isAuthenticated()) {
						String billId = "";
						billId = getRandomBillId();
						String content = billId + " " + dto.getContent();
						// generate qr BIDV
						VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
						vietQRCreateDTO.setBankId(accountBankEntity.getId());
						vietQRCreateDTO.setAmount(dto.getAmount() + "");
						vietQRCreateDTO.setContent(content);
						vietQRCreateDTO.setUserId(accountBankEntity.getUserId());
						vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
						vietQRCreateDTO.setTransType("C");
						vietQRCreateDTO.setUrlLink("");
						ResponseMessageDTO responseMessageDTO =
								insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankEntity, billId);

						// insert success transaction_receive
						if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
							VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
							if ("0".equals(dto.getAmount())) {
								vietQRVaRequestDTO.setAmount("");
							} else {
								vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
							}
							vietQRVaRequestDTO.setBillId(billId);
							vietQRVaRequestDTO.setUserBankName(accountBankEntity.getBankAccountName());
							vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(content));

							ResponseMessageDTO generateVaInvoiceVietQR = CustomerVaUtil.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankEntity.getCustomerId());
							if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
								qr = generateVaInvoiceVietQR.getMessage();
							}
						}
						VietQRBIDVCreateDTO dto1 = new VietQRBIDVCreateDTO();
						dto1.setContent(content);
						dto1.setAmount(dto.getAmount() + "");
						dto1.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
						if (StringUtil.isNullOrEmpty(dto.getTerminalCode()) &&
								!StringUtil.isNullOrEmpty(dto.getSubTerminalCode())) {
							dto1.setTerminalCode(dto.getSubTerminalCode());
						}
						dto1.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
						dto1.setNote(StringUtil.getValueNullChecker(dto.getNote()));
						dto1.setUrlLink(StringUtil.getValueNullChecker(dto.getUrlLink()));
						dto1.setTransType(StringUtil.getValueNullChecker(dto.getTransType()));
						dto1.setSign("");
						dto1.setBillId(billId);
						dto1.setCustomerBankAccount(StringUtil.getValueNullChecker(dto.getCustomerBankAccount()));
						dto1.setCustomerBankCode(StringUtil.getValueNullChecker(dto.getCustomerBankCode()));
						dto1.setCustomerName(StringUtil.getValueNullChecker(dto.getCustomerName()));
						dto1.setQr(qr);
                        Thread thread = new Thread(() -> {
							insertNewTransactionBIDV(transcationUUID, dto1, false, "", accountBankEntity);
						});
						thread.start();

						if (!StringUtil.isNullOrEmpty(qr)) {
							vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
							vietQRDTO.setBankName(bankTypeEntity.getBankName());
							vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
							vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
							vietQRDTO.setAmount(StringUtil.formatNumberAsString(dto.getAmount()));
							vietQRDTO.setContent(content);
							vietQRDTO.setQrCode(qr);
							vietQRDTO.setImgId(bankTypeEntity.getImgId());
							// return transactionId to upload bill image
							vietQRDTO.setTransactionId(transcationUUID.toString());
							// return terminal code of merchant
							vietQRDTO.setTerminalCode(dto.getTerminalCode());
							if (StringUtil.isNullOrEmpty(dto.getTerminalCode()) &&
									!StringUtil.isNullOrEmpty(dto.getSubTerminalCode())) {
								vietQRDTO.setTerminalCode(dto.getSubTerminalCode());
							}
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
						// generate VietQRDTO

					} else {
						// get cai value
						String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
						// generate VietQRGenerateDTO
						VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
						vietQRGenerateDTO.setCaiValue(caiValue);
						vietQRGenerateDTO.setAmount(dto.getAmount());
						vietQRGenerateDTO.setContent(traceId + " " + dto.getContent());
						vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
						qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
						// generate VietQRDTO
						vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
						vietQRDTO.setBankName(bankTypeEntity.getBankName());
						vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
						vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
						vietQRDTO.setAmount(StringUtil.formatNumberAsString(dto.getAmount()));
						vietQRDTO.setContent(traceId + " " + dto.getContent());
						vietQRDTO.setQrCode(qr);
						vietQRDTO.setImgId(bankTypeEntity.getImgId());
						// return transactionId to upload bill image
						vietQRDTO.setTransactionId(transcationUUID.toString());
						// return terminal code of merchant
						vietQRDTO.setTerminalCode(dto.getTerminalCode());
						if (StringUtil.isNullOrEmpty(dto.getTerminalCode()) &&
								!StringUtil.isNullOrEmpty(dto.getSubTerminalCode())) {
							vietQRDTO.setTerminalCode(dto.getSubTerminalCode());
						}
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
					}
				}
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			return new ResponseEntity<>(result, httpStatus);
		} catch (Exception e) {
			System.out.println("Error at generateQR: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			if (result != null && !result.getBankCode().equals("BIDV")) {
				if (!checkFlow2) {
					if (dto.getOrderId() != null && !dto.getOrderId().isEmpty()) {
						insertNewTransaction(transcationUUID, traceId, dto, result, dto.getOrderId(), "", false);
					} else {
						insertNewTransaction(transcationUUID, traceId, dto, result, "", "", false);
					}
				}
			}
			if (result != null) {
				if (!StringUtil.isNullOrEmpty(subRawCode)) {
					TerminalBankReceiveEntity terminalBankReceiveEntity =
							terminalBankReceiveService.getTerminalBankReceiveEntityByTerminalCode(subRawCode);
					if (terminalBankReceiveEntity != null) {
						sendMessageDynamicQrToQrBox(transcationUUID.toString(),
								terminalBankReceiveEntity.getRawTerminalCode() != null ?
										terminalBankReceiveEntity.getRawTerminalCode() : "",
								result, "", qr, dto.getNote()
						);
					}
				}
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
					AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(bankId);
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
					AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
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
			AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
			if (accountBankEntity != null) {
				String content = "";
				if (dto.isNewTransaction() == true) {
					String suffixContent = "";
					String regex = "VQR\\w{10}\\.?";
					suffixContent = dto.getContent().replaceAll(regex, "");
					content = traceId + " " + suffixContent;
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

	private void insertNewTransactionBIDV(UUID transcationUUID, VietQRBIDVCreateDTO dto,
										  boolean isFromMerchantSync,String traceId,
										  AccountBankReceiveEntity entity) {
		logger.info("QR generate - start insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		try {
			if (Objects.nonNull(entity)) {
				TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
				transactionEntity.setId(transcationUUID.toString());
				transactionEntity.setBankAccount(entity.getBankAccount());
				transactionEntity.setBankId(entity.getId());
				transactionEntity.setContent(dto.getContent());
				transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
				transactionEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
				transactionEntity.setRefId("");
				transactionEntity.setType(0);
				transactionEntity.setStatus(0);
				transactionEntity.setTraceId(traceId);
				transactionEntity.setTimePaid(0);
				transactionEntity.setTerminalCode(dto.getTerminalCode());
				transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
				transactionEntity.setQrCode(dto.getQr());
				transactionEntity.setUserId(entity.getUserId());
				transactionEntity.setOrderId(dto.getOrderId());
				transactionEntity.setNote(dto.getNote());
				transactionEntity.setTransStatus(0);
				transactionEntity.setUrlLink(dto.getUrlLink());
				transactionEntity.setTransType("C");
				transactionEntity.setReferenceNumber("");
				transactionEntity.setSign(dto.getSign());
				transactionEntity.setBillId(dto.getBillId());
				//
				if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
					transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
					transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
					transactionEntity.setCustomerName(dto.getCustomerName());
				}
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				logger.info("After insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
			}
		} catch (Exception e) {
			logger.error("Error at insertNewTransactionBIDV: " + e.toString());
		} finally {
			logger.info("QR generate - end insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		}
	}

	private void insertNewTransactionBIDV(UUID transcationUUID, VietQRBIDVCreateDTO dto,
											boolean isFromMerchantSync,String traceId,
										  AccountBankGenerateBIDVDTO accountBank) {
		logger.info("QR generate - start insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		try {
			if (Objects.nonNull(accountBank)) {
				TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
				transactionEntity.setId(transcationUUID.toString());
				transactionEntity.setBankAccount(accountBank.getBankAccount());
				transactionEntity.setBankId(accountBank.getId());
				transactionEntity.setContent(dto.getContent());
				transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
				transactionEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
				transactionEntity.setRefId("");
				transactionEntity.setType(0);
				transactionEntity.setStatus(0);
				transactionEntity.setTraceId(traceId);
				transactionEntity.setTimePaid(0);
				transactionEntity.setTerminalCode(dto.getTerminalCode());
				transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
				transactionEntity.setQrCode(dto.getQr());
				transactionEntity.setUserId(accountBank.getUserId());
				transactionEntity.setOrderId(dto.getOrderId());
				transactionEntity.setNote(dto.getNote());
				transactionEntity.setTransStatus(0);
				transactionEntity.setUrlLink(dto.getUrlLink());
				transactionEntity.setTransType("C");
				transactionEntity.setReferenceNumber("");
				transactionEntity.setSign(dto.getSign());
				transactionEntity.setBillId(dto.getBillId());
				//
				if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
					transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
					transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
					transactionEntity.setCustomerName(dto.getCustomerName());
				}
				transactionReceiveService.insertTransactionReceive(transactionEntity);
				logger.info("After insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
			 }
		} catch (Exception e) {
			logger.error("Error at insertNewTransactionBIDV: " + e.toString());
		} finally {
			logger.info("QR generate - end insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
		}
	}

	private void pushNotitransactionIsFromMerchantSync(boolean isFromMerchantSync, String message,
													   UUID transcationUUID, VietQRDTO result, String userId) {
		Thread thread = new Thread(() -> {
			NotificationEntity notiEntity = new NotificationEntity();
			UUID notificationUUID = UUID.randomUUID();
			// insert notification
			if (!isFromMerchantSync) {
				// push notification
				List<FcmTokenEntity> fcmTokens = new ArrayList<>();
				fcmTokens = fcmTokenService.getFcmTokensKiotByUserId(userId);
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
				try {
					firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
							NotificationUtil
									.getNotiTitleNewTransaction(),
							message);
                    socketHandler.sendMessageToUser(userId, data);
                } catch (Exception e) {
                    logger.error("VietQRController: ERROR: pushNotitransactionIsFromMerchantSync "
					+ e.getMessage() + " at: " + System.currentTimeMillis());
                }
            }

			notiEntity.setId(notificationUUID.toString());
			notiEntity.setRead(false);
			notiEntity.setMessage(message);
			notiEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
			notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
			notiEntity.setUserId(userId);
			notiEntity.setData(transcationUUID.toString());
			notificationService.insertNotification(notiEntity);
			LocalDateTime afterInsertNotificationTransaction = LocalDateTime.now();
			long afterInsertNotificationTransactionLong = afterInsertNotificationTransaction
					.toEpochSecond(ZoneOffset.UTC);
			logger.info("QR generate - after InsertNotificationTransaction at: "
					+ afterInsertNotificationTransactionLong);
		});
		thread.start();
	}

	private String getRandomBillId() {
		String result = "";
		try {
			result = EnvironmentUtil.getPrefixBidvBillIdCommon() + DateTimeUtil.getCurrentWeekYear() +
					StringUtil.convertToHexadecimal(DateTimeUtil.getMinusCurrentDate()) + RandomCodeUtil.generateRandomId(4);
		} catch (Exception e) {
			logger.error("getRandomBillId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
		}
		return result;
	}

	private String getRandomUniqueCodeInTerminalCode() {
		String result = "";
		String checkExistedCode = "";
		String code = "";
		try {
			do {
				code = getTerminalCode();
				checkExistedCode = terminalBankReceiveService
						.checkExistedTerminalCode(code);
				if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
					checkExistedCode = terminalService.checkExistedTerminal(code);
				}
			} while (!StringUtil.isNullOrEmpty(checkExistedCode));
			result = code;
		} catch (Exception e) {
			logger.error("getRandomUniqueCodeInTerminalCode: ERROR: " + e.getMessage()
					+ " at: " + System.currentTimeMillis());
		}
		return result;
	}

	private String getTerminalCode() {
		return RandomCodeUtil.generateRandomId(10);
	}

	private void logUserInfo(String token){
		LocalDateTime currentDateTime = LocalDateTime.now();
		long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
		String secretKey = "mySecretKey";
		String jwtToken = token.substring(7); // remove "Bearer " from the beginning
		Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
		String user = (String) claims.get("user");
		if (user != null) {
			String decodedUser = new String(Base64.getDecoder().decode(user));
			logger.info("qr/generate-customer - user " + decodedUser + " call at " + time);
		} else {
			logger.info("qr/generate-customer - Sytem User call at " + time);
		}
	}
}
