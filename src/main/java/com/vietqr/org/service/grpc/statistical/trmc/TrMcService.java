package com.vietqr.org.service.grpc.statistical.trmc;

import com.example.grpc.TrMc;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrMcService {
    private final TrMcRepository trMcRepository;

    public TrMcService(TrMcRepository trMcRepository) {
        this.trMcRepository = trMcRepository;
    }

    public List<TrMc> getTrMcData(long startDate, long endDate) {
        List<ITrMcDTO> dtoList = trMcRepository.getTrMcData(startDate, endDate);
        return dtoList.stream().map(dto ->
                TrMc.newBuilder()
                        .setMerchantName(dto.getMerchantName())
                        .setTotalNumberCredits(dto.getTotalNumberCredits())
                        .setTotalAmountCredits(dto.getTotalAmountCredits())
                        .setTotalNumberRecon(dto.getTotalReconTransactions())
                        .setTotalAmountRecon(dto.getTotalAmountRecon())
                        .build()
        ).collect(Collectors.toList());
    }
}
