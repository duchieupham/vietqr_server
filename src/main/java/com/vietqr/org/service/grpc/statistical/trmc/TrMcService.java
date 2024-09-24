package com.vietqr.org.service.grpc.statistical.trmc;

import com.example.grpc.TrMc;
import org.springframework.beans.factory.annotation.Autowired;
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
        return dtoList.stream().map(dto -> {
                    TrMc.Builder builder = TrMc.newBuilder();
                    builder.setMerchantName(dto.getMerchantName() != null ? dto.getMerchantName() : "")
                            .setTotalNumberCredits(dto.getTotalNumberCredits())
                            .setTotalAmountCredits(dto.getTotalAmountCredits())
                            .setTotalNumberRecon(dto.getTotalReconTransactions())
                            .setTotalAmountRecon(dto.getTotalAmountRecon());
                    return builder.build();
                }
        ).collect(Collectors.toList());
    }
}
