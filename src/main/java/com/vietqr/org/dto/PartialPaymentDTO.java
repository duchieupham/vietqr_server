package com.vietqr.org.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class PartialPaymentDTO {
    private String invoiceId;
    private List<String> itemIds;
}
