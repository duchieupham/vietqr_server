package com.vietqr.org.service.grpc.statistical.sbduration;

import com.example.grpc.GetSbDurationRequest;
import com.example.grpc.SbDuration;
import com.example.grpc.SbDurationServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.log4j.Logger;

@GrpcService
public class SbDurationServer extends SbDurationServiceGrpc.SbDurationServiceImplBase {
    private static final Logger logger = Logger.getLogger(SbDurationServer.class);
    private final SbDurationService sbDurationService;

    public SbDurationServer(SbDurationService sbDurationService) {
        this.sbDurationService = sbDurationService;
    }

    @Override
    public void getSbDuration(GetSbDurationRequest request, StreamObserver<SbDuration> responseObserver) {
        try {
            com.example.grpc.SbDuration result = sbDurationService.getSbDuration(request.getExpired(), request.getNearingExpiration());
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("ERROR getSbDuration: " + e.getMessage() + " at " + System.currentTimeMillis());
            responseObserver.onError(e);
        }
    }
}
