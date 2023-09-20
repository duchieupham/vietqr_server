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
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Base64;

import javax.validation.Valid;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
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

import org.apache.log4j.Logger;

import com.vietqr.org.dto.AccountBankNameDTO;
import com.vietqr.org.dto.AccountLoginDTO;
import com.vietqr.org.dto.CofirmOTPBankDTO;
import com.vietqr.org.dto.RefTransactionDTO;
import com.vietqr.org.dto.TransactionBankDTO;
import com.vietqr.org.dto.TransactionResponseDTO;
import com.vietqr.org.dto.TransactionTestCallbackDTO;
import com.vietqr.org.dto.UnregisterBankConfirmDTO;
import com.vietqr.org.dto.UnregisterRequestDTO;
import com.vietqr.org.service.TransactionBankService;
import com.vietqr.org.service.TransactionReceiveService;
import com.vietqr.org.service.TransactionWalletService;
import com.vietqr.org.service.TransactionReceiveBranchService;
import com.vietqr.org.service.TransactionReceiveLogService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.BusinessInformationService;
import com.vietqr.org.service.CustomerSyncService;
import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.TelegramAccountBankService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.LarkAccountBankService;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.AccountCustomerBankService;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.AccountWalletService;
import com.vietqr.org.service.BankReceiveBranchService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.TransactionReceiveLogEntity;
import com.vietqr.org.entity.TransactionWalletEntity;
import com.vietqr.org.entity.TransactionReceiveBranchEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.BranchInformationEntity;
import com.vietqr.org.entity.BusinessInformationEntity;
import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.AccountCustomerBankEntity;
import com.vietqr.org.entity.AccountWalletEntity;
import com.vietqr.org.entity.BankReceiveBranchEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.dto.TransactionBankCustomerDTO;
import com.vietqr.org.dto.ConfirmRequestFailedBankDTO;
import com.vietqr.org.dto.RequestBankDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TokenDTO;

import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.SocketHandler;
import com.vietqr.org.util.TelegramUtil;
import com.vietqr.org.util.VNPTEpayUtil;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.LarkUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@RestController
@CrossOrigin
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
	TransactionWalletService transactionWalletService;

	@Autowired
	AccountWalletService accountWalletService;

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	private CustomerSyncService customerSyncService;

	@Autowired
	private SocketHandler socketHandler;

	@Autowired
	TelegramAccountBankService telegramAccountBankService;

	@Autowired
	TransactionReceiveLogService transactionReceiveLogService;

	@Autowired
	AccountLoginService accountLoginService;

	@Autowired
	LarkAccountBankService larkAccountBankService;

	@Autowired
	BankReceiveBranchService bankReceiveBranchService;

	@Autowired
	private AccountCustomerBankService accountCustomerBankService;

	private FirebaseMessagingService firebaseMessagingService;

	public TransactionBankController(FirebaseMessagingService firebaseMessagingService) {
		this.firebaseMessagingService = firebaseMessagingService;
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

	// API test callback transaction for users
	// 1. Input DTO must be contain:
	// bạnkAccount, token save username, content, amount, transType,

	@PostMapping("test/transaction-callback")
	public ResponseEntity<ResponseMessageDTO> testCallbackForCustomer(
			@RequestHeader("Authorization") String token,
			@RequestBody TransactionTestCallbackDTO callbackDTO) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (EnvironmentUtil.isProduction() == false) {
				// 2. Check customer_sync whether existed ussername into token. List<Object>
				String username = getUsernameFromToken(token);
				if (username != null && !username.trim().isEmpty()) {
					if (callbackDTO != null) {
						System.out.println("username: " + username);
						List<String> checkExistedCustomerSync = accountCustomerBankService
								.checkExistedCustomerSyncByUsername(username);
						if (checkExistedCustomerSync != null && !checkExistedCustomerSync.isEmpty()) {
							// 3. If existed, find account_customer_bank by customer_sync_id AND
							// bank_account
							for (String customerSyncId : checkExistedCustomerSync) {
								List<String> checkExistedAccCusBank = accountCustomerBankService
										.checkExistedAccountCustomerBankByBankAccount(callbackDTO.getBankAccount(),
												customerSyncId);
								//
								if (checkExistedAccCusBank != null && !checkExistedAccCusBank.isEmpty()) {
									// 4. If found, find transaction by bankAccount, content, amount, transType (as
									// insert transaction Flow)
									TransactionBankDTO dto = new TransactionBankDTO();
									UUID transactionId = UUID.randomUUID();
									UUID referenceNumber = UUID.randomUUID();
									LocalDateTime transactionLD = LocalDateTime.now();
									long transactionTime = transactionLD.toEpochSecond(ZoneOffset.UTC);
									dto.setTransactionid(transactionId.toString());
									dto.setTransactiontime(transactionTime * 1000);
									dto.setReferencenumber(username + "-" + referenceNumber.toString());
									dto.setAmount(Integer.parseInt(callbackDTO.getAmount()));
									dto.setContent(callbackDTO.getContent());
									dto.setBankaccount(callbackDTO.getBankAccount());
									dto.setTransType(callbackDTO.getTransType());
									dto.setReciprocalAccount(null);
									dto.setReciprocalBankCode(null);
									dto.setVa(null);
									dto.setValueDate(0);
									result = insertTransBank(dto);
									if (result.getStatus().equals("SUCCESS")) {
										httpStatus = HttpStatus.OK;
									} else {
										httpStatus = HttpStatus.BAD_REQUEST;
									}
								} else {
									System.out.println("BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
									logger.error("BANK ACCOUNT IS NOT MATCH WITH MERCHANT INFO");
									result = new ResponseMessageDTO("FAILED", "E77");
									httpStatus = HttpStatus.BAD_REQUEST;
								}
							}

						} else {
							System.out.println("MERCHANT IS NOT EXISTED");
							logger.error("MERCHANT IS NOT EXISTED");
							result = new ResponseMessageDTO("FAILED", "E76");
							httpStatus = HttpStatus.BAD_REQUEST;
						}
					} else {
						System.out.println("INVALID REQUEST BODY");
						logger.error("INVALID REQUEST BODY");
						result = new ResponseMessageDTO("FAILED", "E46");
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					System.out.println("INVALID TOKEN");
					logger.error("INVALID TOKEN");
					result = new ResponseMessageDTO("FAILED", "E74");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				System.out.println("SERVICE IS NOT AVAILABLE");
				logger.error("SERVICE IS NOT AVAILABLE");
				result = new ResponseMessageDTO("FAILED", "E75");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			System.out.println("testCallbackForCusto: ERROR: " + e.toString());
			logger.error("testCallbackForCusto: ERROR: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	public ResponseMessageDTO insertTransBank(TransactionBankDTO dto) {
		ResponseMessageDTO result = null;
		//
		UUID uuid = UUID.randomUUID();
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		LocalDateTime currentDateTime = LocalDateTime.now();
		long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
		logger.info("receive transaction sync from MB: " + dto.toString() + " at: " + time);
		// System.out.println("receive transaction sync from MB: " + dto.toString() + "
		// at: " + time);
		boolean checkDuplicate = checkDuplicateReferenceNumber(dto.getReferencenumber(), dto.getTransType());
		TransactionResponseDTO resultCheck = validateTransactionBank(dto, uuid.toString());
		// String bankCode = "MB";
		// String bankTypeId = bankTypeService.getBankTypeIdByBankCode(bankCode);
		// because of bank_type default is MB => aa4e489b-254e-4351-9cd4-f62e09c63ebc
		String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
		AccountBankReceiveEntity accountBankEntity = accountBankService
				.getAccountBankByBankAccountAndBankTypeId(dto.getBankaccount(), bankTypeId);
		try {
			List<Object> list = transactionBankService.checkTransactionIdInserted(dto.getTransactionid(),
					dto.getTransType());
			// System.out.println("list size: " + list.size());
			if (list != null && list.isEmpty()) {
				if (!resultCheck.isError()) {
					// System.out.println("checkDuplicate: " + checkDuplicate);
					if (checkDuplicate) {
						if (accountBankEntity != null) {
							if (accountBankEntity.isMmsActive() == true
									&& dto.getTransType().trim().toUpperCase().equals("C")) {
								// System.out.println("isMmsActive");
								logger.info("Transaction-sync: mms_active = true => do not insert transaction_bank");
							} else {
								// System.out.println("isMmsActive = false");
								transactionBankService.insertTransactionBank(dto.getTransactionid(),
										dto.getTransactiontime(),
										dto.getReferencenumber(), dto.getAmount(), dto.getContent(),
										dto.getBankaccount(),
										dto.getTransType(), dto.getReciprocalAccount(), dto.getReciprocalBankCode(),
										dto.getVa(),
										dto.getValueDate(), uuid.toString());
								result = new ResponseMessageDTO("SUCCESS", "");
							}
						} else {
							// System.out.println("accountBankEntity = null");
							logger.info("accountBankEntity = null");
							transactionBankService.insertTransactionBank(dto.getTransactionid(),
									dto.getTransactiontime(),
									dto.getReferencenumber(), dto.getAmount(), dto.getContent(),
									dto.getBankaccount(),
									dto.getTransType(), dto.getReciprocalAccount(), dto.getReciprocalBankCode(),
									dto.getVa(),
									dto.getValueDate(), uuid.toString());
							result = new ResponseMessageDTO("SUCCESS", "");
						}
					} else {
						logger.error("Transaction-sync: Duplicate Reference number");
					}
				} else {
					logger.error("Transaction-sync:  Error receive data: " + "Unexpected Error");
					result = new ResponseMessageDTO("FAILED", resultCheck.getErrorReason());
				}
			} else {
				result = new ResponseMessageDTO("FAILED", "006");
				logger.error("Error receive transaction-sync: " + "Duplicated transactionid");
			}

		} catch (Exception e) {
			logger.error("Error at transaction-sync: " + e.toString());
			// System.out.println("Error at transaction-sync: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05" + " - " + e.toString());
		} finally {
			// AccountBankReceiveEntity accountBankEntity = accountBankService
			// .getAccountBankById(transactionReceiveEntity.getBankId());
			if (accountBankEntity != null) {
				if (accountBankEntity.isMmsActive() == true && dto.getTransType().trim().toUpperCase().equals("C")) {
					logger.info(
							"Transaction-sync: mms_active = true => do not update transaction_receive AND push data to customer sync");
				} else {
					if (!resultCheck.isError()) {
						if (checkDuplicate) {
							if (accountBankEntity != null) {
								// find transaction by id
								String traceId = getTraceId(dto.getContent(), "VQR");
								String orderId = "";
								String sign = "";
								if (traceId != null && !traceId.isEmpty()) {
									logger.info("transaction-sync - trace ID detect: " + traceId);
									TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
											.getTransactionByTraceIdAndAmount(traceId, dto.getAmount() + "",
													dto.getTransType().trim().toUpperCase());
									if (transactionReceiveEntity != null) {
										orderId = transactionReceiveEntity.getOrderId();
										sign = transactionReceiveEntity.getSign();
										result = getCustomerSyncEntities(transactionReceiveEntity.getId(), dto,
												accountBankEntity, time, orderId, sign);
										updateTransaction(dto, transactionReceiveEntity, accountBankEntity, time, nf);
										// check if recharge => do update status and push data to customer
										////////// USER RECHAGE VQR || USER RECHARGE MOBILE
										if (transactionReceiveEntity.getType() == 5) {
											// find transactionWallet by billNumber and status = 0
											TransactionWalletEntity transactionWalletEntity = transactionWalletService
													.getTransactionWalletByBillNumber(orderId);
											processTransactionWallet(nf, time, dto, orderId, transactionWalletEntity);
										}
									} else {
										logger.info(
												"transaction-sync - cannot find transaction receive. Receive new transaction outside system");
										// process here
										UUID transcationUUID = UUID.randomUUID();
										result = getCustomerSyncEntities(transcationUUID.toString(), dto,
												accountBankEntity, time, orderId, sign);
										insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity, time,
												traceId, uuid, nf, "", "");
									}
									// }
								} else {
									logger.info(
											"transaction-sync - traceId is empty. Receive new transaction outside system");
									UUID transcationUUID = UUID.randomUUID();
									result = getCustomerSyncEntities(transcationUUID.toString(), dto, accountBankEntity,
											time, orderId, sign);
									insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity, time,
											traceId, uuid, nf, "", "");
								}
							}
						} else {
							logger.error("Transaction-sync: Duplicate Reference number");
						}
					} else {
						// logger.error("Transaction-sync: Error receive data: " + result.toString());
						logger.error("Transaction-sync:  Error receive data: ");
					}
				}

			} else {
				logger.info("transaction-sync - cannot find account bank or account bank is deactive");
			}

		}
		return result;
	}

	// Receive BDSD from MB Bank, so bankCode = 'MB'
	@PostMapping("transaction-sync")
	public ResponseEntity<TransactionResponseDTO> insertTranscationBank(@RequestBody TransactionBankDTO dto) {
		TransactionResponseDTO result = null;
		HttpStatus httpStatus = null;
		UUID uuid = UUID.randomUUID();
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		LocalDateTime currentDateTime = LocalDateTime.now();
		long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
		logger.info("receive transaction sync from MB: " + dto.toString() + " at: " + time);
		// System.out.println("receive transaction sync from MB: " + dto.toString() + "
		// at: " + time);
		boolean checkDuplicate = checkDuplicateReferenceNumber(dto.getReferencenumber(), dto.getTransType());
		result = validateTransactionBank(dto, uuid.toString());
		// String bankCode = "MB";
		// String bankTypeId = bankTypeService.getBankTypeIdByBankCode(bankCode);
		// because of bank_type default is MB => aa4e489b-254e-4351-9cd4-f62e09c63ebc
		String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
		AccountBankReceiveEntity accountBankEntity = accountBankService
				.getAccountBankByBankAccountAndBankTypeId(dto.getBankaccount(), bankTypeId);
		try {
			List<Object> list = transactionBankService.checkTransactionIdInserted(dto.getTransactionid(),
					dto.getTransType());
			// System.out.println("list size: " + list.size());
			if (list != null && list.isEmpty()) {
				if (!result.isError()) {
					// System.out.println("checkDuplicate: " + checkDuplicate);
					if (checkDuplicate) {
						if (accountBankEntity != null) {
							if (accountBankEntity.isMmsActive() == true
									&& dto.getTransType().trim().toUpperCase().equals("C")) {
								// System.out.println("isMmsActive");
								logger.info("Transaction-sync: mms_active = true => do not insert transaction_bank");
							} else {
								// System.out.println("isMmsActive = false");
								transactionBankService.insertTransactionBank(dto.getTransactionid(),
										dto.getTransactiontime(),
										dto.getReferencenumber(), dto.getAmount(), dto.getContent(),
										dto.getBankaccount(),
										dto.getTransType(), dto.getReciprocalAccount(), dto.getReciprocalBankCode(),
										dto.getVa(),
										dto.getValueDate(), uuid.toString());
								// System.out.println("After insert");
							}
						} else {
							// System.out.println("accountBankEntity = null");
							logger.info("accountBankEntity = null");
							transactionBankService.insertTransactionBank(dto.getTransactionid(),
									dto.getTransactiontime(),
									dto.getReferencenumber(), dto.getAmount(), dto.getContent(),
									dto.getBankaccount(),
									dto.getTransType(), dto.getReciprocalAccount(), dto.getReciprocalBankCode(),
									dto.getVa(),
									dto.getValueDate(), uuid.toString());
						}
					} else {
						logger.error("Transaction-sync: Duplicate Reference number");
					}
					httpStatus = HttpStatus.OK;
				} else {
					logger.error("Transaction-sync:  Error receive data: " + "Unexpected Error");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
				result = new TransactionResponseDTO(true, "006", "Duplicated transactionid");
				logger.error("Error receive transaction-sync: " + "Duplicated transactionid");
			}
			return new ResponseEntity<>(result, httpStatus);
		} catch (Exception e) {
			logger.error("Error at transaction-sync: " + e.toString());
			// System.out.println("Error at transaction-sync: " + e.toString());
			result = new TransactionResponseDTO(true, "005", "Unexpected error");
			httpStatus = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			// AccountBankReceiveEntity accountBankEntity = accountBankService
			// .getAccountBankById(transactionReceiveEntity.getBankId());
			if (accountBankEntity != null) {
				if (accountBankEntity.isMmsActive() == true && dto.getTransType().trim().toUpperCase().equals("C")) {
					logger.info(
							"Transaction-sync: mms_active = true => do not update transaction_receive AND push data to customer sync");
				} else {
					if (!result.isError()) {
						if (checkDuplicate) {
							if (accountBankEntity != null) {
								// find transaction by id
								String traceId = getTraceId(dto.getContent(), "VQR");
								String orderId = "";
								String sign = "";
								if (traceId != null && !traceId.isEmpty()) {
									logger.info("transaction-sync - trace ID detect: " + traceId);
									TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
											.getTransactionByTraceIdAndAmount(traceId, dto.getAmount() + "",
													dto.getTransType().trim().toUpperCase());
									if (transactionReceiveEntity != null) {
										orderId = transactionReceiveEntity.getOrderId();
										sign = transactionReceiveEntity.getSign();
										getCustomerSyncEntities(transactionReceiveEntity.getId(), dto,
												accountBankEntity, time, orderId, sign);
										updateTransaction(dto, transactionReceiveEntity, accountBankEntity, time, nf);
										// check if recharge => do update status and push data to customer
										////////// USER RECHAGE VQR || USER RECHARGE MOBILE
										if (transactionReceiveEntity.getType() == 5) {
											// find transactionWallet by billNumber and status = 0
											TransactionWalletEntity transactionWalletEntity = transactionWalletService
													.getTransactionWalletByBillNumber(orderId);
											processTransactionWallet(nf, time, dto, orderId, transactionWalletEntity);
										}
									} else {
										logger.info(
												"transaction-sync - cannot find transaction receive. Receive new transaction outside system");
										// process here
										UUID transcationUUID = UUID.randomUUID();
										getCustomerSyncEntities(transcationUUID.toString(), dto, accountBankEntity,
												time, orderId, sign);
										insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity, time,
												traceId, uuid, nf, "", "");
									}
									// }
								} else {
									logger.info(
											"transaction-sync - traceId is empty. Receive new transaction outside system");
									UUID transcationUUID = UUID.randomUUID();
									getCustomerSyncEntities(transcationUUID.toString(), dto, accountBankEntity, time,
											orderId, sign);
									insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity, time,
											traceId, uuid, nf, "", "");
								}
							}
						} else {
							logger.error("Transaction-sync: Duplicate Reference number");
						}
					} else {
						// logger.error("Transaction-sync: Error receive data: " + result.toString());
						logger.error("Transaction-sync:  Error receive data: ");
					}
				}

			} else {
				logger.info("transaction-sync - cannot find account bank or account bank is deactive");
			}

		}

	}

	public String getTraceId(String inputString, String prefix) {
		String result = "";
		try {
			inputString = inputString.replaceAll("\\.", " ");
			inputString = inputString.replaceAll("\\-", " ");
			String[] newPaths = inputString.split("\\s+");
			String traceId = "";
			int indexSaved = -1;
			for (int i = 0; i < newPaths.length; i++) {
				if (newPaths[i].contains(prefix)) {
					if (newPaths[i].length() >= 13) {
						traceId = newPaths[i].substring(0, 13);
						break;
					}
					traceId = newPaths[i];
					indexSaved = i;
				} else if (indexSaved != -1 && i == indexSaved + 1) {
					if (traceId.length() < 13) {
						traceId += newPaths[i].substring(0, Math.min(13 - traceId.length(), newPaths[i].length()));
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

			result = traceId;
		} catch (Exception e) {
			System.out.println("ERROR: " + e.toString());
		}

		return result;
	}

	@Async
	private void processTransactionWallet(NumberFormat nf, long time, TransactionBankDTO dto, String orderId,
			TransactionWalletEntity entity) {
		if (entity != null) {
			// update transaction wallet
			transactionWalletService.updateTransactionWalletStatus(1, time,
					entity.getId());
			// get userId on transaction wallet
			String userIdRecharge = entity.getUserId();
			if (userIdRecharge != null) {
				// check referenceNumber
				// referenceNumber != null && not empty => check ref.tran.
				// referenceNumber = null || empty => do insert VQR.
				if (entity.getReferenceNumber() != null
						&& !entity.getReferenceNumber().trim()
								.isEmpty()) {
					String[] parts = entity.getReferenceNumber()
							.split("\\*");
					if (parts.length == 5) {
						Integer paymentType = Integer.parseInt(parts[0]);
						String carrierCode = parts[1];
						String phoneNo = parts[2];
						String userId = parts[3];
						String otp = parts[4];
						if (paymentType != null && userId != null && otp != null) {
							// check valid transaction by otp & userId
							// & paymentType & status = 0
							String check = transactionWalletService
									.checkExistedTransactionnWallet(otp,
											userId, 1);
							if (check != null && !check.trim().isEmpty()) {
								// find tranMobile by Id
								TransactionWalletEntity tranMobileEntity = transactionWalletService
										.getTransactionWalletById(check);
								if (tranMobileEntity != null) {
									// call api topup
									String requestId = VNPTEpayUtil
											.createRequestID(EnvironmentUtil
													.getVnptEpayPartnerName());
									int topupResponseCode = VNPTEpayUtil.topup(
											requestId,
											EnvironmentUtil.getVnptEpayPartnerName(),
											carrierCode,
											phoneNo,
											Integer.parseInt(
													tranMobileEntity.getAmount() + ""));
									// initial notification data
									LocalDateTime localTimeM = LocalDateTime
											.now();
									long timeM = localTimeM
											.toEpochSecond(ZoneOffset.UTC);
									String msgErrorCode = "";
									UUID notificationUUID = UUID.randomUUID();
									String notiType = NotificationUtil
											.getNotiMobileTopup();
									String title = (topupResponseCode == 0) ? NotificationUtil
											.getNotiTitleMobileTopup()
											: NotificationUtil.getNotiTitleMobileTopupFailed();
									String message = "";
									if (topupResponseCode == 0) {
										message = NotificationUtil
												.getNotiDescMobileTopup1()
												+ "+"
												+ nf.format(Long.parseLong(
														tranMobileEntity.getAmount()))
												+ NotificationUtil
														.getNotiDescMobileTopup2()
												+ phoneNo
												+ NotificationUtil
														.getNotiDescMobileTopup3();
									} else {
										message = NotificationUtil.getNotiTitleMobileTopupFailed();
									}
									String status = (topupResponseCode == 0) ? "SUCCESS" : "FAILED";
									// process response
									if (topupResponseCode == 0) {
										// update transaction wallet mobile recharge by
										// paymentType
										// & userId & otp & status = 0
										transactionWalletService
												.updateTransactionWallet(1, timeM,
														tranMobileEntity.getAmount()
																+ "",
														userId, otp,
														1);
									} else if (topupResponseCode == 23 || topupResponseCode == 99) {
										msgErrorCode = "E62";
										transactionWalletService
												.updateTransactionWallet(3, timeM,
														tranMobileEntity.getAmount()
																+ "",
														userId, otp,
														1);
										System.out.println(
												"transaction-sync: : TRANSACTION FAILED: " + topupResponseCode);
										logger.error(
												"transaction-sync: : TRANSACTION FAILED: " + topupResponseCode);
									} else if (topupResponseCode == 35) {
										msgErrorCode = "E64";
										transactionWalletService
												.updateTransactionWallet(3, timeM,
														tranMobileEntity.getAmount()
																+ "",
														userId, otp,
														1);
										System.out.println(
												"transaction-sync:  VNPT EPAY BUSY TRAFFIC: "
														+ topupResponseCode);
										logger.error("transaction-sync:  VNPT EPAY BUSY TRAFFIC: "
												+ topupResponseCode);
									} else if (topupResponseCode == 109) {
										msgErrorCode = "E63";
										transactionWalletService
												.updateTransactionWallet(3, timeM,
														tranMobileEntity.getAmount()
																+ "",
														userId, otp,
														1);
										System.out.println(
												"transaction-sync: VNPT EPAY MAINTAN: " + topupResponseCode);
										logger.error("transaction-sync: VNPT EPAY MAINTAN: " + topupResponseCode);
									} else {
										msgErrorCode = "E62";
										transactionWalletService
												.updateTransactionWallet(3, timeM,
														tranMobileEntity.getAmount()
																+ "",
														userId, otp,
														1);
										System.out.println(
												"transaction-sync: TRANSACTION FAILED: " + topupResponseCode);
										logger.error("transaction-sync: TRANSACTION FAILED: " + topupResponseCode);
									}
									// push notification

									NotificationEntity notiEntity = new NotificationEntity();
									notiEntity.setId(notificationUUID.toString());
									notiEntity.setRead(false);
									notiEntity.setMessage(message);
									notiEntity.setTime(time);
									notiEntity.setType(
											notiType);
									notiEntity.setUserId(userId);
									notiEntity.setData(check);
									Map<String, String> data = new HashMap<>();
									data.put("notificationType",
											notiType);
									data.put("notificationId",
											notificationUUID.toString());
									data.put("amount",
											tranMobileEntity.getAmount() + "");
									data.put("transWalletId", check);
									data.put("time", time + "");
									data.put("phoneNo", phoneNo);
									data.put("billNumber", tranMobileEntity.getBillNumber());
									data.put("paymentMethod", "1");
									data.put("paymentType", "1");
									data.put("status", status);
									data.put("message", msgErrorCode);
									pushNotification(title, message, notiEntity, data, userId);
									//
								} else {
									System.out.println(
											"transaction-sync: TRAN MOBILE NULL");
									logger.error("transaction-sync: TRAN MOBILE NULL");
								}

							} else {
								System.out.println("transaction-sync: INVALID OTP");
								logger.error("transaction-sync: INVALID OTP");
							}

						} else {
							System.out.println(
									"transaction-sync: REFERENCE NUMBER INVALID: "
											+ entity
													.getReferenceNumber());
							logger.error("transaction-sync: REFERENCE NUMBER INVALID: "
									+ entity.getReferenceNumber());
						}
					}
				} else {
					// update amount account wallet
					AccountWalletEntity accountWalletEntity = accountWalletService
							.getAccountWalletByUserId(userIdRecharge);
					if (accountWalletEntity != null) {
						Long currentAmount = Long
								.parseLong(accountWalletEntity.getAmount());
						Long updatedAmount = currentAmount + dto.getAmount();
						accountWalletService.updateAmount(updatedAmount + "",
								accountWalletEntity.getId());
					}
					// push notification
					UUID notificationUUID = UUID.randomUUID();
					String notiType = NotificationUtil.getNotiRecharge();
					String title = NotificationUtil.getNotiTitleRecharge();
					String message = NotificationUtil.getNotiDescRecharge1()
							+ "+" + nf.format(dto.getAmount())
							+ NotificationUtil.getNotiDescRecharge2();
					NotificationEntity notiEntity = new NotificationEntity();
					notiEntity.setId(notificationUUID.toString());
					notiEntity.setRead(false);
					notiEntity.setMessage(message);
					notiEntity.setTime(time);
					notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
					notiEntity.setUserId(userIdRecharge);
					notiEntity.setData(entity.getId());
					Map<String, String> data = new HashMap<>();
					data.put("notificationType",
							notiType);
					data.put("notificationId", notificationUUID.toString());
					data.put("amount", dto.getAmount() + "");
					data.put("billNumber", orderId);
					data.put("transWalletId", entity.getId());
					data.put("time", time + "");
					String phoneNo = accountLoginService.getPhoneNoById(userIdRecharge);
					data.put("phoneNo", phoneNo);
					data.put("paymentMethod", "1");
					data.put("paymentType", "0");
					pushNotification(title, message, notiEntity, data, userIdRecharge);
				}

			}

		}
	}

	private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
			String userId) {
		if (notiEntity != null) {
			notificationService.insertNotification(notiEntity);
		}
		List<FcmTokenEntity> fcmTokens = new ArrayList<>();
		fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
		firebaseMessagingService.sendUsersNotificationWithData(data,
				fcmTokens,
				title, message);
		try {
			socketHandler.sendMessageToUser(userId,
					data);
		} catch (IOException e) {
			logger.error(
					"transaction-sync: WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
							+ e.toString());
		}
	}

	@Async
	private void updateTransaction(TransactionBankDTO dto, TransactionReceiveEntity transactionReceiveEntity,
			AccountBankReceiveEntity accountBankEntity, long time, NumberFormat nf) {

		BankTypeEntity bankTypeEntity = bankTypeService
				.getBankTypeById(accountBankEntity.getBankTypeId());
		// update transaction receive
		transactionReceiveService.updateTransactionReceiveStatus(1,
				dto.getTransactionid(),
				dto.getReferencenumber(),
				time,
				transactionReceiveEntity.getId());

		// find transaction-branch-receive to push notification
		TransactionReceiveBranchEntity transactionBranchEntity = transactionReceiveBranchService
				.getTransactionBranchByTransactionId(transactionReceiveEntity.getId());
		if (transactionBranchEntity != null) {
			// push notification
			// find userIds into business_member and branch_member
			List<String> userIds = branchMemberService
					.getUserIdsByBusinessIdAndBranchId(transactionBranchEntity.getBusinessId(),
							transactionBranchEntity.getBranchId());
			// insert AND push notification to users belong to
			// admin business/ member of branch
			String prefix = "";
			if (dto.getTransType().toUpperCase().equals("D")) {
				prefix = "-";
			} else {
				prefix = "+";
			}
			if (userIds != null && !userIds.isEmpty()) {
				// String requestId = "";
				for (String userId : userIds) {
					// insert notification
					UUID notificationUUID = UUID.randomUUID();
					NotificationEntity notiEntity = new NotificationEntity();
					BranchInformationEntity branchEntity = branchInformationService
							.getBranchById(transactionBranchEntity.getBranchId());
					BusinessInformationEntity businessEntity = businessInformationService
							.getBusinessById(branchEntity.getBusinessId());

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
					notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
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
					try {
						socketHandler.sendMessageToUser(accountBankEntity.getUserId(), data);
					} catch (IOException e) {
						logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
					}
					// if (requestId.trim().isEmpty()) {
					// requestId = textToSpeechService.requestTTS(accountBankEntity.getUserId(),
					// data, dto.getAmount() + "");
					// } else {
					// data.put("audioLink", textToSpeechService.find(requestId));
					// try {
					// socketHandler.sendMessageToUser(userId, data);
					// } catch (Exception e) {
					// logger.error("TTS-Transaction: Error: " + e.toString());
					// }
					// }

				}
				/////// DO INSERT TELEGRAM
				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
				if (chatIds != null && !chatIds.isEmpty()) {
					TelegramUtil telegramUtil = new TelegramUtil();
					// String telegramMsg2 = "Thanh toán thành công 🎉."
					// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "\nMã giao dịch: " + dto.getReferencenumber()
					// + "\nThời gian: " + convertLongToDate(time)
					// + "\nNội dung: " + dto.getContent();
					// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "| Ma GD: " + dto.getReferencenumber()
					// + "| ND: " + dto.getContent()
					// + "| " + convertLongToDate(time);
					String telegramMsg = prefix + nf.format(dto.getAmount()) + " VND"
							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
							+ accountBankEntity.getBankAccount()
							+ " | " + convertLongToDate(time)
							+ " | " + dto.getReferencenumber()
							+ " | ND: " + dto.getContent();
					for (String chatId : chatIds) {
						telegramUtil.sendMsg(chatId, telegramMsg);
					}
				}

				/////// DO INSERT LARK
				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (webhooks != null && !webhooks.isEmpty()) {
					LarkUtil larkUtil = new LarkUtil();
					// String larkMsg2 = "Thanh toán thành công 🎉."
					// + "\\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "\\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "\\nMã giao dịch: " + dto.getReferencenumber()
					// + "\\nThời gian: " + convertLongToDate(time)
					// + "\\nNội dung: " + dto.getContent();
					// String larkMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "| Ma GD: " + dto.getReferencenumber()
					// + "| ND: " + dto.getContent()
					// + "| " + convertLongToDate(time);
					String larkMsg = prefix + nf.format(dto.getAmount()) + " VND"
							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
							+ accountBankEntity.getBankAccount()
							+ " | " + convertLongToDate(time)
							+ " | " + dto.getReferencenumber()
							+ " | ND: " + dto.getContent();

					for (String webhook : webhooks) {
						larkUtil.sendMessageToLark(larkMsg, webhook);
					}
				}
				// textToSpeechService.delete(requestId);
			} else {
				logger.info("transaction-sync - userIds empty.");
			}
		} else {
			logger.info("transaction-sync - transaction-branch is empty.");
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
			notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
			notiEntity.setUserId(accountBankEntity.getUserId());
			notiEntity.setData(transactionReceiveEntity.getId());
			notificationService.insertNotification(notiEntity);
			List<FcmTokenEntity> fcmTokens = new ArrayList<>();
			fcmTokens = fcmTokenService.getFcmTokensByUserId(accountBankEntity.getUserId());
			Map<String, String> data = new HashMap<>();
			data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
			data.put("notificationId", notificationUUID.toString());
			data.put("transactionReceiveId", transactionReceiveEntity.getId());
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
			try {
				socketHandler.sendMessageToUser(accountBankEntity.getUserId(), data);
			} catch (IOException e) {
				logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
			}
			/////// DO INSERT TELEGRAM
			List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
			if (chatIds != null && !chatIds.isEmpty()) {
				TelegramUtil telegramUtil = new TelegramUtil();
				// String telegramMsg2 = "Thanh toán thành công 🎉."
				// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
				// + accountBankEntity.getBankAccount()
				// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "\nMã giao dịch: " + dto.getReferencenumber()
				// + "\nThời gian: " + convertLongToDate(time)
				// + "\nNội dung: " + dto.getContent();
				// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
				// + accountBankEntity.getBankAccount()
				// + "| Ma GD: " + dto.getReferencenumber()
				// + "| ND: " + dto.getContent()
				// + "| " + convertLongToDate(time);

				String telegramMsg = prefix + nf.format(dto.getAmount()) + " VND"
						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
						+ accountBankEntity.getBankAccount()
						+ " | " + convertLongToDate(time)
						+ " | " + dto.getReferencenumber()
						+ " | ND: " + dto.getContent();
				for (String chatId : chatIds) {
					telegramUtil.sendMsg(chatId, telegramMsg);
				}
			}

			/////// DO INSERT LARK
			List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (webhooks != null && !webhooks.isEmpty()) {
				LarkUtil larkUtil = new LarkUtil();
				// String larkMsg2 = "Thanh toán thành công 🎉."
				// + "\\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
				// + accountBankEntity.getBankAccount()
				// + "\\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "\\nMã giao dịch: " + dto.getReferencenumber()
				// + "\\nThời gian: " + convertLongToDate(time)
				// + "\\nNội dung: " + dto.getContent();
				// String larkMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
				// + accountBankEntity.getBankAccount()
				// + "| Ma GD: " + dto.getReferencenumber()
				// + "| ND: " + dto.getContent()
				// + "| " + convertLongToDate(time);
				String larkMsg = prefix + nf.format(dto.getAmount()) + " VND"
						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
						+ accountBankEntity.getBankAccount()
						+ " | " + convertLongToDate(time)
						+ " | " + dto.getReferencenumber()
						+ " | ND: " + dto.getContent();
				for (String webhook : webhooks) {
					larkUtil.sendMessageToLark(larkMsg, webhook);
				}
			}
			// String requestId = "";
			// requestId = textToSpeechService.requestTTS(accountBankEntity.getUserId(),
			// data, dto.getAmount() + "");
			// textToSpeechService.delete(requestId);
		}
	}

	// insert new transaction mean it's not created from business. So DO NOT need to
	// push to users
	@Async
	private void insertNewTransaction(String transcationUUID, TransactionBankDTO dto,
			AccountBankReceiveEntity accountBankEntity, long time,
			String traceId, UUID uuid, NumberFormat nf, String orderId, String sign) {

		BankTypeEntity bankTypeEntity = bankTypeService
				.getBankTypeById(accountBankEntity.getBankTypeId());

		TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
		transactionEntity.setId(transcationUUID);
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
		transactionEntity.setReferenceNumber(dto.getReferencenumber());
		transactionEntity.setOrderId(orderId);
		transactionEntity.setSign(sign);
		transactionEntity.setTimePaid(time);
		transactionReceiveService.insertTransactionReceive(transactionEntity);
		//
		///
		// check bank existed business branch
		BankReceiveBranchEntity bankReceiveBranchEntity = bankReceiveBranchService
				.getBankReceiveBranchByBankId(accountBankEntity.getId());
		if (bankReceiveBranchEntity != null) {
			// insert transaction branch
			TransactionReceiveBranchEntity transactionBranchEntity = new TransactionReceiveBranchEntity();
			UUID uuidTransBranch = UUID.randomUUID();
			transactionBranchEntity.setId(uuidTransBranch.toString());
			transactionBranchEntity.setBranchId(bankReceiveBranchEntity.getBranchId());
			transactionBranchEntity.setBusinessId(bankReceiveBranchEntity.getBusinessId());
			transactionBranchEntity.setTransactionReceiveId(transcationUUID);
			// insert and push noti

			// find userIds into business_member and branch_member
			List<String> userIds = branchMemberService
					.getUserIdsByBusinessIdAndBranchId(transactionBranchEntity.getBusinessId(),
							transactionBranchEntity.getBranchId());
			// insert AND push notification to users belong to
			// admin business/ member of branch
			String prefix = "";
			if (dto.getTransType().toUpperCase().equals("D")) {
				prefix = "-";
			} else {
				prefix = "+";
			}
			if (userIds != null && !userIds.isEmpty()) {
				// String requestId = "";
				for (String userId : userIds) {
					// insert notification
					UUID notificationUUID = UUID.randomUUID();
					NotificationEntity notiEntity = new NotificationEntity();
					BranchInformationEntity branchEntity = branchInformationService
							.getBranchById(transactionBranchEntity.getBranchId());
					BusinessInformationEntity businessEntity = businessInformationService
							.getBusinessById(branchEntity.getBusinessId());

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
					notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
					notiEntity.setUserId(userId);
					notiEntity.setData(transcationUUID);
					notificationService.insertNotification(notiEntity);
					List<FcmTokenEntity> fcmTokens = new ArrayList<>();
					fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
					Map<String, String> data = new HashMap<>();
					data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
					data.put("notificationId", notificationUUID.toString());
					data.put("transactionReceiveId", transcationUUID.toString());
					data.put("bankAccount", accountBankEntity.getBankAccount());
					data.put("bankName", bankTypeEntity.getBankName());
					data.put("bankCode", bankTypeEntity.getBankCode());
					data.put("bankId", accountBankEntity.getId());
					data.put("branchName", branchEntity.getName());
					data.put("businessName", businessEntity.getName());
					if (traceId == null || traceId.isEmpty()) {
						data.put("content", dto.getContent().trim());
					} else {
						data.put("content", traceId + "." + dto.getContent());
					}
					data.put("amount", "" + dto.getAmount() + "");
					data.put("time", "" + time);
					data.put("refId", "" + dto.getTransactionid());
					data.put("status", "1");
					data.put("transType", dto.getTransType());
					if (traceId == null || traceId.isEmpty()) {
						data.put("traceId", "");
					} else {
						data.put("traceId", traceId);
					}
					firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
							NotificationUtil
									.getNotiTitleUpdateTransaction(),
							message);
					try {
						socketHandler.sendMessageToUser(accountBankEntity.getUserId(), data);
					} catch (IOException e) {
						logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
					}
					// if (requestId.trim().isEmpty()) {
					// requestId = textToSpeechService.requestTTS(accountBankEntity.getUserId(),
					// data, dto.getAmount() + "");
					// } else {
					// data.put("audioLink", textToSpeechService.find(requestId));
					// try {
					// socketHandler.sendMessageToUser(userId, data);
					// } catch (Exception e) {
					// logger.error("TTS-Transaction: Error: " + e.toString());
					// }
					// }

				}
				/////// DO INSERT TELEGRAM
				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
				if (chatIds != null && !chatIds.isEmpty()) {
					TelegramUtil telegramUtil = new TelegramUtil();
					// String telegramMsg2 = "Thanh toán thành công 🎉."
					// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "\nMã giao dịch: " + dto.getReferencenumber()
					// + "\nThời gian: " + convertLongToDate(time)
					// + "\nNội dung: " + dto.getContent();
					// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "| Ma GD: " + dto.getReferencenumber()
					// + "| ND: " + dto.getContent()
					// + "| " + convertLongToDate(time);
					String telegramMsg = prefix + nf.format(dto.getAmount()) + " VND"
							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
							+ accountBankEntity.getBankAccount()
							+ " | " + convertLongToDate(time)
							+ " | " + dto.getReferencenumber()
							+ " | ND: " + dto.getContent();

					for (String chatId : chatIds) {
						telegramUtil.sendMsg(chatId, telegramMsg);
					}
				}

				/////// DO INSERT LARK
				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (webhooks != null && !webhooks.isEmpty()) {
					LarkUtil larkUtil = new LarkUtil();
					// String larkMsg2 = "Thanh toán thành công 🎉."
					// + "\\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "\\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "\\nMã giao dịch: " + dto.getReferencenumber()
					// + "\\nThời gian: " + convertLongToDate(time)
					// + "\\nNội dung: " + dto.getContent();
					// String larkMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
					// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
					// + accountBankEntity.getBankAccount()
					// + "| Ma GD: " + dto.getReferencenumber()
					// + "| ND: " + dto.getContent()
					// + "| " + convertLongToDate(time);
					String larkMsg = prefix + nf.format(dto.getAmount()) + " VND"
							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
							+ accountBankEntity.getBankAccount()
							+ " | " + convertLongToDate(time)
							+ " | " + dto.getReferencenumber()
							+ " | ND: " + dto.getContent();
					for (String webhook : webhooks) {
						larkUtil.sendMessageToLark(larkMsg, webhook);
					}
				}
				// textToSpeechService.delete(requestId);
			} else {
				logger.info("transaction-sync - userIds empty.");
			}
			//
		} else {
			//
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
			notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
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
			try {
				socketHandler.sendMessageToUser(accountBankEntity.getUserId(), data);
			} catch (IOException e) {
				logger.error("WS: socketHandler.sendMessageToUser - insertNewTransaction ERROR: " + e.toString());
			}
			/////// DO INSERT TELEGRAM
			List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
			if (chatIds != null && !chatIds.isEmpty()) {
				TelegramUtil telegramUtil = new TelegramUtil();
				// String telegramMsg2 = "Thanh toán thành công 🎉."
				// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - " +
				// accountBankEntity.getBankAccount()
				// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "\nMã giao dịch: " + dto.getReferencenumber()
				// + "\nThời gian: " + convertLongToDate(time)
				// + "\nNội dung: " + dto.getContent();
				// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
				// + accountBankEntity.getBankAccount()
				// + "| Ma GD: " + dto.getReferencenumber()
				// + "| ND: " + dto.getContent()
				// + "| " + convertLongToDate(time);
				String telegramMsg = prefix + nf.format(dto.getAmount()) + " VND"
						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
						+ accountBankEntity.getBankAccount()
						+ " | " + convertLongToDate(time)
						+ " | " + dto.getReferencenumber()
						+ " | ND: " + dto.getContent();
				for (String chatId : chatIds) {
					telegramUtil.sendMsg(chatId, telegramMsg);
				}
			}

			/////// DO INSERT LARK
			List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (webhooks != null && !webhooks.isEmpty()) {
				LarkUtil larkUtil = new LarkUtil();
				// String larkMsg2 = "Thanh toán thành công 🎉."
				// + "\\nTài khoản: " + bankTypeEntity.getBankShortName() + " - " +
				// accountBankEntity.getBankAccount()
				// + "\\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "\\nMã giao dịch: " + dto.getReferencenumber()
				// + "\\nThời gian: " + convertLongToDate(time)
				// + "\\nNội dung: " + dto.getContent();
				// String larkMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
				// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
				// + accountBankEntity.getBankAccount()
				// + "| Ma GD: " + dto.getReferencenumber()
				// + "| ND: " + dto.getContent()
				// + "| " + convertLongToDate(time);
				String larkMsg = prefix + nf.format(dto.getAmount()) + " VND"
						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
						+ accountBankEntity.getBankAccount()
						+ " | " + convertLongToDate(time)
						+ " | " + dto.getReferencenumber()
						+ " | ND: " + dto.getContent();
				for (String webhook : webhooks) {
					larkUtil.sendMessageToLark(larkMsg, webhook);
				}
			}
		}

	}

	public String convertLongToDate(long timestamp) {
		String result = "";
		try {
			// Tạo một đối tượng Instant từ timestamp
			Instant instant = Instant.ofEpochSecond(timestamp);

			// Tạo một đối tượng LocalDateTime từ Instant và ZoneOffset.UTC
			LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

			// Chuyển đổi múi giờ từ UTC sang UTC+7
			ZoneOffset offset = ZoneOffset.ofHours(7);
			dateTime = dateTime.plusHours(offset.getTotalSeconds() / 3600);

			// Định dạng ngày tháng năm
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			// Chuyển đổi thành chuỗi ngày tháng năm
			result = dateTime.format(formatter);

		} catch (Exception e) {
			logger.error("convertLongToDate: ERROR: " + e.toString());
		}
		return result;
	}

	// get token bank product
	private TokenProductBankDTO getBankToken() {
		TokenProductBankDTO result = null;
		try {
			String key = EnvironmentUtil.getUserBankAccess() + ":" + EnvironmentUtil.getPasswordBankAccess();
			String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(EnvironmentUtil.getBankUrl() + "oauth2/v1/token")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(EnvironmentUtil.getBankUrl()
							+ "oauth2/v1/token")
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

	@GetMapping("account/info")
	private ResponseEntity<Object> searchUserBankName(
			@RequestParam(value = "bin") String bin,
			@RequestParam(value = "accountNumber") String accountNumber,
			@RequestParam(value = "accountType") String accountType,
			@RequestParam(value = "transferType") String transferType) {
		Object result = null;
		HttpStatus httpStatus = null;
		try {
			// Get bank token
			TokenProductBankDTO token = getBankToken();
			if (bin.trim().isEmpty() || accountNumber.trim().isEmpty() || !accountType.trim().equals("ACCOUNT")
					|| (!transferType.trim().equals("INHOUSE") && !transferType.trim().equals("NAPAS"))) {
				result = new ResponseMessageDTO("FAILED", "E33");
				httpStatus = HttpStatus.BAD_REQUEST;
			} else {
				if (token != null) {
					// Build URL with PathVariable
					UriComponents uriComponents = UriComponentsBuilder
							.fromHttpUrl(
									EnvironmentUtil.getBankUrl() + "ms/bank-info/v1.0/account/info")
							.buildAndExpand(accountNumber);

					// Create WebClient with authorization header
					WebClient webClient = WebClient.builder()
							.baseUrl(uriComponents.toUriString())
							.defaultHeader("Authorization", "Bearer " + token.getAccess_token())
							.defaultHeader("clientMessageId", UUID.randomUUID().toString())
							.build();
					Mono<ClientResponse> responseMono = null;
					if (transferType.trim().equals("INHOUSE")) {
						// Send GET request to API
						responseMono = webClient.get()
								.uri(uriBuilder -> uriBuilder
										.queryParam("accountNumber", accountNumber)
										.queryParam("accountType", accountType)
										.queryParam("transferType", transferType)
										.build())
								.exchange();
					} else {
						// Send GET request to API
						responseMono = webClient.get()
								.uri(uriBuilder -> uriBuilder
										.queryParam("accountNumber", accountNumber)
										.queryParam("accountType", accountType)
										.queryParam("transferType", transferType)
										.queryParam("bankCode", bin)
										.build())
								.exchange();
					}

					ClientResponse response = responseMono.block();
					if (response.statusCode().is2xxSuccessful()) {
						String json = response.bodyToMono(String.class).block();
						logger.info("getBankNameInformation: response: " + json);
						if (json != null && !json.isEmpty()) { // Check if response is not empty
							// Parse response to extract bank name
							ObjectMapper objectMapper = new ObjectMapper();
							JsonNode rootNode = objectMapper.readTree(json);
							String accountName = "";
							String customerName = "";
							String customerShortName = "";
							if (rootNode.get("data").get("accountName") != null) {
								accountName = rootNode.get("data").get("accountName").asText();
							}
							if (rootNode.get("data").get("customerName") != null) {
								customerName = rootNode.get("data").get("customerName").asText();
							}
							if (rootNode.get("data").get("customerShortName") != null) {
								customerShortName = rootNode.get("data").get("customerShortName").asText();
							}
							result = new AccountBankNameDTO(accountName, customerName, customerShortName);
							httpStatus = HttpStatus.OK;
						} else {
							result = new ResponseMessageDTO("FAILED", "E32");
							httpStatus = HttpStatus.BAD_REQUEST;
						}
					} else {
						String json = response.bodyToMono(String.class).block();
						logger.error("Error at getBankNameInformation: status code: " + response.statusCode());
						logger.error("Error at getBankNameInformation: response: " + json);
						result = new ResponseMessageDTO("FAILED", "E32");
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					logger.error("Error at getBankNameInformation: TOKEN NULL");
					result = new ResponseMessageDTO("FAILED", "E05");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			}

		} catch (Exception e) {
			logger.error("Error at getBankNameInformation: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("account/info/{bankCode}/{accountNumber}/{accountType}/{transferType}")
	private ResponseEntity<AccountBankNameDTO> getBankNameInformation(
			@PathVariable(value = "bankCode") String bankCode,
			@PathVariable(value = "accountNumber") String accountNumber,
			@PathVariable(value = "accountType") String accountType,
			@PathVariable(value = "transferType") String transferType) {
		AccountBankNameDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// Get bank token
			TokenProductBankDTO token = getBankToken();
			if (token != null) {
				// Build URL with PathVariable
				UriComponents uriComponents = UriComponentsBuilder
						.fromHttpUrl(
								EnvironmentUtil.getBankUrl() + "ms/bank-info/v1.0/account/info")
						.buildAndExpand(accountNumber);

				// Create WebClient with authorization header
				WebClient webClient = WebClient.builder()
						.baseUrl(uriComponents.toUriString())
						.defaultHeader("Authorization", "Bearer " + token.getAccess_token())
						.defaultHeader("clientMessageId", UUID.randomUUID().toString())
						.build();
				Mono<ClientResponse> responseMono = null;
				if (transferType.trim().equals("INHOUSE")) {
					// Send GET request to API
					responseMono = webClient.get()
							.uri(uriBuilder -> uriBuilder
									.queryParam("accountNumber", accountNumber)
									.queryParam("accountType", accountType)
									.queryParam("transferType", transferType)
									.build())
							.exchange();
				} else {
					// Send GET request to API
					responseMono = webClient.get()
							.uri(uriBuilder -> uriBuilder
									.queryParam("accountNumber", accountNumber)
									.queryParam("accountType", accountType)
									.queryParam("transferType", transferType)
									.queryParam("bankCode", bankCode)
									.build())
							.exchange();
				}

				ClientResponse response = responseMono.block();
				if (response.statusCode().is2xxSuccessful()) {
					String json = response.bodyToMono(String.class).block();
					logger.info("getBankNameInformation: response: " + json);
					if (json != null && !json.isEmpty()) { // Check if response is not empty
						// Parse response to extract bank name
						ObjectMapper objectMapper = new ObjectMapper();
						JsonNode rootNode = objectMapper.readTree(json);
						String accountName = "";
						String customerName = "";
						String customerShortName = "";
						if (rootNode.get("data").get("accountName") != null) {
							accountName = rootNode.get("data").get("accountName").asText();
						}
						if (rootNode.get("data").get("customerName") != null) {
							customerName = rootNode.get("data").get("customerName").asText();
						}
						if (rootNode.get("data").get("customerShortName") != null) {
							customerShortName = rootNode.get("data").get("customerShortName").asText();
						}
						result = new AccountBankNameDTO(accountName, customerName, customerShortName);
						httpStatus = HttpStatus.OK;
					} else {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					String json = response.bodyToMono(String.class).block();
					logger.error("Error at getBankNameInformation: status code: " + response.statusCode());
					logger.error("Error at getBankNameInformation: response: " + json);
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			}
		} catch (Exception e) {
			logger.error("Error at getBankNameInformation: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<>(result, httpStatus);
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
							+ "ms/push-mesages-partner/v1.0/bdsd/subscribe/request")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(
							EnvironmentUtil.getBankUrl()
									+ "ms/push-mesages-partner/v1.0/bdsd/subscribe/request")
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
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode().trim());
				result = new ResponseMessageDTO(status, message);
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			// } catch (HttpClientErrorException ex) {
			// logger.error("HttpClientErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;
			// } catch (HttpServerErrorException ex) {
			// logger.error("HttpServerErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;

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
							+ "ms/push-mesages-partner/v1.0/bdsd/subscribe/confirm")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(
							EnvironmentUtil.getBankUrl()
									+ "ms/push-mesages-partner/v1.0/bdsd/subscribe/confirm")
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
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode().trim());
				result = new ResponseMessageDTO(status, message);
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			// } catch (HttpClientErrorException ex) {
			// logger.error("HttpClientErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;
			// } catch (HttpServerErrorException ex) {
			// logger.error("HttpServerErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;

		} catch (Exception e) {
			logger.error("Error at confirmOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("unregister_request")
	public ResponseEntity<ResponseMessageDTO> unegisterRequest(@Valid @RequestBody UnregisterRequestDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID clientMessageId = UUID.randomUUID();
			Map<String, Object> data = new HashMap<>();
			data.put("accountNumber", dto.getAccountNumber());
			data.put("authenType", "SMS");
			data.put("applicationType", dto.getApplicationType());
			data.put("transType", "DC");
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(EnvironmentUtil.getBankUrl()
							+ "ms/push-mesages-partner/v1.0/bdsd/unsubscribe/request")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(
							EnvironmentUtil.getBankUrl()
									+ "ms/push-mesages-partner/v1.0/bdsd/unsubscribe/request")
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
				logger.info("Response unegisterRequest: " + json);
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
				logger.error("Response unegisterRequest error: " + confirmRequestBankDTO.getSoaErrorCode() + "-"
						+ confirmRequestBankDTO.getSoaErrorDesc() + " at "
						+ currentDateTime.toEpochSecond(ZoneOffset.UTC));
				String status = "FAILED";
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode().trim());
				result = new ResponseMessageDTO(status, message);
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			// } catch (HttpClientErrorException ex) {
			// logger.error("HttpClientErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;
			// } catch (HttpServerErrorException ex) {
			// logger.error("HttpServerErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;

		} catch (Exception e) {
			logger.error("Error at requestOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("unregister_confirm")
	public ResponseEntity<ResponseMessageDTO> unegisterConfirm(@Valid @RequestBody UnregisterBankConfirmDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID clientMessageId = UUID.randomUUID();
			Map<String, Object> data = new HashMap<>();
			data.put("requestId", dto.getRequestId());
			data.put("otpValue", dto.getOtpValue());
			// data.put("authenType", "SMS");
			data.put("applicationType", dto.getApplicationType());
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(EnvironmentUtil.getBankUrl()
							+ "ms/push-mesages-partner/v1.0/bdsd/unsubscribe/confirm")
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(
							EnvironmentUtil.getBankUrl()
									+ "ms/push-mesages-partner/v1.0/bdsd/unsubscribe/confirm")
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
				logger.info("Response unegisterConfirm: " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				String status = rootNode.get("data").get("status").asText();
				if (status.equals("Success")) {
					// do update status bank_account from authen => unauthen
					accountBankService.unRegisterAuthenticationBank(dto.getBankAccount());
					//
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
				logger.error("Response unegisterConfirm error: " + confirmRequestBankDTO.getSoaErrorCode() + "-"
						+ confirmRequestBankDTO.getSoaErrorDesc() + " at "
						+ currentDateTime.toEpochSecond(ZoneOffset.UTC));
				String status = "FAILED";
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode().trim());
				result = new ResponseMessageDTO(status, message);
				httpStatus = HttpStatus.BAD_REQUEST;
			}
			// } catch (HttpClientErrorException ex) {
			// logger.error("HttpClientErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;
			// } catch (HttpServerErrorException ex) {
			// logger.error("HttpServerErrorException: " + ex.getMessage());
			// logger.error("Response body: " + ex.getResponseBodyAsString());
			// result = new ResponseMessageDTO("FAILED", "E05");
			// httpStatus = HttpStatus.BAD_REQUEST;

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
	private boolean checkDuplicateReferenceNumber(String refNumber, String transType) {
		boolean result = false;
		try {
			// if transType = C => check all transaction_bank and transaction_mms
			// if transType = D => check only transaction bank
			String check = transactionBankService.checkExistedReferenceNumber(refNumber, transType);
			if (check == null || check.isEmpty()) {
				result = true;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return result;
	}

	private TokenDTO getCustomerSyncToken(String transReceiveId, CustomerSyncEntity entity, long time) {
		TokenDTO result = null;
		ResponseMessageDTO msgDTO = null;
		try {
			String key = entity.getUsername() + ":" + entity.getPassword();
			String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
			logger.info("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
					+ entity.getPassword());
			// System.out.println("key: " + encodedKey + " - username: " +
			// entity.getUsername() + " - password: "
			// + entity.getPassword());
			String suffixUrl = entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()
					? entity.getSuffixUrl()
					: "";
			UriComponents uriComponents = null;
			WebClient webClient = null;
			if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
				uriComponents = UriComponentsBuilder
						.fromHttpUrl(
								"http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
										+ "/api/token_generate")
						.buildAndExpand();
				webClient = WebClient.builder()
						.baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
								+ "/api/token_generate")
						.build();
			} else {
				uriComponents = UriComponentsBuilder
						.fromHttpUrl(
								entity.getInformation() + "/" + suffixUrl
										+ "/api/token_generate")
						.buildAndExpand();
				webClient = WebClient.builder()
						.baseUrl(entity.getInformation() + "/" + suffixUrl
								+ "/api/token_generate")
						.build();
			}
			// System.out.println("uriComponents: " + uriComponents.toString());
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
			Optional<TokenDTO> resultOptional = responseMono.subscribeOn(Schedulers.boundedElastic())
					.blockOptional();
			if (resultOptional.isPresent()) {
				result = resultOptional.get();
				msgDTO = new ResponseMessageDTO("SUCCESS", "");
				if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
					logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getIpAddress());
				} else {
					logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getInformation());
				}

				// System.out.println("Token got: " + result.getAccess_token() + " - from: " +
				// entity.getIpAddress());
			} else {
				msgDTO = new ResponseMessageDTO("FAILED", "E05");
				if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
					logger.info("Token could not be retrieved from: " + entity.getIpAddress());
				} else {
					logger.info("Token could not be retrieved from: " + entity.getInformation());
				}
			}
		} catch (Exception e) {
			msgDTO = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
			if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
				logger.error("Error at getCustomerSyncToken: " + entity.getIpAddress() + " - " + e.toString());
				// System.out.println("Error at getCustomerSyncToken: " + entity.getIpAddress()
				// + " - " + e.toString());
			} else {
				logger.error("Error at getCustomerSyncToken: " + entity.getInformation() + " - " + e.toString());
			}
		} finally {
			if (msgDTO != null) {
				UUID logUUID = UUID.randomUUID();
				String suffixUrl = "";
				if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
					suffixUrl = "/" + entity.getSuffixUrl();
				}
				String address = "";
				if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
					address = "http://" + entity.getIpAddress() + ":" + entity.getPort() + suffixUrl
							+ "/api/token_generate";
				} else {
					address = entity.getInformation() + suffixUrl + "/api/token_generate";
				}
				TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
				logEntity.setId(logUUID.toString());
				logEntity.setTransactionId(transReceiveId);
				logEntity.setStatus(msgDTO.getStatus());
				logEntity.setMessage(msgDTO.getMessage());
				logEntity.setTime(time);
				logEntity.setUrlCallback(address);
				transactionReceiveLogService.insert(logEntity);
			}
		}
		return result;
	}

	private ResponseMessageDTO pushNewTransactionToCustomerSync(String transReceiveId, CustomerSyncEntity entity,
			TransactionBankCustomerDTO dto,
			long time) {
		ResponseMessageDTO result = null;
		// final ResponseMessageDTO[] results = new ResponseMessageDTO[1];
		// final List<ResponseMessageDTO> results = new ArrayList<>();
		// final String[] msg = new String[1];
		try {
			logger.info("pushNewTransactionToCustomerSync: orderId: " +
					dto.getOrderId());
			logger.info("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
			System.out.println("pushNewTransactionToCustomerSync: orderId: " +
					dto.getOrderId());
			System.out.println("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
			TokenDTO tokenDTO = null;
			if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() &&
					entity.getPassword() != null
					&& !entity.getPassword().trim().isEmpty()) {
				tokenDTO = getCustomerSyncToken(transReceiveId, entity, time);
			} else if (entity.getToken() != null && !entity.getToken().trim().isEmpty()) {
				logger.info("Get token from record: " + entity.getId());
				tokenDTO = new TokenDTO(entity.getToken(), "Bearer", 0);
			}
			Map<String, Object> data = new HashMap<>();
			data.put("transactionid", dto.getTransactionid());
			data.put("transactiontime", dto.getTransactiontime());
			data.put("referencenumber", dto.getReferencenumber());
			data.put("amount", dto.getAmount());
			data.put("content", dto.getContent());
			data.put("bankaccount", dto.getBankaccount());
			data.put("transType", dto.getTransType());
			data.put("orderId", dto.getOrderId());
			data.put("sign", dto.getSign());
			String suffixUrl = "";
			if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
				suffixUrl = entity.getSuffixUrl();
			}
			WebClient.Builder webClientBuilder = WebClient.builder()
					.baseUrl(entity.getInformation() + "/" + suffixUrl +
							"/bank/api/transaction-sync");

			if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
				webClientBuilder.baseUrl("http://" + entity.getIpAddress() + ":" +
						entity.getPort() + "/" + suffixUrl
						+ "/bank/api/transaction-sync");
			}

			// Create SSL context to ignore SSL handshake exception
			SslContext sslContext = SslContextBuilder.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
			HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

			WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
					.build();

			logger.info("uriComponents: " + webClient.get().uri(builder -> builder.path("/").build()).toString());
			System.out
					.println("uriComponents: " + webClient.get().uri(builder -> builder.path("/").build()).toString());
			// Mono<TransactionResponseDTO> responseMono = null;
			Mono<ClientResponse> responseMono = null;
			if (tokenDTO != null) {
				responseMono = webClient.post()
						// .uri("/bank/api/transaction-sync")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + tokenDTO.getAccess_token())
						.body(BodyInserters.fromValue(data))
						.exchange();
				// .retrieve()
				// .bodyToMono(TransactionResponseDTO.class);
			} else {
				responseMono = webClient.post()
						// .uri("/bank/api/transaction-sync")
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(data))
						.exchange();
				// .retrieve()
				// .bodyToMono(TransactionResponseDTO.class);
			}
			ClientResponse response = responseMono.block();
			System.out.println("response: " + response.toString());
			System.out.println("response status code: " + response.statusCode());
			if (response.statusCode().is2xxSuccessful()) {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response pushNewTransactionToCustomerSync: " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				if (rootNode.get("object") != null) {
					String reftransactionid = rootNode.get("object").get("reftransactionid").asText();
					if (reftransactionid != null) {
						result = new ResponseMessageDTO("SUCCESS", "");
					} else {
						result = new ResponseMessageDTO("FAILED", "E05 - " + json);
					}
				} else {
					result = new ResponseMessageDTO("FAILED", "E05 - " + json);
				}
			} else {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response pushNewTransactionToCustomerSync: " + json);
				result = new ResponseMessageDTO("FAILED", "E05 - " + json);
			}
			// TransactionResponseDTO res = (TransactionResponseDTO)
			// responseMono.subscribe(transactionResponseDTO -> {
			// LocalDateTime currentDateTime = LocalDateTime.now();
			// long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
			// if (transactionResponseDTO != null && transactionResponseDTO.getObject() !=
			// null) {
			// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
			// logger.info("pushNewTransactionToCustomerSync SUCCESS: " +
			// entity.getIpAddress() + " - "
			// + transactionResponseDTO.getObject().getReftransactionid() + " at: "
			// + responseTime);
			// System.out
			// .println("pushNewTransactionToCustomerSync SUCCESS: " + entity.getIpAddress()
			// + " - "
			// + transactionResponseDTO.getObject().getReftransactionid() + " at: "
			// + responseTime);
			// } else {
			// logger.info("pushNewTransactionToCustomerSync SUCCESS: " +
			// entity.getInformation() + " - "
			// + transactionResponseDTO.getObject().getReftransactionid() + " at: "
			// + responseTime);
			// System.out
			// .println("pushNewTransactionToCustomerSync SUCCESS: " +
			// entity.getInformation() + " - "
			// + transactionResponseDTO.getObject().getReftransactionid() + " at: "
			// + responseTime);
			// }
			// result.setStatus("SUCCESS");
			// result.setMessage("");
			// } else {
			// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
			// logger.error("Error at pushNewTransactionToCustomerSync: " +
			// entity.getIpAddress() + " - "
			// + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() :
			// "")
			// + " at: " + responseTime);
			// System.out
			// .println("Error at pushNewTransactionToCustomerSync: " +
			// entity.getIpAddress() + " - "
			// + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason()
			// : "")
			// + " at: " + responseTime);
			// } else {
			// logger.error("Error at pushNewTransactionToCustomerSync: " +
			// entity.getInformation() + " - "
			// + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() :
			// "")
			// + " at: " + responseTime);
			// System.out
			// .println("Error at pushNewTransactionToCustomerSync: " +
			// entity.getInformation() + " - "
			// + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason()
			// : "")
			// + " at: " + responseTime);
			// }
			// if (transactionResponseDTO != null) {
			// result.setStatus("FAILED");
			// result.setMessage(
			// "E05 - " + transactionResponseDTO.getErrorReason());
			// } else {
			// result.setStatus("FAILED");
			// result.setMessage(
			// "E05 ");
			// }
			// }
			// }, error -> {
			// LocalDateTime currentDateTime = LocalDateTime.now();
			// long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
			// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
			// logger.error("ERROR at pushNewTransactionToCustomerSync: " +
			// entity.getIpAddress() + " - "
			// + error.toString() + " at: " + responseTime);
			// System.out
			// .println("ERROR at pushNewTransactionToCustomerSync: " +
			// entity.getIpAddress() + " - "
			// + error.toString() + " at: " + responseTime);

			// } else {
			// logger.error("ERROR at pushNewTransactionToCustomerSync: " +
			// entity.getInformation() + " - "
			// + error.toString() + " at: " + responseTime);
			// System.out
			// .println("ERROR at pushNewTransactionToCustomerSync: " +
			// entity.getInformation() + " - "
			// + error.toString() + " at: " + responseTime);
			// }
			// result.setStatus("FAILED");
			// result.setMessage("E05");
			// });
			// if (res != null && res.getObject() != null) {
			// result.setStatus("SUCCESS");
			// result.setMessage("");
			// } else {
			// if (res != null) {
			// result.setStatus("FAILED");
			// result.setMessage(
			// "E05 - " + res.getErrorReason());
			// } else {
			// result.setStatus("FAILED");
			// result.setMessage(
			// "E05 ");
			// }
			// }
		} catch (Exception e) {
			LocalDateTime currentDateTime = LocalDateTime.now();
			long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
			result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
			if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
				logger.error(
						"Error Unexpected at pushNewTransactionToCustomerSync: " +
								entity.getIpAddress() + " - "
								+ e.toString()
								+ " at: " + responseTime);
			} else {
				logger.error(
						"Error Unexpected at pushNewTransactionToCustomerSync: " +
								entity.getInformation() + " - "
								+ e.toString()
								+ " at: " + responseTime);
			}
		} finally {
			if (result != null) {
				UUID logUUID = UUID.randomUUID();
				String suffixUrl = "";
				if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
					suffixUrl = "/" + entity.getSuffixUrl();
				}
				String address = "";
				if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
					address = "http://" + entity.getIpAddress() + ":" + entity.getPort() + suffixUrl
							+ "/bank/api/transaction-sync";
				} else {
					address = entity.getInformation() + suffixUrl + "/bank/api/transaction-sync";
				}
				TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
				logEntity.setId(logUUID.toString());
				logEntity.setTransactionId(transReceiveId);
				logEntity.setStatus(result.getStatus());
				logEntity.setMessage(result.getMessage());
				logEntity.setTime(time);
				logEntity.setUrlCallback(address);
				transactionReceiveLogService.insert(logEntity);
			}
		}
		return result;
	}

	// private void pushNewTransactionToCustomerSync(CustomerSyncEntity entity,
	// TransactionBankCustomerDTO dto, long time) {
	// try {
	// SSLUtil.disableCertificateValidation();

	// logger.info("pushNewTransactionToCustomerSync: orderId: " +
	// dto.getOrderId());
	// logger.info("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
	// TokenDTO tokenDTO = null;
	// if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() &&
	// entity.getPassword() != null
	// && !entity.getPassword().trim().isEmpty()) {
	// tokenDTO = getCustomerSyncToken(entity);
	// } else if (entity.getToken() != null && !entity.getToken().trim().isEmpty())
	// {
	// logger.info("Get token from record: " + entity.getId());
	// tokenDTO = new TokenDTO(entity.getToken(), "Bearer", 0);
	// }
	// Map<String, Object> data = new HashMap<>();
	// data.put("transactionid", dto.getTransactionid());
	// data.put("transactiontime", dto.getTransactiontime());
	// data.put("referencenumber", dto.getReferencenumber());
	// data.put("amount", dto.getAmount());
	// data.put("content", dto.getContent());
	// data.put("bankaccount", dto.getBankaccount());
	// data.put("transType", dto.getTransType());
	// data.put("orderId", dto.getOrderId());
	// data.put("sign", dto.getSign());
	// String suffixUrl = "";
	// if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
	// suffixUrl = entity.getSuffixUrl();
	// }
	// UriComponents uriComponents = null;
	// WebClient webClient = null;
	// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
	// uriComponents = UriComponentsBuilder
	// .fromHttpUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/"
	// + suffixUrl
	// + "/bank/api/transaction-sync")
	// .buildAndExpand(/* add url parameter here */);
	// webClient = WebClient.builder()
	// .baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" +
	// suffixUrl
	// + "/bank/api/transaction-sync")
	// .build();
	// } else {
	// uriComponents = UriComponentsBuilder
	// .fromHttpUrl(entity.getInformation() + "/" + suffixUrl
	// + "/bank/api/transaction-sync")
	// .buildAndExpand(/* add url parameter here */);
	// webClient = WebClient.builder()
	// .baseUrl(entity.getInformation() + "/" + suffixUrl
	// + "/bank/api/transaction-sync")
	// .build();
	// }
	// //
	// logger.info("uriComponents: " + uriComponents.toString());
	// Mono<TransactionResponseDTO> responseMono = null;
	// if (tokenDTO != null) {
	// responseMono = webClient.post()
	// .uri(uriComponents.toUri())
	// .contentType(MediaType.APPLICATION_JSON)
	// .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
	// .body(BodyInserters.fromValue(data))
	// .retrieve()
	// .bodyToMono(TransactionResponseDTO.class);
	// } else {
	// responseMono = webClient.post()
	// .uri(uriComponents.toUri())
	// .contentType(MediaType.APPLICATION_JSON)
	// .body(BodyInserters.fromValue(data))
	// .retrieve()
	// .bodyToMono(TransactionResponseDTO.class);
	// }
	// responseMono.subscribe(transactionResponseDTO -> {
	// LocalDateTime currentDateTime = LocalDateTime.now();
	// long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
	// if (transactionResponseDTO != null && transactionResponseDTO.getObject() !=
	// null) {
	// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
	// logger.info("pushNewTransactionToCustomerSync SUCCESS: " +
	// entity.getIpAddress() + " - "
	// + transactionResponseDTO.getObject().getReftransactionid() + " at: "
	// + responseTime);
	// } else {
	// logger.info("pushNewTransactionToCustomerSync SUCCESS: " +
	// entity.getInformation() + " - "
	// + transactionResponseDTO.getObject().getReftransactionid() + " at: "
	// + responseTime);
	// }
	// // System.out.println("pushNewTransactionToCustomerSync SUCCESS: " +
	// // entity.getIpAddress() + " - "
	// // + transactionResponseDTO.getObject().getReftransactionid());
	// } else {
	// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
	// logger.error("Error at pushNewTransactionToCustomerSync: " +
	// entity.getIpAddress() + " - "
	// + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() :
	// "")
	// + " at: " + responseTime);
	// } else {
	// logger.error("Error at pushNewTransactionToCustomerSync: " +
	// entity.getInformation() + " - "
	// + (transactionResponseDTO != null ? transactionResponseDTO.getErrorReason() :
	// "")
	// + " at: " + responseTime);
	// }
	// }
	// }, error -> {
	// LocalDateTime currentDateTime = LocalDateTime.now();
	// long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
	// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
	// logger.error("Error at pushNewTransactionToCustomerSync: " +
	// entity.getIpAddress() + " - "
	// + error.toString() + " at: " + responseTime);
	// } else {
	// logger.error("Error at pushNewTransactionToCustomerSync: " +
	// entity.getInformation() + " - "
	// + error.toString() + " at: " + responseTime);
	// }

	// });
	// } catch (Exception e) {
	// LocalDateTime currentDateTime = LocalDateTime.now();
	// long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
	// if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
	// logger.error(
	// "Error at pushNewTransactionToCustomerSync: " + entity.getIpAddress() + " - "
	// + e.toString()
	// + " at: " + responseTime);
	// } else {
	// logger.error(
	// "Error at pushNewTransactionToCustomerSync: " + entity.getInformation() + " -
	// " + e.toString()
	// + " at: " + responseTime);
	// }
	// }
	// }

	private ResponseMessageDTO getCustomerSyncEntities(String transReceiveId, TransactionBankDTO dto,
			AccountBankReceiveEntity accountBankEntity,
			long time, String orderId, String sign) {
		ResponseMessageDTO result = new ResponseMessageDTO("SUCCESS", "");
		try {
			// 1. Check bankAccountEntity with sync = true (add sync boolean field)
			// 2. Find account_customer_bank by bank_id/bank_account AND auth = true.
			// 3. Find customer_sync and push data to customer.
			if (accountBankEntity.isSync() == true || accountBankEntity.isWpSync() == true) {
				TransactionBankCustomerDTO transactionBankCustomerDTO = new TransactionBankCustomerDTO();
				transactionBankCustomerDTO.setTransactionid(dto.getTransactionid());
				transactionBankCustomerDTO.setTransactiontime(time);
				transactionBankCustomerDTO.setReferencenumber(dto.getReferencenumber());
				transactionBankCustomerDTO.setAmount(dto.getAmount());
				transactionBankCustomerDTO.setContent(dto.getContent());
				transactionBankCustomerDTO.setBankaccount(dto.getBankaccount());
				transactionBankCustomerDTO.setTransType(dto.getTransType());
				transactionBankCustomerDTO.setReciprocalAccount(dto.getReciprocalAccount());
				transactionBankCustomerDTO.setReciprocalBankCode(dto.getReciprocalBankCode());
				transactionBankCustomerDTO.setVa(dto.getVa());
				transactionBankCustomerDTO.setValueDate(dto.getValueDate());
				transactionBankCustomerDTO.setSign(sign);
				transactionBankCustomerDTO.setOrderId(orderId);
				logger.info("getCustomerSyncEntities: Order ID: " + orderId);
				logger.info("getCustomerSyncEntities: Signature: " + sign);
				List<AccountCustomerBankEntity> accountCustomerBankEntities = new ArrayList<>();
				accountCustomerBankEntities = accountCustomerBankService
						.getAccountCustomerBankByBankId(accountBankEntity.getId());
				if (accountCustomerBankEntities != null && !accountCustomerBankEntities.isEmpty()) {
					for (AccountCustomerBankEntity accountCustomerBankEntity : accountCustomerBankEntities) {
						CustomerSyncEntity customerSyncEntity = customerSyncService
								.getCustomerSyncById(accountCustomerBankEntity.getCustomerSyncId());
						if (customerSyncEntity != null) {
							System.out.println("customerSyncEntity: " + customerSyncEntity.getId() + " - "
									+ customerSyncEntity.getInformation());
							result = pushNewTransactionToCustomerSync(transReceiveId, customerSyncEntity,
									transactionBankCustomerDTO,
									time);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("CustomerSync: Error: " + e.toString());
			System.out.println("CustomerSync: Error: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
		}
		return result;
	}

}
