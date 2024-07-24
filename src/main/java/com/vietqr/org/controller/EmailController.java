package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.EmailVerifyEntity;
import com.vietqr.org.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.ResponseInputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class EmailController {
    private static final Logger logger = Logger.getLogger(EmailController.class);


    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    ImageService imageService;

    @Autowired
    AmazonS3Service amazonS3Service;

    @Autowired
    EmailVerifyService emailVerifyService;

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    AccountBankReceiveShareService accountBankReceiveShareService;

    // lấy thông tin của account xác thực OTP thành công bởi userID
    @GetMapping("bank-accounts/active-key")
    public ResponseEntity<List<BankAccountActiveKeyResponseDTO>> getBankAccounts(@RequestParam String userId) {
        List<BankAccountActiveKeyResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // cần có thông tin này
//        BankAccount
//        BankCode
//        SĐT active key (accountVietQR)
//        Thời gian lấy key
            List<BankAccountActiveKeyResponseDTO> dataBankAccount = new ArrayList<>();
            List<IBankAccountActiveKeyResponseDTO> infoBankAccount = accountBankReceiveShareService.getAccountBankInfoActiveKey(userId);
            dataBankAccount = infoBankAccount.stream().map(item -> {
                BankAccountActiveKeyResponseDTO dto = new BankAccountActiveKeyResponseDTO();
                dto.setBankId(item.getBankId());
                dto.setBankAccount(item.getBankAccount());
                dto.setBankCode(item.getBankCode());
                dto.setBankShortName(item.getBankShortName());
                dto.setUserBankName(item.getUserBankName());
                dto.setUserId(item.getUserId());
                dto.setPhoneAuthenticated(item.getPhoneAuthenticated());
                return dto;
            }).collect(Collectors.toList());

            result = dataBankAccount;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("bankAccountInfo Active Key: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("send-mail")
    public ResponseEntity<Object> sendEmailVerify(@RequestBody SendMailRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            emailService.sendMail(dto.getTo(), dto);


            result = new ResponseMessageDTO("SUCCESS", "Sent mail thành công.");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // Sending email with attachment
    @PostMapping("/sendMailWithAttachment")
    public ResponseEntity<Object> sendMailWithAttachment(@RequestBody EmailDetails details) throws MessagingException {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            // Creating a mime message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper;
//            String status = emailService.sendMailWithAttachment(details);
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setSubject("Xác nhận tài khoản của bạn với mã OTP");

            int randomOTP = generateSixDigitRandomNumber();

            String htmlMsg = "<p>Kính gửi khách hàng, </p>"
                    + "<p>Để hoàn tất quá trình đăng ký và xác minh tài khoản của bạn, vui lòng sử dụng mã OTP dưới đây<br>"
                    + "Mã OTP của bạn là: <span style=\"font-size: 18px; font-weight: bold;\">" + randomOTP + "</span><br>"
                    + "Vui lòng nhập mã OTP này vào trang xác minh để kích hoạt tài khoản của bạn. <br>"
                    + "Nếu bạn không yêu cầu mã OTP này, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi để được hỗ trợ.<br>"
                    + "Vui lòng nhập mã OTP này vào trang xác minh để kích hoạt tài khoản của bạn. </p>"
                    + "<p>Trân trọng, <br>"
                    + "VietQR VN <br>"
                    + "Email: itsupport@vietqr.vn<br>"
                    + "Hotline: 1900 6234 - phím số 3<br>"
                    + "Website: vietqr.com / vietqr.vn / vietqr.ai</p>";
            mimeMessageHelper.setText(htmlMsg, true);

            byte[] imageBytes = getImageBytes("logo-vietqr-official.png");
            if (imageBytes != null) {
                ByteArrayResource dataSource = new ByteArrayResource(imageBytes);
                mimeMessageHelper.addInline("image", dataSource, "image/png");
            }
            Thread thread = new Thread(() -> {
                javaMailSender.send(mimeMessage);
            });
            thread.start();

            // insert data vào bảng EmailVerifyEntity
            UUID id = UUID.randomUUID();
            EmailVerifyEntity emailVerifyEntity = new EmailVerifyEntity();
            emailVerifyEntity.setId(id.toString());
            emailVerifyEntity.setEmail(details.getRecipient());
            emailVerifyEntity.setUserId(details.getUserId());
            emailVerifyEntity.setOtp(randomOTP);
            LocalDateTime currentDateTime = LocalDateTime.now();
            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            emailVerifyEntity.setTimeCreated(time);
            emailVerifyEntity.setVerify(false);
            emailVerifyService.insertEmailVerify(emailVerifyEntity);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Send mail error: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("/send-mail/confirm-otp")
    public ResponseEntity<Object> confirmOTP(@RequestBody ConfirmOtpEmailDTO confirmOtpEmailDTO) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // lấy ra emailverified entity để check
            List<EmailVerifyEntity> emailVerifyByUserId = emailVerifyService.getEmailVerifyByUserId(confirmOtpEmailDTO.getUserId());
            int otpParse = Integer.parseInt(confirmOtpEmailDTO.getOtp());
            for (EmailVerifyEntity entity : emailVerifyByUserId) {
                if (entity.getOtp() == otpParse) {
                    // update isVerified = true ở bảng
                    emailVerifyService.updateEmailVerifiedByUserId(confirmOtpEmailDTO.getUserId(), otpParse);
                    // update isVerified = true ở bảng accountLogin
                    accountLoginService.updateIsVerifiedByUserId(confirmOtpEmailDTO.getUserId(), confirmOtpEmailDTO.getEmail());
                    accountInformationService.updateEmailAccountInformation(confirmOtpEmailDTO.getEmail(), confirmOtpEmailDTO.getUserId());

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                    break;
                }
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    public int generateSixDigitRandomNumber() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    private byte[] getImageBytes(String id) {
        byte[] result = new byte[0];
        try {
            try {
                ResponseInputStream<?> responseInputStream = amazonS3Service.downloadFile(id);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = responseInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                result = outputStream.toByteArray();
            } catch (Exception ignored) {
            }

            if (!(result.length > 0)) {
                result = imageService.getImageById(id);
            }
        } catch (Exception e) {
            System.out.println("Error at getImage: " + e.toString());
        }
        return result;
    }

}
