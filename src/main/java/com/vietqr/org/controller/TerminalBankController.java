// package com.vietqr.org.controller;

// import java.util.Base64;

// import org.apache.log4j.Logger;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.reactive.function.BodyInserters;
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.util.UriComponents;
// import org.springframework.web.util.UriComponentsBuilder;

// import com.vietqr.org.dto.TokenProductBankDTO;
// import com.vietqr.org.service.TerminalBankService;
// import com.vietqr.org.util.EnvironmentUtil;

// @RestController
// @CrossOrigin
// @RequestMapping("bank/api")
// public class TerminalBankController {
// private static final Logger logger =
// Logger.getLogger(TransactionMMSController.class);

// @Autowired
// TerminalBankService terminalBankService;

// // create/update

// // sync list

// // get token
// // private TokenProductBankDTO getBankToken() {
// // TokenProductBankDTO result = null;
// // try {
// // String key = EnvironmentUtil.getUserBankMMSAccess() + ":" +
// // EnvironmentUtil.getPasswordBankMMSAccess();
// // String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
// // UriComponents uriComponents = UriComponentsBuilder
// // .fromHttpUrl(EnvironmentUtil.getBankUrl() + "oauth2/v1/token")
// // .buildAndExpand(/* add url parameter here */);
// // WebClient webClient = WebClient.builder()
// // .baseUrl(EnvironmentUtil.getBankUrl()
// // + "oauth2/v1/token")
// // .build();
// // // Call POST API
// // TokenProductBankDTO response = webClient.method(HttpMethod.POST)
// // .uri(uriComponents.toUri())
// // .contentType(MediaType.APPLICATION_FORM_URLENCODED)
// // .header("Authorization", "Basic " + encodedKey)
// // .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
// // .exchange()
// // .flatMap(clientResponse -> {
// // if (clientResponse.statusCode().is2xxSuccessful()) {
// // return clientResponse.bodyToMono(TokenProductBankDTO.class);
// // } else {
// // clientResponse.body((clientHttpResponse, context) -> {
// //
// logger.info(clientHttpResponse.getBody().collectList().block().toString());
// // return clientHttpResponse.getBody();
// // });
// // return null;
// // }
// // })
// // .block();
// // result = response;
// // } catch (Exception e) {
// // logger.error(e.toString());
// // }
// // return result;
// // }
// }
