package com.vietqr.org.controller;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.ConfirmCustomerVaDTO;
import com.vietqr.org.dto.bidv.RequestCustomerVaDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.security.JWTAuthorizationFilter;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.service.social.*;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.bidv.BIDVUtil;

import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import org.apache.log4j.Logger;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.AesKey;

import com.vietqr.org.dto.example.Header;
import com.vietqr.org.dto.example.JweObj;
import com.vietqr.org.dto.example.Recipients;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
	GoogleChatService googleChatService;

	@Autowired
	GoogleChatAccountBankService googleChatAccountBankService;

	@Autowired
	MerchantMemberRoleService merchantMemberRoleService;

	@Autowired
	TransactionTerminalTempService transactionTerminalTempService;

	@Autowired
	TransactionReceiveService transactionReceiveService;

	@Autowired
	TerminalService terminalService;

	@Autowired
	TransactionReceiveBranchService transactionReceiveBranchService;

	@Autowired
	BranchMemberService branchMemberService;

	@Autowired
	TerminalBankReceiveService terminalBankReceiveService;

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
	private InvoiceTransactionService invoiceTransactionService;

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
	TransReceiveTempService transReceiveTempService;

	@Autowired
	SystemSettingService systemSettingService;

	@Autowired
	private AccountCustomerBankService accountCustomerBankService;

	@Autowired
	private BankReceiveOtpService bankReceiveOtpService;

	@Autowired
	private TrAnnualFeePackageService trAnnualFeePackageService;

	@Autowired
	private AccountBankReceiveService accountBankReceiveService;

	@Autowired
	private BankReceiveActiveHistoryService bankReceiveActiveHistoryService;

    @Autowired
	private FirebaseMessagingService firebaseMessagingService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceItemService invoiceItemService;

	@Autowired
	private CustomerVaService customerVaService;

	@Autowired
	private JWTAuthorizationFilter jwtAuthorizationFilter;

	@Autowired
	TelegramService telegramService;

	@Autowired
	LarkService larkService;

	@Autowired
	SlackAccountBankService slackAccountBankService;

	@Autowired
	SlackService slackService;

	@Autowired
	DiscordAccountBankService discordAccountBankService;

	@Autowired
	DiscordService discordService;

	@Autowired
	GoogleSheetAccountBankService googleSheetAccountBankService;

	@Autowired
	GoogleSheetService googleSheetService;

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

    //push form notification
    @GetMapping(value = "/transaction/notifications/{id}", produces = "text/html")
    public String pushNotificationFrom(Model model, @PathVariable String id) {
        TransactionReceiveEntity transaction = transactionReceiveService.getTransactionsById(id);
        //List<TransactionReceiveEntity> transactions = Arrays.asList(transaction); // Chuyển đổi thành list

        model.addAttribute("transactions", Collections.singletonList(transaction));
        return "transactions";
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

	public ResponseMessageDTO insertTransBank(TransactionBankDTO dto) throws JsonProcessingException{
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
								Map<String, String> data = new HashMap<>();
								data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
								data.put("notificationId", "");
								data.put("transactionReceiveId", "");
								data.put("bankAccount", "1123355589");
                                data.put("bankName", "Ngan hang TMCP Quan Doi"); //
                                data.put("bankCode", "MB");
                                data.put("terminalName", ""); //
                                data.put("terminalCode", ""); //
                                data.put("rawTerminalCode", "");
                                data.put("oderId", "");
                                data.put("referenceNumber", dto.getReferencenumber());
                                data.put("content", dto.getContent());
                                data.put("amount", "" + dto.getAmount());
                                data.put("time", "" + dto.getTransactiontime());
                                data.put("timePaid", "" + dto.getTransactiontime());
                                data.put("refId", "" + dto.getTransactionid());
                                data.put("transType", "" + dto.getTransType()); // thêm transType rồi
                                // thêm type nữa
                                data.put("status", "1");
                                data.put("traceId", "" + "");
                                //data.put("transType", dto.getTransType());
                                data.put("urlLink", "");
                                data.put("type", "0");

                                // push form noti
                                //initialize data check
                                int typeCheck = 0;
                                String statusCheck = data.put("status", "1");
                                String bankCodeCheck = data.put("bankCode", "MB");
                                String terminalCodeCheck = data.put("terminalCode", "");
                                String terminalNameCheck = data.put("terminalName", "");

                                // check conditions to push form notifications
                                if (statusCheck.equals("0") && typeCheck == 0) {
                                    data.put("html", "<div><span style=\"font-size: 12;\">Bạn có 1 hóa đơn<strong> " + dto.getAmount() +
											"</strong><br>cần thanh toán!</span></div>");
                                } // thông báo hoá đơn chưa thanh toán

                                if (dto.getTransType() == "C" && typeCheck == 2) {
                                    data.put("html", "<div><span style=\"font-size: 12;\">" + dto.getAmount() + " VNĐ đến "
                                            + bankCodeCheck + " - " + dto.getBankaccount() + "</span></div>");
                                } // Nhận tiền Đến

                                if (dto.getTransType() == "C" && typeCheck == 1) {
                                    data.put("html", "<div><span style=\"font-size: 12;\">+" + dto.getAmount() + " VNĐ đến MB Bank - "
                                            + dto.getBankaccount() + " - "
                                            + terminalNameCheck + " - "
                                            + terminalCodeCheck + "</span></div>");
                                } // Cập nhật tài khoản cửa hàng

                                if (dto.getTransType() == "D") {
                                    data.put("html", "<div><span style=\"font-size: 12;\">" + dto.getAmount() + " VNĐ từ "
                                            + bankCodeCheck + " - " + dto.getBankaccount() + "</span></div>");
                                } // Chuyển tiền đi

                                if (dto.getTransType() == "C" && typeCheck == 0) {
                                    data.put("html", dto.getBankaccount() + "Chưa biết trả gì");
                                }
                                //--
                                try {
									// send msg to QR Link
									String refId = TransactionRefIdUtil
											.encryptTransactionId(dto.getTransactionid());
									socketHandler.sendMessageToTransactionRefId(refId, data);
								} catch (IOException e) {
									logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: "
											+ e.toString());
								}
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
								String rawCode = "";
								String boxIdRef = "";
								ISubTerminalCodeDTO rawDTO = null;
								if (traceId != null && !traceId.isEmpty()) {
									logger.info("transaction-sync - trace ID detect: " + traceId);
									TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
											.getTransactionByTraceIdAndAmount(traceId, dto.getAmount() + "",
													dto.getTransType().trim().toUpperCase());
									if (transactionReceiveEntity != null) {
										orderId = transactionReceiveEntity.getOrderId();
										sign = transactionReceiveEntity.getSign();
										if (transactionReceiveEntity.getTerminalCode() != null
												&& !transactionReceiveEntity.getTerminalCode().trim().isEmpty()) {
											TerminalEntity terminalEntity = terminalService
													.getTerminalByTerminalCode(
															transactionReceiveEntity.getTerminalCode());
											if (terminalEntity != null) {
												rawCode = terminalEntity.getRawTerminalCode();
											} else {
												rawCode = terminalBankReceiveService
														.getTerminalBankReceiveByTerminalCode(
																transactionReceiveEntity.getTerminalCode());
											}
										}
										String urlLink = transactionReceiveEntity.getUrlLink() != null
												? transactionReceiveEntity.getUrlLink()
												: "";
										result = getCustomerSyncEntities(transactionReceiveEntity.getId(), dto,
												accountBankEntity, time, orderId, sign, rawCode, urlLink, transactionReceiveEntity.getTerminalCode());
										updateTransaction(dto, transactionReceiveEntity, accountBankEntity, time, nf, boxIdRef);
										// check if recharge => do update status and push data to customer
										////////// USER RECHAGE VQR || USER RECHARGE MOBILE
										if (transactionReceiveEntity.getType() == 5) {
											// find transactionWallet by billNumber and status = 0
											TransactionWalletEntity transactionWalletEntity = transactionWalletService
													.getTransactionWalletByBillNumber(orderId);
											processTransactionWallet(nf, time, dto, orderId, transactionWalletEntity, transactionReceiveEntity.getId());
										}
									} else {
										logger.info(
												"transaction-sync - cannot find transaction receive. Receive new transaction outside system");
										// process here
										UUID transcationUUID = UUID.randomUUID();
										String terminalCodeContent = "";
										if (accountBankEntity.getTerminalLength() > 0) {
											terminalCodeContent = getTraceId(dto.getContent(), "SQR",
													accountBankEntity.getTerminalLength());
										}
										String terminalCode = "";
										if (terminalCodeContent.length() > 3) {
											// find terminal by terminalCode
											terminalCode = terminalCodeContent.substring(3);
										}

										if (!terminalCode.trim().isEmpty()) {
											TerminalEntity terminalEntity = terminalService
													.getTerminalByTerminalCode(terminalCode);
											if (terminalEntity != null) {
												rawCode = terminalEntity.getRawTerminalCode();
											} else {
												rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(
														terminalCode);
												if (rawDTO != null) {
													rawCode = rawDTO.getRawCode();
													if (rawDTO.getQrType() == 2) {
														boxIdRef = rawDTO.getRawCode();
													}
												}
											}
										}

										result = getCustomerSyncEntities(transcationUUID.toString(), dto,
												accountBankEntity, time, orderId, sign, rawCode, "", terminalCode);
										insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity, time,
												traceId, uuid, nf, "", "", boxIdRef);
									}
									// }
								} else {
									logger.info(
											"transaction-sync - traceId is empty. Receive new transaction outside system");
									UUID transcationUUID = UUID.randomUUID();
									String terminalCodeContent = "";
									if (accountBankEntity.getTerminalLength() > 0) {
										terminalCodeContent = getTraceId(dto.getContent(), "SQR",
												accountBankEntity.getTerminalLength());
									}
									String terminalCode = "";
									if (terminalCodeContent.length() > 3) {
										// find terminal by terminalCode
										terminalCode = terminalCodeContent.substring(3);
									}

									if (!terminalCode.trim().isEmpty()) {
										TerminalEntity terminalEntity = terminalService
												.getTerminalByTerminalCode(terminalCode);
										if (terminalEntity != null) {
											rawCode = terminalEntity.getRawTerminalCode();
										} else {
											rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(
													terminalCode);
											if (rawDTO != null) {
												rawCode = rawDTO.getRawCode();
												if (rawDTO.getQrType() == 2) {
													boxIdRef = rawDTO.getRawCode();
												}
											}
										}
									}
									result = getCustomerSyncEntities(transcationUUID.toString(), dto, accountBankEntity,
											time, orderId, sign, rawCode, "", terminalCode);
									insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity, time,
											traceId, uuid, nf, "", "", boxIdRef);
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
	public ResponseEntity<TransactionResponseDTO> insertTransactionBank(@RequestBody TransactionBankDTO dto) {
		TransactionResponseDTO result = null;
		HttpStatus httpStatus = null;
		UUID uuid = UUID.randomUUID();
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		LocalDateTime currentDateTime = LocalDateTime.now();
		long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
		logger.info("receive transaction sync from MB: " + dto.toString() + " at: " + time);
		// System.out.println("receive transaction sync from MB: " + dto.toString() + "
		// at: " + time);
		// kiểm tra xem đã có push ở lg 2 chưa
		boolean checkDuplicate = checkDuplicateReferenceNumber(dto.getReferencenumber(), dto.getTransType());

		// validated dau vao cua MB bank
		result = validateTransactionBank(dto, uuid.toString());
		// String bankCode = "MB";
		// String bankTypeId = bankTypeService.getBankTypeIdByBankCode(bankCode);
		// because of bank_type default is MB => aa4e489b-254e-4351-9cd4-f62e09c63ebc
		String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
		AccountBankReceiveEntity accountBankEntity = accountBankService
				.getAccountBankByBankAccountAndBankTypeId(dto.getBankaccount(), bankTypeId);
		try {
			// danh sách transaction Id đã được insert
			List<Object> list = transactionBankService.checkTransactionIdInserted(dto.getTransactionid(),
					dto.getTransType());
			// System.out.println("list size: " + list.size());
			if (list != null && list.isEmpty()) {
				if (!result.isError()) {
					// System.out.println("checkDuplicate: " + checkDuplicate);
					// Nếu chưa insert transaction_bank
					if (checkDuplicate) {
						// có taài khoản bank trong he thong VietQR
						if (accountBankEntity != null) {
							// nêếu mms_active = true và transType = C => không insert transaction_bank do
							// đã insert ở luồng ưu tiên
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
							// MB push gì về collect hết
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
					// Nếu đầu vào của MB bank lỗi
					logger.error("Transaction-sync:  Error receive data: " + "Unexpected Error");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				// nếu transactionid đã được insert thì báo lôi cho MB
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
			// nếu có lỗi thì trả về lỗi cho MB
			// ko có lỗi thì return cho bank trước khi qua finally
			return new ResponseEntity<>(result, httpStatus);
		} finally {
			TransactionResponseDTO finalResult = result;
			Thread thread = new Thread(() -> {
				// AccountBankReceiveEntity accountBankEntity = accountBankService
				// .getAccountBankById(transactionReceiveEntity.getBankId());
				if (accountBankEntity != null) {
					if (accountBankEntity.isMmsActive() == true
							&& dto.getTransType().trim().toUpperCase().equals("C")) {
						if (!finalResult.isError()) {
							if (checkDuplicate) {
								String traceId = getTraceId(dto.getContent(), "VQR");
								String orderId = "";
								String sign = "";
								String rawCode = "";
								String boxIdRef = "";
								ISubTerminalCodeDTO rawDTO = null;
								if (traceId != null && !traceId.isEmpty()) {
									logger.info("transaction-sync - trace ID detect: " + traceId);
									TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
											.getTransactionByTraceIdAndAmount(traceId, dto.getAmount() + "",
													dto.getTransType().trim().toUpperCase());
									if (transactionReceiveEntity != null) {
										if (transactionReceiveEntity.getQrCode() == null
												|| transactionReceiveEntity.getQrCode().trim().isEmpty()) {
											orderId = transactionReceiveEntity.getOrderId();
											sign = transactionReceiveEntity.getSign();
											if (transactionReceiveEntity.getTerminalCode() != null
													&& !transactionReceiveEntity.getTerminalCode().trim().isEmpty()) {
												TerminalEntity terminalEntity = terminalService
														.getTerminalByTerminalCode(
																transactionReceiveEntity.getTerminalCode());
												if (terminalEntity != null) {
													rawCode = terminalEntity.getRawTerminalCode();
												} else {
//													rawCode = terminalBankReceiveService
//															.getTerminalBankReceiveByTerminalCode(
//																	transactionReceiveEntity.getTerminalCode());
													rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(
															transactionReceiveEntity.getTerminalCode());
													if (rawDTO != null) {
														rawCode = rawDTO.getRawCode();
														if (rawDTO.getQrType() == 2) {
															boxIdRef = rawDTO.getRawCode();
														}
													}
												}
											}
											String urlLink = transactionReceiveEntity.getUrlLink() != null
													? transactionReceiveEntity.getUrlLink()
													: "";
											getCustomerSyncEntities(transactionReceiveEntity.getId(), dto,
													accountBankEntity, time, orderId, sign, rawCode, urlLink, "");
                                            try {
                                                updateTransaction(dto, transactionReceiveEntity, accountBankEntity, time,
                                                        nf, boxIdRef);
                                            } catch (JsonProcessingException e) {
                                                throw new RuntimeException(e);
                                            }
                                            // check if recharge => do update status and push data to customer
											////////// USER RECHAGE VQR || USER RECHARGE MOBILE
											// giao dịch nạp tiền đt (VNPTePay) của Bluecom
											// bỏ qua
											if (transactionReceiveEntity.getType() == 5) {
												// find transactionWallet by billNumber and status = 0
												TransactionWalletEntity transactionWalletEntity = transactionWalletService
														.getTransactionWalletByBillNumber(orderId);
                                                try {
                                                    processTransactionWallet(nf, time, dto, orderId,
                                                            transactionWalletEntity, transactionReceiveEntity.getId());
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
										}
									} else {
										logger.info(
												"Transaction-sync: mms_active = true => do not update transaction_receive AND push data to customer sync");
									}
									// }
								} else {
									logger.info(
											"Transaction-sync: mms_active = true => do not update transaction_receive AND push data to customer sync");
								}

							} else {
								logger.error("Transaction-sync: Duplicate Reference number");
							}
						} else {
							logger.error("Transaction-sync: Error receive data: " + finalResult.toString());
						}
					} else {
						if (!finalResult.isError()) {
							if (checkDuplicate) {
								if (accountBankEntity != null) {
									// find transaction by id
									String traceId = getTraceId(dto.getContent(), "VQR");
									String orderId = "";
									String sign = "";
									String rawCode = "";
									String rawCodeResult = "";
									String boxIdRef = "";
									ISubTerminalCodeDTO rawDTO = null;
									if (traceId != null && !traceId.isEmpty()) {
										logger.info("transaction-sync - trace ID detect: " + traceId);
										TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
												.getTransactionByTraceIdAndAmount(traceId, dto.getAmount() + "",
														dto.getTransType().trim().toUpperCase());
										if (transactionReceiveEntity != null) {
											orderId = transactionReceiveEntity.getOrderId();
											sign = transactionReceiveEntity.getSign();
											if (transactionReceiveEntity.getTerminalCode() != null
													&& !transactionReceiveEntity.getTerminalCode().trim().isEmpty()) {
												TerminalEntity terminalEntity = terminalService
														.getTerminalByTerminalCode(
																transactionReceiveEntity.getTerminalCode());
												if (terminalEntity != null) {
													rawCode = terminalEntity.getRawTerminalCode();
												} else {
//													rawCode = terminalBankReceiveService
//															.getTerminalBankReceiveByTerminalCode(
//																	transactionReceiveEntity.getTerminalCode());
													rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(
															transactionReceiveEntity.getTerminalCode());
													if (rawDTO != null) {
														rawCode = rawDTO.getRawCode();
														if (rawDTO.getQrType() == 2) {
															boxIdRef = rawDTO.getRawCode();
														}
													}
												}
											}
											if (rawCode == null || rawCode.trim().isEmpty()) {
												rawCodeResult = "";
											} else {
												rawCodeResult = rawCode;
											}
											String urlLink = transactionReceiveEntity.getUrlLink() != null
													? transactionReceiveEntity.getUrlLink()
													: "";
											getCustomerSyncEntities(transactionReceiveEntity.getId(), dto,
													accountBankEntity, time, orderId, sign, rawCodeResult, urlLink, transactionReceiveEntity.getTerminalCode());
                                            try {
                                                updateTransaction(dto, transactionReceiveEntity, accountBankEntity, time,
                                                        nf, boxIdRef);
                                            } catch (JsonProcessingException e) {
                                                throw new RuntimeException(e);
                                            }
                                            // check if recharge => do update status and push data to customer
											////////// USER RECHAGE VQR || USER RECHARGE MOBILE
											if (transactionReceiveEntity.getType() == 5) {
												// find transactionWallet by billNumber and status = 0
												TransactionWalletEntity transactionWalletEntity = transactionWalletService
														.getTransactionWalletByBillNumber(orderId);
                                                try {
                                                    processTransactionWallet(nf, time, dto, orderId,
                                                            transactionWalletEntity, transactionReceiveEntity.getId());
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
										} else {
											logger.info(
													"transaction-sync - cannot find transaction receive. Receive new transaction outside system");
											// process here
											UUID transcationUUID = UUID.randomUUID();
											// push websocket
											String terminalCodeContent = "";
											if (accountBankEntity.getTerminalLength() > 0) {
												terminalCodeContent = getTraceId(dto.getContent(), "SQR",
														accountBankEntity.getTerminalLength());
											}
											String terminalCode = "";
											if (terminalCodeContent.length() > 3) {
												// find terminal by terminalCode
												terminalCode = terminalCodeContent.substring(3);
											}

											if (!terminalCode.trim().isEmpty()) {
												TerminalEntity terminalEntity = terminalService
														.getTerminalByTerminalCode(terminalCode);
												if (terminalEntity != null) {
													rawCode = terminalEntity.getRawTerminalCode();
												} else {
													rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(terminalCode);
													if (rawDTO != null) {
														rawCode = rawDTO.getRawCode();
														if (rawDTO.getQrType() == 2) {
															boxIdRef = rawDTO.getRawCode();
														}
													}
												}
											}
											getCustomerSyncEntities(transcationUUID.toString(), dto, accountBankEntity,
													time, orderId, sign, rawCode, "", terminalCode);
											// push notification


                                                insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity,
                                                        time,
                                                        traceId, uuid, nf, "", "", boxIdRef);


                                        }
										// }
									} else {
										logger.info(
												"transaction-sync - traceId is empty. Receive new transaction outside system");
										UUID transcationUUID = UUID.randomUUID();
										String terminalCodeContent = "";
										if (accountBankEntity.getTerminalLength() > 0) {
											terminalCodeContent = getTraceId(dto.getContent(), "SQR",
													accountBankEntity.getTerminalLength());
										}
										String terminalCode = "";
										if (terminalCodeContent.length() > 3) {
											// find terminal by terminalCode
											terminalCode = terminalCodeContent.substring(3);
										}

										if (!terminalCode.trim().isEmpty()) {
											TerminalEntity terminalEntity = terminalService
													.getTerminalByTerminalCode(terminalCode);
											if (terminalEntity != null) {
												rawCode = terminalEntity.getRawTerminalCode();
											} else {
												rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(
														terminalCode);
												if (rawDTO != null) {
													rawCode = rawDTO.getRawCode();
													if (rawDTO.getQrType() == 2) {
														boxIdRef = rawDTO.getRawCode();
													}
												}
											}
										}
										getCustomerSyncEntities(transcationUUID.toString(), dto, accountBankEntity,
												time,
												orderId, sign, rawCode, "", terminalCode);
                                            insertNewTransaction(transcationUUID.toString(), dto, accountBankEntity, time,
                                                    traceId, uuid, nf, "", "", boxIdRef);

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
			});
			thread.start();
		}

	}

	public String getTraceId(String inputString, String prefix, int length) {
		String result = "";
		length = length + 3;
		try {
			inputString = inputString.replaceAll("\\.", " ");
			inputString = inputString.replaceAll("\\-", " ");
			String[] newPaths = inputString.split("\\s+");
			String traceId = "";
			int indexSaved = -1;
			for (int i = 0; i < newPaths.length; i++) {
				if (newPaths[i].contains(prefix)) {
					if (newPaths[i].length() >= length) {
						traceId = newPaths[i].substring(0, length);
						if (traceId.startsWith(prefix)) {
							break;
						} else {
							int startIndex = newPaths[i].indexOf(prefix);
							traceId = newPaths[i].substring(startIndex, startIndex + length);
							break;
						}
					}
					traceId = newPaths[i];
					indexSaved = i;
				} else if (indexSaved != -1 && i == indexSaved + 1) {
					if (traceId.length() < length) {
						traceId += newPaths[i].substring(0, Math.min(length - traceId.length(), newPaths[i].length()));
					}
				}
			}

			if (!traceId.isEmpty()) {
				String pattern = String.format("VQR.{%s}", length - 3);
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(traceId);
				if (m.find()) {
					traceId = m.group(0);
				} else {
					String pattern2 = String.format("VQR[0-9a-f]{%s}", length - 3);
					Pattern regex = Pattern.compile(pattern2);
					Matcher matcher = regex.matcher(inputString);

					if (matcher.find()) {
						traceId = matcher.group();
					}
				}
			}

			result = traceId;
		} catch (Exception e) {
			System.out.println("ERROR: " + e.toString());
		}

		return result;
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
					String pattern2 = "VQR[0-9a-f]{10}";
					Pattern regex = Pattern.compile(pattern2);
					Matcher matcher = regex.matcher(inputString);

					if (matcher.find()) {
						traceId = matcher.group();
					}
				}
			}

			result = traceId;
		} catch (Exception e) {
			System.out.println("ERROR: " + e.toString());
		}

		return result;
	}

	@Async
	protected void processTransactionWallet(NumberFormat nf, long time, TransactionBankDTO dto, String orderId,
											TransactionWalletEntity entity, String transactionId) throws JsonProcessingException {
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
														phoneNo,
														userId, otp,
														1);
									} else if (topupResponseCode == 23 || topupResponseCode == 99) {
										msgErrorCode = "E62";
										transactionWalletService
												.updateTransactionWallet(3, timeM,
														tranMobileEntity.getAmount()
																+ "",
														phoneNo,
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
														phoneNo,
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
														phoneNo,
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
														phoneNo,
														userId, otp,
														1);
										System.out.println(
												"transaction-sync: TRANSACTION FAILED: " + topupResponseCode);
										logger.error("transaction-sync: TRANSACTION FAILED: " + topupResponseCode);
									}
									// update account wallet amount if vnpt epay failed
									if (topupResponseCode != 0) {
										// update amount account wallet
										AccountWalletEntity accountWalletEntity = accountWalletService
												.getAccountWalletByUserId(userIdRecharge);
										if (accountWalletEntity != null) {
											Long currentAmount = Long
													.parseLong(accountWalletEntity.getAmount());
											Long updatedAmount = currentAmount + dto.getAmount();
											accountWalletService.updateAmount(updatedAmount + "",
													accountWalletEntity.getId());
											logger.info("transaction-sync: process wallet: refund user: "
													+ userIdRecharge + " - " + dto.getAmount());
										}
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
					} else if (parts.length == 6) {
						Integer paymentType = Integer.parseInt(parts[0]);
						String feeId = parts[1];
						String userId = parts[2];
						String otp = parts[3];
						String bankId = parts[4];
						String otpActive = parts[5];
						if (userId != null && otp != null) {
							// check valid transaction by otp & userId
							// & paymentType & status = 0
							String checkTransWalletId = transactionWalletService
									.checkExistedTransactionnWallet(otp,
											userId, 2);
							if (checkTransWalletId != null && !checkTransWalletId.trim().isEmpty()) {
								// find transactionWalletEntity by Id
								TransactionWalletEntity transactionWalletEntity = transactionWalletService
										.getTransactionWalletById(checkTransWalletId);
								if (transactionWalletEntity != null) {
									// active key
									BankReceiveOtpDTO bankReceiveOtpDTO = bankReceiveOtpService
											.checkBankReceiveOtp(userId, bankId, otpActive, feeId);
									LocalDateTime dateTime = LocalDateTime.now();
									long currentTime = dateTime.toEpochSecond(ZoneOffset.UTC);
									if (bankReceiveOtpDTO == null) {
										// otp sai hoặc ko tìm thấy key
										AccountWalletEntity accountWalletEntity = accountWalletService
												.getAccountWalletByUserId(userId);
										if (accountWalletEntity != null) {
											long amount = Long.parseLong(accountWalletEntity.getAmount()) + dto.getAmount();
											accountWalletService.updateAmount(amount + "", accountWalletEntity.getId());
										} else {
											System.out.println("transaction-sync: accountWalletEntity IS NULL");
											logger.error("transaction-sync: accountWalletEntity IS NULL");
										}
									} else {
										ITrAnnualFeeDTO ITrAnnualFeeDTO = trAnnualFeePackageService
												.getFeeById(feeId);
										if (ITrAnnualFeeDTO == null) {
											System.out.println("transaction-sync: transactionWalletEntity IS NULL");
											logger.error("transaction-sync: transactionWalletEntity IS NULL");
										} else {
											long validFeeFrom;
											long validFeeTo = 0;
											// update validFeeFrom, validFeeTo
											BankReceiveCheckDTO bankReceiveCheckDTO = accountBankReceiveService
													.checkBankReceiveActive(bankId);
											if (bankReceiveCheckDTO.getIsValidService()) {
                                                validFeeFrom = 0;
                                                // update validFeeTo
												validFeeTo = bankReceiveCheckDTO.getValidTo();
												validFeeTo = DateTimeUtil.plusMonthAsLongInt(validFeeTo, ITrAnnualFeeDTO.getDuration());
											} else {
												// update validFeeFrom, validFeeTo
												validFeeFrom = currentTime;
												validFeeTo = DateTimeUtil.plusMonthAsLongInt(currentTime, ITrAnnualFeeDTO.getDuration());
											}
											// save history
											BankReceiveActiveHistoryEntity bankReceiveActiveHistoryEntity = new BankReceiveActiveHistoryEntity();
											bankReceiveActiveHistoryEntity.setId(UUID.randomUUID().toString());
											bankReceiveActiveHistoryEntity.setKeyId(bankReceiveOtpDTO.getId());
											bankReceiveActiveHistoryEntity.setType(1);
											bankReceiveActiveHistoryEntity.setKeyActive(bankReceiveOtpDTO.getKeyActive());
											bankReceiveActiveHistoryEntity.setBankId(bankId);
											bankReceiveActiveHistoryEntity.setUserId(userId);
											bankReceiveActiveHistoryEntity.setCreateAt(currentTime);
											bankReceiveActiveHistoryEntity.setValidFeeFrom(validFeeFrom);
											bankReceiveActiveHistoryEntity.setValidFeeTo(validFeeTo);
											bankReceiveActiveHistoryEntity.setData("");
											bankReceiveActiveHistoryEntity.setRefId(transactionWalletEntity.getId());
											long finalValidFeeTo = validFeeTo;
											Thread thread = new Thread(() -> {
												Double vat = systemSettingService.getVatSystemSetting();
												IBankAccountInfoDTO bankAccountInfoDTO = accountBankReceiveService
														.getAccountBankInfoById(bankId);
												Long totalAMount = trAnnualFeePackageService
														.getTotalAmount(feeId);
												TrAnnualFeeInvoiceItemDTO itemFee = new TrAnnualFeeInvoiceItemDTO();
												itemFee.setAmount(totalAMount);
												long amountAfterVat = Long.parseLong(transactionWalletEntity.getAmount());
												itemFee.setAmountAfterVat(amountAfterVat);
												itemFee.setAmountVat(amountAfterVat - totalAMount);
												itemFee.setBankId(bankId);
												itemFee.setUserId(userId);
												itemFee.setTransWalletId(transactionWalletEntity.getId());
												itemFee.setBankAccount(bankAccountInfoDTO.getBankAccount());
												itemFee.setBankShortName(bankAccountInfoDTO.getBankShortName());
												itemFee.setUserBankName(bankAccountInfoDTO.getUserBankName());
												itemFee.setValidFrom(validFeeFrom);
												itemFee.setValidTo(finalValidFeeTo);
												BankAccountInfoDTO bankInfo = new BankAccountInfoDTO();
												bankInfo.setBankAccount(bankAccountInfoDTO.getBankAccount());
												bankInfo.setBankShortName(bankAccountInfoDTO.getBankShortName());
												bankInfo.setUserBankName(bankAccountInfoDTO.getUserBankName());
												createInvoiceTransWallet(totalAMount, bankInfo, itemFee, vat);
											});
											thread.start();

											bankReceiveOtpService.updateStatusBankReceiveOtp(bankReceiveOtpDTO.getId(), 1);
											bankReceiveActiveHistoryService.insert(bankReceiveActiveHistoryEntity);
											accountBankReceiveService.updateActiveBankReceive(bankId, validFeeFrom, validFeeTo);

											// notification
											// push notification
											String notificationUUID = UUID.randomUUID().toString();
											String notiType = NotificationUtil.getNotiAnnualFee();
											String title = NotificationUtil
													.getNotiRecharge();
											String message = "";
											message = NotificationUtil
													.getNotiDescActiveKey1()
													+ "+"
													+ nf.format(Long.parseLong(
													transactionWalletEntity.getAmount()))
													+ NotificationUtil
													.getNotiDescActiveKey2()
													+ NotificationUtil
													.getNotiDescActiveKey3();
											NotificationEntity notiEntity = new NotificationEntity();
											notiEntity.setId(notificationUUID);
											notiEntity.setRead(false);
											notiEntity.setMessage(message);
											notiEntity.setTime(time);
											notiEntity.setType(
													notiType);
											notiEntity.setUserId(userId);
											notiEntity.setData(checkTransWalletId);
											Map<String, String> data = new HashMap<>();
											data.put("notificationType",
													notiType);
											data.put("notificationId",
                                                    notificationUUID);
											data.put("amount",
													transactionWalletEntity.getAmount() + "");
											data.put("transWalletId", checkTransWalletId);
											data.put("time", time + "");
											data.put("bankId", bankId);
											data.put("billNumber", transactionWalletEntity.getBillNumber());
											data.put("paymentMethod", "1");
											data.put("paymentType", "2");
											data.put("status", 1 + "");
											data.put("message", message);
											pushNotification(title, message, notiEntity, data, userId);

										}
									}
									// update transaction wallet
									transactionWalletService.updateTransactionWallet(1, time,
											transactionWalletEntity.getAmount() + "",
											"",
											transactionWalletEntity.getUserId(),
											transactionWalletEntity.getOtp(),
											2);
								} else {
									System.out.println("transaction-sync: transactionWalletEntity IS NULL");
									logger.error("transaction-sync: transactionWalletEntity IS NULL");
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
					} else if (parts.length == 4) {
						Integer paymentType = Integer.parseInt(parts[0]);
						String userId = parts[1];
						String otp = parts[2];
						String bankId = parts[3];
						int checkSuccess = 0;
						boolean isTotal = false;
						if (userId != null && otp != null && paymentType != null) {
							String checkTransWalletId = transactionWalletService
									.checkExistedTransactionnWallet(otp,
											userId, paymentType);
							boolean checkSuccessProcess = false;
							if (!StringUtil.isNullOrEmpty(checkTransWalletId)) {
								// find transactionWalletEntity by Id
								TransactionWalletEntity transactionWalletDebit = transactionWalletService
										.getTransactionWalletById(checkTransWalletId);
								if (transactionWalletDebit != null) {

									ObjectMapper mapper = new ObjectMapper();
									long timePaid = DateTimeUtil.getCurrentDateTimeUTC();
									long amount = Long.parseLong(transactionWalletDebit.getAmount());
									InvoiceTransactionEntity invoiceTransactionEntity = invoiceTransactionService.getInvoiceTransactionByRefId(transactionId);
									InvoiceEntity invoiceEntity = invoiceService.getInvoiceEntityById(invoiceTransactionEntity.getInvoiceId());
									if (invoiceTransactionEntity != null && invoiceEntity != null) {
										String invoiceItemIds = invoiceTransactionEntity.getInvoiceItemIds();
										List<String> itemIds = mapper.readValue(invoiceItemIds, new TypeReference<List<String>>() {});
										List<InvoiceItemEntity> invoiceItemEntities = new ArrayList<>();
										int check = invoiceItemService.updateAllItemIds(itemIds, timePaid);
										int checkNotPaid = 0;
										checkNotPaid = invoiceItemService.checkCountUnPaid(invoiceEntity.getId());
										if (checkNotPaid == 0) {
											isTotal = true;
											invoiceService.updateStatusInvoice(invoiceEntity.getId(), 1, timePaid);
										} else {
											invoiceService.updateStatusInvoice(invoiceEntity.getId(), 3);
										}
										checkSuccess = 1;
										if (checkSuccess > 0) {
											checkSuccessProcess = true;
											// update transaction wallet
											transactionWalletService.updateTransactionWallet(1, time,
													transactionWalletDebit.getAmount() + "",
													"",
													transactionWalletDebit.getUserId(),
													transactionWalletDebit.getOtp(),
													paymentType);
										}
									} else {
										InvoiceEntity entityByRefId = invoiceService.getInvoiceEntityByRefId(transactionWalletDebit.getId(), amount);
										if (entityByRefId != null && entityByRefId.getStatus() != 1) {
											checkSuccess = invoiceService.updateStatusInvoice(entityByRefId.getId(), 1);
											invoiceItemService.updateStatusInvoiceItem(entityByRefId.getId());
											if (checkSuccess > 0) {
												checkSuccessProcess = true;
												// update transaction wallet
												transactionWalletService.updateTransactionWallet(1, time,
														transactionWalletDebit.getAmount() + "",
														"",
														transactionWalletDebit.getUserId(),
														transactionWalletDebit.getOtp(),
														paymentType);
											}
										}
									}

									if (checkSuccess > 0) {
										// notification
										// push notification
										String notificationUUID = UUID.randomUUID().toString();
										String notiType = NotificationUtil.getNotiInvoice();
										String title = NotificationUtil.getNotiTitleInvoiceSucessFinal();
										String message = NotificationUtil.getNotiDescActiveKey1()
												+ invoiceEntity.getInvoiceId()
												+ NotificationUtil.getNotiTitleInvoiceTotalAmount()
												+ invoiceEntity.getTotalAmount() + " VNĐ"
												//+ NotificationUtil.getNotiDescActiveKey2()
												+ NotificationUtil.getNotiDescActiveKey3();

										NotificationEntity notiEntity = new NotificationEntity();
										notiEntity.setId(notificationUUID);
										notiEntity.setRead(false);
										notiEntity.setMessage(message);
										notiEntity.setTime(time);
										notiEntity.setType(
												notiType);
										notiEntity.setUserId(userId);
										notiEntity.setData(checkTransWalletId);
										Map<String, String> data = new HashMap<>();
										data.put("notificationType",
												notiType);
										data.put("notificationId",
												notificationUUID);
										data.put("amount",
												dto.getAmount() + "");
										data.put("time", time + "");
										data.put("status", 1 + "");
										data.put("message", message);
										data.put("status", "1");
										if (!isTotal) {
											data.put("html", "<div><span style=\"font-size: 12;\">Bạn đã thanh toán thành công số tiền <br> <strong> "
													+ StringUtil.formatNumberAsString(dto.getAmount() + "") + " VND " +
													"</strong><br>cho một phần hóa đơn " + invoiceEntity.getInvoiceId() + " </span></div>");
										} else {
											data.put("html", "<div><span style=\"font-size: 12;\">Bạn đã thanh toán thành công số tiền <br> <strong> "
													+ StringUtil.formatNumberAsString(dto.getAmount() + "") + " VND " +
													"</strong><br>cho hóa đơn " + invoiceEntity.getInvoiceId() + " </span></div>");
										}

										Thread thread = new Thread(() -> {
											pushNotification(title, message, notiEntity, data, notiEntity.getUserId());
										});
										thread.start();

									} else {
											System.out.println(
													"transaction-sync: TRAN WALLET INVOICE NULL");
											logger.error("transaction-sync: TRAN WALLET INVOICE IS ALREADY PAID OR CANCELED ");
										}
								} else {
									System.out.println(
											"transaction-sync: TRAN WALLET INVOICE NULL");
									logger.error("transaction-sync: TRAN WALLET INVOICE NULL");
								}
							} else {
								System.out.println("transaction-sync: INVALID OTP");
								logger.error("transaction-sync: INVALID OTP");
							}
							if (!checkSuccessProcess) {
								// update amount account wallet
								AccountWalletEntity accountWalletEntity = accountWalletService
										.getAccountWalletByUserId(userIdRecharge);
								if (accountWalletEntity != null) {
									Long currentAmount = Long
											.parseLong(accountWalletEntity.getAmount());
									Long updatedAmount = currentAmount + dto.getAmount();
									accountWalletService.updateAmount(updatedAmount + "",
											accountWalletEntity.getId());
									logger.info("transaction-sync: process wallet: refund user: "
											+ userIdRecharge + " - " + dto.getAmount());
								}
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
					//pushNotification(title, message, notiEntity, data, userIdRecharge);
				}
			}
		}
	}

	private void pushFakenotification(String userId, Map<String, String> data) {
		List<FcmTokenEntity> fcmTokens = new ArrayList<>();
		fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
		firebaseMessagingService.sendUsersNotificationWithData(data,
				fcmTokens,
				"", "");
		try {
			socketHandler.sendMessageToUser(userId,
					data);
		} catch (IOException e) {
			logger.error(
					"transaction-sync: WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
							+ e.toString());
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

	// cập nhaatk transaction và push notification cho user
	@Async
	public void updateTransaction(TransactionBankDTO dto, TransactionReceiveEntity transactionReceiveEntity,
			AccountBankReceiveEntity accountBankEntity, long time, NumberFormat nf, String boxIdRef) throws JsonProcessingException {

		String amount = "";
		if (dto.getAmount() != 0) {
			amount = dto.getAmount() + "";
		} else {
			amount = transactionReceiveEntity.getAmount() + "";
		}
		SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
		if (accountBankEntity.isValidService()) {
			amount = "";
			if (dto.getAmount() != 0) {
				amount = dto.getAmount() + "";
			} else {
				amount = "0";
			}
			if (systemSetting.getServiceActive() <= time) {
				TransReceiveTempEntity entity = transReceiveTempService
						.getLastTimeByBankId(accountBankEntity.getId());
				if (entity == null) {
					entity = new TransReceiveTempEntity();
					List<String> transIds = new ArrayList<>();
					transIds.add(transactionReceiveEntity.getId());
					entity.setId(UUID.randomUUID().toString());
					entity.setBankId(accountBankEntity.getId());
					entity.setLastTimes(time);
					entity.setTransIds(String.join(",", transIds));
					transReceiveTempService.insert(entity);
				} else {
					List<String> transIds = new ArrayList<>();
					if (entity.getTransIds() != null && !entity.getTransIds().isEmpty()) {
						transIds = new ArrayList<>(Arrays.asList(entity.getTransIds().split(",")));
					}
					if (transIds.size() < 5) {
						transIds.add(transactionReceiveEntity.getId());
						entity.setLastTimes(time);
						entity.setTransIds(String.join(",", transIds));
						transReceiveTempService.insert(entity);
					}
				}
			}
		} else {
			if (systemSetting.getServiceActive() <= time) {
				TransReceiveTempEntity entity = transReceiveTempService
						.getLastTimeByBankId(accountBankEntity.getId());
				if (entity == null) {
					entity = new TransReceiveTempEntity();
					List<String> transIds = new ArrayList<>();
					transIds.add(transactionReceiveEntity.getId());
					entity.setId(UUID.randomUUID().toString());
					entity.setBankId(accountBankEntity.getId());
					entity.setLastTimes(time);
					entity.setTransIds(String.join(",", transIds));
					transReceiveTempService.insert(entity);
				} else {
					List<String> transIds = new ArrayList<>();
					if (entity.getTransIds() != null && !entity.getTransIds().isEmpty()) {
						transIds = new ArrayList<>(Arrays.asList(entity.getTransIds().split(",")));
					}
					if (transIds.size() < 5) {
						transIds.add(transactionReceiveEntity.getId());
						entity.setLastTimes(time);
						entity.setTransIds(String.join(",", transIds));

						transReceiveTempService.insert(entity);
					} else {
						if (!accountBankEntity.isValidService()) {
							amount = "*****";
						}
					}
				}
			}
		}
		String amountForVoice = amount;
		amount = formatAmountNumber(amount);

		BankTypeEntity bankTypeEntity = bankTypeService
				.getBankTypeById(accountBankEntity.getBankTypeId());
		// update transaction receive
		transactionReceiveService.updateTransactionReceiveStatus(1,
				dto.getTransactionid(),
				dto.getReferencenumber(),
				time,
				transactionReceiveEntity.getId());

		// push notification

		// find userIds into terminal
		if (StringUtil.isNullOrEmpty(transactionReceiveEntity.getTerminalCode()) == false) {
			// find all userIds belong to terminal
			TerminalEntity terminalEntity = terminalService
					.getTerminalByTerminalCode(transactionReceiveEntity.getTerminalCode(),
							accountBankEntity.getBankAccount());
			if (terminalEntity != null) {
				List<String> userIds = terminalService
						.getUserIdsByTerminalCode(transactionReceiveEntity.getTerminalCode());
				String prefix = "";
				if (dto.getTransType().toUpperCase().equals("D")) {
					prefix = "-";
				} else {
					prefix = "+";
				}
				Thread thread = new Thread(() -> {
					TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
					transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
					transactionTerminalTempEntity.setTransactionId(transactionReceiveEntity.getId());
					transactionTerminalTempEntity.setTerminalCode(terminalEntity.getCode());
					transactionTerminalTempEntity.setTime(time);
					transactionTerminalTempEntity.setAmount(Long.parseLong(dto.getAmount() + ""));
					transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);
				});
				thread.start();
				// push notification to member of terminal
				if (userIds != null && !userIds.isEmpty()) {
					int numThread = userIds.size();
					ExecutorService executorService = Executors.newFixedThreadPool(numThread);
					for (String userId : userIds) {
						Map<String, String> data = new HashMap<>();
						// insert notification
						UUID notificationUUID = UUID.randomUUID();
						NotificationEntity notiEntity = new NotificationEntity();
						String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
								+ accountBankEntity.getBankAccount()
								+ NotificationUtil.getNotiDescUpdateTransSuffix2()
								+ prefix + amount
								+ NotificationUtil.getNotiDescUpdateTransSuffix3()
								+ terminalEntity.getName()
								+ NotificationUtil.getNotiDescUpdateTransSuffix4()
								+ dto.getContent();
						notiEntity.setId(notificationUUID.toString());
						notiEntity.setRead(false);
						notiEntity.setMessage(message);
						notiEntity.setTime(time);
						notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
						notiEntity.setUserId(userId);
						notiEntity.setData(transactionReceiveEntity.getId());
						data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
						data.put("notificationId", notificationUUID.toString());
						data.put("transactionReceiveId", transactionReceiveEntity.getId());
						data.put("bankAccount", transactionReceiveEntity.getBankAccount());
						data.put("bankName", bankTypeEntity.getBankName());
						data.put("bankCode", bankTypeEntity.getBankCode());
						data.put("bankId", transactionReceiveEntity.getBankId());
						data.put("terminalName", terminalEntity.getName() != null ? terminalEntity.getName() : "");
						data.put("terminalCode", terminalEntity.getCode() != null ? terminalEntity.getCode() : "");
						data.put("rawTerminalCode", terminalEntity.getRawTerminalCode() != null
								? terminalEntity.getRawTerminalCode()
								: "");
						data.put("orderId", transactionReceiveEntity.getOrderId() != null
								? transactionReceiveEntity.getOrderId()
								: "");
						data.put("referenceNumber", dto.getReferencenumber() != null
								? dto.getReferencenumber()
								: "");
						data.put("content", transactionReceiveEntity.getContent());
						data.put("amount", "" + amount);
						data.put("timePaid", "" + time);
						data.put("type", "" + transactionReceiveEntity.getType());
						data.put("time", "" + transactionReceiveEntity.getTime());
						data.put("refId", "" + dto.getTransactionid());
						data.put("status", "1");
						data.put("traceId", "" + transactionReceiveEntity.getTraceId());
						data.put("transType", dto.getTransType());
						data.put("urlLink", transactionReceiveEntity.getUrlLink() != null ? transactionReceiveEntity.getUrlLink() : "");
						executorService.submit(() -> pushNotification(NotificationUtil
								.getNotiTitleUpdateTransaction(), message, notiEntity, data, userId));
						try {
							// send msg to QR Link
							String refId = TransactionRefIdUtil.encryptTransactionId(transactionReceiveEntity.getId());
							socketHandler.sendMessageToTransactionRefId(refId, data);
						} catch (IOException e) {
							logger.error(
									"WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
						}
					}
					executorService.shutdown();
				}
				/////// DO INSERT TELEGRAM
//				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
//				if (chatIds != null && !chatIds.isEmpty()) {
//					TelegramUtil telegramUtil = new TelegramUtil();
//					// String telegramMsg2 = "Thanh toán thành công 🎉."
//					// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
//					// + accountBankEntity.getBankAccount()
//					// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
//					// + "\nMã giao dịch: " + dto.getReferencenumber()
//					// + "\nThời gian: " + convertLongToDate(time)
//					// + "\nNội dung: " + dto.getContent();
//					// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
//					// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
//					// + accountBankEntity.getBankAccount()
//					// + "| Ma GD: " + dto.getReferencenumber()
//					// + "| ND: " + dto.getContent()
//					// + "| " + convertLongToDate(time);
//					String telegramMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String chatId : chatIds) {
//						telegramUtil.sendMsg(chatId, telegramMsg);
//					}
//				}

				/////// DO INSERT TELEGRAM BY QVAN
				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
				if (chatIds != null && !chatIds.isEmpty()) {
					TelegramUtil telegramUtil = new TelegramUtil();
					for (String chatId : chatIds) {
						try {
							TelegramEntity telegramEntity = telegramService.getTelegramByChatId(chatId);
							if (telegramEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(telegramEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(telegramEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String telegramMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									telegramUtil.sendMsg(chatId, telegramMsg);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Telegram notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Telegram notification: " + e.getMessage());
						}
					}
				}


				/////// DO INSERT LARK
//				if (accountBankEntity.getBankAccount().equals("699699699996")) {
//					if (transactionReceiveEntity.getTransType().equals("C")) {
//						List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//						if (webhooks != null && !webhooks.isEmpty()) {
//							LarkUtil larkUtil = new LarkUtil();
//							String larkMsg = prefix + amount + " VND"
//									+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//									+ accountBankEntity.getBankAccount()
//									+ " | " + convertLongToDate(time)
//									+ " | " + dto.getReferencenumber()
//									+ " | ND: " + dto.getContent();
//							for (String webhook : webhooks) {
//								larkUtil.sendMessageToLark(larkMsg, webhook);
//							}
//						}
//					}
//				} else {
//					List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//					if (webhooks != null && !webhooks.isEmpty()) {
//						LarkUtil larkUtil = new LarkUtil();
//						String larkMsg = prefix + amount + " VND"
//								+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//								+ accountBankEntity.getBankAccount()
//								+ " | " + convertLongToDate(time)
//								+ " | " + dto.getReferencenumber()
//								+ " | ND: " + dto.getContent();
//						for (String webhook : webhooks) {
//							larkUtil.sendMessageToLark(larkMsg, webhook);
//						}
//					}
//				}

				/////// DO INSERT LARK BY QVAN
				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (webhooks != null && !webhooks.isEmpty()) {
					LarkUtil larkUtil = new LarkUtil();
					for (String webhook : webhooks) {
						try {
							LarkEntity larkEntity = larkService.getLarkByWebhook(webhook);
							if (larkEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String larkMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									larkUtil.sendMessageToLark(larkMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Lark notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Lark notification: " + e.getMessage());
						}
					}
				}



				/////// DO INSERT GOOGLE CHAT
//				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//					String googleChatMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String webhook : ggChatWebhooks) {
//						googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//					}
//				}


				// DO INSERT GOOGLE CHAT BY QVAN
				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
					for (String webhook : ggChatWebhooks) {
						try {
							GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
							if (googleChatEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String googleChatMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Google Chat notification: " + e.getMessage());
						}
					}
				}

				// DO INSERT SLACK BY QVAN
				List<String> slackWebhooks = slackAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (slackWebhooks != null && !slackWebhooks.isEmpty()) {
					SlackUtil slackUtil = new SlackUtil();
					for (String webhook : slackWebhooks) {
						try {
							SlackEntity slackEntity = slackService.getSlackByWebhook(webhook);
							if (slackEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(slackEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(slackEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String slackMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									slackUtil.sendMessageToSlack(slackMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Slack notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Slack notification: " + e.getMessage());
						}
					}
				}

				// DO INSERT DISCORD BY QVAN
				List<String> discordWebhooks = discordAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (discordWebhooks != null && !discordWebhooks.isEmpty()) {
					DiscordUtil discordUtil = new DiscordUtil();
					for (String webhook : discordWebhooks) {
						try {
							DiscordEntity discordEntity = discordService.getDiscordByWebhook(webhook);
							if (discordEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(discordEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(discordEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String discordMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									discordUtil.sendMessageToDiscord(discordMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Discord notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Discord notification: " + e.getMessage());
						}
					}
				}

				Map<String, String> data = new HashMap<>();
				data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
				data.put("notificationId", UUID.randomUUID().toString());
				data.put("transactionReceiveId", transactionReceiveEntity.getId());
				data.put("bankAccount", transactionReceiveEntity.getBankAccount());
				data.put("bankName", bankTypeEntity.getBankName());
				data.put("bankCode", bankTypeEntity.getBankCode());
				data.put("bankId", transactionReceiveEntity.getBankId());
				data.put("terminalName", terminalEntity.getName() != null ? terminalEntity.getName() : "");
				data.put("terminalCode", terminalEntity.getCode() != null ? terminalEntity.getCode() : "");
				data.put("rawTerminalCode", terminalEntity.getRawTerminalCode() != null
						? terminalEntity.getRawTerminalCode()
						: "");
				data.put("orderId", transactionReceiveEntity.getOrderId() != null
						? transactionReceiveEntity.getOrderId()
						: "");
				data.put("referenceNumber", dto.getReferencenumber() != null
						? dto.getReferencenumber()
						: "");
				data.put("content", transactionReceiveEntity.getContent());
				data.put("amount", "" + amount);
				data.put("timePaid", "" + time);
				data.put("type", "" + transactionReceiveEntity.getType());
				data.put("time", "" + transactionReceiveEntity.getTime());
				data.put("refId", "" + dto.getTransactionid());
				data.put("status", "1");
				data.put("traceId", "" + transactionReceiveEntity.getTraceId());
				data.put("transType", dto.getTransType());
				data.put("urlLink", transactionReceiveEntity.getUrlLink() != null ? transactionReceiveEntity.getUrlLink() : "");
				if (!StringUtil.isNullOrEmpty(boxIdRef)) {
					try {
						// send msg to QR Link
						BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
						String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
						data.put("message", String.format(messageForBox, amountForVoice));
						String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
						socketHandler.sendMessageToBoxId(idRefBox, data);
						logger.info("WS: socketHandler.sendMessageToQRBox - "
								+ boxIdRef + " at: " + System.currentTimeMillis());
					} catch (IOException e) {
						logger.error(
								"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
					}
				}
				// textToSpeechService.delete(requestId);
			} else {
				logger.info("transaction-sync - userIds empty.");
				// not have terminal in terminal table but still available in
				// transaction_receive
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
						+ prefix + amount
						+ NotificationUtil.getNotiDescUpdateTransSuffix4()
						+ dto.getContent();
				notiEntity.setId(notificationUUID.toString());
				notiEntity.setRead(false);
				notiEntity.setMessage(message);
				notiEntity.setTime(time);
				notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
				notiEntity.setUserId(accountBankEntity.getUserId());
				notiEntity.setData(transactionReceiveEntity.getId());
				Map<String, String> data = new HashMap<>();
				data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
				data.put("notificationId", notificationUUID.toString());
				data.put("transactionReceiveId", transactionReceiveEntity.getId());
				data.put("bankAccount", accountBankEntity.getBankAccount());
				data.put("bankName", bankTypeEntity.getBankName());
				data.put("bankCode", bankTypeEntity.getBankCode());
				data.put("bankId", accountBankEntity.getId());
				data.put("terminalName", "");
				data.put("terminalCode", transactionReceiveEntity.getTerminalCode() != null
						? transactionReceiveEntity.getTerminalCode()
						: "");
				data.put("rawTerminalCode", "");
				data.put("orderId", transactionReceiveEntity.getOrderId() != null
						? transactionReceiveEntity.getOrderId()
						: "");
				data.put("referenceNumber", dto.getReferencenumber() != null
						? dto.getReferencenumber()
						: "");
				data.put("content", dto.getContent());
				data.put("amount", "" + amount);
				data.put("timePaid", "" + time);
				data.put("type", "" + transactionReceiveEntity.getType());
				data.put("time", "" + time);
				data.put("refId", "" + dto.getTransactionid());
				data.put("status", "1");
				data.put("traceId", "");
				data.put("transType", dto.getTransType());
				data.put("urlLink", transactionReceiveEntity.getUrlLink() != null ? transactionReceiveEntity.getUrlLink() : "");
				pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
						message, notiEntity, data, accountBankEntity.getUserId());
				try {
					String refId = TransactionRefIdUtil.encryptTransactionId(transactionReceiveEntity.getId());
					socketHandler.sendMessageToTransactionRefId(refId, data);
					if (!StringUtil.isNullOrEmpty(boxIdRef)) {
						try {
							// send msg to QR Link
							BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
							String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
							data.put("message", String.format(messageForBox, amountForVoice));
							String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
							socketHandler.sendMessageToBoxId(idRefBox, data);
							logger.info("WS: socketHandler.sendMessageToQRBox - "
									+ boxIdRef + " at: " + System.currentTimeMillis());
						} catch (IOException e) {
							logger.error(
									"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
						}
					}
				} catch (IOException e) {
					logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
				}
				/////// DO INSERT TELEGRAM
//				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
//				if (chatIds != null && !chatIds.isEmpty()) {
//					TelegramUtil telegramUtil = new TelegramUtil();
//					String telegramMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String chatId : chatIds) {
//						telegramUtil.sendMsg(chatId, telegramMsg);
//					}
//				}


				/////// DO INSERT TELEGRAM BY QVAN
				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
				if (chatIds != null && !chatIds.isEmpty()) {
					TelegramUtil telegramUtil = new TelegramUtil();
					for (String chatId : chatIds) {
						try {
							TelegramEntity telegramEntity = telegramService.getTelegramByChatId(chatId);
							if (telegramEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(telegramEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(telegramEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String telegramMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									telegramUtil.sendMsg(chatId, telegramMsg);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Telegram notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Telegram notification: " + e.getMessage());
						}
					}
				}


				/////// DO INSERT LARK
//				if (accountBankEntity.getBankAccount().equals("699699699996")) {
//					if (transactionReceiveEntity.getTransType().equals("C")) {
//						List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//						if (webhooks != null && !webhooks.isEmpty()) {
//							LarkUtil larkUtil = new LarkUtil();
//							String larkMsg = prefix + amount + " VND"
//									+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//									+ accountBankEntity.getBankAccount()
//									+ " | " + convertLongToDate(time)
//									+ " | " + dto.getReferencenumber()
//									+ " | ND: " + dto.getContent();
//							for (String webhook : webhooks) {
//								larkUtil.sendMessageToLark(larkMsg, webhook);
//							}
//						}
//					}
//				} else {
//					List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//					if (webhooks != null && !webhooks.isEmpty()) {
//						LarkUtil larkUtil = new LarkUtil();
//						String larkMsg = prefix + amount + " VND"
//								+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//								+ accountBankEntity.getBankAccount()
//								+ " | " + convertLongToDate(time)
//								+ " | " + dto.getReferencenumber()
//								+ " | ND: " + dto.getContent();
//						for (String webhook : webhooks) {
//							larkUtil.sendMessageToLark(larkMsg, webhook);
//						}
//					}
//				}

/////// DO INSERT LARK BY QVAN
				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (webhooks != null && !webhooks.isEmpty()) {
					LarkUtil larkUtil = new LarkUtil();
					for (String webhook : webhooks) {
						try {
							LarkEntity larkEntity = larkService.getLarkByWebhook(webhook);
							if (larkEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String larkMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									larkUtil.sendMessageToLark(larkMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Lark notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Lark notification: " + e.getMessage());
						}
					}
				}




				/////// DO INSERT GOOGLE CHAT
//				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//					String googleChatMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String webhook : ggChatWebhooks) {
//						googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//					}
//				}

				// DO INSERT GOOGLE CHAT BY QVAN
				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
					for (String webhook : ggChatWebhooks) {
						try {
							GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
							if (googleChatEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String googleChatMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Google Chat notification: " + e.getMessage());
						}
					}
				}

				// DO INSERT SLACK BY QVAN
				List<String> slackWebhooks = slackAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (slackWebhooks != null && !slackWebhooks.isEmpty()) {
					SlackUtil slackUtil = new SlackUtil();
					for (String webhook : slackWebhooks) {
						try {
							SlackEntity slackEntity = slackService.getSlackByWebhook(webhook);
							if (slackEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(slackEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(slackEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String slackMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									slackUtil.sendMessageToSlack(slackMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Slack notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Slack notification: " + e.getMessage());
						}
					}
				}

				// DO INSERT DISCORD BY QVAN
				List<String> discordWebhooks = discordAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (discordWebhooks != null && !discordWebhooks.isEmpty()) {
					DiscordUtil discordUtil = new DiscordUtil();
					for (String webhook : discordWebhooks) {
						try {
							DiscordEntity discordEntity = discordService.getDiscordByWebhook(webhook);
							if (discordEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(discordEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(discordEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
								if (sendNotification) {
									String discordMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									discordUtil.sendMessageToDiscord(discordMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Discord notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Discord notification: " + e.getMessage());
						}
					}
				}

			}
		} else {
			logger.info("transaction-sync - no have terminal is empty.");
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
					+ prefix + amount
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
			// notificationService.insertNotification(notiEntity);
			// List<FcmTokenEntity> fcmTokens = new ArrayList<>();
			// fcmTokens =
			// fcmTokenService.getFcmTokensByUserId(accountBankEntity.getUserId());
			Map<String, String> data = new HashMap<>();
			data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
			data.put("notificationId", notificationUUID.toString());
			data.put("transactionReceiveId", transactionReceiveEntity.getId());
			data.put("bankAccount", accountBankEntity.getBankAccount());
			data.put("bankName", bankTypeEntity.getBankName());
			data.put("bankCode", bankTypeEntity.getBankCode());
			data.put("bankId", accountBankEntity.getId());
			data.put("terminalName", "");
			data.put("terminalCode", "");
			data.put("rawTerminalCode", "");
			data.put("orderId", transactionReceiveEntity.getOrderId() != null
					? transactionReceiveEntity.getOrderId()
					: "");
			data.put("referenceNumber", dto.getReferencenumber() != null
					? dto.getReferencenumber()
					: "");
			data.put("content", dto.getContent());
			data.put("amount", "" + amount);
			data.put("timePaid", "" + time);
			data.put("type", "" + transactionReceiveEntity.getType());
			data.put("time", "" + time);
			data.put("refId", "" + dto.getTransactionid());
			data.put("status", "1");
			data.put("traceId", "");
			data.put("transType", dto.getTransType());
			data.put("urlLink", transactionReceiveEntity.getUrlLink() != null ? transactionReceiveEntity.getUrlLink() : "");
			// firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
			// NotificationUtil
			// .getNotiTitleUpdateTransaction(),
			// message);
			pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
					message, notiEntity, data, accountBankEntity.getUserId());
			try {
				// send msg to user
				// socketHandler.sendMessageToUser(accountBankEntity.getUserId(), data);
				// send msg to QR Link
				String refId = TransactionRefIdUtil.encryptTransactionId(transactionReceiveEntity.getId());
				socketHandler.sendMessageToTransactionRefId(refId, data);
				if (!StringUtil.isNullOrEmpty(boxIdRef)) {
					try {
						BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
						String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
						data.put("message", String.format(messageForBox, amountForVoice));
						String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
						socketHandler.sendMessageToBoxId(idRefBox, data);
						logger.info("WS: socketHandler.sendMessageToQRBox - "
								+ boxIdRef + " at: " + System.currentTimeMillis());
					} catch (IOException e) {
						logger.error(
								"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
					}
				}
			} catch (IOException e) {
				logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
			}
			/////// DO INSERT TELEGRAM
//			List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
//			if (chatIds != null && !chatIds.isEmpty()) {
//				TelegramUtil telegramUtil = new TelegramUtil();
//				// String telegramMsg2 = "Thanh toán thành công 🎉."
//				// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
//				// + accountBankEntity.getBankAccount()
//				// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
//				// + "\nMã giao dịch: " + dto.getReferencenumber()
//				// + "\nThời gian: " + convertLongToDate(time)
//				// + "\nNội dung: " + dto.getContent();
//				// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
//				// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
//				// + accountBankEntity.getBankAccount()
//				// + "| Ma GD: " + dto.getReferencenumber()
//				// + "| ND: " + dto.getContent()
//				// + "| " + convertLongToDate(time);
//
//				String telegramMsg = prefix + amount + " VND"
//						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//						+ accountBankEntity.getBankAccount()
//						+ " | " + convertLongToDate(time)
//						+ " | " + dto.getReferencenumber()
//						+ " | ND: " + dto.getContent();
//				for (String chatId : chatIds) {
//					telegramUtil.sendMsg(chatId, telegramMsg);
//				}
//			}


/////// DO INSERT TELEGRAM BY QVAN
			List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
			if (chatIds != null && !chatIds.isEmpty()) {
				TelegramUtil telegramUtil = new TelegramUtil();
				for (String chatId : chatIds) {
					try {
						TelegramEntity telegramEntity = telegramService.getTelegramByChatId(chatId);
						if (telegramEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(telegramEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(telegramEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
							if (sendNotification) {
								String telegramMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								telegramUtil.sendMsg(chatId, telegramMsg);
							}
						}
					} catch (JsonProcessingException e) {
						logger.error("Error processing JSON for Telegram notification: " + e.getMessage());
					} catch (Exception e) {
						logger.error("Error sending Telegram notification: " + e.getMessage());
					}
				}
			}


			/////// DO INSERT LARK
//			if (accountBankEntity.getBankAccount().equals("699699699996")) {
//				if (transactionReceiveEntity.getTransType().equals("C")) {
//					List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//					if (webhooks != null && !webhooks.isEmpty()) {
//						LarkUtil larkUtil = new LarkUtil();
//						String larkMsg = prefix + amount + " VND"
//								+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//								+ accountBankEntity.getBankAccount()
//								+ " | " + convertLongToDate(time)
//								+ " | " + dto.getReferencenumber()
//								+ " | ND: " + dto.getContent();
//						for (String webhook : webhooks) {
//							larkUtil.sendMessageToLark(larkMsg, webhook);
//						}
//					}
//				}
//			} else {
//				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (webhooks != null && !webhooks.isEmpty()) {
//					LarkUtil larkUtil = new LarkUtil();
//					String larkMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String webhook : webhooks) {
//						larkUtil.sendMessageToLark(larkMsg, webhook);
//					}
//				}
//			}


			/////// DO INSERT LARK BY QVAN
			List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (webhooks != null && !webhooks.isEmpty()) {
				LarkUtil larkUtil = new LarkUtil();
				for (String webhook : webhooks) {
					try {
						LarkEntity larkEntity = larkService.getLarkByWebhook(webhook);
						if (larkEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
							if (sendNotification) {
								String larkMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								larkUtil.sendMessageToLark(larkMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						logger.error("Error processing JSON for Lark notification: " + e.getMessage());
					} catch (Exception e) {
						logger.error("Error sending Lark notification: " + e.getMessage());
					}
				}
			}



			/////// DO INSERT GOOGLE CHAT
//			List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//			if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//				GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//				String googleChatMsg = prefix + amount + " VND"
//						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//						+ accountBankEntity.getBankAccount()
//						+ " | " + convertLongToDate(time)
//						+ " | " + dto.getReferencenumber()
//						+ " | ND: " + dto.getContent();
//				for (String webhook : ggChatWebhooks) {
//					googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//				}
//			}

			// DO INSERT GOOGLE CHAT BY QVAN
			List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
				GoogleChatUtil googleChatUtil = new GoogleChatUtil();
				for (String webhook : ggChatWebhooks) {
					try {
						GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
						if (googleChatEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
							if (sendNotification) {
								String googleChatMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
					} catch (Exception e) {
						logger.error("Error sending Google Chat notification: " + e.getMessage());
					}
				}
			}

			// DO INSERT SLACK BY QVAN
			List<String> slackWebhooks = slackAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (slackWebhooks != null && !slackWebhooks.isEmpty()) {
				SlackUtil slackUtil = new SlackUtil();
				for (String webhook : slackWebhooks) {
					try {
						SlackEntity slackEntity = slackService.getSlackByWebhook(webhook);
						if (slackEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(slackEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(slackEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
							if (sendNotification) {
								String slackMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								slackUtil.sendMessageToSlack(slackMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						// Log error but do not interrupt the transaction process
						logger.error("Error processing JSON for Slack notification: " + e.getMessage());
					} catch (Exception e) {
						// Log any other errors
						logger.error("Error sending Slack notification: " + e.getMessage());
					}
				}
			}

			// DO INSERT DISCORD BY QVAN
			List<String> discordWebhooks = discordAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (discordWebhooks != null && !discordWebhooks.isEmpty()) {
				DiscordUtil discordUtil = new DiscordUtil();
				for (String webhook : discordWebhooks) {
					try {
						DiscordEntity discordEntity = discordService.getDiscordByWebhook(webhook);
						if (discordEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(discordEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(discordEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionReceiveEntity);
							if (sendNotification) {
								String discordMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								discordUtil.sendMessageToDiscord(discordMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						// Log error but do not interrupt the transaction process
						logger.error("Error processing JSON for Discord notification: " + e.getMessage());
					} catch (Exception e) {
						// Log any other errors
						logger.error("Error sending Discord notification: " + e.getMessage());
					}
				}
			}


			// String requestId = "";
			// requestId = textToSpeechService.requestTTS(accountBankEntity.getUserId(),
			// data, dto.getAmount() + "");
			// textToSpeechService.delete(requestId);
		}
	}
	private boolean shouldSendNotification(List<String> notificationTypes, TransactionBankDTO dto, TransactionReceiveEntity transactionReceiveEntity) {
		if (notificationTypes.containsAll(Arrays.asList("CREDIT", "DEBIT", "RECON"))) {
			return true;
		}
		if (notificationTypes.contains("CREDIT") && dto.getTransType().equals("C")) {
			return true;
		}
		if (notificationTypes.contains("DEBIT") && dto.getTransType().equals("D")) {
			return true;
		}
		return notificationTypes.contains("RECON") && dto.getTransType().equals("C") && (transactionReceiveEntity.getType() == 0 || transactionReceiveEntity.getType() == 1);
	}


	private String createMessage(List<String> notificationContents, String transType, String amount, BankTypeEntity bankTypeEntity, String bankAccount, long time, String referenceNumber, String content) {
		StringBuilder msgBuilder = new StringBuilder();

		if (notificationContents.contains("AMOUNT")) {
			String prefix = transType.toUpperCase().equals("D") ? "-" : "+";
			msgBuilder.append(prefix).append(amount).append(" VND ");
		}

		msgBuilder.append("| TK: ").append(bankTypeEntity.getBankShortName()).append(" - ").append(bankAccount)
				.append(" | ").append(convertLongToDate(time));

		if (notificationContents.contains("REFERENCE_NUMBER")) {
			msgBuilder.append(" | ").append(referenceNumber);
		}
		if (notificationContents.contains("CONTENT")) {
			msgBuilder.append(" | ND: ").append(content);
		}

		// Loại bỏ ký tự "|" thừa ở đầu nếu không có "AMOUNT"
		String message = msgBuilder.toString();
		if (!notificationContents.contains("AMOUNT")) {
			message = message.replaceFirst("^\\| ", "");
		}

		return message;
	}

	// Phương thức chuyển đổi thời gian Unix Epoch Seconds sang LocalDateTime và định dạng thành chuỗi thời gian theo định dạng "dd/MM/yyyy HH:mm:ss"
	private String convertLongToFormattedDateTime(long time) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of("GMT"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
		return dateTime.format(formatter);
	}

	// Phương thức chuyển đổi thời gian Unix Epoch Seconds sang LocalDateTime với múi giờ GMT
	private LocalDateTime convertLongToLocalDateTime(long time) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of("GMT"));
	}

	// Định dạng LocalDateTime thành chuỗi thời gian theo định dạng "dd/MM/yyyy HH:mm:ss"
	private String formatLocalDateTime(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
		return dateTime.format(formatter);
	}
	// insert new transaction mean it's not created from business. So DO NOT need to
	// push to users
	public void insertNewTransaction(String transcationUUID, TransactionBankDTO dto,
			AccountBankReceiveEntity accountBankEntity, long time,
			String traceId, UUID uuid, NumberFormat nf, String orderId, String sign, String boxIdRef)  {
		SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
		String amount = "";
		if (dto.getAmount() != 0) {
			amount = dto.getAmount() + "";
		} else {
			amount = "0";
		}
		if (accountBankEntity.isValidService() == true) {
			if (dto.getAmount() != 0) {
				amount = dto.getAmount() + "";
			} else {
				amount = "0";
			}
			if (systemSetting.getServiceActive() <= time) {
				TransReceiveTempEntity entity = transReceiveTempService
						.getLastTimeByBankId(accountBankEntity.getId());
				if (entity == null) {
					entity = new TransReceiveTempEntity();
					List<String> transIds = new ArrayList<>();
					transIds.add(transcationUUID);
					entity.setId(UUID.randomUUID().toString());
					entity.setBankId(accountBankEntity.getId());
					entity.setLastTimes(time);
					entity.setTransIds(String.join(",", transIds));
					transReceiveTempService.insert(entity);
				} else {
					List<String> transIds = new ArrayList<>();
					if (entity.getTransIds() != null && !entity.getTransIds().isEmpty()) {
						transIds = new ArrayList<>(Arrays.asList(entity.getTransIds().split(",")));
					}
					if (transIds.size() < 5) {
						transIds.add(transcationUUID);
						entity.setLastTimes(time);
						entity.setTransIds(String.join(",", transIds));
						transReceiveTempService.insert(entity);
					}
				}
			}
		} else {
			if (systemSetting.getServiceActive() <= time) {
				TransReceiveTempEntity entity = transReceiveTempService
						.getLastTimeByBankId(accountBankEntity.getId());
				if (entity == null) {
					entity = new TransReceiveTempEntity();
					List<String> transIds = new ArrayList<>();
					transIds.add(transcationUUID);
					entity.setId(UUID.randomUUID().toString());
					entity.setBankId(accountBankEntity.getId());
					entity.setLastTimes(time);
					entity.setTransIds(String.join(",", transIds));
					transReceiveTempService.insert(entity);
				} else {
					List<String> transIds = new ArrayList<>();

					if (entity.getTransIds() != null && !entity.getTransIds().isEmpty()) {
						transIds = new ArrayList<>(Arrays.asList(entity.getTransIds().split(",")));
					}

					if (transIds.size() < 5) {
						transIds.add(transcationUUID);
						entity.setLastTimes(time);
						entity.setTransIds(String.join(",", transIds));

						transReceiveTempService.insert(entity);
					} else {
						if (accountBankEntity.isValidService() == false) {
							amount = "*****";
						}
					}

				}
			}
		}
		String amountForVoice = amount;
		amount = formatAmountNumber(amount);

		BankTypeEntity bankTypeEntity = bankTypeService
				.getBankTypeById(accountBankEntity.getBankTypeId());
		// get terminalCode
		int length = accountBankEntity.getTerminalLength();
		String terminalCodeContent = "";
		if (length > 0) {
			terminalCodeContent = getTraceId(dto.getContent(), "SQR", length);
		}
		String terminalCode = "";
		if (terminalCodeContent.length() > 3) {
			// find terminal by terminalCode
			terminalCode = terminalCodeContent.substring(3);
		}
		if (StringUtil.isNullOrEmpty(terminalCode) == false) {
			logger.info("transaction-sync - insertNewTransaction - terminalCode: " + terminalCode);
			// find all userIds belong to terminal
			TerminalEntity terminalEntity = terminalService
					.getTerminalByTerminalCode(terminalCode,
							accountBankEntity.getBankAccount());
			if (terminalEntity == null) {
				logger.info(
						"transaction-sync - insertNewTransaction - terminalEntity is null: find terminalBankReceive");
				terminalEntity = terminalService
						.getTerminalByTerminalBankReceiveCode(terminalCode);
				if (terminalEntity == null) {
					terminalEntity = terminalService
							.getTerminalByTerminalCode(terminalCode);
				}
			}
			if (terminalEntity != null) {
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
				transactionEntity.setType(1);
				transactionEntity.setStatus(1);
				transactionEntity.setTraceId("");
				transactionEntity.setTransType(dto.getTransType());
				transactionEntity.setReferenceNumber(dto.getReferencenumber());
				transactionEntity.setOrderId(orderId);
				transactionEntity.setSign(sign);
				transactionEntity.setTimePaid(time);
				transactionEntity.setTerminalCode(terminalCode != null ? terminalCode : "");
				transactionEntity.setQrCode("");
				transactionEntity.setUserId(accountBankEntity.getUserId());
				transactionEntity.setNote("");
				transactionEntity.setTransStatus(0);
				transactionEntity.setUrlLink("");
				// int check =
				// transactionReceiveService.insertTransactionReceiveWithCheckDuplicated(transactionEntity);
				int check = transactionReceiveService.insertTransactionReceive(transactionEntity);
				// if (check == 0) {
				// logger.info("transaction-sync - insertNewTransaction -
				// insertTransactionReceive failed: Duplicated when insert");
				// } else {
				final String tempTerminalCode = terminalCode;
				Thread thread = new Thread(() -> {
					TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
					transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
					transactionTerminalTempEntity.setTransactionId(transcationUUID);
					transactionTerminalTempEntity.setTerminalCode(tempTerminalCode);
					transactionTerminalTempEntity.setTime(time);
					transactionTerminalTempEntity.setAmount(Long.parseLong(dto.getAmount() + ""));
					transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);
				});
				thread.start();
				List<String> userIds = terminalService
						.getUserIdsByTerminalCode(terminalEntity.getCode());
				String prefix = "";
				if (dto.getTransType().toUpperCase().equals("D")) {
					prefix = "-";
				} else {
					prefix = "+";
				}
				// push notification to member of terminal
				if (userIds != null && !userIds.isEmpty()) {
					int numThread = userIds.size();
					ExecutorService executorService = Executors.newFixedThreadPool(numThread);
					Map<String, String> data1 = new HashMap<>();
					for (String userId : userIds) {
						Map<String, String> data = new HashMap<>();
						// insert notification
						UUID notificationUUID = UUID.randomUUID();
						NotificationEntity notiEntity = new NotificationEntity();
						String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
								+ accountBankEntity.getBankAccount()
								+ NotificationUtil.getNotiDescUpdateTransSuffix2()
								+ prefix + amount
								+ NotificationUtil.getNotiDescUpdateTransSuffix3()
								+ terminalEntity.getName()
								+ NotificationUtil.getNotiDescUpdateTransSuffix4()
								+ dto.getContent();
						notiEntity.setId(notificationUUID.toString());
						notiEntity.setRead(false);
						notiEntity.setMessage(message);
						notiEntity.setTime(time);
						notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
						notiEntity.setUserId(userId);
						notiEntity.setData(transactionEntity.getId());
						data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
						data.put("notificationId", notificationUUID.toString());
						data.put("transactionReceiveId", transactionEntity.getId());
						data.put("bankAccount", transactionEntity.getBankAccount());
						data.put("bankName", bankTypeEntity.getBankName());
						data.put("bankCode", bankTypeEntity.getBankCode());
						data.put("bankId", transactionEntity.getBankId());
						data.put("terminalName", terminalEntity.getName() != null
								? terminalEntity.getName()
								: "");
						data.put("terminalCode", terminalEntity.getCode() != null
								? terminalEntity.getCode()
								: "");
						data.put("rawTerminalCode", terminalEntity.getRawTerminalCode() != null
								? terminalEntity.getRawTerminalCode()
								: "");
						data.put("content", transactionEntity.getContent());
						data.put("orderId", transactionEntity.getOrderId() != null
								? transactionEntity.getOrderId()
								: "");
						data.put("referenceNumber", transactionEntity.getReferenceNumber() != null
								? transactionEntity.getReferenceNumber()
								: "");
						data.put("amount", "" + amount);
						data.put("timePaid", "" + transactionEntity.getTimePaid());
						data.put("type", "" + transactionEntity.getType());
						data.put("time", "" + transactionEntity.getTime());
						data.put("refId", "" + dto.getTransactionid());
						data.put("status", "1");
						data.put("traceId", "" + transactionEntity.getTraceId());
						data.put("transType", dto.getTransType());
						data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
						data1 = data;
						String roleFCM = merchantMemberRoleService.checkMemberHaveRole(userId,
								EnvironmentUtil.getFcmNotificationRoleId());
						if (roleFCM != null && !roleFCM.isEmpty()) {
							executorService.submit(() -> pushNotification(NotificationUtil
									.getNotiTitleUpdateTransaction(), message, notiEntity, data, userId));
						} else {
							executorService.submit(() -> pushNotificationWithSocket(NotificationUtil
									.getNotiTitleUpdateTransaction(), message, notiEntity, data, userId));
						}
					}
					executorService.shutdown();
					try {
						// send msg to QR Link
						String refId = TransactionRefIdUtil.encryptTransactionId(transactionEntity.getId());
						socketHandler.sendMessageToTransactionRefId(refId, data1);
						if (!StringUtil.isNullOrEmpty(boxIdRef)) {
							try {
								BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
								String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
								data1.put("message", String.format(messageForBox, amountForVoice));
								String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
								socketHandler.sendMessageToBoxId(idRefBox, data1);
								logger.info("WS: socketHandler.sendMessageToQRBox - "
										+ boxIdRef + " at: " + System.currentTimeMillis());
							} catch (IOException e) {
								logger.error(
										"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
							}
						}
					} catch (IOException e) {
						logger.error(
								"WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
					}
				} else {
					Map<String, String> data = new HashMap<>();
					UUID notificationUUID = UUID.randomUUID();
					NotificationEntity notiEntity = new NotificationEntity();
					String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
							+ accountBankEntity.getBankAccount()
							+ NotificationUtil.getNotiDescUpdateTransSuffix2()
							+ prefix + amount
							+ NotificationUtil.getNotiDescUpdateTransSuffix3()
							+ terminalEntity.getName()
							+ NotificationUtil.getNotiDescUpdateTransSuffix4()
							+ dto.getContent();
					notiEntity.setId(notificationUUID.toString());
					notiEntity.setRead(false);
					notiEntity.setMessage(message);
					notiEntity.setTime(time);
					notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
					notiEntity.setUserId(accountBankEntity.getUserId());
					notiEntity.setData(transcationUUID.toString());
					data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
					data.put("notificationId", notificationUUID.toString());
					data.put("transactionReceiveId", transcationUUID.toString());
					data.put("bankAccount", accountBankEntity.getBankAccount());
					data.put("bankName", bankTypeEntity.getBankName());
					data.put("bankCode", bankTypeEntity.getBankCode());
					data.put("bankId", accountBankEntity.getId());
					data.put("content", transactionEntity.getContent());
					data.put("amount", "" + amount);
					data.put("terminalName", "");
					data.put("terminalCode", "");
					data.put("rawTerminalCode", "");
					data.put("orderId", transactionEntity.getOrderId() != null
							? transactionEntity.getOrderId()
							: "");
					data.put("referenceNumber", transactionEntity.getReferenceNumber() != null
							? transactionEntity.getReferenceNumber()
							: "");
					data.put("timePaid", "" + transactionEntity.getTimePaid());
					data.put("type", "" + transactionEntity.getType());
					data.put("time", "" + time);
					data.put("refId", "" + uuid.toString());
					data.put("status", "1");
					data.put("traceId", "");
					data.put("transType", "C");
					pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
							message, notiEntity, data, accountBankEntity.getUserId());
					try {
						// send msg to QR Link
						String refId = TransactionRefIdUtil.encryptTransactionId(transactionEntity.getId());
						socketHandler.sendMessageToTransactionRefId(refId, data);
						if (!StringUtil.isNullOrEmpty(boxIdRef)) {
							try {
								BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
								String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
								data.put("message", String.format(messageForBox, amountForVoice));
								String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
								socketHandler.sendMessageToBoxId(idRefBox, data);
								logger.info("WS: socketHandler.sendMessageToQRBox - "
										+ boxIdRef + " at: " + System.currentTimeMillis());
							} catch (IOException e) {
								logger.error(
										"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
							}
						}
					} catch (IOException e) {
						logger.error(
								"WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
					}
				}
				/////// DO INSERT TELEGRAM
//				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
//				if (chatIds != null && !chatIds.isEmpty()) {
//					TelegramUtil telegramUtil = new TelegramUtil();
//					// String telegramMsg2 = "Thanh toán thành công 🎉."
//					// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - "
//					// + accountBankEntity.getBankAccount()
//					// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
//					// + "\nMã giao dịch: " + dto.getReferencenumber()
//					// + "\nThời gian: " + convertLongToDate(time)
//					// + "\nNội dung: " + dto.getContent();
//					// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
//					// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
//					// + accountBankEntity.getBankAccount()
//					// + "| Ma GD: " + dto.getReferencenumber()
//					// + "| ND: " + dto.getContent()
//					// + "| " + convertLongToDate(time);
//					String telegramMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String chatId : chatIds) {
//						telegramUtil.sendMsg(chatId, telegramMsg);
//					}
//				}

				/////// DO INSERT TELEGRAM BY QVAN
				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
				if (chatIds != null && !chatIds.isEmpty()) {
					TelegramUtil telegramUtil = new TelegramUtil();
					for (String chatId : chatIds) {
						try {
							TelegramEntity telegramEntity = telegramService.getTelegramByChatId(chatId);
							if (telegramEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(telegramEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(telegramEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String telegramMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									telegramUtil.sendMsg(chatId, telegramMsg);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Telegram notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Telegram notification: " + e.getMessage());
						}
					}
				}


				/////// DO INSERT LARK
//				if (accountBankEntity.getBankAccount().equals("699699699996")) {
//					if (transactionEntity.getTransType().equals("C")) {
//						List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//						if (webhooks != null && !webhooks.isEmpty()) {
//							LarkUtil larkUtil = new LarkUtil();
//							String larkMsg = prefix + amount + " VND"
//									+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//									+ accountBankEntity.getBankAccount()
//									+ " | " + convertLongToDate(time)
//									+ " | " + dto.getReferencenumber()
//									+ " | ND: " + dto.getContent();
//							for (String webhook : webhooks) {
//								larkUtil.sendMessageToLark(larkMsg, webhook);
//							}
//						}
//					}
//				} else {
//					List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//					if (webhooks != null && !webhooks.isEmpty()) {
//						LarkUtil larkUtil = new LarkUtil();
//						String larkMsg = prefix + amount + " VND"
//								+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//								+ accountBankEntity.getBankAccount()
//								+ " | " + convertLongToDate(time)
//								+ " | " + dto.getReferencenumber()
//								+ " | ND: " + dto.getContent();
//						for (String webhook : webhooks) {
//							larkUtil.sendMessageToLark(larkMsg, webhook);
//						}
//					}
//				}
				/////// DO INSERT LARK BY QVAN
				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (webhooks != null && !webhooks.isEmpty()) {
					LarkUtil larkUtil = new LarkUtil();
					for (String webhook : webhooks) {
						try {
							LarkEntity larkEntity = larkService.getLarkByWebhook(webhook);
							if (larkEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String larkMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									larkUtil.sendMessageToLark(larkMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Lark notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Lark notification: " + e.getMessage());
						}
					}
				}


				/////// DO INSERT GOOGLE CHAT
//				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//					String googleChatMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String webhook : ggChatWebhooks) {
//						googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//					}
//				}

				// DO INSERT GOOGLE CHAT BY QVAN
//				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//					for (String webhook : ggChatWebhooks) {
//						try {
//							GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
//							if (googleChatEntity != null) {
//								List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
//								List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
//								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
//								if (sendNotification) {
//									String googleChatMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
//									googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//								}
//							}
//						} catch (JsonProcessingException e) {
//							logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
//						} catch (Exception e) {
//							logger.error("Error sending Google Chat notification: " + e.getMessage());
//						}
//					}
//				}

				// DO INSERT GOOGLE CHAT BY QVAN
				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
					for (String webhook : ggChatWebhooks) {
						try {
							GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
							if (googleChatEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									// Chuyển đổi thời gian sử dụng múi giờ GMT và định dạng giống Google Sheets
									LocalDateTime transactionTime = convertLongToLocalDateTime(dto.getTransactiontime());
									String formattedTime = formatLocalDateTime(transactionTime);
									long transactionTimeAsLong = dto.getTransactiontime(); // giữ nguyên giá trị long để truyền vào createMessage
									String googleChatMsg = createMessage(notificationContents, dto.getTransType(), String.valueOf(dto.getAmount()), bankTypeEntity, accountBankEntity.getBankAccount(), transactionTimeAsLong, dto.getReferencenumber(), dto.getContent());
									googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Google Chat notification: " + e.getMessage());
						}
					}
				}

				// DO INSERT SLACK BY QVAN
				List<String> slackWebhooks = slackAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (slackWebhooks != null && !slackWebhooks.isEmpty()) {
					SlackUtil slackUtil = new SlackUtil();
					for (String webhook : slackWebhooks) {
						try {
							SlackEntity slackEntity = slackService.getSlackByWebhook(webhook);
							if (slackEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(slackEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(slackEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String slackMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									slackUtil.sendMessageToSlack(slackMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Slack notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Slack notification: " + e.getMessage());
						}
					}
				}

				// DO INSERT DISCORD BY QVAN
				List<String> discordWebhooks = discordAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (discordWebhooks != null && !discordWebhooks.isEmpty()) {
					DiscordUtil discordUtil = new DiscordUtil();
					for (String webhook : discordWebhooks) {
						try {
							DiscordEntity discordEntity = discordService.getDiscordByWebhook(webhook);
							if (discordEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(discordEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(discordEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String discordMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									discordUtil.sendMessageToDiscord(discordMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Discord notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Discord notification: " + e.getMessage());
						}
					}
				}


				// DO INSERT GOOGLE SHEET BY QVAN
//				List<String> ggSheetWebhooks = googleSheetAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (ggSheetWebhooks != null && !ggSheetWebhooks.isEmpty()) {
//					GoogleSheetUtil googleSheetUtil = GoogleSheetUtil.getInstance();
//					for (String webhook : ggSheetWebhooks) {
//						try {
//							GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetByWebhook(webhook);
//							if (googleSheetEntity != null) {
//								List<String> notificationTypes = new ObjectMapper().readValue(googleSheetEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
//								List<String> notificationContents = new ObjectMapper().readValue(googleSheetEntity.getNotificationContents(), new TypeReference<List<String>>() {});
//								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
//								if (sendNotification) {
//
//									// Chèn tiêu đề chỉ một lần
//									if (!googleSheetUtil.headerInserted) {
//										googleSheetUtil.insertHeader(webhook);
//									}
//
//									String amountString = String.valueOf(dto.getAmount());
//									String message = createMessage(notificationContents, dto.getTransType(), amountString, bankTypeEntity, accountBankEntity.getBankAccount(), dto.getTransactiontime(), dto.getReferencenumber(), dto.getContent());
//									googleSheetUtil.insertTransactionToGoogleSheet(message, webhook);
//								}
//							}
//						} catch (Exception e) {
//							logger.error("Error sending Google Sheets notification: " + e.getMessage());
//						}
//					}
//				}

				// DO INSERT GOOGLE SHEET BY QVAN
				List<String> ggSheetWebhooks = googleSheetAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (ggSheetWebhooks != null && !ggSheetWebhooks.isEmpty()) {
					GoogleSheetUtil googleSheetUtil = GoogleSheetUtil.getInstance();
					for (String webhook : ggSheetWebhooks) {
						try {
							GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetByWebhook(webhook);
							if (googleSheetEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(googleSheetEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(googleSheetEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									if (!googleSheetUtil.headerInserted) {
										googleSheetUtil.insertHeader(webhook);
									}

									Map<String, String> data = new HashMap<>();
									data.put("notificationId", UUID.randomUUID().toString());
									data.put("transactionReceiveId", transactionEntity.getId());
									data.put("bankAccount", accountBankEntity.getBankAccount());
									data.put("bankName", bankTypeEntity.getBankName());
									data.put("bankCode", bankTypeEntity.getBankCode());
									data.put("bankId", accountBankEntity.getId());
									data.put("content", dto.getContent());
									data.put("amount", String.valueOf(dto.getAmount()));
									data.put("timePaid", String.valueOf(transactionEntity.getTimePaid()));
									data.put("time", String.valueOf(time));
									data.put("type", String.valueOf(transactionEntity.getType()));
									data.put("refId", dto.getTransactionid());
									data.put("terminalName", "");
									data.put("terminalCode", "");
									data.put("rawTerminalCode", "");
									data.put("orderId", transactionEntity.getOrderId() != null ? transactionEntity.getOrderId() : "");
									data.put("referenceNumber", transactionEntity.getReferenceNumber() != null ? transactionEntity.getReferenceNumber() : "");
									data.put("status", "1");
									data.put("traceId", "");
									data.put("transType", dto.getTransType());
									data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
									//data.put("note", dto.getNote() != null ? dto.getNote() : "");

									googleSheetUtil.insertTransactionToGoogleSheet(data, notificationContents, webhook);
								}
							}
						} catch (Exception e) {
							logger.error("Error sending Google Sheets notification: " + e.getMessage());
						}
					}
				}

				// }

				// textToSpeechService.delete(requestId);
			} else {
				logger.info("transaction-sync - cannot find terminal but already detech code.");

				//
				//

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
				transactionEntity.setTerminalCode(terminalCode != null ? terminalCode : "");
				transactionEntity.setQrCode("");
				transactionEntity.setUserId(accountBankEntity.getUserId());
				transactionEntity.setNote("");
				transactionEntity.setTransStatus(0);
				transactionEntity.setUrlLink("");
				// int check =
				// transactionReceiveService.insertTransactionReceiveWithCheckDuplicated(transactionEntity);
				int check = transactionReceiveService.insertTransactionReceive(transactionEntity);
				// if (check == 0) {
				// logger.info("transaction-sync - insertNewTransaction -
				// insertTransactionReceive failed: Duplicated when insert");
				// } else {
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
						+ prefix + amount
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
				// String userIds =
				// merchantMemberRoleService.getListUserIdByMerchantId(accountBankEntity.get);
				// 1. get all user id belong to merchant
				List<String> userIds = new ArrayList<>();
				List<String> list = new ArrayList<>();
				list.add(EnvironmentUtil.getRequestReceiveTerminalRoleId());
				list.add(EnvironmentUtil.getRequestReceiveMerchantRoleId());
				String roles = String.join("|", list);
				userIds = merchantMemberRoleService.getListUserIdRoles(accountBankEntity.getId(), roles);
				// pussh notification to all member that have role
				if (userIds != null && !userIds.isEmpty()) {
					int numThread = userIds.size();
					ExecutorService executorService = Executors.newFixedThreadPool(numThread);
					Map<String, String> data1 = new HashMap<>();
					for (String userId : userIds) {
						Map<String, String> data = new HashMap<>();
						// insert notification
						UUID notiUUID = UUID.randomUUID();
						NotificationEntity notificationEntity = new NotificationEntity();
						notificationEntity.setId(notiUUID.toString());
						notificationEntity.setRead(false);
						notificationEntity.setMessage(message);
						notificationEntity.setTime(time);
						notificationEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
						notificationEntity.setUserId(userId);
						notificationEntity.setData(transactionEntity.getId());
						data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
						data.put("notificationId", notificationUUID.toString());
						data.put("transactionReceiveId", transactionEntity.getId());
						data.put("bankAccount", transactionEntity.getBankAccount());
						data.put("bankName", bankTypeEntity.getBankName());
						data.put("bankCode", bankTypeEntity.getBankCode());
						data.put("bankId", transactionEntity.getBankId());
						data.put("terminalName", "");
						data.put("terminalCode", "");
						data.put("orderId", transactionEntity.getOrderId() != null
								? transactionEntity.getOrderId()
								: "");
						data.put("referenceNumber", transactionEntity.getReferenceNumber() != null
								? transactionEntity.getReferenceNumber()
								: "");
						data.put("rawTerminalCode", "");
						data.put("content", transactionEntity.getContent());
						data.put("amount", "" + amount);
						data.put("timePaid", "" + transactionEntity.getTimePaid());
						data.put("type", "" + transactionEntity.getType());
						data.put("time", "" + transactionEntity.getTime());
						data.put("refId", "" + dto.getTransactionid());
						data.put("status", "1");
						data.put("traceId", "" + transactionEntity.getTraceId());
						data.put("transType", dto.getTransType());
						data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
						data1 = data;
						String roleFCM = merchantMemberRoleService.checkMemberHaveRole(userId,
								EnvironmentUtil.getFcmNotificationRoleId());
						if (roleFCM != null && !roleFCM.isEmpty()) {
							executorService.submit(() -> pushNotification(NotificationUtil
									.getNotiTitleUpdateTransaction(), message, notificationEntity, data, userId));
						} else {
							executorService.submit(() -> pushNotificationWithSocket(NotificationUtil
									.getNotiTitleUpdateTransaction(), message, notificationEntity, data, userId));
						}
					}
					try {
						// send msg to QR Link
						String refId = TransactionRefIdUtil.encryptTransactionId(transactionEntity.getId());
						socketHandler.sendMessageToTransactionRefId(refId, data1);
					} catch (IOException e) {
						logger.error(
								"WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
					}
				}
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
				data.put("content", dto.getContent());
				data.put("terminalName", "");
				data.put("terminalCode", "");
				data.put("rawTerminalCode", "");
				data.put("orderId", transactionEntity.getOrderId() != null
						? transactionEntity.getOrderId()
						: "");
				data.put("referenceNumber", transactionEntity.getReferenceNumber() != null
						? transactionEntity.getReferenceNumber()
						: "");
				data.put("amount", "" + amount);
				data.put("timePaid", "" + transactionEntity.getTimePaid());
				data.put("type", "" + transactionEntity.getType());
				data.put("time", "" + time);
				data.put("refId", "" + dto.getTransactionid());
				data.put("status", "1");
				data.put("traceId", "");
				data.put("transType", dto.getTransType());
				data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
				firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
						NotificationUtil
								.getNotiTitleUpdateTransaction(),
						message);
				try {
					// send msg to user
					socketHandler.sendMessageToUser(accountBankEntity.getUserId(), data);
					// send msg to QR Link
					String refId = TransactionRefIdUtil.encryptTransactionId(transcationUUID.toString());
					socketHandler.sendMessageToTransactionRefId(refId, data);
					if (!StringUtil.isNullOrEmpty(boxIdRef)) {
						try {
							BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
							String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv()).trim();
							data.put("message", String.format(messageForBox, amountForVoice));
							String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
							socketHandler.sendMessageToBoxId(idRefBox, data);
							logger.info("WS: socketHandler.sendMessageToQRBox - "
									+ boxIdRef + " at: " + System.currentTimeMillis());
						} catch (IOException e) {
							logger.error(
									"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
						}
					}
				} catch (IOException e) {
					logger.error("WS: socketHandler.sendMessageToUser - insertNewTransaction ERROR: " + e.toString());
				}
//				/////// DO INSERT TELEGRAM
//				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
//				if (chatIds != null && !chatIds.isEmpty()) {
//					TelegramUtil telegramUtil = new TelegramUtil();
//					// String telegramMsg2 = "Thanh toán thành công 🎉."
//					// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - " +
//					// accountBankEntity.getBankAccount()
//					// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
//					// + "\nMã giao dịch: " + dto.getReferencenumber()
//					// + "\nThời gian: " + convertLongToDate(time)
//					// + "\nNội dung: " + dto.getContent();
//					// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
//					// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
//					// + accountBankEntity.getBankAccount()
//					// + "| Ma GD: " + dto.getReferencenumber()
//					// + "| ND: " + dto.getContent()
//					// + "| " + convertLongToDate(time);
//					String telegramMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String chatId : chatIds) {
//						telegramUtil.sendMsg(chatId, telegramMsg);
//					}
//				}

				/////// DO INSERT TELEGRAM BY QVAN
				List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
				if (chatIds != null && !chatIds.isEmpty()) {
					TelegramUtil telegramUtil = new TelegramUtil();
					for (String chatId : chatIds) {
						try {
							TelegramEntity telegramEntity = telegramService.getTelegramByChatId(chatId);
							if (telegramEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(telegramEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(telegramEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String telegramMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									telegramUtil.sendMsg(chatId, telegramMsg);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Telegram notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Telegram notification: " + e.getMessage());
						}
					}
				}


//				/////// DO INSERT LARK
//				if (accountBankEntity.getBankAccount().equals("699699699996")) {
//					if (transactionEntity.getTransType().equals("C")) {
//						List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//						if (webhooks != null && !webhooks.isEmpty()) {
//							LarkUtil larkUtil = new LarkUtil();
//							String larkMsg = prefix + amount + " VND"
//									+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//									+ accountBankEntity.getBankAccount()
//									+ " | " + convertLongToDate(time)
//									+ " | " + dto.getReferencenumber()
//									+ " | ND: " + dto.getContent();
//							for (String webhook : webhooks) {
//								larkUtil.sendMessageToLark(larkMsg, webhook);
//							}
//						}
//					}
//				} else {
//					List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//					if (webhooks != null && !webhooks.isEmpty()) {
//						LarkUtil larkUtil = new LarkUtil();
//						String larkMsg = prefix + amount + " VND"
//								+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//								+ accountBankEntity.getBankAccount()
//								+ " | " + convertLongToDate(time)
//								+ " | " + dto.getReferencenumber()
//								+ " | ND: " + dto.getContent();
//						for (String webhook : webhooks) {
//							larkUtil.sendMessageToLark(larkMsg, webhook);
//						}
//					}
//				}

				/////// DO INSERT LARK BY QVAN
				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (webhooks != null && !webhooks.isEmpty()) {
					LarkUtil larkUtil = new LarkUtil();
					for (String webhook : webhooks) {
						try {
							LarkEntity larkEntity = larkService.getLarkByWebhook(webhook);
							if (larkEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String larkMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									larkUtil.sendMessageToLark(larkMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Lark notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Lark notification: " + e.getMessage());
						}
					}
				}


				/////// DO INSERT GOOGLE CHAT
//				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//					String googleChatMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String webhook : ggChatWebhooks) {
//						googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//					}
//				}

				// DO INSERT GOOGLE CHAT BY QVAN
//				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//					for (String webhook : ggChatWebhooks) {
//						try {
//							GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
//							if (googleChatEntity != null) {
//								List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
//								List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
//								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
//								if (sendNotification) {
//									String googleChatMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
//									googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//								}
//							}
//						} catch (JsonProcessingException e) {
//							logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
//						} catch (Exception e) {
//							logger.error("Error sending Google Chat notification: " + e.getMessage());
//						}
//					}
//				}

				// DO INSERT GOOGLE CHAT BY QVAN
				List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
					GoogleChatUtil googleChatUtil = new GoogleChatUtil();
					for (String webhook : ggChatWebhooks) {
						try {
							GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
							if (googleChatEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									// Chuyển đổi thời gian sử dụng múi giờ GMT và định dạng giống Google Sheets
									LocalDateTime transactionTime = convertLongToLocalDateTime(dto.getTransactiontime());
									String formattedTime = formatLocalDateTime(transactionTime);
									long transactionTimeAsLong = dto.getTransactiontime(); // giữ nguyên giá trị long để truyền vào createMessage
									String googleChatMsg = createMessage(notificationContents, dto.getTransType(), String.valueOf(dto.getAmount()), bankTypeEntity, accountBankEntity.getBankAccount(), transactionTimeAsLong, dto.getReferencenumber(), dto.getContent());
									googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
						} catch (Exception e) {
							logger.error("Error sending Google Chat notification: " + e.getMessage());
						}
					}
				}

				// DO INSERT SLACK BY QVAN
				List<String> slackWebhooks = slackAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (slackWebhooks != null && !slackWebhooks.isEmpty()) {
					SlackUtil slackUtil = new SlackUtil();
					for (String webhook : slackWebhooks) {
						try {
							SlackEntity slackEntity = slackService.getSlackByWebhook(webhook);
							if (slackEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(slackEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(slackEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String slackMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									slackUtil.sendMessageToSlack(slackMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Slack notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Slack notification: " + e.getMessage());
						}
					}
				}


				// DO INSERT DISCORD BY QVAN
				List<String> discordWebhooks = discordAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (discordWebhooks != null && !discordWebhooks.isEmpty()) {
					DiscordUtil discordUtil = new DiscordUtil();
					for (String webhook : discordWebhooks) {
						try {
							DiscordEntity discordEntity = discordService.getDiscordByWebhook(webhook);
							if (discordEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(discordEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(discordEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									String discordMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
									discordUtil.sendMessageToDiscord(discordMsg, webhook);
								}
							}
						} catch (JsonProcessingException e) {
							// Log error but do not interrupt the transaction process
							logger.error("Error processing JSON for Discord notification: " + e.getMessage());
						} catch (Exception e) {
							// Log any other errors
							logger.error("Error sending Discord notification: " + e.getMessage());
						}
					}
				}



				// DO INSERT GOOGLE SHEET BY QVAN
				List<String> ggSheetWebhooks = googleSheetAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
				if (ggSheetWebhooks != null && !ggSheetWebhooks.isEmpty()) {
					GoogleSheetUtil googleSheetUtil = GoogleSheetUtil.getInstance();
					for (String webhook : ggSheetWebhooks) {
						try {
							GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetByWebhook(webhook);
							if (googleSheetEntity != null) {
								List<String> notificationTypes = new ObjectMapper().readValue(googleSheetEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
								List<String> notificationContents = new ObjectMapper().readValue(googleSheetEntity.getNotificationContents(), new TypeReference<List<String>>() {});
								boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
								if (sendNotification) {
									if (!googleSheetUtil.headerInserted) {
										googleSheetUtil.insertHeader(webhook);
									}

									Map<String, String> datas = new HashMap<>();
									datas.put("notificationId", UUID.randomUUID().toString());
									datas.put("transactionReceiveId", transactionEntity.getId());
									datas.put("bankAccount", accountBankEntity.getBankAccount());
									datas.put("bankName", bankTypeEntity.getBankName());
									datas.put("bankCode", bankTypeEntity.getBankCode());
									datas.put("content", dto.getContent());
									datas.put("amount", String.valueOf(dto.getAmount()));
									datas.put("timePaid", String.valueOf(transactionEntity.getTimePaid()));
									datas.put("time", String.valueOf(time));
									datas.put("type", String.valueOf(transactionEntity.getType()));
									datas.put("refId", dto.getTransactionid());
									datas.put("terminalName", "");
									datas.put("terminalCode", "");
									datas.put("rawTerminalCode", "");
									datas.put("orderId", transactionEntity.getOrderId() != null ? transactionEntity.getOrderId() : "");
									datas.put("referenceNumber", transactionEntity.getReferenceNumber() != null ? transactionEntity.getReferenceNumber() : "");
									datas.put("status", "1");
									datas.put("traceId", "");
									datas.put("transType", dto.getTransType());
									datas.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
									//data.put("note", dto.getNote() != null ? dto.getNote() : "");

									googleSheetUtil.insertTransactionToGoogleSheet(datas, notificationContents, webhook);
								}
							}
						} catch (Exception e) {
							logger.error("Error sending Google Sheets notification: " + e.getMessage());
						}
					}
				}


				// }
			}
		} else {

			// insert transactions:
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
			transactionEntity.setTerminalCode("");
			transactionEntity.setQrCode("");
			transactionEntity.setUserId(accountBankEntity.getUserId());
			transactionEntity.setNote("");
			transactionEntity.setTransStatus(0);
			transactionEntity.setUrlLink("");
			// int check =
			// transactionReceiveService.insertTransactionReceiveWithCheckDuplicated(transactionEntity);
			int check = transactionReceiveService.insertTransactionReceive(transactionEntity);
			//
			//
			// if (check == 0) {
			// logger.info("transaction-sync - insertNewTransaction -
			// insertTransactionReceive failed: Duplicated when insert");
			// } else {
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
					+ prefix + amount
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
			// String userIds =
			// merchantMemberRoleService.getListUserIdByMerchantId(accountBankEntity.get);
			// 1. get all user id belong to merchant
			List<String> userIds = new ArrayList<>();
			List<String> list = new ArrayList<>();
			list.add(EnvironmentUtil.getRequestReceiveTerminalRoleId());
			list.add(EnvironmentUtil.getRequestReceiveMerchantRoleId());
			String roles = String.join("|", list);
			userIds = merchantMemberRoleService.getListUserIdRoles(accountBankEntity.getId(), roles);
			// pussh notification to all member that have role
			if (userIds != null && !userIds.isEmpty()) {
				int numThread = userIds.size();
				ExecutorService executorService = Executors.newFixedThreadPool(numThread);
				Map<String, String> data1 = new HashMap<>();
				for (String userId : userIds) {
					Map<String, String> data = new HashMap<>();
					// insert notification
					UUID notiUUID = UUID.randomUUID();
					NotificationEntity notificationEntity = new NotificationEntity();
					notificationEntity.setId(notiUUID.toString());
					notificationEntity.setRead(false);
					notificationEntity.setMessage(message);
					notificationEntity.setTime(time);
					notificationEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
					notificationEntity.setUserId(userId);
					notificationEntity.setData(transactionEntity.getId());
					data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
					data.put("notificationId", notificationUUID.toString());
					data.put("transactionReceiveId", transactionEntity.getId());
					data.put("bankAccount", transactionEntity.getBankAccount());
					data.put("bankName", bankTypeEntity.getBankName());
					data.put("bankCode", bankTypeEntity.getBankCode());
					data.put("bankId", transactionEntity.getBankId());
					data.put("terminalName", "");
					data.put("terminalCode", "");
					data.put("orderId", transactionEntity.getOrderId() != null
							? transactionEntity.getOrderId()
							: "");
					data.put("referenceNumber", transactionEntity.getReferenceNumber() != null
							? transactionEntity.getReferenceNumber()
							: "");
					data.put("rawTerminalCode", "");
					data.put("content", transactionEntity.getContent());
					data.put("amount", "" + amount);
					data.put("timePaid", "" + transactionEntity.getTimePaid());
					data.put("type", "" + transactionEntity.getType());
					data.put("time", "" + transactionEntity.getTime());
					data.put("refId", "" + dto.getTransactionid());
					data.put("status", "1");
					data.put("traceId", "" + transactionEntity.getTraceId());
					data.put("transType", dto.getTransType());
					data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
					data1 = data;
					String roleFCM = merchantMemberRoleService.checkMemberHaveRole(userId,
							EnvironmentUtil.getFcmNotificationRoleId());
					if (roleFCM != null && !roleFCM.isEmpty()) {
						executorService.submit(() -> pushNotification(NotificationUtil
								.getNotiTitleUpdateTransaction(), message, notificationEntity, data, userId));
					} else {
						executorService.submit(() -> pushNotificationWithSocket(NotificationUtil
								.getNotiTitleUpdateTransaction(), message, notificationEntity, data, userId));
					}
				}
				try {
					// send msg to QR Link
					String refId = TransactionRefIdUtil.encryptTransactionId(transactionEntity.getId());
					socketHandler.sendMessageToTransactionRefId(refId, data1);
					if (!StringUtil.isNullOrEmpty(boxIdRef)) {
						try {
							BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
							String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
							data1.put("message", String.format(messageForBox, amountForVoice));
							String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
							socketHandler.sendMessageToBoxId(idRefBox, data1);
							logger.info("WS: socketHandler.sendMessageToQRBox - "
									+ boxIdRef + " at: " + System.currentTimeMillis());
						} catch (IOException e) {
							logger.error(
									"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
						}
					}
				} catch (IOException e) {
					logger.error(
							"WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
				}
			}
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
			data.put("content", dto.getContent());
			data.put("amount", "" + amount);
			data.put("timePaid", "" + transactionEntity.getTimePaid());
			data.put("time", "" + time);
			data.put("type", "" + transactionEntity.getType());
			data.put("refId", "" + dto.getTransactionid());
			data.put("terminalName", "");
			data.put("terminalCode", "");
			data.put("rawTerminalCode", "");
			data.put("orderId", transactionEntity.getOrderId() != null
					? transactionEntity.getOrderId()
					: "");
			data.put("referenceNumber", transactionEntity.getReferenceNumber() != null
					? transactionEntity.getReferenceNumber()
					: "");
			data.put("status", "1");
			data.put("traceId", "");
			data.put("transType", dto.getTransType());
			data.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
			firebaseMessagingService.sendUsersNotificationWithData(data, fcmTokens,
					NotificationUtil
							.getNotiTitleUpdateTransaction(),
					message);
			try {
				// send msg to user
				socketHandler.sendMessageToUser(accountBankEntity.getUserId(), data);
				// send msg to QR Link
				String refId = TransactionRefIdUtil.encryptTransactionId(transcationUUID.toString());
				socketHandler.sendMessageToTransactionRefId(refId, data);
				if (!StringUtil.isNullOrEmpty(boxIdRef)) {
					try {
						BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
						String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
						data.put("message", String.format(messageForBox, amountForVoice));
						String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
						socketHandler.sendMessageToBoxId(idRefBox, data);
						logger.info("WS: socketHandler.sendMessageToQRBox - "
								+ boxIdRef + " at: " + System.currentTimeMillis());
					} catch (IOException e) {
						logger.error(
								"WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
					}
				}
			} catch (IOException e) {
				logger.error("WS: socketHandler.sendMessageToUser - insertNewTransaction ERROR: " + e.toString());
			}
			/////// DO INSERT TELEGRAM
//			List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
//			if (chatIds != null && !chatIds.isEmpty()) {
//				TelegramUtil telegramUtil = new TelegramUtil();
//				// String telegramMsg2 = "Thanh toán thành công 🎉."
//				// + "\nTài khoản: " + bankTypeEntity.getBankShortName() + " - " +
//				// accountBankEntity.getBankAccount()
//				// + "\nGiao dịch: " + prefix + nf.format(dto.getAmount()) + " VND"
//				// + "\nMã giao dịch: " + dto.getReferencenumber()
//				// + "\nThời gian: " + convertLongToDate(time)
//				// + "\nNội dung: " + dto.getContent();
//				// String telegramMsg = "GD: " + prefix + nf.format(dto.getAmount()) + " VND"
//				// + "| TK: " + bankTypeEntity.getBankShortName() + " - "
//				// + accountBankEntity.getBankAccount()
//				// + "| Ma GD: " + dto.getReferencenumber()
//				// + "| ND: " + dto.getContent()
//				// + "| " + convertLongToDate(time);
//				String telegramMsg = prefix + amount + " VND"
//						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//						+ accountBankEntity.getBankAccount()
//						+ " | " + convertLongToDate(time)
//						+ " | " + dto.getReferencenumber()
//						+ " | ND: " + dto.getContent();
//				for (String chatId : chatIds) {
//					telegramUtil.sendMsg(chatId, telegramMsg);
//				}
//			}

			/////// DO INSERT TELEGRAM BY QVAN
			List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(accountBankEntity.getId());
			if (chatIds != null && !chatIds.isEmpty()) {
				TelegramUtil telegramUtil = new TelegramUtil();
				for (String chatId : chatIds) {
					try {
						TelegramEntity telegramEntity = telegramService.getTelegramByChatId(chatId);
						if (telegramEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(telegramEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(telegramEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
							if (sendNotification) {
								String telegramMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								telegramUtil.sendMsg(chatId, telegramMsg);
							}
						}
					} catch (JsonProcessingException e) {
						logger.error("Error processing JSON for Telegram notification: " + e.getMessage());
					} catch (Exception e) {
						logger.error("Error sending Telegram notification: " + e.getMessage());
					}
				}
			}


			/////// DO INSERT LARK
//			if (accountBankEntity.getBankAccount().equals("699699699996")) {
//				if (transactionEntity.getTransType().equals("C")) {
//					List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//					if (webhooks != null && !webhooks.isEmpty()) {
//						LarkUtil larkUtil = new LarkUtil();
//						String larkMsg = prefix + amount + " VND"
//								+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//								+ accountBankEntity.getBankAccount()
//								+ " | " + convertLongToDate(time)
//								+ " | " + dto.getReferencenumber()
//								+ " | ND: " + dto.getContent();
//						for (String webhook : webhooks) {
//							larkUtil.sendMessageToLark(larkMsg, webhook);
//						}
//					}
//				}
//			} else {
//				List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//				if (webhooks != null && !webhooks.isEmpty()) {
//					LarkUtil larkUtil = new LarkUtil();
//					String larkMsg = prefix + amount + " VND"
//							+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//							+ accountBankEntity.getBankAccount()
//							+ " | " + convertLongToDate(time)
//							+ " | " + dto.getReferencenumber()
//							+ " | ND: " + dto.getContent();
//					for (String webhook : webhooks) {
//						larkUtil.sendMessageToLark(larkMsg, webhook);
//					}
//				}
//			}

/////// DO INSERT LARK BY QVAN
			List<String> webhooks = larkAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (webhooks != null && !webhooks.isEmpty()) {
				LarkUtil larkUtil = new LarkUtil();
				for (String webhook : webhooks) {
					try {
						LarkEntity larkEntity = larkService.getLarkByWebhook(webhook);
						if (larkEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
							if (sendNotification) {
								String larkMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								larkUtil.sendMessageToLark(larkMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						logger.error("Error processing JSON for Lark notification: " + e.getMessage());
					} catch (Exception e) {
						logger.error("Error sending Lark notification: " + e.getMessage());
					}
				}
			}

			/////// DO INSERT GOOGLE CHAT
//			List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//			if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//				GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//				String googleChatMsg = prefix + amount + " VND"
//						+ " | TK: " + bankTypeEntity.getBankShortName() + " - "
//						+ accountBankEntity.getBankAccount()
//						+ " | " + convertLongToDate(time)
//						+ " | " + dto.getReferencenumber()
//						+ " | ND: " + dto.getContent();
//				for (String webhook : ggChatWebhooks) {
//					googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//				}
//			}

			// DO INSERT GOOGLE CHAT BY QVAN
//			List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
//			if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
//				GoogleChatUtil googleChatUtil = new GoogleChatUtil();
//				for (String webhook : ggChatWebhooks) {
//					try {
//						GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
//						if (googleChatEntity != null) {
//							List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
//							List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
//							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
//							if (sendNotification) {
//								String googleChatMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
//								googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
//							}
//						}
//					} catch (JsonProcessingException e) {
//						logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
//					} catch (Exception e) {
//						logger.error("Error sending Google Chat notification: " + e.getMessage());
//					}
//				}
//			}


			// DO INSERT GOOGLE CHAT BY QVAN
			List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
				GoogleChatUtil googleChatUtil = new GoogleChatUtil();
				for (String webhook : ggChatWebhooks) {
					try {
						GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatByWebhook(webhook);
						if (googleChatEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(googleChatEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(googleChatEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
							if (sendNotification) {
								// Chuyển đổi thời gian sử dụng múi giờ GMT và định dạng giống Google Sheets
								LocalDateTime transactionTime = convertLongToLocalDateTime(dto.getTransactiontime());
								String formattedTime = formatLocalDateTime(transactionTime);
								String googleChatMsg = createMessage(notificationContents, dto.getTransType(), String.valueOf(dto.getAmount()), bankTypeEntity, accountBankEntity.getBankAccount(), Long.parseLong(formattedTime), dto.getReferencenumber(), dto.getContent());
								googleChatUtil.sendMessageToGoogleChat(googleChatMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						logger.error("Error processing JSON for Google Chat notification: " + e.getMessage());
					} catch (Exception e) {
						logger.error("Error sending Google Chat notification: " + e.getMessage());
					}
				}
			}

			// DO INSERT SLACK BY QVAN
			List<String> slackWebhooks = slackAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (slackWebhooks != null && !slackWebhooks.isEmpty()) {
				SlackUtil slackUtil = new SlackUtil();
				for (String webhook : slackWebhooks) {
					try {
						SlackEntity slackEntity = slackService.getSlackByWebhook(webhook);
						if (slackEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(slackEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(slackEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
							if (sendNotification) {
								String slackMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								slackUtil.sendMessageToSlack(slackMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						// Log error but do not interrupt the transaction process
						logger.error("Error processing JSON for Slack notification: " + e.getMessage());
					} catch (Exception e) {
						// Log any other errors
						logger.error("Error sending Slack notification: " + e.getMessage());
					}
				}
			}

			// DO INSERT DISCORD BY QVAN
			List<String> discordWebhooks = discordAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (discordWebhooks != null && !discordWebhooks.isEmpty()) {
				DiscordUtil discordUtil = new DiscordUtil();
				for (String webhook : discordWebhooks) {
					try {
						DiscordEntity discordEntity = discordService.getDiscordByWebhook(webhook);
						if (discordEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(discordEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(discordEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
							if (sendNotification) {
								String discordMsg = createMessage(notificationContents, dto.getTransType(), amount, bankTypeEntity, accountBankEntity.getBankAccount(), time, dto.getReferencenumber(), dto.getContent());
								discordUtil.sendMessageToDiscord(discordMsg, webhook);
							}
						}
					} catch (JsonProcessingException e) {
						// Log error but do not interrupt the transaction process
						logger.error("Error processing JSON for Discord notification: " + e.getMessage());
					} catch (Exception e) {
						// Log any other errors
						logger.error("Error sending Discord notification: " + e.getMessage());
					}
				}
			}


			// DO INSERT GOOGLE SHEET BY QVAN
			List<String> ggSheetWebhooks = googleSheetAccountBankService.getWebhooksByBankId(accountBankEntity.getId());
			if (ggSheetWebhooks != null && !ggSheetWebhooks.isEmpty()) {
				GoogleSheetUtil googleSheetUtil = GoogleSheetUtil.getInstance();
				for (String webhook : ggSheetWebhooks) {
					try {
						GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetByWebhook(webhook);
						if (googleSheetEntity != null) {
							List<String> notificationTypes = new ObjectMapper().readValue(googleSheetEntity.getNotificationTypes(), new TypeReference<List<String>>() {});
							List<String> notificationContents = new ObjectMapper().readValue(googleSheetEntity.getNotificationContents(), new TypeReference<List<String>>() {});
							boolean sendNotification = shouldSendNotification(notificationTypes, dto, transactionEntity);
							if (sendNotification) {
								if (!googleSheetUtil.headerInserted) {
									googleSheetUtil.insertHeader(webhook);
								}

								Map<String, String> datas = new HashMap<>();

								datas.put("transactionReceiveId", transactionEntity.getId());
								datas.put("bankAccount", accountBankEntity.getBankAccount());
								datas.put("bankName", bankTypeEntity.getBankName());
								datas.put("bankCode", bankTypeEntity.getBankCode());
								datas.put("bankId", accountBankEntity.getId());
								datas.put("content", dto.getContent());
								datas.put("amount", String.valueOf(dto.getAmount()));
								datas.put("timePaid", String.valueOf(transactionEntity.getTimePaid()));
								datas.put("time", String.valueOf(time));
								datas.put("type", String.valueOf(transactionEntity.getType()));
								datas.put("refId", dto.getTransactionid());
								datas.put("terminalName", "");
								datas.put("terminalCode", "");
								datas.put("rawTerminalCode", "");
								datas.put("orderId", transactionEntity.getOrderId() != null ? transactionEntity.getOrderId() : "");
								datas.put("referenceNumber", transactionEntity.getReferenceNumber() != null ? transactionEntity.getReferenceNumber() : "");
								datas.put("status", "1");
								datas.put("traceId", "");
								datas.put("transType", dto.getTransType());
								datas.put("urlLink", transactionEntity.getUrlLink() != null ? transactionEntity.getUrlLink() : "");
								//data.put("note", dto.getNote() != null ? dto.getNote() : "");

								googleSheetUtil.insertTransactionToGoogleSheet(datas, notificationContents, webhook);
							}
						}
					} catch (Exception e) {
						logger.error("Error sending Google Sheets notification: " + e.getMessage());
					}
				}
			}



			// }
		}

	}

	private void pushNotificationWithSocket(String notiTitleUpdateTransaction, String message,
			NotificationEntity notificationEntity, Map<String, String> data, String userId) {
		try {
			socketHandler.sendMessageToUser(userId,
					data);
		} catch (IOException e) {
			logger.error(
					"transaction-sync: WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
							+ e.toString());
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

	// get token BIDV
	private TokenBankBIDVDTO getBIDVToken(String scope) {
		TokenBankBIDVDTO result = null;
		try {
			String url = EnvironmentUtil.getBidvUrlGetToken();
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(url)
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(url)
					.build();
			MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			formData.add("grant_type", "client_credentials");
			formData.add("scope", scope);
			formData.add("client_id", EnvironmentUtil.getBidvGetTokenClientId());
			formData.add("client_secret", EnvironmentUtil.getBidvGetTokenClientSecret());
			// Call POST API
			TokenBankBIDVDTO response = webClient.method(HttpMethod.POST)
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(BodyInserters.fromFormData(formData))
					.exchange()
					.flatMap(clientResponse -> {
						if (clientResponse.statusCode().is2xxSuccessful()) {
							return clientResponse.bodyToMono(TokenBankBIDVDTO.class);
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
		} catch (

		Exception e) {
			logger.error("getBIDVToken: ERROR: " + e.toString());
			System.out.println("getBIDVToken: ERROR: " + e.toString());
		}
		return result;
	}

	// get token MB Bank
	private TokenProductBankDTO getMBBankToken() {
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

	@PostMapping("get_token_bank/bidv")
	public ResponseEntity<TokenBankBIDVDTO> getMBTokenBank() {
		TokenBankBIDVDTO result = null;
		HttpStatus httpStatus = null;
		try {
			result = getBIDVToken("ewallet");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			logger.error(e.toString());
		}
		return new ResponseEntity<TokenBankBIDVDTO>(result, httpStatus);
	}

	@PostMapping("get_token_bank")
	public ResponseEntity<TokenProductBankDTO> getTokenBank() {
		TokenProductBankDTO result = null;
		HttpStatus httpStatus = null;
		try {
			result = getMBBankToken();
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			httpStatus = HttpStatus.BAD_REQUEST;
			logger.error(e.toString());
		}
		return new ResponseEntity<TokenProductBankDTO>(result, httpStatus);
	}

	@PostMapping("account/info")
	private ResponseEntity<AccountBankNameDTO> getBankNameInformation(
			@RequestHeader("Authorization") String userToken,
			@RequestBody AccountInfoRequestDTO dto
	) {
		AccountBankNameDTO result = null;
		HttpStatus httpStatus = null;
		try {
			logger.info("Token getBankNameInformation: " + userToken + " at: " + System.currentTimeMillis());
			String checkSum = BankEncryptUtil.generateMD5GetAccountInfoCheckSum(dto.getAccountNumber(),
					dto.getBankCode(), dto.getAccountType());
			// check sum
			if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
				// Get bank token
				TokenProductBankDTO token = getMBBankToken();
				if (token != null) {
					// Build URL with PathVariable
					UriComponents uriComponents = UriComponentsBuilder
							.fromHttpUrl(
									EnvironmentUtil.getBankUrl() + "ms/bank-info/v1.0/account/info")
							.buildAndExpand(dto.getAccountNumber());

					// Create WebClient with authorization header
					WebClient webClient = WebClient.builder()
							.baseUrl(uriComponents.toUriString())
							.defaultHeader("Authorization", "Bearer " + token.getAccess_token())
							.defaultHeader("clientMessageId", UUID.randomUUID().toString())
							.build();
					Mono<ClientResponse> responseMono = null;
					if (dto.getTransferType().trim().equals("INHOUSE")) {
						// Send GET request to API
						responseMono = webClient.get()
								.uri(uriBuilder -> uriBuilder
										.queryParam("accountNumber", dto.getAccountNumber())
										.queryParam("accountType", dto.getAccountType())
										.queryParam("transferType", dto.getTransferType())
										.build())
								.exchange();
					} else {
						// Send GET request to API
						responseMono = webClient.get()
								.uri(uriBuilder -> uriBuilder
										.queryParam("accountNumber", dto.getAccountNumber())
										.queryParam("accountType", dto.getAccountType())
										.queryParam("transferType", dto.getTransferType())
										.queryParam("bankCode", dto.getBankCode())
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
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error("Error at getBankNameInformation: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<>(result, httpStatus);
	}

	// ONLY LINKED ACCOUNT MB BANK
	@PostMapping("request_otp_bank")
	public ResponseEntity<ResponseMessageDTO> requestOTP(@Valid @RequestBody RequestBankDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			result = requestLinkedMBOTP(dto);
			if (result != null && result.getStatus().trim().equals("SUCCESS")) {
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error("Error at requestOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("check-log/request_otp_bank")
	public ResponseEntity<ResponseMessageDTO> requestOTPCheckLog(@Valid @RequestBody RequestBankDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			logger.info("requestOTPCheckLog: " + dto.toString() + " at: " + System.currentTimeMillis());
			result = requestLinkedMBOTP(dto);
			if (result != null && result.getStatus().trim().equals("SUCCESS")) {
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
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
			result = confirmLinkedMBOTP(dto);
			if (result != null && result.getStatus().trim().equals("SUCCESS")) {
				httpStatus = HttpStatus.OK;
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
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
					.header("Authorization", "Bearer " + getMBBankToken().getAccess_token())
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
					.header("Authorization", "Bearer " + getMBBankToken().getAccess_token())
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

		} catch (Exception e) {
			logger.error("Error at confirmOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	///////
	///
	///
	///
	// LINKED ACCOUNT BANKS (MB, BIDV)
	@PostMapping("account-bank/linked/request_otp")
	public ResponseEntity<Object> requestLinkedBankOTP(@RequestBody RequestLinkedBankDTO dto,
													   @RequestHeader("Authorization") String token) {
		Object result = null;
		HttpStatus httpStatus = null;
		try {
			if (dto != null) {
				if (dto.getBankCode().trim().equals("MB")) {
					RequestBankDTO requestBankDTO = new RequestBankDTO();
					requestBankDTO.setNationalId(dto.getNationalId());
					requestBankDTO.setAccountNumber(dto.getAccountNumber());
					requestBankDTO.setAccountName(dto.getAccountName());
					requestBankDTO.setPhoneNumber(dto.getPhoneNumber());
					requestBankDTO.setApplicationType(dto.getApplicationType());
					ResponseMessageDTO responseMessageDTO = requestLinkedMBOTP(requestBankDTO);
					if (responseMessageDTO != null && responseMessageDTO.getStatus().trim().equals("SUCCESS")) {
						result = responseMessageDTO;
						httpStatus = HttpStatus.OK;
					} else {
						if (StringUtil.getValueNullChecker(responseMessageDTO.getMessage()).contains("Record existed")) {
							logger.error("Already linked VietQR: Resquest Body: " + dto.toString()
									+ " at: " + System.currentTimeMillis() + " Phone no: "
									+ validatePhoneNoToken(token));
						}
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else if (dto.getBankCode().trim().equals("BIDV")) {
					Long customerVaLength = customerVaService.getCustomerVaLength() + 1;
					String merchantName = "";
					String rawMerchantName = dto.getAccountName().replaceAll(" ", "");
					if (!StringUtil.isNullOrEmpty(rawMerchantName)) {
						// GENERATE MERCHANT NAME
						int size = (rawMerchantName.length() / 3) * 2;
						merchantName = (rawMerchantName.substring(size) + RandomCodeUtil.generateOTP(3)).toUpperCase();
					} else {
						merchantName = rawMerchantName.toUpperCase();
					}
					String merchantId = CustomerVaUtil.generateMerchantId(merchantName, customerVaLength);
					String checkExistedMerchantId = customerVaService.checkExistedMerchantId(merchantId);
					RequestCustomerVaDTO requestCustomerVaDTO = new RequestCustomerVaDTO();
					requestCustomerVaDTO.setBankCode(dto.getBankCode());
					requestCustomerVaDTO.setBankAccount(dto.getAccountNumber());
					requestCustomerVaDTO.setMerchantName(merchantName);
					requestCustomerVaDTO.setUserBankName(dto.getAccountName());
					requestCustomerVaDTO.setNationalId(dto.getNationalId());
					requestCustomerVaDTO.setPhoneAuthenticated(dto.getPhoneNumber());
					result = CustomerVaUtil.requestCustomerVa(requestCustomerVaDTO, merchantId, "1",
							customerVaLength, checkExistedMerchantId);
//					result = requestLinkedBIDVOTP(dto);
					if (result instanceof ResponseObjectDTO) {
						httpStatus = HttpStatus.OK;
					} else if (result instanceof ResponseMessageDTO) {
						httpStatus = HttpStatus.BAD_REQUEST;
					} else {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					logger.error("requestLinkedBankOTP: INVALID BANK CODE");
					System.out.println("requestLinkedBankOTP: INVALID BANK CODE");
					result = new ResponseMessageDTO("FAILED", "E109");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				logger.error("requestLinkedBankOTP: INVALID REQUEST BODY");
				System.out.println("requestLinkedBankOTP: INVALID REQUEST BODY");
				result = new ResponseMessageDTO("FAILED", "E46");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error("Error at requestLinkedBankOTP: " + e.toString());
			System.out.println("Error at requestLinkedBankOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("account-bank/linked/confirm_otp")
	public ResponseEntity<ResponseMessageDTO> confirmLinkedBankOTP(
			@RequestBody ConfirmLinkedBankDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (dto != null) {
				if (dto.getBankCode().trim().equals("MB")) {
					CofirmOTPBankDTO cofirmOTPBankDTO = new CofirmOTPBankDTO();
					cofirmOTPBankDTO.setRequestId(dto.getRequestId());
					cofirmOTPBankDTO.setOtpValue(dto.getOtpValue());
					cofirmOTPBankDTO.setApplicationType(dto.getApplicationType());
					result = confirmLinkedMBOTP(cofirmOTPBankDTO);
					if (result != null && result.getStatus().trim().equals("SUCCESS")) {
						httpStatus = HttpStatus.OK;
					} else {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else if (dto.getBankCode().trim().equals("BIDV")) {
//					result = confirmLinkedBIDVOTP(dto);
					ConfirmCustomerVaDTO customerVaDTO = new ConfirmCustomerVaDTO();
					customerVaDTO.setMerchantId(dto.getMerchantId());
					customerVaDTO.setConfirmId(dto.getConfirmId());
					customerVaDTO.setOtpNumber(dto.getOtpNumber());
					customerVaDTO.setMerchantName(dto.getMerchantName());
					result = CustomerVaUtil.confirmCustomerVa(customerVaDTO);
					if (result != null && result.getStatus().trim().equals("SUCCESS")) {
						httpStatus = HttpStatus.OK;
					} else {
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					logger.error("confirmLinkedBankOTP: INVALID BANK CODE");
					System.out.println("confirmLinkedBankOTP: INVALID BANK CODE");
					result = new ResponseMessageDTO("FAILED", "E109");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				logger.error("confirmLinkedBankOTP: INVALID REQUEST BODY");
				System.out.println("confirmLinkedBankOTP: INVALID REQUEST BODY");
				result = new ResponseMessageDTO("FAILED", "E46");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error("Error at confirmLinkedBankOTP: " + e.toString());
			System.out.println("Error at confirmLinkedBankOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("account-bank/unlinked")
	public ResponseEntity<ResponseMessageDTO> unlinkedBankOTP(
			@RequestBody RequestUnlinkedBankDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (Objects.nonNull(dto)) {
				BidvUnlinkedDTO bidvUnlinkedDTO = accountBankReceiveService
						.getMerchantIdByBankAccountBidvAuthen(dto.getBankAccount(), dto.getBankCode());
				result = CustomerVaUtil.unregisterCustomerVa(bidvUnlinkedDTO.getMerchantId());
				if (result.getStatus().equals("SUCCESS")) {
					accountBankReceiveService.updateRegisterUnlinkBidv(bidvUnlinkedDTO.getUserId(),
							bidvUnlinkedDTO.getBankId());
					customerVaService.removeCustomerVa(bidvUnlinkedDTO.getUserId(), bidvUnlinkedDTO.getMerchantId());
					result = new ResponseMessageDTO("SUCCESS", "");
					httpStatus = HttpStatus.OK;
				}
			}
//			if (dto != null) {
//				UUID interactionId = UUID.randomUUID();
//				UUID idemKey = UUID.randomUUID();
//				String url = EnvironmentUtil.getBidvUrlUnlinked();
//				String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
//				String merchantId = EnvironmentUtil.getBidvLinkedMerchantId();
//				String merchantName = EnvironmentUtil.getBidvLinkedMerchantName();
//				String channelId = EnvironmentUtil.getBidvLinkedChannelId();
//				String transDate = getTransDate();
//				//
//				// jws and jwe request body
//				String myKey = JwsUtil.getSymmatricKey();
//				Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
//				JsonWebEncryption jwe = new JsonWebEncryption();
//				String payload = BIDVUtil.generateUnlinkedBody(serviceId, merchantId, merchantName, channelId,
//						transDate, dto.getEwalletToken(), dto.getBankAccount(), "Y");
//				System.out.println("Payload: " + payload);
//				//
//				jwe.setPayload(payload);
//				jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW);
//				jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
//				jwe.setKey(key);
//				String serializedJwe = jwe.getCompactSerialization();
//				String[] split = serializedJwe.split("\\.");
//				Gson gson = new Gson();
//				String protected_ = split[0];
//				byte[] decodedBytes = Base64.getDecoder().decode(protected_);
//				String decodedString = new String(decodedBytes);
//				Header h = gson.fromJson(decodedString, Header.class);
//				String encryptedKey = split[1];
//				String iv = split[2];
//				String ciphertext = split[3];
//				String tag = split[4];
//				Recipients recipient = new Recipients();
//				recipient.setHeader(h);
//				recipient.setEncrypted_key(encryptedKey);
//				Recipients[] recipients = new Recipients[1];
//				recipients[0] = recipient;
//				// JWE
//				JweObj j = new JweObj(recipients, protected_, ciphertext, iv, tag);
//				String jweString = gson.toJson(j);
//				System.out.println("\n\nJWE: " + jweString);
//				Map<String, Object> body = gson.fromJson(jweString, Map.class);
//
//				// JWS
//				JsonWebSignature jws = new JsonWebSignature();
//				jws.setPayload(jweString);
//				jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
//				PrivateKey privateKey = JwsUtil.getPrivateKey();
//				jws.setKey(privateKey);
//				String jwsString = jws.getCompactSerialization();
//				System.out.println("\n\nJWS: " + jwsString);
//				///
//				// call API
//				UriComponents uriComponents = UriComponentsBuilder
//						.fromHttpUrl(url)
//						.buildAndExpand(/* add url parameter here */);
//				WebClient webClient = WebClient.builder()
//						.baseUrl(url)
//						.build();
//				String token = getBIDVToken("ewallet").getAccess_token();
//				String clientXCertification = JwsUtil.getClientXCertificate();
//				System.out.println("\n\nToken BIDV: " + token);
//				System.out.println("\n\nclientXCertification BIDV: " + clientXCertification);
//				// System.out.println("\n\nTime: " + "2020-01-31T09:59:34.000+10:12");
//				Mono<ClientResponse> responseMono = webClient.post()
//						.uri(uriComponents.toUri())
//						.contentType(MediaType.APPLICATION_JSON)
//						.header("Channel", EnvironmentUtil.getBidvLinkedChannelId())
//						.header("User-Agent", EnvironmentUtil.getBidvLinkedMerchantName())
//						.header("X-Client-Certificate", clientXCertification)
//						.header("X-API-Interaction-ID", interactionId.toString())
//						.header("X-Idempotency-Key", idemKey.toString())
//						.header("Timestamp", "2020-01-31T09:59:34.000+10:12")
//						.header("X-Customer-IP-Address", EnvironmentUtil.getIpVietQRVN())
//						.header("Authorization", "Bearer " + token)
//						.header("X-JWS-Signature", jwsString)
//						.body(BodyInserters.fromValue(body))
//						.exchange();
//				System.out.println("\n\n");
//				ClientResponse response = responseMono.block();
//				if (response.statusCode().is2xxSuccessful()) {
//					String json = response.bodyToMono(String.class).block();
//					logger.info("Response unlinkedBankOTP: " + json);
//					System.out.println("Response unlinkedBankOTP: " + json);
//					ObjectMapper objectMapper = new ObjectMapper();
//					JsonNode rootNode = objectMapper.readTree(json);
//					// if (rootNode.get("errorCode") != null) {
//					// String errCode = rootNode.get("errorCode").asText();
//					// if (errCode.trim().equals("000")) {
//					// result = new ResponseMessageDTO("SUCCESS", "");
//					// // do update status bank_account from authen => unauthen
//					// accountBankService.unRegisterAuthenticationBank(dto.getBankAccount());
//					// httpStatus = HttpStatus.OK;
//					// } else {
//					// result = new ResponseMessageDTO("FAILED", "E05");
//					// httpStatus = HttpStatus.BAD_REQUEST;
//					// }
//					// } else {
//					// result = new ResponseMessageDTO("FAILED", "E05");
//					// httpStatus = HttpStatus.BAD_REQUEST;
//					// }
//					if (rootNode.get("body") != null) {
//						if (rootNode.get("body").get("additionalInfo") != null) {
//							if (rootNode.get("body").get("additionalInfo").get("detailedError") != null) {
//								if (rootNode.get("body").get("additionalInfo").get("detailedError")
//										.get("errorCode") != null) {
//									String errorCode = rootNode.get("body").get("additionalInfo").get("detailedError")
//											.get("errorCode").asText();
//									if (errorCode != null && errorCode.trim().equals("000")) {
//										// do update status bank_account from authen => unauthen
//										accountBankService.unRegisterAuthenBank(dto.getBankAccount(),
//												dto.getEwalletToken());
//										result = new ResponseMessageDTO("SUCCESS", "");
//										httpStatus = HttpStatus.OK;
//									} else {
//										result = new ResponseMessageDTO("FAILED", "E05");
//										httpStatus = HttpStatus.BAD_REQUEST;
//									}
//								} else {
//									result = new ResponseMessageDTO("FAILED", "E05");
//									httpStatus = HttpStatus.BAD_REQUEST;
//								}
//							} else {
//								result = new ResponseMessageDTO("FAILED", "E05");
//								httpStatus = HttpStatus.BAD_REQUEST;
//							}
//						} else {
//							result = new ResponseMessageDTO("FAILED", "E05");
//							httpStatus = HttpStatus.BAD_REQUEST;
//						}
//					} else {
//						if (rootNode.get("errorCode") != null) {
//							String errorCode = rootNode.get("errorCode").asText();
//							if (errorCode != null && errorCode.trim().equals("000")) {
//								accountBankService.unRegisterAuthenBank(dto.getBankAccount(), dto.getEwalletToken());
//								result = new ResponseMessageDTO("SUCCESS", "");
//								httpStatus = HttpStatus.OK;
//							} else {
//								result = new ResponseMessageDTO("FAILED", "E05");
//								httpStatus = HttpStatus.BAD_REQUEST;
//							}
//						} else {
//							result = new ResponseMessageDTO("FAILED", "E05");
//							httpStatus = HttpStatus.BAD_REQUEST;
//						}
//
//					}
//				} else {
//					String json = response.bodyToMono(String.class).block();
//					logger.info("Response unlinkedBankOTP: " + json);
//					System.out.println("Response unlinkedBankOTP: " + json);
//					result = new ResponseMessageDTO("FAILED", "E05");
//					httpStatus = HttpStatus.BAD_REQUEST;
//				}
//			} else {
//				logger.error("unlinkedBankOTP: INVALID REQUEST BODY");
//				System.out.println("unlinkedBankOTP: INVALID REQUEST BODY");
//				result = new ResponseMessageDTO("FAILED", "E46");
//				httpStatus = HttpStatus.BAD_REQUEST;
//			}
		} catch (Exception e) {
			logger.error("Error at unlinkedBankOTP: " + e.toString());
			System.out.println("Error at unlinkedBankOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("account-bank/unlinked/confirm_otp")
	public ResponseEntity<ResponseMessageDTO> confirmUnlinkedBankOTP() {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
		} catch (Exception e) {
			logger.error("Error at confirmUnlinkedBankOTP: " + e.toString());
			System.out.println("Error at confirmUnlinkedBankOTP: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	///////
	///
	///
	// bank functions
	private ResponseMessageDTO requestLinkedMBOTP(RequestBankDTO dto) {
		ResponseMessageDTO result = null;
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
					.header("Authorization", "Bearer " + getMBBankToken().getAccess_token())
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
			} else {
				ConfirmRequestFailedBankDTO confirmRequestBankDTO = response.bodyToMono(
						ConfirmRequestFailedBankDTO.class)
						.block();
				LocalDateTime currentDateTime = LocalDateTime.now();
				logger.info("Response requestOTP error: Request Body: " + dto.toString() + " at: " + System.currentTimeMillis());
				logger.error("Response requestOTP error: client msg id: " + clientMessageId.toString() + " - "
						+ confirmRequestBankDTO.getSoaErrorCode() + "-"
						+ confirmRequestBankDTO.getSoaErrorDesc() + " at "
						+ currentDateTime.toEpochSecond(ZoneOffset.UTC));
				String status = "FAILED";
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode().trim());
				result = new ResponseMessageDTO(status, message);
			}
		} catch (Exception e) {
			logger.error("requestLinkedMBOTP: ERROR: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
		}
		return result;
	}

	private ResponseMessageDTO requestLinkedBIDVOTP(RequestLinkedBankDTO dto) {
		ResponseMessageDTO result = null;
		try {
			//
			// initial
			UUID interactionId = UUID.randomUUID();
			UUID idemKey = UUID.randomUUID();
			String url = EnvironmentUtil.getBidvUrlLinkedRequest();
			String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
			String merchantId = EnvironmentUtil.getBidvLinkedMerchantId();
			String merchantName = EnvironmentUtil.getBidvLinkedMerchantName();
			String channelId = EnvironmentUtil.getBidvLinkedChannelId();
			String transDate = getTransDate();
			String payerDebitType = EnvironmentUtil.getBidvLinkedPayerDebitTypeAcc();
			String registerSmartBanking = "0";
			//
			// jws and jwe request body
			String myKey = JwsUtil.getSymmatricKey();
			Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
			JsonWebEncryption jwe = new JsonWebEncryption();
			String payload = BIDVUtil.generateRequestLinkedBody(serviceId, merchantId, merchantName, channelId,
					transDate, payerDebitType, registerSmartBanking, dto);
			System.out.println("Payload: " + payload);
			logger.info("requestLinkedBIDVOTP: Payload: " + payload);
			//
			jwe.setPayload(payload);
			jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW);
			jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
			jwe.setKey(key);
			String serializedJwe = jwe.getCompactSerialization();
			String[] split = serializedJwe.split("\\.");
			Gson gson = new Gson();
			String protected_ = split[0];
			byte[] decodedBytes = Base64.getDecoder().decode(protected_);
			String decodedString = new String(decodedBytes);
			Header h = gson.fromJson(decodedString, Header.class);
			String encryptedKey = split[1];
			String iv = split[2];
			String ciphertext = split[3];
			String tag = split[4];
			Recipients recipient = new Recipients();
			recipient.setHeader(h);
			recipient.setEncrypted_key(encryptedKey);
			Recipients[] recipients = new Recipients[1];
			recipients[0] = recipient;
			// JWE
			JweObj j = new JweObj(recipients, protected_, ciphertext, iv, tag);
			String jweString = gson.toJson(j);
			System.out.println("\n\nJWE: " + jweString);
			logger.info("requestLinkedBIDVOTP: JWE: " + jweString);
			Map<String, Object> body = gson.fromJson(jweString, Map.class);

			// JWS
			JsonWebSignature jws = new JsonWebSignature();
			jws.setPayload(jweString);
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
			PrivateKey privateKey = JwsUtil.getPrivateKey();
			jws.setKey(privateKey);
			String jwsString = jws.getCompactSerialization();
			System.out.println("\n\nJWS: " + jwsString);
			logger.info("requestLinkedBIDVOTP: JWS: " + jwsString);
			///
			// call API
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(url)
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(url)
					.build();
			String token = getBIDVToken("ewallet").getAccess_token();
			String clientXCertification = JwsUtil.getClientXCertificate();
			System.out.println("\n\nToken BIDV: " + token);
			logger.info("requestLinkedBIDVOTP: Token BIDV: " + token);
			System.out.println("\n\nclientXCertification BIDV: " + clientXCertification);
			logger.info("requestLinkedBIDVOTP: clientXCertification BIDV: " + clientXCertification);
			Mono<ClientResponse> responseMono = webClient.post()
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_JSON)
					.header("Channel", EnvironmentUtil.getBidvLinkedChannelId())
					.header("User-Agent", EnvironmentUtil.getBidvLinkedMerchantName())
					.header("X-Client-Certificate", clientXCertification)
					.header("X-API-Interaction-ID", interactionId.toString())
					.header("X-Idempotency-Key", idemKey.toString())
					.header("TimeStamp", getSystemTimeWithOffset())
					.header("X-Customer-IP-Address", EnvironmentUtil.getIpVietQRVN())
					.header("Authorization", "Bearer " + token)
					.header("X-JWS-Signature", jwsString)
					.body(BodyInserters.fromValue(body))
					.exchange();
			System.out.println("\n\n");
			ClientResponse response = responseMono.block();
			if (response.statusCode().is2xxSuccessful()) {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response requestLinkedBIDVOTP: " + json);
				System.out.println("Response requestLinkedBIDVOTP: " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				if (rootNode.get("errorCode") != null) {
					String errCode = rootNode.get("errorCode").asText();
					if (errCode.trim().equals("000")) {
						result = new ResponseMessageDTO("SUCCESS", interactionId.toString());
					} else if (errCode.trim().equals("110") || errCode.trim().equals("113")) {
						result = new ResponseMessageDTO("FAILED", "E23");
					} else if (errCode.trim().equals("021")) {
						result = new ResponseMessageDTO("FAILED", "E117");
					} else if (errCode.trim().equals("022")) {
						result = new ResponseMessageDTO("FAILED", "E118");
					} else if (errCode.trim().equals("033")) {
						result = new ResponseMessageDTO("FAILED", "E119");
					} else if (errCode.trim().equals("034")) {
						result = new ResponseMessageDTO("FAILED", "E120");
					} else if (errCode.trim().equals("036")) {
						result = new ResponseMessageDTO("FAILED", "E121");
					} else if (errCode.trim().equals("037")) {
						result = new ResponseMessageDTO("FAILED", "E122");
					} else if (errCode.trim().equals("041")) {
						result = new ResponseMessageDTO("FAILED", "E123");
					} else {
						result = new ResponseMessageDTO("FAILED", "E05");
					}
				} else {
					result = new ResponseMessageDTO("FAILED", "E05");
				}

			} else {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response requestLinkedBIDVOTP: " + json);
				System.out.println("Response requestLinkedBIDVOTP: " + json);
				result = new ResponseMessageDTO("FAILED", "E05");
			}
		} catch (Exception e) {
			logger.error("requestLinkedMBOTP: ERROR: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
		}
		return result;
	}

	private ResponseMessageDTO confirmLinkedMBOTP(CofirmOTPBankDTO dto) {
		ResponseMessageDTO result = null;
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
					.header("Authorization", "Bearer " + getMBBankToken().getAccess_token())
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
				} else {
					result = new ResponseMessageDTO("FAILED",
							"E05");
				}
			} else {
				ConfirmRequestFailedBankDTO confirmRequestBankDTO = response.bodyToMono(
						ConfirmRequestFailedBankDTO.class)
						.block();
				logger.error("Response confirmOTP error: request: " + dto.toString() + " at: " + System.currentTimeMillis());
				LocalDateTime currentDateTime = LocalDateTime.now();
				logger.error("Response confirmOTP error: " + confirmRequestBankDTO.getSoaErrorCode() + " - "
						+ confirmRequestBankDTO.getSoaErrorDesc() + " at: "
						+ currentDateTime.toEpochSecond(ZoneOffset.UTC));
				String status = "FAILED";
				String message = getMessageBankCode(confirmRequestBankDTO.getSoaErrorCode().trim());
				result = new ResponseMessageDTO(status, message);
			}
		} catch (Exception e) {
			logger.error("confirmLinkedMBOTP: ERROR: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
		}
		return result;
	}

	private ResponseMessageDTO confirmLinkedBIDVOTP(ConfirmLinkedBankDTO dto) {
		ResponseMessageDTO result = null;
		try {
			UUID interactionId = UUID.randomUUID();
			UUID idemKey = UUID.randomUUID();
			String url = EnvironmentUtil.getBidvUrlLinkedConfirm();
			String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
			String merchantId = EnvironmentUtil.getBidvLinkedMerchantId();
			String merchantName = EnvironmentUtil.getBidvLinkedMerchantName();
			String channelId = EnvironmentUtil.getBidvLinkedChannelId();
			String transDate = getTransDate();
			//
			// jws and jwe request body
			String myKey = JwsUtil.getSymmatricKey();
			Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
			JsonWebEncryption jwe = new JsonWebEncryption();
			String payload = BIDVUtil.generateConfirmLinkedBody(serviceId, merchantId, merchantName, channelId,
					transDate, dto);
			System.out.println("Payload: " + payload);
			logger.info("confirmLinkedBIDVOTP: Payload: " + payload);
			//
			jwe.setPayload(payload);
			jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW);
			jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
			jwe.setKey(key);
			String serializedJwe = jwe.getCompactSerialization();
			String[] split = serializedJwe.split("\\.");
			Gson gson = new Gson();
			String protected_ = split[0];
			byte[] decodedBytes = Base64.getDecoder().decode(protected_);
			String decodedString = new String(decodedBytes);
			Header h = gson.fromJson(decodedString, Header.class);
			String encryptedKey = split[1];
			String iv = split[2];
			String ciphertext = split[3];
			String tag = split[4];
			Recipients recipient = new Recipients();
			recipient.setHeader(h);
			recipient.setEncrypted_key(encryptedKey);
			Recipients[] recipients = new Recipients[1];
			recipients[0] = recipient;
			// JWE
			JweObj j = new JweObj(recipients, protected_, ciphertext, iv, tag);
			String jweString = gson.toJson(j);
			System.out.println("\n\nJWE: " + jweString);
			logger.info("confirmLinkedBIDVOTP: JWE: " + jweString);
			Map<String, Object> body = gson.fromJson(jweString, Map.class);

			// JWS
			JsonWebSignature jws = new JsonWebSignature();
			jws.setPayload(jweString);
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
			PrivateKey privateKey = JwsUtil.getPrivateKey();
			jws.setKey(privateKey);
			String jwsString = jws.getCompactSerialization();
			System.out.println("\n\nJWS: " + jwsString);
			logger.info("confirmLinkedBIDVOTP: JWS: " + jwsString);
			///
			// call API
			UriComponents uriComponents = UriComponentsBuilder
					.fromHttpUrl(url)
					.buildAndExpand(/* add url parameter here */);
			WebClient webClient = WebClient.builder()
					.baseUrl(url)
					.build();
			String token = getBIDVToken("ewallet").getAccess_token();
			String clientXCertification = JwsUtil.getClientXCertificate();
			System.out.println("\n\nToken BIDV: " + token);
			logger.info("confirmLinkedBIDVOTP: Token BIDV: " + token);
			System.out.println("\n\nclientXCertification BIDV: " + clientXCertification);
			logger.info("confirmLinkedBIDVOTP: clientXCertification BIDV: " + clientXCertification);
			Mono<ClientResponse> responseMono = webClient.post()
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_JSON)
					.header("Channel", EnvironmentUtil.getBidvLinkedChannelId())
					.header("User-Agent", EnvironmentUtil.getBidvLinkedMerchantName())
					.header("X-Client-Certificate", clientXCertification)
					.header("X-API-Interaction-ID", interactionId.toString())
					.header("X-Idempotency-Key", idemKey.toString())
					.header("TimeStamp", getSystemTimeWithOffset())
					.header("X-Customer-IP-Address", EnvironmentUtil.getIpVietQRVN())
					.header("Authorization", "Bearer " + token)
					.header("X-JWS-Signature", jwsString)
					.body(BodyInserters.fromValue(body))
					.exchange();
			System.out.println("\n\n");
			ClientResponse response = responseMono.block();
			if (response.statusCode().is2xxSuccessful()) {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response confirmLinkedBIDVOTP: " + json);
				System.out.println("Response confirmLinkedBIDVOTP: " + json);
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(json);
				if (rootNode.get("errorCode") != null) {
					String errCode = rootNode.get("errorCode").asText();
					if (errCode.trim().equals("000")) {
						String ewalletToken = "";
						if (rootNode.get("ewalletToken").asText() != null) {
							ewalletToken = rootNode.get("ewalletToken").asText();
						}
						result = new ResponseMessageDTO("SUCCESS", ewalletToken);
					} else if (errCode.trim().equals("061")) {
						result = new ResponseMessageDTO("FAILED", "E124");
					} else if (errCode.trim().equals("060")) {
						result = new ResponseMessageDTO("FAILED", "E125");
					} else {
						result = new ResponseMessageDTO("FAILED", "E05");
					}
				} else {
					result = new ResponseMessageDTO("FAILED", "E05");
				}
			} else {
				String json = response.bodyToMono(String.class).block();
				logger.info("Response confirmLinkedBIDVOTP: " + json);
				System.out.println("Response confirmLinkedBIDVOTP: " + json);
				result = new ResponseMessageDTO("FAILED", "E05");
			}
		} catch (Exception e) {
			logger.error("confirmLinkedBIDVOTP: ERROR: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
		}
		return result;
	}

	///////
	///
	///
	public static String getTransDate() {
		// Lấy thời gian hệ thống hiện tại
		LocalDate localDate = LocalDate.now();

		// Tạo đối tượng ZoneId với múi giờ UTC+7
		ZoneId zoneId = ZoneId.of("UTC+7");

		// Chuyển đổi thời gian hệ thống sang múi giờ UTC+7
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);

		// Định dạng ngày theo yêu cầu (231128)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
		String formattedDate = zonedDateTime.format(formatter);

		return formattedDate;
	}

	public static String getSystemTimeWithOffset() {
		// Lấy thời gian hệ thống hiện tại
		LocalDateTime localDateTime = LocalDateTime.now();

		// Tạo đối tượng ZoneOffset với độ lệch múi giờ UTC+7
		ZoneOffset zoneOffset = ZoneOffset.ofHours(7);

		// Tạo đối tượng OffsetDateTime từ thời gian hệ thống và độ lệch múi giờ
		OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);

		// Định dạng thời gian theo yêu cầu (2020-01-31T09:59:34.000+07:00)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String formattedTime = offsetDateTime.format(formatter);
		System.out.println("getSystemTimeWithOffset: formattedTime: " + formattedTime);
		return formattedTime;
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

			System.out.println("key: " + encodedKey + " - username: " +
					entity.getUsername() + " - password: "
					+ entity.getPassword());
			String suffixUrl = entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()
					? entity.getSuffixUrl()
					: "";
			UriComponents uriComponents = null;
			WebClient webClient = null;
			Map<String, Object> data = new HashMap<>();
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
			System.out.println("uriComponents: " + uriComponents.getPath());
			Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
					.uri(uriComponents.toUri())
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Basic " + encodedKey)
					.body(BodyInserters.fromValue(data))
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
			data.put("terminalCode", dto.getTerminalCode());
			data.put("urlLink", dto.getUrlLink());
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
			System.out.println("response status code: " + response.statusCode());
			if (response.statusCode().is2xxSuccessful()) {
				String json = response.bodyToMono(String.class).block();
				System.out.println("Response pushNewTransactionToCustomerSync: " + json);
				logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
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
				System.out.println("Response pushNewTransactionToCustomerSync: " + json);
				logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
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

	private ResponseMessageDTO getCustomerSyncEntities(String transReceiveId, TransactionBankDTO dto,
			AccountBankReceiveEntity accountBankEntity,
			long time, String orderId, String sign, String rawTerminalCode, String urlLink, String terminalCode) {
		ResponseMessageDTO result = new ResponseMessageDTO("SUCCESS", "");
		try {
			// 1. Check bankAccountEntity with sync = true (add sync boolean field)
			// 2. Find account_customer_bank by bank_id/bank_account AND auth = true.
			// 3. Find customer_sync and push data to customer.
			if (accountBankEntity.isSync() == true || accountBankEntity.isWpSync() == true) {
				TransactionBankCustomerDTO transactionBankCustomerDTO = new TransactionBankCustomerDTO();
				transactionBankCustomerDTO.setTransactionid(dto.getTransactionid());
				transactionBankCustomerDTO.setTransactiontime(time * 1000);
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
				if (!StringUtil.isNullOrEmpty(rawTerminalCode)) {
					transactionBankCustomerDTO.setTerminalCode(rawTerminalCode);
				} else if (!StringUtil.isNullOrEmpty(terminalCode)) {
					transactionBankCustomerDTO.setTerminalCode(terminalCode);
				} else {
					transactionBankCustomerDTO.setTerminalCode("");
				}
				transactionBankCustomerDTO.setTerminalCode(rawTerminalCode);
				transactionBankCustomerDTO.setUrlLink(urlLink);
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

	private String formatAmountNumber(String amount) {
		String result = amount;
		try {
			if (StringUtil.containsOnlyDigits(amount)) {
				NumberFormat nf = NumberFormat.getInstance(Locale.US);
				Long numberAmount = Long.parseLong(amount);
				result = nf.format(numberAmount);
			}
		} catch (Exception ignored) {}
		return result;
	}

	private String getInvoiceUnique() {
		String result = "";
		boolean checkDuplicated = true;
		String invoiceId = "";
		do {
			result = RandomCodeUtil.generateRandomId(10);
			invoiceId = invoiceService.checkDuplicatedInvoiceId(result);
			if (invoiceId == null || invoiceId.isEmpty()) {
				checkDuplicated = false;
			}
		} while (checkDuplicated);
		return result;
	}

	private void createInvoiceTransWallet(long totalAmount, BankAccountInfoDTO bankInfo,
										  TrAnnualFeeInvoiceItemDTO dto, Double vat) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String invoiceUUIDId = UUID.randomUUID().toString();
			InvoiceEntity invoiceEntity = new InvoiceEntity();
			invoiceEntity.setId(invoiceUUIDId);
			invoiceEntity.setInvoiceId(getInvoiceUnique());
			invoiceEntity.setVat(vat);
			invoiceEntity.setName(NotificationUtil.getNotiPaymentAnnualFeeVietqrName());
			invoiceEntity.setDescription(NotificationUtil.getNotiPaymentAnnualFeeVietqrName());
			invoiceEntity.setMerchantId("");
			invoiceEntity.setStatus(1);
			invoiceEntity.setAmount(dto.getAmount());
			invoiceEntity.setTotalAmount(dto.getAmountAfterVat());
			invoiceEntity.setVatAmount(dto.getAmountVat());
			invoiceEntity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
			invoiceEntity.setTimePaid(DateTimeUtil.getCurrentDateTimeUTC());
			invoiceEntity.setUserId(dto.getUserId());
			invoiceEntity.setBankId(dto.getBankId());
			invoiceEntity.setRefId(dto.getTransWalletId());
			invoiceEntity.setDataType(0);
			invoiceEntity.setData(mapper.writeValueAsString(bankInfo));
			invoiceEntity.setBankIdRecharge(EnvironmentUtil.getBankIdRecharge());
			InvoiceItemEntity itemEntity = new InvoiceItemEntity();
			itemEntity.setId(UUID.randomUUID().toString());
			itemEntity.setInvoiceId(invoiceUUIDId);
			itemEntity.setQuantity(1);
			itemEntity.setAmount(totalAmount);
			itemEntity.setTotalAmount(dto.getAmount());
			itemEntity.setTotalAfterVat(dto.getAmountAfterVat());
			itemEntity.setVatAmount(dto.getAmountVat());
			itemEntity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
			itemEntity.setTimePaid(DateTimeUtil.getCurrentDateTimeUTC()); //
			itemEntity.setName(NotificationUtil.getNotiPaymentBdsdVietqr1());
			itemEntity.setDescription(NotificationUtil.getNotiPaymentBdsdVietqr1() +
					NotificationUtil.getNotiPaymentBdsdVietqr2() +
					bankInfo.getBankAccount() + " - "
					+ bankInfo.getBankShortName());
			itemEntity.setType(0);
			itemEntity.setUnit("");
			itemEntity.setVat(vat);
			itemEntity.setStatus(1);
			itemEntity.setTypeName(NotificationUtil.getNotiPaymentBdsdVietqr1());
			itemEntity.setData(mapper.writeValueAsString(dto));
			itemEntity.setDataType(0);
			itemEntity.setProcessDate("");
			invoiceService.insert(invoiceEntity);
			invoiceItemService.insert(itemEntity);

		} catch (Exception e) {
			logger.error("TransactionBankController: ERROR: createInvoiceTransWallet: "
			+ e.getMessage() + " at: " + System.currentTimeMillis());
		}
	}

	private String validatePhoneNoToken(String token) {
		String result = "";
		try {
			Claims claims = jwtAuthorizationFilter.validateToken(token.replace("Bearer ", ""));
			String phoneNo = claims.get("phoneNo", String.class);
			result = phoneNo;
		} catch (Exception e) {
			logger.error("Invalid token: " + e.toString());
			result = "";
		}
		return result;
	}
}
