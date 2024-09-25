package com.vietqr.org.service.grpc.statistical.userregister;

public class UserRegisterDTO {
    private String userCount;
    private long androidPlatform;
    private long iosPlatform;
    private long webPlatform;

    public UserRegisterDTO() {
    }

    public UserRegisterDTO(IUserRegisterDTO dto) {
        this.userCount = dto.getUserCount();
        this.androidPlatform = dto.getAndroidPlatform();
        this.iosPlatform = dto.getIosPlatform();
        this.webPlatform = dto.getWebPlatform();
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public long getAndroidPlatform() {
        return androidPlatform;
    }

    public void setAndroidPlatform(long androidPlatform) {
        this.androidPlatform = androidPlatform;
    }

    public long getIosPlatform() {
        return iosPlatform;
    }

    public void setIosPlatform(long iosPlatform) {
        this.iosPlatform = iosPlatform;
    }

    public long getWebPlatform() {
        return webPlatform;
    }

    public void setWebPlatform(long webPlatform) {
        this.webPlatform = webPlatform;
    }
}
