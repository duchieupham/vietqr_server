package com.vietqr.org.service.grpc;

import com.example.grpc.BankAccountServiceGrpc;
import com.example.grpc.BankAccountStatisticsResponse;
import com.example.grpc.GetBankAccountStatisticsRequest;
import com.example.grpc.UserServiceGrpc;
import com.vietqr.org.dto.IAccountBankMonthDTO;
import com.vietqr.org.repository.AccountBankReceiveRepository;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class BankAccountServiceImpl extends BankAccountServiceGrpc.BankAccountServiceImplBase  {
    private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    @Autowired
    private AccountBankReceiveRepository accountBankReceiveRepository;
    @Override
    public void getBankAccountStatistics(GetBankAccountStatisticsRequest request, StreamObserver<BankAccountStatisticsResponse> responseObserver) {
        List<IAccountBankMonthDTO> statistics = accountBankReceiveRepository.getBankAccountStatistics();

        for (IAccountBankMonthDTO stat : statistics) {
            logger.info("Bank Short Name: " + stat.getBankShortName());
            logger.info("Total Accounts: " + stat.getTotalAccounts());
            logger.info("Linked Accounts: " + stat.getLinkedAccounts());
            logger.info("Unlinked Accounts: " + stat.getUnlinkedAccounts());

            BankAccountStatisticsResponse response = BankAccountStatisticsResponse.newBuilder()
                    .setBankShortName(stat.getBankShortName())
                    .setTotalAccounts(stat.getTotalAccounts())
                    .setLinkedAccounts(stat.getLinkedAccounts())
                    .setUnlinkedAccounts(stat.getUnlinkedAccounts())
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }


}
