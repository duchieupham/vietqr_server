package com.vietqr.org.service.redis;

import com.vietqr.org.dto.StartEndTimeDTO;
import com.vietqr.org.dto.SumOfBankDTO;
import com.vietqr.org.entity.redis.SumBankEntity;
import com.vietqr.org.repository.AccountBankReceiveRepository;
import com.vietqr.org.repository.redis.SumBankRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Service
public class SumOfBankServiceImpl implements SumOfBankService {

    @Autowired
    private SumBankRepository repo;

    @Autowired
    private AccountBankReceiveRepository accountBankReceiveRepository;

    @Override
    public void save(SumBankEntity sumBankEntity) {
        repo.save(sumBankEntity);
    }

    @Override
    public SumBankEntity findByAllTime() {
        SumBankEntity result = null;
        String key = "ALL_TIME";
        result = repo.findById(key).orElse(null);
        if (result == null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1).with(LocalTime.MAX);
            SumOfBankDTO sumOfBank = accountBankReceiveRepository
                    .sumOfBankByStartDateEndDate(0 - DateTimeUtil.GMT_PLUS_7_OFFSET,
                            yesterday.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.GMT_PLUS_7_OFFSET);
            result = new SumBankEntity();
            result.setDate(key);
            result.setTimeValue(yesterday.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.GMT_PLUS_7_OFFSET);
            result.setNumberOfBankNotAuthenticated(sumOfBank.getNumberOfBankNotAuthenticated());
            result.setNumberOfBankAuthenticated(sumOfBank.getNumberOfBankAuthenticated());
            result.setNumberOfBank(sumOfBank.getNumberOfBank());
            result.setId(key);
            result.setTimeZone(DateTimeUtil.GMT_PLUS_7);
            repo.save(result);
        }
        return result;
    }

    @Override
    public SumBankEntity findByDate(String date) {
        SumBankEntity result = null;
        result = repo.findById(DateTimeUtil.getSimpleDate(date)).orElse(null);
        if (result == null) {
            StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndADate(date);
            SumOfBankDTO sumOfBank = accountBankReceiveRepository
                    .sumOfBankByStartDateEndDate(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                            startEndTimeDTO.getToDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
            result = new SumBankEntity();
            result.setDate(date);
            result.setTimeValue(startEndTimeDTO.getFromDate() - DateTimeUtil.GMT_PLUS_7_OFFSET);
            result.setNumberOfBankNotAuthenticated(sumOfBank.getNumberOfBankNotAuthenticated());
            result.setNumberOfBankAuthenticated(sumOfBank.getNumberOfBankAuthenticated());
            result.setNumberOfBank(sumOfBank.getNumberOfBank());
            result.setId(DateTimeUtil.getSimpleDate(date));
            result.setTimeZone(DateTimeUtil.GMT_PLUS_7);
            repo.save(result);
        }
        return result;
    }
}
