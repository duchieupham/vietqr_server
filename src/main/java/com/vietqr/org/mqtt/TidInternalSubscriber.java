package com.vietqr.org.mqtt;

import com.vietqr.org.util.annotation.SubscribeTopic;
import org.springframework.stereotype.Component;

@Component
public class TidInternalSubscriber {

    @SubscribeTopic("/my/topic")
    public void handleIncomingMessage(String message) {
        System.out.println("Received message on my/topic: " + message);
        // Add your message handling logic here
    }
}
