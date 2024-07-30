package com.vietqr.org.dto;


import java.util.List;

public class MidSyncResponseV2DTO {
    private MasterDataDTO masterData;
    private List<MidSynchronizeV2DTO> merchantData;

    public MidSyncResponseV2DTO() {
    }

    public MidSyncResponseV2DTO(MasterDataDTO masterData, List<MidSynchronizeV2DTO> merchantData) {
        this.masterData = masterData;
        this.merchantData = merchantData;
    }

    public MidSyncResponseV2DTO(String mid, String merchantName, MasterDataDTO masterData, List<MidSynchronizeV2DTO> merchantData) {
        this.masterData = masterData;
        this.merchantData = merchantData;
    }

    public MasterDataDTO getMasterData() {
        return masterData;
    }

    public void setMasterData(MasterDataDTO masterData) {
        this.masterData = masterData;
    }

    public List<MidSynchronizeV2DTO> getMerchantData() {
        return merchantData;
    }

    public void setMerchantData(List<MidSynchronizeV2DTO> merchantData) {
        this.merchantData = merchantData;
    }
}
