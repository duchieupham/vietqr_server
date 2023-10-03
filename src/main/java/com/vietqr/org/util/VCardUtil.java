package com.vietqr.org.util;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.vietqr.org.dto.VCardInputDTO;

public class VCardUtil {
    private static final Logger logger = Logger.getLogger(VCardUtil.class);

    public static String getVcardQR(VCardInputDTO dto) {
        String result = "";
        try {
            if (dto != null) {
                String fullnameFormatted = dto.getFullname().trim().replaceAll(" ", ";");
                String nameFormatted = dto.getFullname().trim();

                StringBuilder sb = new StringBuilder();
                sb.append("BEGIN:VCARD\r\n");
                sb.append("VERSION:3.0\r\n");
                sb.append("N;CHARSET=UTF-8:").append(fullnameFormatted).append(";\r\n");
                sb.append("FN;CHARSET=UTF-8:").append(nameFormatted).append("\r\n");
                sb.append("ORG;CHARSET=UTF-8:").append(dto.getCompanyName()).append("\r\n");
                sb.append("TITLE:\r\n");
                sb.append("TEL;TYPE=WORK,VOICE:\r\n");
                sb.append("TEL;TYPE=CELL,VOICE:").append(dto.getPhoneNo()).append("\r\n");
                sb.append("TEL;TYPE=CELL,VOICE:\r\n");
                sb.append("TEL;TYPE=CELL,VOICE:\r\n");
                sb.append("TEL;TYPE=FAX,WORK,VOICE:\r\n");
                if (dto.getAddress() != null && !dto.getAddress().trim().isEmpty()) {
                    sb.append("ADR;CHARSET=UTF-8;TYPE=HOME:;;" + dto.getAddress() + ";;;;\r\n");
                    sb.append("LABEL;CHARSET=UTF-8;TYPE=HOME:" + dto.getAddress() + "\r\n");
                    sb.append(",\r\n");
                }
                if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
                    sb.append("EMAIL;TYPE=PREF,internet:").append(dto.getEmail()).append("\r\n");
                    sb.append("EMAIL;TYPE=WORK,internet:\r\n");
                }
                if (dto.getWebsite() != null && !dto.getWebsite().trim().isEmpty()) {
                    sb.append("URL:").append(dto.getWebsite()).append("\r\n");
                    sb.append("URL:\r\n");
                    sb.append("URL:\r\n");
                }
                sb.append("REV:" + getCurrentDateTime() + "\r\n");
                sb.append("END:VCARD");

                result = sb.toString();
            }
        } catch (Exception e) {
            logger.error("VcardQR: ERROR: " + e.toString());
            System.out.println("VcardQR: ERROR: " + e.toString());
        }
        return result;
    }

    public static String normalizeVietnamese(String input) {
        String normalizedString = Normalizer.normalize(input, Normalizer.Form.NFC);
        StringBuilder sb = new StringBuilder();
        for (char c : normalizedString.toCharArray()) {
            if (c < 128) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }
}