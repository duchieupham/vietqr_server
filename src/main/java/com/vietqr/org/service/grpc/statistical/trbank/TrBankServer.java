package com.vietqr.org.service.grpc.statistical.trbank;

import com.example.grpc.GetTrBankRequest;
import com.example.grpc.TBank;
import com.example.grpc.TrBankServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class TrBankServer extends TrBankServiceGrpc.TrBankServiceImplBase{
    private static final Logger logger = Logger.getLogger(TrBankServer.class);
    private final TrBankService trBankService;

    @Autowired
    public TrBankServer(TrBankService trBankService) {
        this.trBankService = trBankService;
    }

    @Override
    public void getTrBank(GetTrBankRequest request, StreamObserver<TBank> responseObserver) {
        try {
            com.example.grpc.TBank result = trBankService.getTrBankData(request.getStartDate(), request.getEndDate());
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("ERROR getTrBank: " + e.getMessage() + " at " + System.currentTimeMillis());
            responseObserver.onError(e);
        }
    }
}
