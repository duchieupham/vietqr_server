package com.vietqr.org.service;

import com.vietqr.org.dto.EmailDetailDTO;
import com.vietqr.org.dto.SendMailRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private MailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    ImageService imageService;

    @Autowired
    AmazonS3Service amazonS3Service;

    private static final String FROM_ADDRESS = "linh.npn@vietqr.vn";

//    @O

    @Override
    public void sendMessage(SimpleMailMessage simpleMailMessage) {
        this.mailSender.send(simpleMailMessage);
    }

    @Override
    public void sendMail(String toMail, SendMailRequestDTO sendMailRequest) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setSubject(sendMailRequest.getSubject());

        String htmlMsg = "Kính gửi khách hàng,\n\n"
                + "Để hoàn tất quá trình đăng ký và xác minh tài khoản của bạn, vui lòng sử dụng mã OTP dưới đây:\n"
                + "Mã OTP của bạn là: " + sendMailRequest.getMessage() + "\n"
                + "Vui lòng nhập mã OTP này vào trang xác minh để kích hoạt tài khoản của bạn. Mã OTP này sẽ hết hạn sau 10 phút.\n"
                + "Nếu bạn không yêu cầu mã OTP này, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi để được hỗ trợ.\n\n"
                + "Trân trọng,\n"
                + "Tên Công Ty: VietQR VN\n"
                + "Email: itsupport@vietqr.vn\n"
                + "Hotline: 1900 6234 - phím số 3\n"
                + "Website: vietqr.com / vietqr.vn / vietqr.ai";
        simpleMailMessage.setText(htmlMsg);

        mailSender.send(simpleMailMessage);
    }

    @Override
    public String sendMailWithAttachment(EmailDetailDTO emailDetailDTO) throws MessagingException {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            // Setting multipart as true for attachments to
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(emailDetailDTO.getRecipient());

            int randomOTP = generateSixDigitRandomNumber();

            String htmlMsg = "<p>Kính gửi khách hàng, </p>"
                    + "<p>Để hoàn tất quá trình đăng ký và xác minh tài khoản của bạn, vui lòng sử dụng mã OTP dưới đây<br>"
                    + "Mã OTP của bạn là: <span style=\"font-size: 18px; font-weight: bold;\">" + randomOTP + "</span><br>"
                    + "Vui lòng nhập mã OTP này vào trang xác minh để kích hoạt tài khoản của bạn. Mã OTP này sẽ hết hạn sau 10 phút.<br>"
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
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        } catch (MessagingException e) {
            return "Error";
        }
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
            //System.out.println("Error at getImage: " + e.toString());
        }
        return result;
    }

}
