package com.vietqr.org.entity.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("SumBank")
public class SumBankEntity implements Serializable {
    @Id
    private String id;
    private long numberOfBankAuthenticated;

    private long numberOfBankNotAuthenticated;
    private long numberOfBank;
    @Indexed
    private String date;
    private String timeZone;
    private long timeValue;

    public SumBankEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getNumberOfBankAuthenticated() {
        return numberOfBankAuthenticated;
    }

    public void setNumberOfBankAuthenticated(long numberOfBankAuthenticated) {
        this.numberOfBankAuthenticated = numberOfBankAuthenticated;
    }

    public long getNumberOfBankNotAuthenticated() {
        return numberOfBankNotAuthenticated;
    }

    public void setNumberOfBankNotAuthenticated(long numberOfBankNotAuthenticated) {
        this.numberOfBankNotAuthenticated = numberOfBankNotAuthenticated;
    }

    public long getNumberOfBank() {
        return numberOfBank;
    }

    public void setNumberOfBank(long numberOfBank) {
        this.numberOfBank = numberOfBank;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(long timeValue) {
        this.timeValue = timeValue;
    }
}
