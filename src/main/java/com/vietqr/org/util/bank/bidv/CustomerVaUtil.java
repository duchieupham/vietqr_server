package com.vietqr.org.util.bank.bidv;

import java.security.Key;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.AesKey;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.ResponseObjectDTO;
import com.vietqr.org.dto.bidv.ConfirmCustomerVaDTO;
import com.vietqr.org.dto.bidv.RequestCustomerVaDTO;
import com.vietqr.org.dto.bidv.ResponseReqCustomerVaDTO;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.dto.example.Header;
import com.vietqr.org.dto.example.JweObj;
import com.vietqr.org.dto.example.Recipients;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.JwsUtil;

import reactor.core.publisher.Mono;

public class CustomerVaUtil {
    private static final Logger logger = Logger.getLogger(CustomerVaUtil.class);

    public static Object requestCustomerVa(RequestCustomerVaDTO dto, String merchantId, String merchantType,
            Long customerVaLength, String checkExistedMerchantId) {
        Object result = null;
        try {
            //
            // initial
            UUID interactionId = UUID.randomUUID();
            UUID idemKey = UUID.randomUUID();
            String url = EnvironmentUtil.getBidvUrlRequestAddMerchant();
            String serviceId = EnvironmentUtil.getBidvLinkedServiceId();

            // String merchantName = EnvironmentUtil.getBidvLinkedMerchantName();
            String merchantName = dto.getMerchantName();
            String channelId = EnvironmentUtil.getBidvLinkedChannelId();
            String tranDate = DateTimeUtil.getBidvTranDate();
            //
            // jws and jwe request body
            String myKey = JwsUtil.getSymmatricKey();
            logger.info("myKey: " + myKey);
            Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
            logger.info("new AesKey(JwsUtil.hexStringToBytes(myKey): " + key);
            JsonWebEncryption jwe = new JsonWebEncryption();
            String payload = BIDVUtil.generateRequestVaBody(serviceId, channelId, merchantId, merchantName, dto,
                    merchantType, tranDate);
            logger.info("requestCustomerVa: Payload: " + payload);
            System.out.println("Payload: " + payload);
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
            // requestCustomerVa
            logger.info("\n\nrequestCustomerVa: JWE: " + jweString);
            Map<String, Object> body = gson.fromJson(jweString, Map.class);
            // JWS
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(jweString);
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            PrivateKey privateKey = JwsUtil.getPrivateKey();
            jws.setKey(privateKey);
            String jwsString = jws.getCompactSerialization();
            System.out.println("\n\nrequestCustomerVa: JWS: " + jwsString);
            logger.info("\n\nrequestCustomerVa: JWS: " + jwsString);
            //
            // call API
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .build();
            String token = BIDVTokenUtil.getBIDVToken("ewallet").getAccess_token();
            String clientXCertification = JwsUtil.getClientXCertificate();
            System.out.println("\n\nToken BIDV: " + token);
            logger.info("\n\nrequestCustomerVa: Token BIDV: " + token);
            System.out.println("\n\nclientXCertification BIDV: " + clientXCertification);
            logger.info("\n\nrequestCustomerVa: clientXCertification BIDV: " + clientXCertification);
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Channel", EnvironmentUtil.getBidvLinkedChannelId())
                    .header("User-Agent", EnvironmentUtil.getBidvLinkedMerchantName())
                    .header("X-Client-Certificate", clientXCertification)
                    .header("X-API-Interaction-ID", interactionId.toString())
                    .header("X-Idempotency-Key", idemKey.toString())
                    .header("TimeStamp", BIDVDateUtil.getSystemTimeWithOffset())
                    .header("X-Customer-IP-Address", EnvironmentUtil.getIpVietQRVN())
                    .header("Authorization", "Bearer " + token)
                    .header("X-JWS-Signature", jwsString)
                    .body(BodyInserters.fromValue(body))
                    .exchange();
            System.out.println("\n\n");
            logger.info("requestCustomerVa: X-Client-Certificate: " + clientXCertification +
                    " X-API-Interaction-ID: " + interactionId.toString() + " X-Idempotency-Key: " +
                    idemKey.toString());
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response requestCustomerVa: " + json);
                System.out.println("Response requestCustomerVa: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                //
                if (rootNode.get("errorCode") != null) {
                    String errorCode = rootNode.get("errorCode").asText();
                    if (errorCode != null) {
                        if (errorCode.trim().equals("000")) {
                            ResponseReqCustomerVaDTO data = new ResponseReqCustomerVaDTO(merchantId,
                                    interactionId.toString(), merchantName);
                            result = new ResponseObjectDTO("SUCCESS", data);
                        } else if (errorCode.trim().equals("182")) {
                            // check merchantId đã tồn tại trong hệ thống VietQR hay chưa, xong nếu ko tồn
                            // tại mới gọi unregister
                            if (checkExistedMerchantId == null || checkExistedMerchantId.trim().isEmpty()) {
                                ResponseMessageDTO unregisterResult = unregisterCustomerVa(merchantId);
                                if (unregisterResult.getStatus().trim().equals("SUCCESS")) {
                                    result = requestCustomerVa(dto, merchantId, merchantType, customerVaLength,
                                            checkExistedMerchantId);
                                } else {
                                    result = new ResponseMessageDTO("FAILED", "E112");
                                }
                            } else {
                                result = new ResponseMessageDTO("FAILED", "E112");
                            }

                        } else if (errorCode.trim().equals("033")) {
                            result = new ResponseMessageDTO("FAILED", "E133");
                        } else if (errorCode.trim().equals("036")) {
                            result = new ResponseMessageDTO("FAILED", "E139");
                        } else if (errorCode.trim().equals("040")) {
                            result = new ResponseMessageDTO("FAILED", "E134");
                        } else if (errorCode.trim().equals("021")) {
                            result = new ResponseMessageDTO("FAILED", "E136");
                        } else if (errorCode.trim().equals("022")) {
                            result = new ResponseMessageDTO("FAILED", "E137");
                        } else if (!errorCode.trim().isEmpty()) {
                            result = new ResponseMessageDTO("ERROR", errorCode);
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E05");
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E05");
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response requestCustomerVa: " + json);
                System.out.println("Response requestCustomerVa: " + json);
                result = new ResponseMessageDTO("FAILED", "E05");
            }
            //
        } catch (Exception e) {
            logger.error("CustomerVaUtil: requestCustomerVa: ERROR: " + e.toString());
            System.out.println("CustomerVaUtil: requestCustomerVa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    // private static Object requestCustomerVaBIDV(RequestCustomerVaDTO dto, String
    // merchantType,
    // Long customerVaLength) {
    // Object result = null;
    // try {
    // //
    // } catch (Exception e) {
    // logger.error("CustomerVaUtil: requestCustomerVaBIDV: ERROR: " +
    // e.toString());
    // System.out.println("CustomerVaUtil: requestCustomerVaBIDV: ERROR: " +
    // e.toString());
    // result = new ResponseMessageDTO("FAILED", "E05");
    // }
    // return result;
    // }

    public static ResponseMessageDTO confirmCustomerVa(ConfirmCustomerVaDTO dto) {
        ResponseMessageDTO result = null;
        try {
            String url = EnvironmentUtil.getBidvUrlConfirmAddMerchant();
            String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
            String channelId = EnvironmentUtil.getBidvLinkedChannelId();
            //
            UUID interactionId = UUID.randomUUID();
            UUID idemKey = UUID.randomUUID();
            //
            // jws and jwe request body
            String myKey = JwsUtil.getSymmatricKey();
            Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
            JsonWebEncryption jwe = new JsonWebEncryption();
            String payload = BIDVUtil.generateConfirmVaBody(serviceId, channelId, dto);
            System.out.println("Payload: " + payload);
            logger.info("confirmCustomerVa: Payload: " + payload);
            //
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
            logger.info("confirmCustomerVa: JWE: " + jweString);
            Map<String, Object> body = gson.fromJson(payload, Map.class);
            // JWS
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(jweString);
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            PrivateKey privateKey = JwsUtil.getPrivateKey();
            jws.setKey(privateKey);
            String jwsString = jws.getCompactSerialization();
            System.out.println("\n\nJWS: " + jwsString);
            logger.info("confirmCustomerVa: JWS: " + jwsString);
            //
            // call API
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .build();
            String token = BIDVTokenUtil.getBIDVToken("ewallet").getAccess_token();
            String clientXCertification = JwsUtil.getClientXCertificate();
            System.out.println("\n\nToken BIDV: " + token);
            System.out.println("\n\nclientXCertification BIDV: " + clientXCertification);
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Channel", EnvironmentUtil.getBidvLinkedChannelId())
                    .header("User-Agent", EnvironmentUtil.getBidvLinkedMerchantName())
                    .header("X-Client-Certificate", clientXCertification)
                    .header("X-API-Interaction-ID", interactionId.toString())
                    .header("X-Idempotency-Key", idemKey.toString())
                    .header("TimeStamp", BIDVDateUtil.getSystemTimeWithOffset())
                    .header("X-Customer-IP-Address", EnvironmentUtil.getIpVietQRVN())
                    .header("Authorization", "Bearer " + token)
                    .header("X-JWS-Signature", jwsString)
                    .body(BodyInserters.fromValue(body))
                    .exchange();
            System.out.println("\n\n");
            logger.info("confirmCustomerVa: X-Client-Certificate: " + clientXCertification +
                    " X-API-Interaction-ID: " + interactionId.toString() + " X-Idempotency-Key: " +
                    idemKey.toString());
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response confirmCustomerVa: " + json);
                System.out.println("Response confirmCustomerVa: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                //
                if (rootNode.get("errorCode") != null) {
                    String errorCode = rootNode.get("errorCode").asText();
                    if (errorCode != null) {
                        if (errorCode.trim().equals("000")) {
                            String vaNumber = rootNode.get("vaNumber").asText();
                            result = new ResponseMessageDTO("SUCCESS", vaNumber);
                            // ResponseReqCustomerVaDTO data = new ResponseReqCustomerVaDTO(merchantId,
                            // interactionId.toString());
                            // result = new ResponseObjectDTO("SUCCESS", data);
                            // } else if (errorCode.trim().equals("182")) {
                            // result = new ResponseMessageDTO("FAILED", "E112");
                        } else if (errorCode.trim().equals("004") || errorCode.trim().equals("005")) {
                            result = new ResponseMessageDTO("FAILED", "E135");
                        } else if (errorCode.trim().equals("061")) {
                            result = new ResponseMessageDTO("FAILED", "E138");
                        } else if (!errorCode.trim().isEmpty()) {
                            result = new ResponseMessageDTO("ERROR", errorCode);
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E05");
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E05");
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response confirmCustomerVa: " + json);
                System.out.println("Response confirmCustomerVa: " + json);
                result = new ResponseMessageDTO("FAILED", "E05");
            }
        } catch (Exception e) {
            logger.error("CustomerVaUtil: confirmCustomerVa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    public static ResponseMessageDTO unregisterCustomerVa(String merchantId) {
        ResponseMessageDTO result = null;
        try {
            String url = EnvironmentUtil.getBidvUrlUnregisterMerchant();
            String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
            String channelId = EnvironmentUtil.getBidvLinkedChannelId();
            String tranDate = DateTimeUtil.getBidvTranDate();
            //
            UUID interactionId = UUID.randomUUID();
            UUID idemKey = UUID.randomUUID();
            //
            // jws and jwe request body
            String myKey = JwsUtil.getSymmatricKey();
            Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
            JsonWebEncryption jwe = new JsonWebEncryption();
            String payload = BIDVUtil.generateUnregisterVaBody(serviceId, channelId, merchantId, tranDate);
            System.out.println("Payload: " + payload);
            logger.info("unregisterCustomerVa: Payload: " + payload);
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
            logger.info("unregisterCustomerVa: JWE: " + jweString);
            Map<String, Object> body = gson.fromJson(payload, Map.class);
            // JWS
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(jweString);
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            PrivateKey privateKey = JwsUtil.getPrivateKey();
            jws.setKey(privateKey);
            String jwsString = jws.getCompactSerialization();
            System.out.println("\n\nJWS: " + jwsString);
            logger.info("unregisterCustomerVa: JWS: " + jwsString);
            //
            // call API
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .build();
            String token = BIDVTokenUtil.getBIDVToken("ewallet").getAccess_token();
            String clientXCertification = JwsUtil.getClientXCertificate();
            System.out.println("\n\nToken BIDV: " + token);
            System.out.println("\n\nclientXCertification BIDV: " + clientXCertification);
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Channel", EnvironmentUtil.getBidvLinkedChannelId())
                    .header("User-Agent", EnvironmentUtil.getBidvLinkedMerchantName())
                    .header("X-Client-Certificate", clientXCertification)
                    .header("X-API-Interaction-ID", interactionId.toString())
                    .header("X-Idempotency-Key", idemKey.toString())
                    .header("TimeStamp", BIDVDateUtil.getSystemTimeWithOffset())
                    .header("X-Customer-IP-Address", EnvironmentUtil.getIpVietQRVN())
                    .header("Authorization", "Bearer " + token)
                    .header("X-JWS-Signature", jwsString)
                    .body(BodyInserters.fromValue(body))
                    .exchange();
            logger.info("unregisterCustomerVa: X-Client-Certificate: " + clientXCertification +
                    " X-API-Interaction-ID: " + interactionId.toString() + " X-Idempotency-Key: " +
                    idemKey.toString());
            System.out.println("\n\n");
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response unregisterCustomerVa: " + json);
                System.out.println("Response unregisterCustomerVa: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                //
                if (rootNode.get("errorCode") != null) {
                    String errorCode = rootNode.get("errorCode").asText();
                    if (errorCode != null) {
                        if (errorCode.trim().equals("000")) {
                            result = new ResponseMessageDTO("SUCCESS", "");
                            // ResponseReqCustomerVaDTO data = new ResponseReqCustomerVaDTO(merchantId,
                            // interactionId.toString());
                            // result = new ResponseObjectDTO("SUCCESS", data);
                            // } else if (errorCode.trim().equals("182")) {
                            // result = new ResponseMessageDTO("FAILED", "E112");
                        } else if (!errorCode.trim().isEmpty()) {
                            result = new ResponseMessageDTO("ERROR", errorCode);
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E05");
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E05");
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response unregisterCustomerVa: " + json);
                System.out.println("Response unregisterCustomerVa: " + json);
                result = new ResponseMessageDTO("FAILED", "E05");
            }
        } catch (Exception e) {
            logger.error("CustomerVaUtil: unregisterCustomerVa: " + e.toString());
            System.out.println("CustomerVaUtil: unregisterCustomerVa: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    // create VietQR
    public static ResponseMessageDTO generateVaInvoiceVietQR(VietQRVaRequestDTO dto, String customerId) {
        ResponseMessageDTO result = null;
        try {
            String url = EnvironmentUtil.getBidvUrlCreateVietQRVa();
            String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
            //
            UUID interactionId = UUID.randomUUID();
            UUID idemKey = UUID.randomUUID();
            //
            // jws and jwe request body
            String myKey = JwsUtil.getSymmatricKey();
            Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
            JsonWebEncryption jwe = new JsonWebEncryption();
            String payload = BIDVUtil.generateVietQRBody(serviceId, customerId, dto.getUserBankName(),
                    dto.getAmount(), dto.getDescription());
            System.out.println("Payload: " + payload);
            logger.info("generateVaInvoiceVietQR: payload: " + payload);
            //
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
            logger.info("generateVaInvoiceVietQR: JWE: " + jweString);
            Map<String, Object> body = gson.fromJson(jweString, Map.class);
            // JWS
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(jweString);
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            PrivateKey privateKey = JwsUtil.getPrivateKey();
            jws.setKey(privateKey);
            String jwsString = jws.getCompactSerialization();
            System.out.println("\n\nJWS: " + jwsString);
            logger.info("generateVaInvoiceVietQR: JWS: " + jwsString);
            //
            // call API
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(url)
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .build();
            String token = BIDVTokenUtil.getBIDVToken("ewallet").getAccess_token();
            String clientXCertification = JwsUtil.getClientXCertificate().trim();
            System.out.println("\n\nToken BIDV: " + token);
            System.out.println("\n\nclientXCertification BIDV: " + clientXCertification);
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Channel", EnvironmentUtil.getBidvLinkedChannelId())
                    // .header("User-Agent", EnvironmentUtil.getBidvLinkedMerchantName())
                    .header("X-Client-Certificate", clientXCertification)
                    .header("X-API-Interaction-ID", interactionId.toString())
                    // .header("X-API-Interaction-ID", "123456789112")
                    .header("X-Idempotency-Key", idemKey.toString())
                    // .header("X-Idempotency-Key", "abcd1234454566")
                    .header("Timestamp", BIDVDateUtil.getSystemTimeWithOffset())
                    // .header("Timestamp", "2018-06-01T21:14:00.974Z")
                    .header("X-Customer-IP-Address", EnvironmentUtil.getIpVietQRVN())
                    .header("Authorization", "Bearer " + token)
                    .header("X-JWS-Signature", jwsString)
                    .body(BodyInserters.fromValue(
                            body))
                    .exchange();
            System.out.println("\n\n");
            logger.info("generateVaInvoiceVietQR: X-Client-Certificate: " + clientXCertification +
                    " X-API-Interaction-ID: " + interactionId.toString() + " X-Idempotency-Key: " +
                    idemKey.toString());
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response generateVaInvoiceVietQR: " + json);
                System.out.println("Response generateVaInvoiceVietQR: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                //

                if (rootNode.get("errorCode") != null) {
                    String errorCode = rootNode.get("errorCode").asText();
                    if (errorCode != null) {
                        String qrCode = "";

                        if (rootNode.get("vietQR") != null) {
                            qrCode = rootNode.get("vietQR").asText();
                            result = new ResponseMessageDTO("SUCCESS", qrCode);
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E05");
                        }
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E05");
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response generateVaInvoiceVietQR: " + json);
                System.out.println("Response generateVaInvoiceVietQR: " + json);
                result = new ResponseMessageDTO("FAILED", "E05");
            }
        } catch (Exception e) {
            logger.error("CustomerVaUtil: generateVaInvoiceVietQR: " + e.toString());
            System.out.println("CustomerVaUtil: generateVaInvoiceVietQR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    public static String generateMerchantId(String merchantName, Long customerVaLength) {
        String result = "";
        try {
            String prefix = "BC";
            result = prefix + merchantName.substring(0, 2).toUpperCase()
                    + String.format("%05d", customerVaLength);
        } catch (Exception e) {
            System.out.println("generateMerchantId: ERROR: " + e.toString());
            logger.error("generateMerchantId: ERROR: " + e.toString());
        }
        return result;
    }
}
