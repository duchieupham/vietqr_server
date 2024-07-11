package com.vietqr.org.util;

import com.vietqr.org.dto.ITransactionRelatedDetailDTO;
import com.vietqr.org.entity.TransactionReceiveEntity;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleSheetUtil {
//
//    // fix lần 1
//
    private static final Logger logger = Logger.getLogger(GoogleSheetUtil.class);
//
//    public boolean insertRowToGoogleSheet(String webhook, String[] rowData) {
//        boolean check = false;
//        try {
//            if (webhook != null && !webhook.trim().isEmpty()) {
//                Map<String, Object> data = new HashMap<>();
//                data.put("values", new String[][]{ rowData });
//
//                // Build URL with PathVariable
//                UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(webhook).buildAndExpand();
//
//                // Create WebClient with authorization header
//                WebClient webClient = WebClient.builder().baseUrl(uriComponents.toUriString()).build();
//                Mono<ClientResponse> responseMono = webClient.post()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(BodyInserters.fromValue(data))
//                        .exchange();
//                ClientResponse response = responseMono.block();
//                if (response.statusCode().is2xxSuccessful()) {
//                    check = true;
//                }
//            }
//        } catch (Exception e) {
//            logger.error("GoogleSheetUtil: insertRowToGoogleSheet: ERROR: " + e.getMessage() + System.currentTimeMillis());
//        }
//        return check;
//    }
//
//    public boolean headersExist(String webhook) {
//        boolean exist = false;
//        try {
//            // Build URL with PathVariable
//            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(webhook).buildAndExpand();
//
//            // Create WebClient
//            WebClient webClient = WebClient.builder().baseUrl(uriComponents.toUriString()).build();
//            Mono<ClientResponse> responseMono = webClient.get()
//                    .exchange();
//            ClientResponse response = responseMono.block();
//
//            if (response.statusCode().is2xxSuccessful()) {
//                String responseBody = response.bodyToMono(String.class).block();
//                // Check if the headers exist in the response body
//                exist = responseBody != null && responseBody.contains("Thời gian") && responseBody.contains("Số tiền")
//                        && responseBody.contains("Tài khoản") && responseBody.contains("Mã tham chiếu") && responseBody.contains("Nội dung");
//            }
//        } catch (Exception e) {
//            logger.error("GoogleSheetUtil: headersExist: ERROR: " + e.getMessage() + System.currentTimeMillis());
//        }
//        return exist;
//    }


    public boolean insertRowToGoogleSheet(String webhook, List<String[]> rowsData) {
        boolean check = false;
        try {
            if (webhook != null && !webhook.trim().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("values", rowsData);

                // Build URL with PathVariable
                UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(webhook).buildAndExpand();

                // Create WebClient with authorization header
                WebClient webClient = WebClient.builder().baseUrl(uriComponents.toUriString()).build();
                Mono<ClientResponse> responseMono = webClient.post()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                ClientResponse response = responseMono.block();
                if (response.statusCode().is2xxSuccessful()) {
                    check = true;
                }
            }
        } catch (Exception e) {
            logger.error("GoogleSheetUtil: insertRowToGoogleSheet: ERROR: " + e.getMessage() + System.currentTimeMillis());
        }
        return check;
    }

    public boolean headersExist(String webhook) {
        boolean exist = false;
        try {
            // Build URL with PathVariable
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(webhook).buildAndExpand();

            // Create WebClient
            WebClient webClient = WebClient.builder().baseUrl(uriComponents.toUriString()).build();
            Mono<ClientResponse> responseMono = webClient.get()
                    .exchange();
            ClientResponse response = responseMono.block();

            if (response.statusCode().is2xxSuccessful()) {
                String responseBody = response.bodyToMono(String.class).block();
                // Check if the headers exist in the response body
                exist = responseBody != null && responseBody.contains("STT") && responseBody.contains("Thời gian TT")
                        && responseBody.contains("Số tiền (VND)") && responseBody.contains("Loại")
                        && responseBody.contains("Trạng thái") && responseBody.contains("Mã giao dịch")
                        && responseBody.contains("Mã đơn hàng") && responseBody.contains("Cửa hàng")
                        && responseBody.contains("Tài khoản nhận") && responseBody.contains("Thời gian tạo")
                        && responseBody.contains("Nội dung TT") && responseBody.contains("Ghi chú")
                        && responseBody.contains("Loại giao dịch");
            }
        } catch (Exception e) {
            logger.error("GoogleSheetUtil: headersExist: ERROR: " + e.getMessage() + System.currentTimeMillis());
        }
        return exist;
    }

    public String[] createGoogleSheetRowData(ITransactionRelatedDetailDTO item) {
        return new String[]{
                String.valueOf(item.getTransactionId()),
                DateTimeUtil.getDateStringBaseLong(item.getTimePaid()),
                String.valueOf(item.getAmount()),
                item.getTransType().equals("C") ? "Giao dịch đến" : "Giao dịch đi",
                getStatusTransaction(item.getStatus()),
                item.getReferenceNumber(),
                item.getOrderId(),
                "-", // Cửa hàng - không có thông tin trong DTO
                StringUtil.formatBankAccount(item.getBankAccount()) + " - " + (item.getBankShortName() != null ? item.getBankShortName() : "-"),
                DateTimeUtil.getDateStringBaseLong(item.getTime()),
                item.getContent() != null ? item.getContent() : "-",
                item.getNote() != null ? item.getNote() : "-",
                getTypeTransaction(item.getType())
        };
    }

    private String getStatusTransaction(int status) {
        switch (status) {
            case 0:
                return "Thành công";
            case 1:
                return "Đang xử lý";
            case 2:
                return "Thất bại";
            default:
                return "Không xác định";
        }
    }

    private String getTypeTransaction(int type) {
        switch (type) {
            case 0:
                return "Giao dịch nội bộ";
            case 1:
                return "Giao dịch liên ngân hàng";
            case 2:
                return "Giao dịch thẻ";
            default:
                return "Giao dịch khác";
        }
    }


}
