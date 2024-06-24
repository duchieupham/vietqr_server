package com.vietqr.org.service;

import com.example.grpc.GetUsersRequest;
import com.example.grpc.GetUsersResponse;
import com.example.grpc.User;
import com.example.grpc.UserServiceGrpc;
import com.vietqr.org.dto.IAccountLogin;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    @Autowired
    private AccountLoginService accountLoginService;

    @Override
    public void getRegisteredUsers(GetUsersRequest request, StreamObserver<GetUsersResponse> responseObserver) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(request.getDate(), formatter);
        long startTime = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long endTime = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        System.out.println("Received request for date: " + date);

        // Số người dùng đăng ký trong ngày
        List<IAccountLogin> users = accountLoginService.findUsersRegisteredInDay(startTime, endTime);
        long userRegister = users.size();

        // Tổng số người dùng
        long totalUsers = accountLoginService.getTotalUsers();

        // Tạo JSON string cho sumUser
        String sumUserJson = String.format("{\"user_register\": \"%d\", \"total\":\"%d\"}", userRegister, totalUsers);

        // Xây dựng phản hồi gRPC
        GetUsersResponse.Builder responseBuilder = GetUsersResponse.newBuilder();
        responseBuilder.setSumUser(sumUserJson);

        for (IAccountLogin user : users) {
            User.Builder grpcUserBuilder = User.newBuilder().setId(user.getId());

            if (user.getPhoneNo() != null) {
                grpcUserBuilder.setPhoneNo(user.getPhoneNo());
            }
            if (user.getEmail() != null) {
                grpcUserBuilder.setEmail(user.getEmail());
            }
            if (user.getTime() != null) {
                grpcUserBuilder.setTime(user.getTime());
            }

            responseBuilder.addUsers(grpcUserBuilder.build());
        }

        System.out.println("Sending response with " + users.size() + " users");

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}