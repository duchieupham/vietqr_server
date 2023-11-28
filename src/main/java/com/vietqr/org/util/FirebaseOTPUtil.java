package com.vietqr.org.util;

import java.util.concurrent.TimeUnit;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.type.PhoneNumber;

public class FirebaseOTPUtil {
    private FirebaseAuth firebaseAuth;

    // public FirebaseOTPUtil() {
    // // Khởi tạo FirebaseApp
    // FirebaseApp.initializeApp();
    // // Lấy thể hiện của FirebaseAuth
    // firebaseAuth = FirebaseAuth.getInstance();
    // }

    // public String sendOTP(String phoneNumber) throws FirebaseAuthException {

    // UserRecord userRecord = firebaseAuth.getUserByEmail(phoneNumber);
    // String uid = userRecord.getUid();

    // String verificationCode =
    // firebaseAuth.generateSignInWithEmailLink(phoneNumber,
    // actionCodeSettings).getLink();

    // return verificationCode;
    // }

    // public boolean verifyOTP(String verificationId, String otp) throws
    // FirebaseAuthException {

    // }

}
