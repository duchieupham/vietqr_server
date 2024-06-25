package com.vietqr.org.service;

import com.example.grpc.GetUsersRequest;
import com.example.grpc.User;
import com.example.grpc.UserServiceGrpc;
import com.vietqr.org.dto.IAccountLogin;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    @Autowired
    private AccountLoginService accountLoginService;

    @Override
    public void getRegisteredUsers(GetUsersRequest request, StreamObserver<User> responseObserver) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(request.getDate(), formatter);
        long startTime = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long endTime = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        System.out.println("Received request for date: " + date);

        List<IAccountLogin> users = accountLoginService.findUsersRegisteredInDay(startTime, endTime);
        long userRegister = users.size();
        long totalUsers = accountLoginService.getTotalUsersUntilDate(endTime);

        // Tạo JSON string cho sumUser
        String sumUserJson = String.format("{\"user_register\": \"%d\", \"total\":\"%d\"}", userRegister, totalUsers);

        for (IAccountLogin user : users) {
            User grpcUser = User.newBuilder()
                    .setId(user.getId())
                    .setPhoneNo(user.getPhoneNo() != null ? user.getPhoneNo() : "")
                    .setEmail(user.getEmail() != null ? user.getEmail() : "")
                    .setTime(user.getTime() != null ? user.getTime() : 0)
                    .build();
            responseObserver.onNext(grpcUser);
        }

        // Send the sumUserJson as the last message
        User summaryUser = User.newBuilder()
                .setId("summary")
                .setPhoneNo("")
                .setEmail("")
                .setTime(0)
                .setSumUserJson(sumUserJson)
                .build();
        responseObserver.onNext(summaryUser);

        responseObserver.onCompleted();
    }

    @Override
    public void getRegisteredUsersInMonth(GetUsersRequest request, StreamObserver<User> responseObserver) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(request.getDate(), formatter);
        long startTime = yearMonth.atDay(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long endTime = yearMonth.plusMonths(1).atDay(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        System.out.println("Received request for month: " + yearMonth);

        List<IAccountLogin> users = accountLoginService.findUsersRegisteredInMonth(startTime, endTime);
        long userRegister = users.size();
        long totalUsers = accountLoginService.getTotalUsersUntilDate(endTime);

        // Tạo JSON string cho sumUser
        String sumUserJson = String.format("{\"user_register\": \"%d\", \"total\":\"%d\"}", userRegister, totalUsers);

        for (IAccountLogin user : users) {
            User grpcUser = User.newBuilder()
                    .setId(user.getId())
                    .setPhoneNo(user.getPhoneNo() != null ? user.getPhoneNo() : "")
                    .setEmail(user.getEmail() != null ? user.getEmail() : "")
                    .setTime(user.getTime() != null ? user.getTime() : 0)
                    .build();
            responseObserver.onNext(grpcUser);
        }

        // Send the sumUserJson as the last message
        User summaryUser = User.newBuilder()
                .setId("summary")
                .setPhoneNo("")
                .setEmail("")
                .setTime(0)
                .setSumUserJson(sumUserJson)
                .build();
        responseObserver.onNext(summaryUser);

        responseObserver.onCompleted();
    }
}