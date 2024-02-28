package com.vietqr.org.entity.redis;

import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash("StatisticBank")
public class StatisticBankEntity {
    private String id;
    private List<SumEachBankEntity> sumEachBankEntities;
    private String date;
    private String timeZone;
    private long timeValue;

    public StatisticBankEntity() {
    }

    public StatisticBankEntity(String id, List<SumEachBankEntity> sumEachBankEntities,
                               String date, String timeZone, long timeValue) {
        this.id = id;
        this.sumEachBankEntities = sumEachBankEntities;
        this.date = date;
        this.timeZone = timeZone;
        this.timeValue = timeValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SumEachBankEntity> getSumEachBankEntities() {
        return sumEachBankEntities;
    }

    public void setSumEachBankEntities(List<SumEachBankEntity> sumEachBankEntities) {
        this.sumEachBankEntities = sumEachBankEntities;
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
