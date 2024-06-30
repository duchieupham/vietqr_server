package com.vietqr.org.dto;

import java.util.List;

public class MidSyncDTO {
    private List<MidSynchronizeDTO> merchants;

    public MidSyncDTO() {
    }

    public MidSyncDTO(List<MidSynchronizeDTO> merchants) {
        this.merchants = merchants;
    }

    public List<MidSynchronizeDTO> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<MidSynchronizeDTO> merchants) {
        this.merchants = merchants;
    }
}
