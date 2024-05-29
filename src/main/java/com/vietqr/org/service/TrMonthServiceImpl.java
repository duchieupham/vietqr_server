package com.vietqr.org.service;

import com.vietqr.org.dto.TrMonthDTO;
import com.vietqr.org.repository.TrMonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrMonthServiceImpl implements TrMonthService {

    @Autowired
    private TrMonthRepository repo;

    @Override
    public List<TrMonthDTO> getTrMonthByMonth(String time) {
        return repo.getTrMonthByMonth(time);
    }

//    @Override
//    public List<TrMonthDTO> getTrMonthByMonths(String time) {
//        return repo.getTrMonthByMonths(time);
//    }
}
