package com.vietqr.org.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.FcmRequestDTO;
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.util.FcmUtil;

@Service
public class FirebaseMessagingService {
	//
	// @Autowired
	// FirebaseMessaging firebaseMessaging;
	//
	// public void sendNotification(String registrationToken, String title, String
	// body) throws Exception {
	// Notification notification = Notification.builder()
	// .setTitle(title)
	// .setBody(body)
	// .build();
	// Message message = Message.builder()
	// .setNotification(notification)
	// .setToken(registrationToken)
	//// .putAllData(null)
	// .build();
	// //local host
	// String response = FirebaseMessaging.getInstance().send(message);
	// //deploy
	//// String response = firebaseMessaging.send(message);
	// System.out.println("Successfully sent message: " + response);
	// }

	private Logger logger = Logger.getLogger(FirebaseMessagingService.class);
	private FcmUtil fcmUtil;

	@Autowired
	FcmTokenService fcmTokenService;

	public FirebaseMessagingService(FcmUtil fcmUtil) {
		this.fcmUtil = fcmUtil;
	}

	// @Scheduled(initialDelay = 60000, fixedDelay = 60000)
	// public void sendSamplePushNotification() {
	// try {
	// fcmUtil.sendMessageWithoutData(getSamplePushNotificationRequest());
	// } catch (InterruptedException | ExecutionException e) {
	// logger.error(e.getMessage());
	// }
	// }

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

	public void sendPushNotificationWithData(Map<String, String> data, FcmRequestDTO request) {
		try {
			fcmUtil.sendMessage(data, request);
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Error at sendPushNotificationWithData" + e.toString());
			logger.error(e.getMessage());
			if (e.toString()
					.contains(
							"The registration token is not a valid FCM registration token")
					|| e.toString().contains("Requested entity was not found")) {
				fcmTokenService.deleteFcmToken(request.getToken());
			}
		}
	}

	public void sendUsersNotificationWithData(Map<String, String> data, List<FcmTokenEntity> fcmTokens, String title,
			String message) {
//		System.out.println("Before sleep: " + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			Thread.currentThread().interrupt();
//		}
//		System.out.println("After sleep: " + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

		if (fcmTokens != null && !fcmTokens.isEmpty()) {
			for (FcmTokenEntity fcmToken : fcmTokens) {
				try {
					if (!fcmToken.getToken().trim().isEmpty()) {
						FcmRequestDTO fcmDTO = new FcmRequestDTO();
						fcmDTO.setTitle(title);
						fcmDTO.setMessage(message);
						fcmDTO.setToken(fcmToken.getToken());
						sendPushNotificationWithData(data, fcmDTO);
						LocalDateTime currentDateTime = LocalDateTime.now();
						long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
						logger.info("Send notification to device " + fcmToken.getToken() + " at: " + time);

					}
				} catch (Exception e) {
					System.out.println("Error at sendUsersNotificationWithData" + e.toString());
					logger.error(
							"Error when Send Notification using FCM " + e.toString());
					if (e.toString()
							.contains(
									"The registration token is not a valid FCM registration token")) {
						fcmTokenService.deleteFcmToken(fcmToken.getToken());
					}
				}
			}
		}
	}

	// private Map<String, String> getSamplePayloadData() {
	// Map<String, String> pushData = new HashMap<>();
	// pushData.put("messageId", defaults.get("payloadMessageId"));
	// pushData.put("text", defaults.get("payloadData") + " " +
	// LocalDateTime.now());
	// return pushData;
	// }
	//
	//
	// private PushNotificationRequest getSamplePushNotificationRequest() {
	// PushNotificationRequest request = new
	// PushNotificationRequest(defaults.get("title"),
	// defaults.get("message"),
	// defaults.get("topic"));
	// return request;
	// }
}
