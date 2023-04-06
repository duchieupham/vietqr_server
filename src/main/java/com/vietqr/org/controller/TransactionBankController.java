package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.ZoneOffset;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Base64;

import javax.validation.Valid;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import org.apache.log4j.Logger;

import com.vietqr.org.dto.AccountLoginDTO;
import com.vietqr.org.dto.CofirmOTPBankDTO;
import com.vietqr.org.dto.RefTransactionDTO;
import com.vietqr.org.dto.TransactionBankDTO;
import com.vietqr.org.dto.TransactionResponseDTO;
import com.vietqr.org.service.TransactionBankService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.BusinessInformationService;
import com.vietqr.org.service.CustomerSyncService;
import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.BusinessInformationEntity;
import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.dto.ConfirmRequestFailedBankDTO;
import com.vietqr.org.dto.RequestBankDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TokenDTO;

import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.RandomCodeUtil;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.vietqr.org.util.EnvironmentUtil;

@RestController
@RequestMapping("/bank/api")
public class TransactionBankController {
	private static final Logger logger = Logger.getLogger(TransactionBankController.class);

	@Autowired
	AccountBankReceiveService accountBankService;

	@Autowired
	TransactionBankService transactionBankService;

	@Autowired
	TransactionReceiveService transactionReceiveService;

	@Autowired
	TransactionReceiveBranchService transactionReceiveBranchService;

	@Autowired
	BranchMemberService branchMemberService;

	@Autowired
	BranchInformationService branchInformationService;

	@Autowired
	BusinessInformationService businessInformationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	FcmTokenService fcmTokenService;

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	CustomerSyncService customerSyncService;

	private FirebaseMessagingService firebaseMessagingService;

	public TransactionBankController(FirebaseMessagingService firebaseMessagingService) {
		this.firebaseMessagingService = firebaseMessagingService;
	}

	@PostMapping("transaction-sync")
	public ResponseEntity<TransactionResponseDTO> insertTranscationBank(@RequestBody TransactionBankDTO dto) {
		TransactionResponseDTO result = null;
		HttpStatus httpStatus = null;
		UUID uuid = UUID.randomUUID();
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		LocalDateTime currentDateTime = LocalDateTime.now();
		long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
		boolean checkDuplicate = checkDuplicateReferenceNumber(dto.getReferencenumber());
		try {
			List<Object> list = transactionBankService.checkTransactionIdInserted(dto.getTransactionid());
			if (list.isEmpty()) {
				result = validateTransactionBank(dto, uuid.toString());
				if (!result.isError()) {
					if (checkDuplicate) {
						transactionBankService.insertTransactionBank(dto.getTransactionid(), dto.getTransactiontime(),
								dto.getReferencenumber(), dto.getAmount(), dto.getContent(), dto.getBankaccount(),
								dto.getTransType(), dto.getReciprocalAccount(), dto.getReciprocalBankCode(),
								dto.getVa(),
								dto.getValueDate(), uuid.toString());
					}
					httpStatus = HttpStatus.OK;
				} else {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
				result = new TransactionResponseDTO(true, "006", "Duplicated transactionid");
			}
			return new ResponseEntity<>(result, httpStatus);
		} catch (Exception e) {
			logger.error("Error at transaction-sync: " + e.toString());
			result = new TransactionResponseDTO(true, "005", "Unexpected error");
			httpStatus = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			if (checkDuplicate) {
				// find transaction by id
				String traceId = "";
				String[] newPaths = dto.getContent().split("\\s+");
				logger.info("content: " + dto.getContent() + "-" + newPaths.length + "-" + newPaths.toString());
				if (newPaths != null && newPaths.length > 0) {
					int indexSaved = 0;
					for (int i = 0; i < newPaths.length; i++) {
						if (newPaths[i].contains("VQR")) {
							traceId = newPaths[i];
							indexSaved = i;
						}
						if (i == indexSaved + 1) {
							if (traceId.length() < 13) {
								traceId = traceId + newPaths[i];
							}
						}
					}
					if (!traceId.isEmpty()) {
						String pattern = "VQR.{10}";
						Pattern r = Pattern.compile(pattern);
						Matcher m = r.matcher(traceId);
						if (m.find()) {
							traceId = m.group(0);
						} else {
							traceId = "";
						}
					}
					logger.info("traceId: " + traceId);
				}
				if (traceId != null && !traceId.isEmpty()) {
					logger.info("transaction-sync - trace ID detect: " + traceId);
					TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
							.getTransactionByTraceId(traceId);
					if (transactionReceiveEntity != null) {
						updateTransaction(dto, transactionReceiveEntity, time, nf);
					} else {
						logger.info(
								"transaction-sync - cannot find transaction receive. Receive new transaction outside system");
						// process here
						insertNewTransaction(dto, time, traceId, uuid, nf);
					}
				} else {
					logger.info("transaction-sync - traceId is empty. Receive new transaction outside system");
					insertNewTransaction(dto, time, traceId, uuid, nf);
				}
				getCustomerSyncEntities(dto, time);
			}
		}
	}

	@Async
	private void updateTransaction(TransactionBankDTO dto, TransactionReceiveEntity transactionReceiveEntity,
			long time, NumberFormat nf) {
		AccountBankReceiveEntity accountBankEntity = accountBankService
				.getAccountBankById(transactionReceiveEntity.getBankId()); // update status
		if (accountBankEntity != null) {
			// find transaction-branch-receive to push notification
			BankTypeEntity bankTypeEntity = bankTypeService
					.getBankTypeById(accountBankEntity.getBankTypeId());
			TransactionReceiveBranchEntity transactionBranchEntity = transactionReceiveBranchService
					.getTransactionBranchByTransactionId(transactionReceiveEntity.getId());
			if (transactionBranchEntity != null) {
				transactionReceiveService.updateTransactionReceiveStatus(1,
						dto.getTransactionid(),
						transactionBranchEntity.getTransactionReceiveId());
				// push notification
				// find userIds into business_member and branch_member
				List<String> userIds = branchMemberService
						.getUserIdsByBusinessIdAndBranchId(transactionBranchEntity.getBusinessId(),
								transactionBranchEntity.getBranchId());
				// insert AND push notification to users belong to
				// admin business/ member of branch
				if (userIds != null && !userIds.isEmpty()) {
					for (String userId : userIds) {
						// insert notification
						UUID notificationUUID = UUID.randomUUID();
						NotificationEntity notiEntity = new NotificationEntity();
						BranchInformationEntity branchEntity = branchInformationService
								.getBranchById(transactionBranchEntity.getBranchId());
						BusinessInformationEntity businessEntity = businessInformationService
								.getBusinessById(branchEntity.getBusinessId());
						String prefix = "";
						if (dto.getTransType().toUpperCase().equals("D")) {
							prefix = "-";
						} else {
							prefix = "+";
						}
						String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
								+ accountBankEntity.getBankAccount()
								+ NotificationUtil.getNotiDescUpdateTransSuffix2()
								+ prefix + nf.format(dto.getAmount())
								+ NotificationUtil.getNotiDescUpdateTransSuffix3()
								+ branchEntity.getName()
								+ NotificationUtil.getNotiDescUpdateTransSuffix4()
								+ dto.getContent();
						// String title = NotificationUtil.getNotiTitleNewTransaction();
						notiEntity.setId(notificationUUID.toString());
						notiEntity.setRead(false);
						notiEntity.setMessage(message);
						notiEntity.setTime(time);
						notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
						notiEntity.setUserId(userId);
						notiEntity.setData(transactionReceiveEntity.getId());
						notificationService.insertNotification(notiEntity);
						List<FcmTokenEntity> fcmTokens = new ArrayList<>();
						fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
						Map<String, String> data = new HashMap<>();
						data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
						data.put("notificationId", notificationUUID.toString());
						data.put("transactionReceiveId", transactionReceiveEntity.getId());
						data.put("bankAccount", transactionReceiveEntity.getBankAccount());
						data.put("bankName", bankTypeEntity.getBankName());
						data.put("bankCode", bankTypeEntity.getBankCode());
						data.put("bankId", transactionReceiveEntity.getBankId());
						data.put("branchName", branchEntity.getName());
						data.put("businessName", businessEntity.getName());
						data.put("content", transactionReceiveEntity.getContent());
						data.put("amount", "" + transactionReceiveEntity.getAmount());
						data.put("time", "" + transactionReceiveEntity.getTime());
						data.put("refId", "" + dto.getTransactionid());
						data.put("status", "1");
						data.put("traceId", "" + transactionReceiveEntity.getTraceId());
						data.put("transType", dto.getTransType());
						firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
								NotificationUtil
										.getNotiTitleUpdateTransaction(),
								message);
					}
				} else {
					logger.info("transaction-sync - userIds empty.");
				}
			} else {
				logger.info("transaction-sync - transaction-branch is empty.");
			}
		} else {
			logger.info("transaction-sync - cannot find account bank");
		}
	}

	@Async
	private void insertNewTransaction(TransactionBankDTO dto, long time, String traceId, UUID uuid, NumberFormat nf) {
		AccountBankReceiveEntity accountBankEntity = accountBankService
				.getAccountBankByBankAccount(dto.getBankaccount());
		if (accountBankEntity != null) {
			BankTypeEntity bankTypeEntity = bankTypeService
					.getBankTypeById(accountBankEntity.getBankTypeId());
			UUID transcationUUID = UUID.randomUUID();
			TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
			transactionEntity.setId(transcationUUID.toString());
			transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
			transactionEntity.setBankId(accountBankEntity.getId());
			if (traceId == null || traceId.isEmpty()) {
				transactionEntity.setContent(dto.getContent().trim());
			} else {
				transactionEntity.setContent(traceId + "." + dto.getContent());
			}
			transactionEntity.setAmount(Long.parseLong(dto.getAmount() + ""));
			transactionEntity.setTime(time);
			transactionEntity.setRefId(uuid.toString());
			transactionEntity.setType(2);
			transactionEntity.setStatus(1);
			transactionEntity.setTraceId("");
			transactionEntity.setTransType(dto.getTransType());
			transactionReceiveService.insertTransactionReceive(transactionEntity);
			//
			// insert notification
			UUID notificationUUID = UUID.randomUUID();
			NotificationEntity notiEntity = new NotificationEntity();
			String prefix = "";
			if (dto.getTransType().toUpperCase().equals("D")) {
				prefix = "-";
			} else {
				prefix = "+";
			}
			String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
					+ accountBankEntity.getBankAccount()
					+ NotificationUtil.getNotiDescUpdateTransSuffix2()
					+ prefix + nf.format(dto.getAmount())
					+ NotificationUtil.getNotiDescUpdateTransSuffix4()
					+ dto.getContent();
			// String title = NotificationUtil.getNotiTitleNewTransaction();
			notiEntity.setId(notificationUUID.toString());
			notiEntity.setRead(false);
			notiEntity.setMessage(message);
			notiEntity.setTime(time);
			notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
			notiEntity.setUserId(accountBankEntity.getUserId());
			notiEntity.setData(transcationUUID.toString());
			notificationService.insertNotification(notiEntity);
			List<FcmTokenEntity> fcmTokens = new ArrayList<>();
			fcmTokens = fcmTokenService.getFcmTokensByUserId(accountBankEntity.getUserId());
			Map<String, String> data = new HashMap<>();
			data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
			data.put("notificationId", notificationUUID.toString());
			data.put("transactionReceiveId", transcationUUID.toString());
			data.put("bankAccount", accountBankEntity.getBankAccount());
			data.put("bankName", bankTypeEntity.getBankName());
			data.put("bankCode", bankTypeEntity.getBankCode());
			data.put("bankId", accountBankEntity.getId());
			data.put("branchName", "");
			data.put("businessName", "");
			data.put("content", dto.getContent());
			data.put("amount", "" + dto.getAmount());
			data.put("time", "" + time);
			data.put("refId", "" + dto.getTransactionid());
			data.put("status", "1");
			data.put("traceId", "");
			data.put("transType", dto.getTransType());
			firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
					NotificationUtil
							.getNotiTitleUpdateTransaction(),
					message);
		}

	}

	// get token bank product
	private TokenProductBankDTO getBankToken() {
		TokenProductBankDTO result = null;
		try {
			String key = EnvironmentUtil.getUserBankAccess() + ":" + EnvironmentUtil.getPasswordBankAccess();
			String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(EnvironmentUtil.getBankUrl() + "private/oauth2/v1/token")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(EnvironmentUtil.getBankUrl()
							+ "private/oauth2/v1/token")
					.build();
			// Call POST API
			TokenProductBankDTO response = webClient.method(HttpMethod.POST)
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.header("Authorization", "Basic " + encodedKey)
					.body(BodyInserters.fromFormData("grant_type", "client_credentials"))
					.exchange()
					.flatMap(clientResponse -> {
						if (clientResponse.statusCode().is2xxSuccessful()) {
							return clientResponse.bodyToMono(TokenProductBankDTO.class);
						} else {
							clientResponse.body((clientHttpResponse, context) -> {
								logger.info(clientHttpResponse.getBody().collectList().block().toString());
								return clientHttpResponse.getBody();
							});
							return null;
						}
					})
					.block();
			result = response;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return result;
	}

	@PostMapping("get_token_bank")
	public ResponseEntity<TokenProductBankDTO> getTokenBank() {
		TokenProductBankDTO result = null;
		HttpStatus httpStatus = null;
		try {
			result = getBankToken();
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			logger.error(e.toString());
		}
		return new ResponseEntity<TokenProductBankDTO>(result, httpStatus);
	}

	@PostMapping("request_otp_bank")
	public ResponseEntity<ResponseMessageDTO> requestOTP(@Valid @RequestBody RequestBankDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID clientMessageId = UUID.randomUUID();
			Map<String, Object> data = new HashMap<>();
			data.put("nationalId", dto.getNationalId());
			data.put("accountNumber", dto.getAccountNumber());
			data.put("accountName", dto.getAccountName());
			data.put("phoneNumber", dto.getPhoneNumber());
			data.put("authenType", "SMS");
			data.put("applicationType", dto.getApplicationType());
			data.put("transType", "DC");
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(EnvironmentUtil.getBankUrl()
							+ "private/ms/push-mesages-partner/v1.0/bdsd/subscribe/request")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(
							EnvironmentUtil.getBankUrl()
									+ "private/ms/push-mesages-partner/v1.0/bdsd/subscribe/request")
					.build();
			Mono<ClientResponse> responseMono = webClient.post()
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_JSON)
					.header("clientMessageId", clientMessageId.toString())
					.header("transactionId", RandomCodeUtil.generateRandomUUID())
					.header("Authorization", "Bearer " + getBankToken().getAccess_token())
					.body(BodyInserters.fromValue(data))
					.exchange();
			ClientResponse response = responseMono.block();
			if (response.statusCode().is2xxSuccessful()) {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response requestOTP: " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				String requestId = rootNode.get("data").get("requestId").asText();
				result = new ResponseMessageDTO("SUCCESS",
						requestId);
				httpStatus = HttpStatus.OK;
			} else {

				ConfirmRequestFailedBankDTO confirmRequestBankDTO = response.bodyToMono(
						ConfirmRequestFailedBankDTO.class)
						.block();
				LocalDateTime currentDateTime = LocalDateTime.now();
				logger.error("Response requestOTP error: " + confirmRequestBankDTO.getSoaErrorCode() + "-"
						+ confirmRequestBankDTO.getSoaErrorDesc() + " at "
						+ currentDateTime.toEpochSecond(ZoneOffset.UTC));
				String status = "FAILED";
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode());
				result = new ResponseMessageDTO(status, message);
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (HttpClientErrorException ex) {
			logger.error("HttpClientErrorException: " + ex.getMessage());
			logger.error("Response body: " + ex.getResponseBodyAsString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		} catch (HttpServerErrorException ex) {
			logger.error("HttpServerErrorException: " + ex.getMessage());
			logger.error("Response body: " + ex.getResponseBodyAsString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;

		} catch (Exception e) {
			logger.error("Error at requestOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("confirm_otp_bank")
	public ResponseEntity<ResponseMessageDTO> confirmOTP(@Valid @RequestBody CofirmOTPBankDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID clientMessageId = UUID.randomUUID();
			Map<String, Object> data = new HashMap<>();
			data.put("requestId", dto.getRequestId());
			data.put("otpValue", dto.getOtpValue());
			data.put("authenType", "SMS");
			data.put("applicationType", dto.getApplicationType());
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(EnvironmentUtil.getBankUrl()
							+ "private/ms/push-mesages-partner/v1.0/bdsd/subscribe/confirm")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(
							EnvironmentUtil.getBankUrl()
									+ "private/ms/push-mesages-partner/v1.0/bdsd/subscribe/confirm")
					.build();
			Mono<ClientResponse> responseMono = webClient.post()
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_JSON)
					.header("clientMessageId", clientMessageId.toString())
					.header("transactionId", RandomCodeUtil.generateRandomUUID())
					.header("Authorization", "Bearer " + getBankToken().getAccess_token())
					.body(BodyInserters.fromValue(data))
					.exchange();
			ClientResponse response = responseMono.block();
			if (response.statusCode().is2xxSuccessful()) {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response requestOTP: " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				String status = rootNode.get("data").get("status").asText();
				if (status.equals("Success")) {
					result = new ResponseMessageDTO("SUCCESS",
							"");
					httpStatus = HttpStatus.OK;
				} else {
					result = new ResponseMessageDTO("FAILED",
							"E05");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				ConfirmRequestFailedBankDTO confirmRequestBankDTO = response.bodyToMono(
						ConfirmRequestFailedBankDTO.class)
						.block();
				LocalDateTime currentDateTime = LocalDateTime.now();
				logger.error("Response confirmOTP error: " + confirmRequestBankDTO.getSoaErrorCode() + "-"
						+ confirmRequestBankDTO.getSoaErrorDesc() + " at "
						+ currentDateTime.toEpochSecond(ZoneOffset.UTC));
				String status = "FAILED";
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode());
				result = new ResponseMessageDTO(status, message);
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (HttpClientErrorException ex) {
			logger.error("HttpClientErrorException: " + ex.getMessage());
			logger.error("Response body: " + ex.getResponseBodyAsString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		} catch (HttpServerErrorException ex) {
			logger.error("HttpServerErrorException: " + ex.getMessage());
			logger.error("Response body: " + ex.getResponseBodyAsString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;

		} catch (Exception e) {
			logger.error("Error at confirmOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	String getMessageBankCode(String errBankCode) {
		String result = "E05";
		if (errBankCode.equals("293")) {
			result = "E15";
		} else if (errBankCode.equals("40503")) {
			result = "E16";
		} else if (errBankCode.equals("1020")) {
			result = "E17";
		} else if (errBankCode.equals("40600")) {
			result = "E18";
		} else if (errBankCode.equals("219")) {
			result = "E19";
		} else if (errBankCode.equals("40017")) {
			result = "E20";
		} else if (errBankCode.equals("40506")) {
			result = "E21";
		} else if (errBankCode.equals("40509")) {
			result = "E22";
		} else if (errBankCode.equals("40504")) {
			result = "E23";
		} else if (errBankCode.equals("40507")) {
			result = "E24";
		} else if (errBankCode.equals("40505")) {
			result = "E25";
		} else if (errBankCode.equals("203")) {
			result = "E26";
		} else if (errBankCode.equals("4630")) {
			result = "E27";
		} else if (errBankCode.equals("237")) {
			result = "E28";
		} else if (errBankCode.equals("002")) {
			result = "E29";
		}
		return result;
	}

	@PostMapping("callback-login")
	public ResponseEntity<String> callBackLogin(@Valid @RequestBody AccountLoginDTO dto) {
		String result = "";
		HttpStatus httpStatus = null;
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("phoneNo", dto.getPhoneNo());
			data.put("password", dto.getPassword());
			UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl("http://112.78.1.220:8084/vqr/api/accounts")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl("http://112.78.1.220:8084/vqr/api/accounts")
					.build();
			// Call POST API
			String response = webClient.method(HttpMethod.POST)
					.uri(uriComponents.toUri())
					.headers(httpHeaders -> {
						httpHeaders.add("Content-Type", "application/json");
						// httpHeaders.add("Authorization", "Bearer
						// eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJCTlMwMiIsImZpcnN0TmFtZSI6IkhpZXUiLCJtaWRkbGVOYW1lIjoiRHVjIiwibGFzdE5hbWUiOiJQaGFtIiwiYmlydGhEYXRlIjoiMTcvMDkvMTk5NyIsImdlbmRlciI6MCwiYWRkcmVzcyI6IkVjb3BhcmsiLCJlbWFpbCI6IiIsImltZ0lkIjoiNzc4Zjg5YzAtNGM5MS00ZDViLWJmNTQtNDU4ZGMzODdiYTNmIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTY3NjUzNzc2NSwiZXhwIjoxNjc3NDM3NzY1fQ.7k1UGPCqRO6SX3egXDgo6aUgKWHIO7gBtGxikiFRDTYKy9h6GkUYjIIlnIJGAoCYOEATTuYTrJ6Dk-YtHhUatQ");
					})
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(data))
					.exchange().flatMap(clientResponse -> {
						if (clientResponse.statusCode().is5xxServerError()) {
							clientResponse.body((clientHttpResponse, context) -> {
								return clientHttpResponse.getBody();
							});
							return clientResponse.bodyToMono(String.class);
						} else
							return clientResponse.bodyToMono(String.class);
					})
					.block();
			result = response;
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at callBackLogin: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private TransactionResponseDTO validateTransactionBank(TransactionBankDTO dto, String reftransactionid) {
		TransactionResponseDTO result = new TransactionResponseDTO(true, "005", "Unexpected error");
		try {
			if (dto != null) {
				if (dto.getAmount() == 0) {
					result = new TransactionResponseDTO(true, "002", "Invalid amount");
				} else if (dto.getTransactionid() == null || dto.getTransactionid().trim().isEmpty()) {
					result = new TransactionResponseDTO(true, "007", "transactionid is invalid");
				} else if (dto.getReferencenumber() == null || dto.getReferencenumber().trim().isEmpty()) {
					result = new TransactionResponseDTO(true, "008", "referencenumber is invalid");
				} else if (dto.getContent() == null) {
					result = new TransactionResponseDTO(true, "009", "content is invalid");
				} else if (dto.getBankaccount() == null || dto.getBankaccount().isEmpty()) {
					result = new TransactionResponseDTO(true, "010", "bankaccount is invalid");
				} else if (dto.getTransactiontime() == 0) {
					result = new TransactionResponseDTO(true, "004", "Invalid transaction time");
				} else if (!dto.getTransType().trim().equals("D") && !dto.getTransType().trim().equals("C")) {
					result = new TransactionResponseDTO(true, "003", "Invalid transaction type");
				} else {
					result = new TransactionResponseDTO(false, "000", "", new RefTransactionDTO(reftransactionid));
				}
			} else {
				result = new TransactionResponseDTO(true, "001", "Invalid request body");
			}
		} catch (Exception e) {
			result = new TransactionResponseDTO(true, "005", "Unexpected error");
		}
		return result;
	}

	// check duplicate referenceNumber fromMB
	private boolean checkDuplicateReferenceNumber(String refNumber) {
		boolean result = false;
		try {
			String check = transactionBankService.checkExistedReferenceNumber(refNumber);
			if (check == null || check.isEmpty()) {
				result = true;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return result;
	}

	// @Async
	// private CompletableFuture<TokenDTO> getCustomerSyncToken(CustomerSyncEntity
	// entity) {
	// return CompletableFuture.supplyAsync(() -> {
	// String key = entity.getUsername() + ":" + entity.getPassword();
	// String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
	// String suffixUrl = entity.getSuffixUrl() != null &&
	// !entity.getSuffixUrl().isEmpty()
	// ? entity.getSuffixUrl()
	// : "";
	// UriComponents uriComponents = UriComponentsBuilder
	// .fromHttpUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/"
	// + suffixUrl
	// + "/api/token_generate")
	// .buildAndExpand();
	// WebClient webClient = WebClient.builder()
	// .baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" +
	// suffixUrl
	// + "/api/token_generate")
	// .build();
	// TokenDTO response = webClient.method(HttpMethod.POST)
	// .uri(uriComponents.toUri())
	// .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	// .header("Authorization", "Basic " + encodedKey)
	// .retrieve()
	// .onStatus(HttpStatus::isError, clientResponse -> {
	// logger.info("getCustomerSyncToken - clientHttpResponse <> status code 200: "
	// + clientResponse.bodyToMono(String.class).block());
	// System.out.println("getCustomerSyncToken - clientHttpResponse <> status code
	// 200: "
	// + clientResponse.bodyToMono(String.class).block());
	// return Mono.error(new RuntimeException("Failed to retrieve access token"));
	// })
	// .bodyToMono(TokenDTO.class)
	// .block();
	// logger.info("get access token response: " + response.getAccess_token());
	// System.out.println("get access token response: " +
	// response.getAccess_token());
	// return response;
	// });
	// }

	// @Async
	// private CompletableFuture<TransactionResponseDTO>
	// pushNewTransactionToCustomerSync(CustomerSyncEntity entity,
	// TransactionBankDTO dto, long time) {
	// return getCustomerSyncToken(entity)
	// .thenCompose(tokenDTO -> {
	// Map<String, Object> data = new HashMap<>();
	// data.put("transactionid", dto.getTransactionid());
	// data.put("transactiontime", dto.getTransactiontime());
	// data.put("referencenumber", dto.getReferencenumber());
	// data.put("amount", dto.getAmount());
	// data.put("content", dto.getContent());
	// data.put("bankaccount", dto.getBankaccount());
	// data.put("transType", dto.getTransType());

	// String suffixUrl = entity.getSuffixUrl() != null &&
	// !entity.getSuffixUrl().isEmpty()
	// ? entity.getSuffixUrl()
	// : "";
	// UriComponents uriComponents = UriComponentsBuilder
	// .fromHttpUrl("https://" + entity.getIpAddress() + ":" + entity.getPort() +
	// "/"
	// + suffixUrl + "/api/transaction-sync")
	// .buildAndExpand(/* add url parameter here */);

	// WebClient webClient = WebClient.builder()
	// .baseUrl("https://" + entity.getIpAddress() + ":" + entity.getPort() + "/" +
	// suffixUrl
	// + "/api/transaction-sync")
	// .build();

	// return webClient.post()
	// .uri(uriComponents.toUri())
	// .contentType(MediaType.APPLICATION_JSON)
	// .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
	// .body(BodyInserters.fromValue(data))
	// .retrieve()
	// .bodyToMono(TransactionResponseDTO.class)
	// .toFuture();
	// });
	// }

	// @Async
	// private CompletableFuture<Void> getCustomerSyncEntities(TransactionBankDTO
	// dto, long time) {
	// try {
	// List<CustomerSyncEntity> list = new ArrayList<>();
	// list = customerSyncService.getCustomerSyncEntities();
	// if (list != null && !list.isEmpty()) {
	// logger.info("getCustomerSyncEntities size: " + list.size());
	// System.out.println("getCustomerSyncEntities size:" + list.size());
	// List<CompletableFuture<TransactionResponseDTO>> futures = new ArrayList<>();
	// for (CustomerSyncEntity entity : list) {
	// futures.add(pushNewTransactionToCustomerSync(entity, dto, time));
	// }
	// return CompletableFuture.allOf(futures.toArray(new
	// CompletableFuture[futures.size()]));
	// } else {
	// logger.info("getCustomerSyncEntities empty.");
	// System.out.println("getCustomerSyncEntities empty.");
	// return CompletableFuture.completedFuture(null);
	// }
	// } catch (Exception e) {
	// logger.error("Error at getCustomerSyncEntities: " + e.toString());
	// System.out.println("Error at getCustomerSyncEntities: " + e.toString());
	// return CompletableFuture.completedFuture(null);
	// }
	// }

	private TokenDTO getCustomerSyncToken(CustomerSyncEntity entity) {
		TokenDTO result = null;
		try {
			String key = entity.getUsername() + ":" + entity.getPassword();
			String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
			logger.info("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
					+ entity.getPassword());
			System.out.println("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
					+ entity.getPassword());
			String suffixUrl = entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty() ? entity.getSuffixUrl()
					: "";
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
							+ "/api/token_generate")
					.buildAndExpand();
			WebClient webClient = WebClient.builder()
					.baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
							+ "/api/token_generate")
					.build();
			System.out.println("uriComponents: " + uriComponents.toString());
			Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Basic " + encodedKey)
					.exchange()
					.flatMap(clientResponse -> {
						System.out.println("status code: " + clientResponse.statusCode());
						if (clientResponse.statusCode().is2xxSuccessful()) {
							return clientResponse.bodyToMono(TokenDTO.class);
						} else {
							return clientResponse.bodyToMono(String.class)
									.flatMap(error -> {
										logger.info("Error response: " + error);
										return Mono.empty();
									});
						}
					});

			Optional<TokenDTO> resultOptional = responseMono.subscribeOn(Schedulers.boundedElastic()).blockOptional();
			if (resultOptional.isPresent()) {
				result = resultOptional.get();
				logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getIpAddress());
				System.out.println("Token got: " + result.getAccess_token() + " - from: " + entity.getIpAddress());
			} else {
				logger.info("Token could not be retrieved from: " + entity.getIpAddress());
				System.out.println("Token could not be retrieved from: " + entity.getIpAddress());
			}
			///
		} catch (Exception e) {
			logger.error("Error at getCustomerSyncToken: " + entity.getIpAddress() + " - " + e.toString());
			System.out.println("Error at getCustomerSyncToken: " + entity.getIpAddress() + " - " + e.toString());
		}
		return result;
	}

	private void pushNewTransactionToCustomerSync(CustomerSyncEntity entity, TransactionBankDTO dto, long time) {
		try {

			TokenDTO tokenDTO = getCustomerSyncToken(entity);
			if (tokenDTO != null) {
				Map<String, Object> data = new HashMap<>();
				data.put("transactionid", dto.getTransactionid());
				data.put("transactiontime", dto.getTransactiontime());
				data.put("referencenumber", dto.getReferencenumber());
				data.put("amount", dto.getAmount());
				data.put("content", dto.getContent());
				data.put("bankaccount", dto.getBankaccount());
				data.put("transType", dto.getTransType());
				String suffixUrl = "";
				if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
					suffixUrl = entity.getSuffixUrl();
				}
				UriComponents uriComponents = UriComponentsBuilder
						.fromHttpUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
								+ "/bank/api/transaction-sync")
						.buildAndExpand(/* add url parameter here */);
				WebClient webClient = WebClient.builder()
						.baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
								+ "/bank/api/transaction-sync")
						.build();
				logger.info("uriComponents: " + uriComponents.toString());
				Mono<TransactionResponseDTO> responseMono = webClient.post()
						.uri(uriComponents.toUri())
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + tokenDTO.getAccess_token())
						.body(BodyInserters.fromValue(data))
						.retrieve()
						.bodyToMono(TransactionResponseDTO.class);
				responseMono.subscribe(transactionResponseDTO -> {
					if (transactionResponseDTO != null && transactionResponseDTO.getObject() != null) {
						logger.info("pushNewTransactionToCustomerSync SUCCESS: " + entity.getIpAddress() + " - "
								+ transactionResponseDTO.getObject().getReftransactionid());
						System.out.println("pushNewTransactionToCustomerSync SUCCESS: " + entity.getIpAddress() + " - "
								+ transactionResponseDTO.getObject().getReftransactionid());
					} else {
						logger.error("Error at pushNewTransactionToCustomerSync: " + entity.getIpAddress() + " - "
								+ (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() : ""));
						System.out.println("Error at pushNewTransactionToCustomerSync: " + entity.getIpAddress() + " - "
								+ (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() : ""));
					}
				}, error -> {
					logger.error("Error at pushNewTransactionToCustomerSync: " + entity.getIpAddress() + " - "
							+ error.toString());
					System.out.println("Error at pushNewTransactionToCustomerSync: " + entity.getIpAddress() + " - "
							+ error.toString());
				});
			}

		} catch (Exception e) {
			logger.error("Error at pushNewTransactionToCustomerSync: " + entity.getIpAddress() + " - " + e.toString());
			System.out.println(
					"Error at pushNewTransactionToCustomerSync: " + entity.getIpAddress() + " - " + e.toString());
		}
	}

	private void getCustomerSyncEntities(TransactionBankDTO dto, long time) {
		try {
			List<CustomerSyncEntity> list = new ArrayList<>();
			list = customerSyncService.getCustomerSyncEntities();
			if (list != null && !list.isEmpty()) {
				for (CustomerSyncEntity entity : list) {
					pushNewTransactionToCustomerSync(entity, dto, time);
				}
			} else {
				logger.info("getCustomerSyncEntities empty.");
				System.out.println("getCustomerSyncEntities empty.");
			}
		} catch (Exception e) {
			logger.error("Error at getCustomerSyncEntities: " + e.toString());
			System.out.println("Error at getCustomerSyncEntities: " + e.toString());
		}
	}

}
