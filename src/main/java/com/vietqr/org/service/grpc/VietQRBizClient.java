package com.vietqr.org.service.grpc;

import com.example.vietqr_biz.MessageRequest;
import com.example.vietqr_biz.MessageResponse;
import com.example.vietqr_biz.VietQRBizServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.CountDownLatch;

import java.util.concurrent.TimeUnit;

public class VietQRBizClient {

    private static final Logger logger = LoggerFactory.getLogger(VietQRBizClient.class);
    private final VietQRBizServiceGrpc.VietQRBizServiceStub vietQRBizServiceStub;

    public VietQRBizClient(ManagedChannel channel) {
        // Tạo stub cho VietQRBizService
        vietQRBizServiceStub = VietQRBizServiceGrpc.newStub(channel);
    }

    // Phương thức gửi message "Hello vietqr_biz" và nhận phản hồi
    public void sendMessageToBiz(String message) throws InterruptedException {
        MessageRequest request = MessageRequest.newBuilder().setMessage(message).build();
        CountDownLatch latch = new CountDownLatch(1);

        vietQRBizServiceStub.sendMessage(request, new StreamObserver<MessageResponse>() {
            @Override
            public void onNext(MessageResponse response) {
                logger.info("Received reply from vietqr_biz: " + response.getReply());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Error during communication with vietqr_biz: ", t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Message exchange completed with vietqr_biz");
                latch.countDown();
            }
        });

        // Đợi tối đa 1 phút để hoàn tất giao tiếp
        latch.await(1, TimeUnit.MINUTES);
    }

    @Scheduled(fixedRate = 30000) // Mỗi 30 giây
    public void scheduledMessage() {
        try {
            sendMessageToBiz("Hello vietqr_biz");
        } catch (InterruptedException e) {
            logger.error("Failed to send message: ", e);
        }
    }
}
