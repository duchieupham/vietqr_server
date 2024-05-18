package com.vietqr.org.service;

import com.vietqr.org.dto.TrMonthDTO;
import com.vietqr.org.repository.TrMonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrMonthServiceImpl implements TrMonthService {

    @Autowired
    private TrMonthRepository repo;
    @Override
    public TrMonthDTO getTrMonthByMonth(String time) {
        return repo.getTrMonthByMonth(time);
    }
}
