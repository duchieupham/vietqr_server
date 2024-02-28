package com.vietqr.org.entity.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("SumUser")
public class SumUserEntity implements Serializable {
    @Id
    private String id;
    private int sumOfUser;

    @Indexed
    private String date;

    private long dateValue;

    private String timeZone;

    public SumUserEntity() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSumOfUser(int sumOfUser) {
        this.sumOfUser = sumOfUser;
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

    public String getId() {
        return id;
    }

    public int getSumOfUser() {
        return sumOfUser;
    }
}
