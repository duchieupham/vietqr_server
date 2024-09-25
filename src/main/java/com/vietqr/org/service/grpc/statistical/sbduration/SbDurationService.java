package com.vietqr.org.service.grpc.statistical.sbduration;

import com.example.grpc.SbDuration;
import org.springframework.stereotype.Service;

@Service
public class SbDurationService {
    private final SbDurationRepository sbDurationRepository;

    public SbDurationService(SbDurationRepository sbDurationRepository) {
        this.sbDurationRepository = sbDurationRepository;
    }

    public SbDuration getSbDuration(long expired, long nearing) {
        ISbDurationDTO dto = sbDurationRepository.getSbDurationData(expired, nearing);
        SbDurationDTO sbDurationDTO = new SbDurationDTO(dto);
        return SbDuration.newBuilder()
                .setExpiredCount(sbDurationDTO.getOverdueCount())
                .setNearingExpirationCount(sbDurationDTO.getNearlyExpireCount())
                .build();
    }
}
