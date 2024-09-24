package com.vietqr.org.service.grpc.statistical.trbank;

import com.example.grpc.TBank;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrBankService {
    private final TrBankRepository trBankRepository;

    public TrBankService(TrBankRepository trBankRepository) {
        this.trBankRepository = trBankRepository;
    }

    public List<TBank> getTrBankData(long startDate, long endDate) {
        List<ITrBankDTO> dtoList = trBankRepository.getTrBankData(startDate, endDate);
        return dtoList.stream().map(dto ->
                TBank.newBuilder()
                        .setBankShortName(dto.getBankShortName())
                        .setTotalAmountCredits(dto.getTotalAmountCredits())
                        .setTotalAmountRecon(dto.getTotalAmountRecon())
                        .setTotalNumberCredits(dto.getTotalNumberCredits())
                        .setTotalNumberRecon(dto.getTotalNumberRecon())
                        .build()
                ).collect(Collectors.toList());
    }
}
