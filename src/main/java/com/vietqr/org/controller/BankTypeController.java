package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.AccountBankNameDTO;
import com.vietqr.org.dto.BankTypeCustomerDTO;
import com.vietqr.org.dto.BankTypeDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TokenProductBankDTO;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.CaiBankEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.util.EnvironmentUtil;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BankTypeController {
	private static final Logger logger = Logger.getLogger(BankTypeController.class);

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	ImageService imageService;

	@Autowired
	CaiBankService caiBankService;

	@PostMapping(value = "bank-type", produces = "application/json;charset=UTF-8")
	public ResponseEntity<ResponseMessageDTO> insertBankType(@Valid @RequestParam String bankCode,
			@Valid @RequestParam String bankName, @Valid @RequestParam String bankShortName,
			@Valid @RequestParam String swiftCode,
			@Valid @RequestParam MultipartFile imgUrl) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID uuid = UUID.randomUUID();
			UUID uuidImage = UUID.randomUUID();
			String fileName = StringUtils.cleanPath(imgUrl.getOriginalFilename());
			BankTypeEntity entity = new BankTypeEntity(uuid.toString(), bankCode, bankName, bankShortName,
					uuidImage.toString(), swiftCode, 0, false, 0);
			bankTypeService.insertBankType(entity);
			// insert image
			ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, imgUrl.getBytes());
			imageService.insertImage(imageEntity);
			/// old process, save image to spring boot project
			// FileUploadUtil.saveFile("src/main/webapp/WEB-INF/images", fileName, imgUrl);
			result = new ResponseMessageDTO("SUCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at insertBankType: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("bank-types")
	public ResponseEntity<List<BankTypeCustomerDTO>> getBankTypesForCustomer() {
		List<BankTypeCustomerDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			List<BankTypeEntity> entities = bankTypeService.getBankTypes();
			if (!entities.isEmpty()) {
				for (BankTypeEntity entity : entities) {
					BankTypeCustomerDTO dto = new BankTypeCustomerDTO();
					dto.setBankCode(entity.getBankCode());
					dto.setBankName(entity.getBankName());
					dto.setShortName(entity.getBankShortName());
					String imageName = imageService.getImageNameById(entity.getImgId());
					String imageUrl = "https://api.vietqr.org/vqr/images/" + imageName;
					dto.setImgUrl(imageUrl);
					String caiValue = caiBankService.getCaiValue(entity.getId());
					dto.setBin(caiValue);
					dto.setSwiftCode(entity.getSwiftCode());
					result.add(dto);
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBankTypes: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// find bank account information for customer
	@GetMapping("account-bank/search")
	private ResponseEntity<Object> searchUserBankName(
			@RequestParam(value = "bin") String bin,
			@RequestParam(value = "accountNumber") String accountNumber,
			@RequestHeader("Authorization") String token) {
		Object result = null;
		HttpStatus httpStatus = null;
		try {
			// Get bank token
			TokenProductBankDTO mbBankToken = getMBBankToken();
			//
			if (bin != null && !bin.trim().isEmpty()) {
				String transferType;
				String accountType = "ACCOUNT";
				if (bin.trim().equals("970422")) {
					transferType = "INHOUSE";
				} else {
					transferType = "NAPAS";
				}
				//
				if (mbBankToken != null) {
					// Build URL with PathVariable
					UriComponents uriComponents = UriComponentsBuilder
							.fromHttpUrl(
									EnvironmentUtil.getBankUrl() + "ms/bank-info/v1.0/account/info")
							.buildAndExpand(accountNumber);

					// Create WebClient with authorization header
					WebClient webClient = WebClient.builder()
							.baseUrl(uriComponents.toUriString())
							.defaultHeader("Authorization", "Bearer " + mbBankToken.getAccess_token())
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
						logger.error("searchUserBankName: status code: " + response.statusCode());
						logger.error("searchUserBankName: ERROR: response: " + json);
						result = new ResponseMessageDTO("FAILED", "E32");
						httpStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					logger.error("searchUserBankName: ERROR: TOKEN MB BANK NULL");
					result = new ResponseMessageDTO("FAILED", "E05");
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				result = new ResponseMessageDTO("FAILED", "E33");
				httpStatus = HttpStatus.BAD_REQUEST;
			}

		} catch (Exception e) {
			System.out.println("searchUserBankName: ERROR:: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "E05");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("bank-type")
	public ResponseEntity<List<BankTypeDTO>> getBankTypes() {
		List<BankTypeDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			List<BankTypeEntity> entities = bankTypeService.getBankTypes();
			if (!entities.isEmpty()) {
				for (BankTypeEntity entity : entities) {
					BankTypeDTO dto = new BankTypeDTO();
					dto.setId(entity.getId());
					dto.setBankCode(entity.getBankCode());
					dto.setBankName(entity.getBankName());
					dto.setBankShortName(entity.getBankShortName());
					dto.setImageId(entity.getImgId());
					dto.setStatus(entity.getStatus());
					String caiValue = caiBankService.getCaiValue(dto.getId());
					dto.setCaiValue(caiValue);
					dto.setUnlinkedType(entity.getUnlinkedType());
					result.add(dto);
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBankTypes: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// bank type in login screens
	@GetMapping("bank-type/unauthenticated")
	public ResponseEntity<List<BankTypeDTO>> getBankTypesUnauthenticated() {
		List<BankTypeDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			List<BankTypeEntity> entities = bankTypeService.getBankTypes();
			if (!entities.isEmpty()) {
				for (BankTypeEntity entity : entities) {
					BankTypeDTO dto = new BankTypeDTO();
					dto.setId("");
					dto.setBankCode(entity.getBankCode());
					dto.setBankName(entity.getBankName());
					dto.setBankShortName(entity.getBankShortName());
					dto.setImageId(entity.getImgId());
					dto.setStatus(0);
					dto.setCaiValue("");
					dto.setUnlinkedType(entity.getUnlinkedType());
					result.add(dto);
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBankTypes: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("bank-type/cai/{caiValue}")
	public ResponseEntity<BankTypeDTO> getBankTypeByCaiValue(@PathVariable("caiValue") String caiValue) {
		BankTypeDTO result = null;
		HttpStatus httpStatus = null;
		try {
			CaiBankEntity caiBankEntity = caiBankService.getCaiBankByCaiValue(caiValue);
			if (caiBankEntity != null) {
				BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(caiBankEntity.getBankTypeId());
				if (bankTypeEntity != null) {
					BankTypeDTO dto = new BankTypeDTO();
					dto.setId(bankTypeEntity.getId());
					dto.setBankCode(bankTypeEntity.getBankCode());
					dto.setBankName(bankTypeEntity.getBankName());
					dto.setBankShortName(bankTypeEntity.getBankShortName());
					dto.setImageId(bankTypeEntity.getImgId());
					dto.setStatus(bankTypeEntity.getStatus());
					dto.setCaiValue(caiValue);
					dto.setUnlinkedType(bankTypeEntity.getUnlinkedType());
					result = dto;
					httpStatus = HttpStatus.OK;
				} else {
					httpStatus = HttpStatus.BAD_REQUEST;
				}
			} else {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
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
}