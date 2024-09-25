package com.vietqr.org.service.grpc.biz;

import com.vietqr.org.repository.AccountBankReceiveRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.log4j.Logger;
import com.example.grpc.bankaccountreceive.BankAccountReceiveServiceGrpc;
import com.example.grpc.bankaccountreceive.BankAccountReceive;
import com.example.grpc.bankaccountreceive.RequestByBankId;
import com.example.grpc.bankaccountreceive.RequestByUserId;
import com.example.grpc.bankaccountreceive.BankAccountReceiveList;

import java.util.List;

@GrpcService
public class BankAccountReceiveServer extends BankAccountReceiveServiceGrpc.BankAccountReceiveServiceImplBase {
    private static final Logger logger = Logger.getLogger(BankAccountReceiveServer.class);
    private final String LOG_ERROR = "Failed at BankAccountReceiveServer: ";

    private final AccountBankReceiveRepository accountBankReceiveRepository;

    public BankAccountReceiveServer(AccountBankReceiveRepository accountBankReceiveRepository) {
        this.accountBankReceiveRepository = accountBankReceiveRepository;
    }

    @Override
    public void getBankAccountReceiveByBankId(RequestByBankId request, StreamObserver<BankAccountReceive> responseObserver) {
        try {
            IBankAccountReceiveUserDTO result = accountBankReceiveRepository.getBankAccountReceiveByBankIdGrpc(request.getBankId().trim());
            BankAccountReceive response = BankAccountReceive
                    .newBuilder()
                    .setId(request.getBankId().trim())
                    .setBankAccount(result.getBankAccount())
                    .setUserBankName(result.getUserBankName())
                    .setIsSync(result.getIsSync())
                    .setBankTypeId(result.getBankTypeId())
                    .setUserId(result.getUserId())
                    .setBankShortName(result.getBankShortName())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error(LOG_ERROR + "getBankAccountReceiveByBankId: " + e.getMessage() + " at: " + System.currentTimeMillis());
            BankAccountReceive response = BankAccountReceive
                    .newBuilder()
                    .setId(request.getBankId().trim())
                    .setBankAccount("")
                    .setUserBankName("")
                    .setIsSync(false)
                    .setBankTypeId("")
                    .setUserId("")
                    .setBankShortName("")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getBankAccountReceiveByUserId(RequestByUserId request, StreamObserver<BankAccountReceiveList> responseObserver) {
        try {
            List<IBankAccountReceiveBankDTO> result = accountBankReceiveRepository.getBankAccountReceiveByUserIdGrpc(request.getUserId().trim());
            BankAccountReceiveList.Builder response = BankAccountReceiveList.newBuilder();
            for (IBankAccountReceiveBankDTO item : result) {
                BankAccountReceive bankAccountReceive = BankAccountReceive
                        .newBuilder()
                        .setId(item.getBankId())
                        .setBankAccount(item.getBankAccount())
                        .setUserBankName(item.getUserBankName())
                        .setIsSync(item.getIsSync())
                        .setBankTypeId(item.getBankTypeId())
                        .setUserId(request.getUserId().trim())
                        .setBankShortName(item.getBankShortName())
                        .build();
                response.addBankAccountReceives(bankAccountReceive);
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error(LOG_ERROR + "getBankAccountReceiveByUserId: " + e.getMessage() + " at: " + System.currentTimeMillis());
            BankAccountReceiveList response = BankAccountReceiveList
                    .newBuilder()
                    .addBankAccountReceives(
                            BankAccountReceive
                                    .newBuilder()
                                    .setBankAccount("")
                                    .setUserBankName("")
                                    .setIsSync(false)
                                    .setBankTypeId("")
                                    .setUserId("")
                                    .setBankShortName("")
                                    .build()
                    ).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}