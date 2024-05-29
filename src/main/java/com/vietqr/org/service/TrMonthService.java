package com.vietqr.org.service;

import com.vietqr.org.dto.TrMonthDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrMonthService {
    List<TrMonthDTO> getTrMonthByMonth(String time);
}
