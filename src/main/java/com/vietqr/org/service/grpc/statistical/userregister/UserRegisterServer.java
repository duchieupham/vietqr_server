package com.vietqr.org.service.grpc.statistical.userregister;

import com.example.grpc.GetUserRegisterRequest;
import com.example.grpc.UserRegister;
import com.example.grpc.UserRegisterServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.log4j.Logger;

@GrpcService
public class UserRegisterServer extends UserRegisterServiceGrpc.UserRegisterServiceImplBase {
    private static final Logger logger = Logger.getLogger(UserRegisterServer.class);

    private final UserRegisterService userRegisterService;

    public UserRegisterServer(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @Override
    public void getUserRegister(GetUserRegisterRequest request, StreamObserver<UserRegister> responseObserver) {
        try {
            com.example.grpc.UserRegister result = userRegisterService.getUserRegisterData(request.getStartDate(), request.getEndDate());

            responseObserver.onNext(result);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("ERROR getUserRegister: " + e.getMessage() + " at " + System.currentTimeMillis());
            responseObserver.onError(e);
        }
    }
}
