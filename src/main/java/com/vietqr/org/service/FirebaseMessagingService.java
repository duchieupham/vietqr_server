package com.vietqr.org.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.FcmRequestDTO;
import com.vietqr.org.util.FcmUtil;

@Service
public class FirebaseMessagingService {
//
//	@Autowired
//	FirebaseMessaging firebaseMessaging;
//
//	 public void sendNotification(String registrationToken, String title, String body) throws Exception {
//		 Notification notification = Notification.builder()
//			        .setTitle(title)
//			        .setBody(body)
//			        .build();
//	        Message message = Message.builder()
//	                .setNotification(notification)
//	                .setToken(registrationToken)
////	                .putAllData(null)
//	                .build();
//	        //local host
//	        String response = FirebaseMessaging.getInstance().send(message);
//	        //deploy
////	        String response = firebaseMessaging.send(message);
//	        System.out.println("Successfully sent message: " + response);
//	    }

	    private Logger logger = Logger.getLogger(FirebaseMessagingService.class);
	    private FcmUtil fcmUtil;

	    public FirebaseMessagingService(FcmUtil fcmUtil) {
	        this.fcmUtil = fcmUtil;
	    }

//	    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
//	    public void sendSamplePushNotification() {
//	        try {
//	            fcmUtil.sendMessageWithoutData(getSamplePushNotificationRequest());
//	        } catch (InterruptedException | ExecutionException e) {
//	            logger.error(e.getMessage());
//	        }
//	    }

	    public void sendPushNotification(FcmRequestDTO request, Map<String, String> data) {
	        try {
	            fcmUtil.sendMessage(data, request);
	        } catch (InterruptedException | ExecutionException e) {
	            logger.error(e.getMessage());
	        }
	    }

	    public void sendPushNotificationWithoutData(FcmRequestDTO request) {
	        try {
	            fcmUtil.sendMessageWithoutData(request);
	        } catch (InterruptedException | ExecutionException e) {
	            logger.error(e.getMessage());
	        }
	    }

	    public void sendPushNotificationToToken(FcmRequestDTO request) {
	        try {
	            fcmUtil.sendMessageToToken(request);
	        } catch (InterruptedException | ExecutionException e) {
	            logger.error(e.getMessage());
	        }
	    }


//	    private Map<String, String> getSamplePayloadData() {
//	        Map<String, String> pushData = new HashMap<>();
//	        pushData.put("messageId", defaults.get("payloadMessageId"));
//	        pushData.put("text", defaults.get("payloadData") + " " + LocalDateTime.now());
//	        return pushData;
//	    }
//
//
//	    private PushNotificationRequest getSamplePushNotificationRequest() {
//	        PushNotificationRequest request = new PushNotificationRequest(defaults.get("title"),
//	                defaults.get("message"),
//	                defaults.get("topic"));
//	        return request;
//	    }
}
