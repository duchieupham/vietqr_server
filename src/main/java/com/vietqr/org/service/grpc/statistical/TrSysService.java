package com.vietqr.org.service.grpc.statistical;

import com.example.grpc.TSys;
import org.springframework.stereotype.Service;

@Service
public class TrSysService {
    private final TrSysRepository trSysRepository;

    public TrSysService(TrSysRepository trSysRepository) {
        this.trSysRepository = trSysRepository;
    }

    public TSys getTrSysData(long startDate, long endDate) {
        ITrSysDTO dto = trSysRepository.getTrSysData(startDate, endDate);
        TrSysDTO trSysDTO = new TrSysDTO(dto);
        return TSys.newBuilder()
                .setTotalNumberCredits(trSysDTO.getTotalNumberCredits())
                .setTotalAmountCredits(trSysDTO.getTotalAmountCredits())
                .setTotalNumberRecon(trSysDTO.getTotalNumberRecon())
                .setTotalAmountRecon(trSysDTO.getTotalAmountRecon())
                .setTotalNumberWithoutRecon(trSysDTO.getTotalNumberWithoutRecon())
                .setTotalAmountWithoutRecon(trSysDTO.getTotalAmountWithoutRecon())
                .setTotalNumberPushError(trSysDTO.getTotalNumberPushError())
                .setTotalAmountPushErrorSum(trSysDTO.getTotalAmountPushErrorSum())
                .build();
    }
}