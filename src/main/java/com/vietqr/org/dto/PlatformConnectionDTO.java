package com.vietqr.org.dto;

public class PlatformConnectionDTO {
    private String platformId;
    private String platformName;
    private String connectionDetail;
    private String platform;

    public PlatformConnectionDTO(String platformId, String platformName, String connectionDetail, String platform) {
        this.platformId = platformId;
        this.platformName = platformName;
        this.connectionDetail = connectionDetail;
        this.platform = platform;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
