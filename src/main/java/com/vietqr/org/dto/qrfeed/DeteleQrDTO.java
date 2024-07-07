package com.vietqr.org.dto.qrfeed;

import java.util.List;

public class DeteleQrDTO {
    private List<String> qrIds;

    public DeteleQrDTO(String userId, List<String> qrIds) {
        this.qrIds = qrIds;
    }

    public DeteleQrDTO() {
    }

    public List<String> getQrIds() {
        return qrIds;
    }

    public void setQrIds(List<String> qrIds) {
        this.qrIds = qrIds;
    }
}
