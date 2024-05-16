package com.vietqr.org.service;

import com.vietqr.org.dto.TrMonthDTO;
import org.springframework.stereotype.Service;

@Service
public interface TrMonthService {
    TrMonthDTO getTrMonthByMonth(String time);
}
