package com.vietqr.org.service.grpc.statistical;

import com.example.grpc.GetTrSysRequest;
import com.example.grpc.TSys;
import com.example.grpc.TrSysServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class TrSysServer extends TrSysServiceGrpc.TrSysServiceImplBase {
    private static final Logger logger = Logger.getLogger(TrSysServer.class);

    private final TrSysService trSysService;

    @Autowired
    public TrSysServer(TrSysService trSysService) {
        this.trSysService = trSysService;
    }

    @Override
    public void getTrSys(GetTrSysRequest request, StreamObserver<TSys> responseObserver) {
        try {
            com.example.grpc.TSys result = trSysService.getTrSysData(request.getStartDate(), request.getEndDate());

            TSys response = TSys.newBuilder()
                    .setTotalNumberCredits(result.getTotalNumberCredits())
                    .setTotalAmountCredits(result.getTotalAmountCredits())
                    .setTotalNumberRecon(result.getTotalNumberRecon())
                    .setTotalAmountRecon(result.getTotalAmountRecon())
                    .setTotalNumberWithoutRecon(result.getTotalNumberWithoutRecon())
                    .setTotalAmountWithoutRecon(result.getTotalAmountWithoutRecon())
                    .setTotalNumberPushError(result.getTotalNumberPushError())
                    .setTotalAmountPushErrorSum(result.getTotalAmountPushErrorSum())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("ERROR getTrSys: " + e.getMessage() + " at " + System.currentTimeMillis());
            responseObserver.onError(e);
        }
    }
}