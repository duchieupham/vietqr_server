package com.vietqr.org.util;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

//import com.google.api.client.util.Value;
import org.springframework.beans.factory.annotation.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FcmConfiguration {
	private static final Logger logger = Logger.getLogger(FcmConfiguration.class);
//
//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//    	//For deploy API
//		String firebaseConfigPath = System.getenv("FIREBASE_CONFIG_PATH");
//		LocalDateTime currentDateTime = LocalDateTime.now();
//		logger.info("firebaseConfigPath " + firebaseConfigPath + " - time: " + currentDateTime.toString());
//        FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//        return FirebaseApp.initializeApp(options);
//    }
//
//    @Bean
//    public FirebaseMessaging firebaseMessaging() throws IOException {
//        return FirebaseMessaging.getInstance(firebaseApp());
//    }

	@Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

	@PostConstruct
	public void initialize() {
	        try {
	            FirebaseOptions options = new FirebaseOptions.Builder()
	                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())).build();
	            if (FirebaseApp.getApps().isEmpty()) {
	                FirebaseApp.initializeApp(options);
	        		LocalDateTime currentDateTime = LocalDateTime.now();
	                logger.info("Firebase application has been initialized at " + currentDateTime.toString());
	            }
	        } catch (IOException e) {
	            logger.error(e.getMessage());
	        }
	}
}

