package com.vietqr.org.util.bank.mb;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
import com.vietqr.org.util.EnvironmentUtil;

import reactor.core.publisher.Mono;

public class MBVietQRUtil {
    private static final Logger logger = Logger.getLogger(MBVietQRUtil.class);

    public static String generateStaticVietQRMMS(VietQRStaticMMSRequestDTO dto) {
        String result = "";
        //
        LocalDateTime requestLDT = LocalDateTime.now();
        long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
        logger.info("MBVietQRUtil: generateStaticVietQRMMS: start request QR to MB at: " + requestTime);
        try {
            UUID clientMessageId = UUID.randomUUID();
            Map<String, Object> data = new HashMap<>();
            data.put("terminalID", dto.getTerminalId());
            data.put("qrcodeType", EnvironmentUtil.getMbQrCodeStaticType());
            data.put("partnerType", 2);
            data.put("initMethod", EnvironmentUtil.getMbQrInitStaticMethod());
            data.put("transactionAmount", "");
            data.put("billNumber", "");
            data.put("additionalAddress", 0);
            data.put("additionalMobile", 0);
            data.put("additionalEmail", 0);
            data.put("referenceLabelCode", "");
            data.put("transactionPurpose", dto.getContent());
            //
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
                logger.info("MBVietQRUtil: generateStaticVietQRMMS: RESPONSE: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("data") != null) {
                    if (rootNode.get("data").get("qrcode") != null) {
                        result = rootNode.get("data").get("qrcode").asText();
                        logger.info("MBVietQRUtil: generateStaticVietQRMMS: RESPONSE qrcode: " + result);
                    } else {
                        logger.info("MBVietQRUtil: generateStaticVietQRMMS: RESPONSE qrcode is null");
                    }
                } else {
                    logger.info("MBVietQRUtil: generateStaticVietQRMMS: RESPONSE data is null");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("MBVietQRUtil: generateStaticVietQRMMS: RESPONSE: " + response.statusCode().value() + " - "
                        + json);
            }
            //
        } catch (Exception e) {
            logger.error("MBVietQRUtil: generateStaticVietQRMMS: ERROR: " + e.toString());
        }
        return result;
    }

    public static String getTraceTransfer(String qrCode) {
        String result = "";
        try {
            if (qrCode != null) {
                if (qrCode.trim().contains("0010A000000727")) {
                    String valueBankTransfer = qrCode.split("0010A000000727")[1];
                    // 0208QRIBFTTA = quick transfer NAPAS
                    String valueBankInfo = valueBankTransfer.split("0208QRIBFTTA")[0];
                    // 970422 = MB Bank
                    String valueBankAccount = valueBankInfo.split("0006970422")[1];
                    String bankAccountEncrypted = valueBankAccount.substring(4);
                    String traceTransfer = bankAccountEncrypted.substring(3);
                    result = traceTransfer;
                }
            }
        } catch (Exception e) {
            logger.error("MBVietQRUtil: getTraceTransfer: ERROR: " + e.toString());
        }
        return result;
    }

}
