package com.vietqr.org.service.qrfeed;

import com.google.zxing.WriterException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface QRCodeService {

    public byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException;
    public String generateQRCodeImageBase64(String text, int width, int height) throws WriterException, IOException;
}
