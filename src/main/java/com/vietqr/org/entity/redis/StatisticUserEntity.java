package com.vietqr.org.entity.redis;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("StatisticUser")
public class StatisticUserEntity {

    @TimeToLive
    private long expiration = 86400;

    private String id;

    private String userId;

    private String phoneNo;

    private String email;

    private String fullName;

    private String address;

    private String ipAddress;

    private String registerPlatform;

    private long registerDate;

    private String date;

    private long dateValue;

    private String timeZone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRegisterPlatform() {
        return registerPlatform;
    }

    public void setRegisterPlatform(String registerPlatform) {
        this.registerPlatform = registerPlatform;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateValue() {
        return dateValue;
    }

    public void setDateValue(long dateValue) {
        this.dateValue = dateValue;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public StatisticUserEntity(String id, String userId, String phoneNo,
                               String email, String fullName, String address,
                               String ipAddress, String registerPlatform,
                               long registerDate, String date, long dateValue,
                               String timeZone) {
        this.id = id;
        this.userId = userId;
        this.phoneNo = phoneNo;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.ipAddress = ipAddress;
        this.registerPlatform = registerPlatform;
        this.registerDate = registerDate;
        this.date = date;
        this.dateValue = dateValue;
        this.timeZone = timeZone;
    }

    public StatisticUserEntity() {
    }
}
