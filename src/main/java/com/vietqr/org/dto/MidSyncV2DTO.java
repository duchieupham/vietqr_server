package com.vietqr.org.dto;

import java.util.List;

public class MidSyncV2DTO {
    public List<MidSynchronizeV2DTO> merchants;

    public MidSyncV2DTO() {
    }

    public MidSyncV2DTO(List<MidSynchronizeV2DTO> merchants) {
        this.merchants = merchants;
    }

    public List<MidSynchronizeV2DTO> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<MidSynchronizeV2DTO> merchants) {
        this.merchants = merchants;
    }


}
