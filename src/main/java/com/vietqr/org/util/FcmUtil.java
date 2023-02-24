package com.vietqr.org.util;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.vietqr.org.dto.FcmRequestDTO;

@Service
public class FcmUtil {
	 private Logger logger = Logger.getLogger(FcmUtil.class);

	    public void sendMessage(Map<String, String> data, FcmRequestDTO request)
	            throws InterruptedException, ExecutionException {
	        Message message = getPreconfiguredMessageWithData(data, request);
	        String response = sendAndGetResponse(message);
	        logger.info("Sent message with data. Topic: " + request.getTopic() + ", " + response);
	    }

	    public void sendMessageWithoutData(FcmRequestDTO request)
	            throws InterruptedException, ExecutionException {
	        Message message = getPreconfiguredMessageWithoutData(request);
	        String response = sendAndGetResponse(message);
	        logger.info("Sent message without data. Topic: " + request.getTopic() + ", " + response);
	    }

	    public void sendMessageToToken(FcmRequestDTO request)
	            throws InterruptedException, ExecutionException {
	        Message message = getPreconfiguredMessageToToken(request);
	        String response = sendAndGetResponse(message);
	        logger.info("Sent message to token. Device token: " + request.getToken() + ", " + response);
	    }

	    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
	        return FirebaseMessaging.getInstance().sendAsync(message).get();
	    }

	    private AndroidConfig getAndroidConfig(String topic) {
	    	 return AndroidConfig.builder()
	                 .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
	                 .setPriority(AndroidConfig.Priority.HIGH)
	                 .setNotification(AndroidNotification.builder()
//	                		 .setSound(NotificationParameter.SOUND.getValue())
//	                         .setColor(NotificationParameter.COLOR.getValue()).setTag(topic)
	                 .build()).build();
	    }

	    private ApnsConfig getApnsConfig(String topic) {
	        return ApnsConfig.builder()
	                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
	    }

	    private Message getPreconfiguredMessageToToken(FcmRequestDTO request) {
	        return getPreconfiguredMessageBuilder(request).setToken(request.getToken())
	                .build();
	    }

	    private Message getPreconfiguredMessageWithoutData(FcmRequestDTO request) {
	        return getPreconfiguredMessageBuilder(request).setTopic(request.getTopic())
	                .build();
	    }

	    private Message getPreconfiguredMessageWithData(Map<String, String> data, FcmRequestDTO request) {
	        return getPreconfiguredMessageBuilder(request).putAllData(data).setTopic(request.getTopic())
	                .build();
	    }

	    private Message.Builder getPreconfiguredMessageBuilder(FcmRequestDTO request) {
	        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
	        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
	        Notification notification =  Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getMessage()).build();
	        return Message.builder()
	                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
	                        notification);

	    }

}
//