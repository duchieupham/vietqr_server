package com.vietqr.org.service.grpc.statistical.trbank;

import com.example.grpc.TBank;
import org.springframework.stereotype.Service;

@Service
public class TrBankService {
    private final TrBankRepository trBankRepository;

    public TrBankService(TrBankRepository trBankRepository) {
        this.trBankRepository = trBankRepository;
    }

    public TBank getTrBankData(long startDate, long endDate) {
        ITrBankDTO dto = trBankRepository.getTrBankData(startDate, endDate);
        TrBankDTO trBankDTO = new TrBankDTO(dto);
        return TBank.newBuilder()
                .setBankShortName(trBankDTO.getBankShortName())
                .setTotalAmountCredits(trBankDTO.getTotalAmountCredits())
                .setTotalAmountRecon(trBankDTO.getTotalAmountRecon())
                .setTotalNumberCredits(trBankDTO.getTotalNumberCredits())
                .setTotalNumberRecon(trBankDTO.getTotalNumberRecon())
                .build();
    }
}
