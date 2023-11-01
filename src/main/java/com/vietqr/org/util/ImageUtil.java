package com.vietqr.org.util;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.vietqr.org.dto.ImagePostConvertDTO;

public class ImageUtil {

    private static final Logger logger = Logger.getLogger(ImageUtil.class);

    public static List<ImagePostConvertDTO> parseImages(String html) {
        List<ImagePostConvertDTO> imageDTOs = new ArrayList<>();
        try {
            if (html != null && !html.trim().isEmpty()) {
                Document doc = Jsoup.parse(html);
                // Lấy danh sách các thẻ img trong HTML
                List<Element> imgElements = doc.select("img");
                if (imgElements != null && !imgElements.isEmpty()) {
                    // Lặp qua từng thẻ img và lấy giá trị base64 của hình ảnh cùng với tên
                    for (Element imgElement : imgElements) {
                        String base64Image = imgElement.attr("src").split(",")[1];
                        String name = imgElement.attr("data-filename");
                        ImagePostConvertDTO imageDTO = new ImagePostConvertDTO(base64Image, name);
                        imageDTOs.add(imageDTO);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parseImages: ERROR: " + e.toString());
        }
        return imageDTOs;
    }

    public static byte[] convertBase64ToByteArray(String base64Image) {
        byte[] result = null;
        try {
            if (base64Image != null && !base64Image.trim().isEmpty()) {
                // Lấy phần dữ liệu base64 từ đoạn input
                String base64Data = base64Image.split(",")[1];

                // Chuyển đổi base64 thành mảng byte
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                result = imageBytes;
            }
        } catch (Exception e) {
            logger.error("convertBase64ToByteArray: ERROR: " + e.toString());
        }
        return result;
    }
}
