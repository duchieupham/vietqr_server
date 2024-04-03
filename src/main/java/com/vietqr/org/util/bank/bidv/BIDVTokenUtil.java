package com.vietqr.org.util.bank.bidv;

import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.vietqr.org.dto.TokenBankBIDVDTO;
import com.vietqr.org.util.EnvironmentUtil;

public class BIDVTokenUtil {
    private static final Logger logger = Logger.getLogger(BIDVTokenUtil.class);

    public static TokenBankBIDVDTO getBIDVToken(String scope) {
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
            logger.info("BIDVTokenUtil: getBIDVToken: client_id: " + EnvironmentUtil.getBidvGetTokenClientId()
            + " client_secret: " + EnvironmentUtil.getBidvGetTokenClientSecret());
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
        } catch (Exception e) {
            logger.error("BIDVTokenUtil: getBIDVToken: ERROR: " + e.toString());
            System.out.println("BIDVTokenUtil: getBIDVToken: ERROR: " + e.toString());
        }
        return result;
    }
}
