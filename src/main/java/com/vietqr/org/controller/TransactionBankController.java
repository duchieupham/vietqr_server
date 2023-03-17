package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.ZoneOffset;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import org.apache.log4j.Logger;

import com.vietqr.org.dto.AccountLoginDTO;
import com.vietqr.org.dto.RefTransactionDTO;
import com.vietqr.org.dto.TransactionBankDTO;
import com.vietqr.org.dto.TransactionResponseDTO;
import com.vietqr.org.service.TransactionBankService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.AccountBankReceiveService;

import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.AccountBankReceiveEntity;

import com.vietqr.org.dto.FcmRequestDTO;

import com.vietqr.org.util.NotificationUtil;

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
	NotificationService notificationService;

	@Autowired
	FcmTokenService fcmTokenService;

	private FirebaseMessagingService firebaseMessagingService;

	public TransactionBankController(FirebaseMessagingService firebaseMessagingService) {
		this.firebaseMessagingService = firebaseMessagingService;
	}

	@PostMapping("transaction-sync")
	public ResponseEntity<TransactionResponseDTO> insertTranscationBank(@RequestBody TransactionBankDTO dto) {
		TransactionResponseDTO result = null;
		HttpStatus httpStatus = null;
		UUID uuid = UUID.randomUUID();
		try {
			List<Object> list = transactionBankService.checkTransactionIdInserted(dto.getTransactionid());
			if (list.isEmpty()) {
				result = validateTransactionBank(dto, uuid.toString());
				if (!result.isError()) {
					transactionBankService.insertTransactionBank(dto.getTransactionid(), dto.getTransactiontime(),
							dto.getReferencenumber(), dto.getAmount(), dto.getContent(), dto.getBankaccount(),
							dto.getTransType(), dto.getReciprocalAccount(), dto.getReciprocalBankCode(), dto.getVa(),
							dto.getValueDate(), uuid.toString());
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
			System.out.println("Error at insertTranscationBank: " + e.toString());
			result = new TransactionResponseDTO(true, "005", "Unexpected error");
			httpStatus = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			// find transaction by id
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			LocalDateTime currentDateTime = LocalDateTime.now();
			String transcationId = dto.getContent().split("  ")[0];
			if (transcationId != null && !transcationId.isBlank()) {
				TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
						.getTransactionById(transcationId);
				if (transactionReceiveEntity != null) {
					AccountBankReceiveEntity accountBankEntity = accountBankService
							.getAccountBankById(transactionReceiveEntity.getBankId()); // update status
					// transaction
					transactionReceiveService.updateTransactionReceiveStatus(1, uuid.toString(), transcationId);
					// find transaction-branch-receive to push notification
					TransactionReceiveBranchEntity transactionBranchEntity = transactionReceiveBranchService
							.getTransactionBranchByTransactionId(transcationId);
					if (transactionBranchEntity != null) {
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
										+ branchEntity.getName();
								// String title = NotificationUtil.getNotiTitleNewTransaction();
								notiEntity.setId(notificationUUID.toString());
								notiEntity.setRead(false);
								notiEntity.setMessage(message);
								notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
								notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
								notiEntity.setUserId(userId);
								notiEntity.setData(transactionReceiveEntity.getId());
								notificationService.insertNotification(notiEntity);
								// push notification
								List<FcmTokenEntity> fcmTokens = new ArrayList<>();
								fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
								if (fcmTokens != null && !fcmTokens.isEmpty()) {
									for (FcmTokenEntity fcmToken : fcmTokens) {
										try {
											FcmRequestDTO fcmDTO = new FcmRequestDTO();
											fcmDTO.setTitle(NotificationUtil.getNotiTitleUpdateTransaction());
											fcmDTO.setMessage(message);
											fcmDTO.setToken(fcmToken.getToken());
											firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
											logger.info("Send notification to device " + fcmToken.getToken());
										} catch (Exception e) {
											System.out.println("Error at send noti" + e.toString());
											logger.error(
													"Error when Send Notification using FCM " + e.toString());
											if (e.toString()
													.contains(
															"The registration token is not a valid FCM registration token")) {
												fcmTokenService.deleteFcmToken(fcmToken.getToken());
											}

										}
									}
								}
							}
						} else {
							// không tồn tài userId nào
						}
					} else {
						// Không có transaction-branch tương ứng nào
					}
				} else {
					// Không có giao dịch nào trùng id, trạng thái: chưa thanh toán,
				}

			}

		}
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
}
