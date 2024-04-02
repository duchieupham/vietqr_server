package com.vietqr.org.service.statistic;

import io.grpc.stub.StreamObserver;
import statistic.GreeterGrpc;
import statistic.HelloReply;
import statistic.HelloRequest;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class TransReceiveStatisticService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
