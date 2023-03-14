package com.vietqr.org.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.dto.AccountInformationDTO;
import com.vietqr.org.dto.AccountLoginDTO;
import com.vietqr.org.dto.FcmRequestDTO;
import com.vietqr.org.dto.LogoutDTO;
import com.vietqr.org.dto.PasswordUpdateDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.AccountSearchDTO;
import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.service.AccountInformationService;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.util.NotificationUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api")
public class AccountController {
	private static final Logger logger = Logger.getLogger(AccountController.class);

	@Autowired
	AccountLoginService accountLoginService;

	@Autowired
	AccountInformationService accountInformationService;

	@Autowired
	ImageService imageService;

	@Autowired
	FcmTokenService fcmTokenService;

	@Autowired
	NotificationService notificationService;

	private FirebaseMessagingService firebaseMessagingService;

	public AccountController(FirebaseMessagingService firebaseMessagingService) {
		this.firebaseMessagingService = firebaseMessagingService;
	}

	@PostMapping("accounts")
	public ResponseEntity<String> login(@Valid @RequestBody AccountLoginDTO dto) {
		String result = "";
		HttpStatus httpStatus = null;
		try {
			if (dto.getPhoneNo() != null && !dto.getPhoneNo().isEmpty()) {
				String userId = accountLoginService.login(dto.getPhoneNo(), dto.getPassword());
				if (userId != null && !userId.isEmpty()) {
					// get user information
					AccountInformationEntity accountInformationEntity = accountInformationService
							.getAccountInformation(userId);
					// push notification to other devices if user logged in before
					LocalDateTime currentDateTime = LocalDateTime.now();
					List<FcmTokenEntity> fcmTokens = new ArrayList<>();
					fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
					if (fcmTokens != null && !fcmTokens.isEmpty()) {
						System.out.println("FCM token != null" + fcmTokens.size());
						// insert new Notification
						String messageNotification = NotificationUtil.getNotiDescLoginWarningPrefix()
								+ dto.getPlatform() + " " + dto.getDevice();
						UUID notificationUuid = UUID.randomUUID();
						NotificationEntity notificationEntity = new NotificationEntity();
						notificationEntity.setId(notificationUuid.toString());
						notificationEntity.setRead(false);
						notificationEntity.setMessage(messageNotification);
						notificationEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
						notificationEntity.setType(NotificationUtil.getNotiTypeLogin());
						notificationEntity.setUserId(userId);
						notificationService.insertNotification(notificationEntity);
						// push notification to devices
						for (FcmTokenEntity fcmToken : fcmTokens) {
							try {
								FcmRequestDTO fcmDTO = new FcmRequestDTO();
								fcmDTO.setTitle(NotificationUtil.getNotiTitleLoginWarning());
								fcmDTO.setMessage(messageNotification);
								fcmDTO.setToken(fcmToken.getToken());
								firebaseMessagingService.sendPushNotificationToToken(fcmDTO);
								logger.info("Send notification to device " + fcmToken.getToken());
							} catch (Exception e) {
								System.out.println("Error at send noti" + e.toString());
								logger.error("Error when Send Notification using FCM " + e.toString());
								if (e.toString()
										.contains("The registration token is not a valid FCM registration token")) {
									fcmTokenService.deleteFcmToken(fcmToken.getToken());
								}

							}
						}
					}
					// insert new FCM token
					FcmTokenEntity fcmTokenEntity = new FcmTokenEntity();
					UUID uuid = UUID.randomUUID();
					fcmTokenEntity.setId(uuid.toString());
					fcmTokenEntity.setToken(dto.getFcmToken());
					fcmTokenEntity.setUserId(userId);
					fcmTokenEntity.setPlatform(dto.getPlatform());
					fcmTokenEntity.setDevice(dto.getDevice());
					fcmTokenService.insertFcmToken(fcmTokenEntity);
					// response login success
					result = getJWTToken(accountInformationEntity);
					httpStatus = HttpStatus.OK;
				} else {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			System.out.println("Error at login: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("accounts/logout")
	public ResponseEntity<ResponseMessageDTO> logout(@Valid @RequestBody LogoutDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			System.out.println("FCM TOKEN: " + dto.getFcmToken());
			fcmTokenService.deleteFcmToken(dto.getFcmToken());
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at logout: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("accounts/search/{phoneNo}")
	public ResponseEntity<Object> searchAccount(@Valid @PathVariable("phoneNo") String phoneNo) {
		Object result = null;
		HttpStatus httpStatus = null;
		try {
			AccountSearchDTO dto = accountInformationService.getAccountSearch(phoneNo);
			if (dto == null) {
				result = new ResponseMessageDTO("CHECK", "C01");
				httpStatus = HttpStatus.valueOf(201);
			} else {
				result = dto;
				httpStatus = HttpStatus.OK;
			}

		} catch (Exception e) {
			System.out.println("Error at searchAccount: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Object>(result, httpStatus);
	}

	@PostMapping("accounts/register")
	public ResponseEntity<ResponseMessageDTO> registerAccount(@Valid @RequestBody AccountLoginDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			String checkExistedPhoneNo = accountLoginService.checkExistedPhoneNo(dto.getPhoneNo());
			if (checkExistedPhoneNo == null || checkExistedPhoneNo.isEmpty()) {
				UUID uuid = UUID.randomUUID();
				UUID accountInformationUUID = UUID.randomUUID();
				// insert account_login
				AccountLoginEntity accountLoginEntity = new AccountLoginEntity();
				accountLoginEntity.setId(uuid.toString());
				accountLoginEntity.setPhoneNo(dto.getPhoneNo());
				accountLoginEntity.setPassword(dto.getPassword());
				accountLoginService.insertAccountLogin(accountLoginEntity);
				// insert account_information
				AccountInformationEntity accountInformationEntity = new AccountInformationEntity();
				accountInformationEntity.setId(accountInformationUUID.toString());
				accountInformationEntity.setUserId(uuid.toString());
				accountInformationEntity.setAddress("");
				accountInformationEntity.setBirthDate("01/01/1970");
				accountInformationEntity.setEmail("");
				accountInformationEntity.setFirstName("Undefined");
				accountInformationEntity.setMiddleName("");
				accountInformationEntity.setLastName("");
				accountInformationEntity.setGender(0);
				accountInformationEntity.setImgId("");
				int check = accountInformationService.insertAccountInformation(accountInformationEntity);
				if (check == 1) {
					result = new ResponseMessageDTO("SUCCESS", "");
					httpStatus = HttpStatus.OK;
				} else {
					result = new ResponseMessageDTO("FAILED", "E03");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				result = new ResponseMessageDTO("FAILED", "E02");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			System.out.println("Error at registerAccount: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E04");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PutMapping("user/password")
	public ResponseEntity<ResponseMessageDTO> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// check old password
			// if valid, update password
			String passwordCheck = accountLoginService.checkOldPassword(dto.getUserId(), dto.getOldPassword());
			if (passwordCheck != null && !passwordCheck.isEmpty()) {
				if (passwordCheck.equals(dto.getOldPassword())) {
					accountLoginService.updatePassword(dto.getNewPassword(), dto.getUserId());
					result = new ResponseMessageDTO("SUCCESS", "");
					httpStatus = HttpStatus.OK;
				}
			} else {
				result = new ResponseMessageDTO("FAIELD", "Old Password is not match.");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			System.out.println("Error at updatePassword: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Cannot update password");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PutMapping("user/information")
	public ResponseEntity<ResponseMessageDTO> updateInformation(@Valid @RequestBody AccountInformationDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			accountInformationService.updateAccountInformation(dto.getFirstName(), dto.getMiddleName(),
					dto.getLastName(), dto.getBirthDate(), dto.getAddress(), dto.getGender(), dto.getEmail(),
					dto.getUserId());
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at updateInformation: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Cannot update information");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PutMapping("user/image")
	public ResponseEntity<ResponseMessageDTO> updateImage(@Valid @RequestParam String imgId,
			@Valid @RequestParam MultipartFile image, @Valid @RequestParam String userId) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			if (imgId.isEmpty()) {
				UUID uuidImage = UUID.randomUUID();
				String fileName = StringUtils.cleanPath(image.getOriginalFilename());
				ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, image.getBytes());
				imageService.insertImage(imageEntity);
				accountInformationService.updateImageId(uuidImage.toString(), userId);
				result = new ResponseMessageDTO("SUCCESS", uuidImage.toString());
				httpStatus = HttpStatus.OK;
			} else {
				String fileName = StringUtils.cleanPath(image.getOriginalFilename());
				imageService.updateImage(image.getBytes(), fileName, imgId);
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
			System.out.println("Error at updateImage: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Cannot update information");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private String getJWTToken(AccountInformationEntity entity) {
		String result = "";
		String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");
		result = Jwts
				.builder()
				.claim("userId", entity.getUserId())
				.claim("firstName", entity.getFirstName())
				.claim("middleName", entity.getMiddleName())
				.claim("lastName", entity.getLastName())
				.claim("birthDate", entity.getBirthDate())
				.claim("gender", entity.getGender())
				.claim("address", entity.getAddress())
				.claim("email", entity.getEmail())
				.claim("imgId", entity.getImgId())
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 900000000))
				.signWith(SignatureAlgorithm.HS512,
						secretKey.getBytes())
				.compact();
		return result;
	}

}
