package com.vietqr.org.service.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 10000)
                .usePlaintext() // Sử dụng PLAINTEXT
                .build();
    }

    @Bean
    public VietQRBizClient vietQRBizClient(ManagedChannel managedChannel) {
        return new VietQRBizClient(managedChannel);
    }
}
