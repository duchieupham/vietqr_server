package com.vietqr.org.service.grpc.statistical.trmc;

import com.example.grpc.GetTrMcRequest;
import com.example.grpc.TrMc;
import com.example.grpc.TrMcServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class TrMcServer extends TrMcServiceGrpc.TrMcServiceImplBase {
    private static final Logger logger = Logger.getLogger(TrMcServer.class);
    private final TrMcService trMcService;

    @Autowired
    public TrMcServer(TrMcService trMcService) {
        this.trMcService = trMcService;
    }

    @Override
    public void getTrMc(GetTrMcRequest request, StreamObserver<TrMc> responseObserver) {
        try {
                List<TrMc> resultList = trMcService.getTrMcData(request.getStartDate(), request.getEndDate());

            for (TrMc result : resultList) {
                responseObserver.onNext(result);
            }

            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("ERROR getTrMc: " + e.getMessage() + " at " + System.currentTimeMillis());
            responseObserver.onError(e);
        }
    }

}
