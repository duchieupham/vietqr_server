package com.vietqr.org.dto;

public class PlatformConnectionDTO {
    private String platformId;
    private String platformName;
    private String connectionDetail;

    public PlatformConnectionDTO(String platformId, String platformName, String connectionDetail) {
        this.platformId = platformId;
        this.platformName = platformName;
        this.connectionDetail = connectionDetail;
    }


    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getConnectionDetail() {
        return connectionDetail;
    }

    public void setConnectionDetail(String connectionDetail) {
        this.connectionDetail = connectionDetail;
    }
}
