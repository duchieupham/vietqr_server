package com.vietqr.org.util;

// import org.springframework.scheduling.annotation.Async;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vietqr.org.dto.VietQRGenerateDTO;

import javax.imageio.ImageIO;

import com.vietqr.org.dto.qrfeed.QrLinkDTO;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class VietQRUtil {
	private static final Logger logger = Logger.getLogger(VietQRUtil.class);

		private static final int QR_CODE_WIDTH = 400;
		private static final int QR_CODE_HEIGHT = 600;
	private static final int CENTER_ICON_WIDTH = 30;
	private static final int CENTER_ICON_HEIGHT = 30;
	private static final int HEADER_WIDTH = 140;
	private static final int HEADER_HEIGHT = 60;
	private static final int BOTTOM_LEFT_WIDTH = 100;
	private static final int BOTTOM_LEFT_HEIGHT = 30;

	// @Async
	public static String generateStaticQR(VietQRGenerateDTO dto) {
		String result = "";
		// Payload Format Indicator
		String pfi = VietQRServiceIdUtil.getPayloadFormatIndicatorId()
				+ getValueLength(VietQRServiceValueUtil.getPayloadFormatIndicatorValue())
				+ VietQRServiceValueUtil.getPayloadFormatIndicatorValue();
		// Point of Initiation Method
		String poim = VietQRServiceIdUtil.getPointOfInitiationMethodId() +
				getValueLength(VietQRServiceValueUtil.getPointOfInitiationMethodValueStatic()) +
				VietQRServiceValueUtil.getPointOfInitiationMethodValueStatic();
		// Consumer Account Information
		String cai = VietQRServiceIdUtil.getMerchantAccountInformationId() +
				getValueLength(generateCAI(dto.getCaiValue(), dto.getBankAccount())) +
				generateCAI(dto.getCaiValue(), dto.getBankAccount());
		// Transaction Currency
		String tc = VietQRServiceIdUtil.getTransactionCurrencyId() +
				getValueLength(VietQRServiceValueUtil.getTransactionCurrencyValue()) +
				VietQRServiceValueUtil.getTransactionCurrencyValue();
		// Country Code
		String cc = VietQRServiceIdUtil.getCountryCodeId() +
				getValueLength(VietQRServiceValueUtil.getCountryCodeValue()) +
				VietQRServiceValueUtil.getCountryCodeValue();
		// CRC ID + CRC Length + CRC value (Cyclic Redundancy Check)
		String crcValue = generateCRC(pfi +
				poim +
				cai +
				tc +
				cc +
				VietQRServiceIdUtil.getCrcId() +
				VietQRServiceValueUtil.getCrcLength());
		String crc = VietQRServiceIdUtil.getCrcId()
				+ VietQRServiceValueUtil.getCrcLength()
				+ crcValue;
		result = pfi + poim + cai + tc + cc + crc;
		return result;
	}

	// @Async
	public static String generateTransactionQR(VietQRGenerateDTO dto) {
		String result = "";
		if (dto.getAmount().isEmpty() && dto.getContent().isEmpty()) {
			result = generateStaticQR(dto);
		} else {
			// Payload Format Indicator
			String pfi = VietQRServiceIdUtil.getPayloadFormatIndicatorId()
					+ getValueLength(VietQRServiceValueUtil.getPayloadFormatIndicatorValue())
					+ VietQRServiceValueUtil.getPayloadFormatIndicatorValue();
			// Point of Initiation Method
			String poim = VietQRServiceIdUtil.getPointOfInitiationMethodId() +
					getValueLength(VietQRServiceValueUtil.getPointOfInitiationMethodValueStatic()) +
					VietQRServiceValueUtil.getPointOfInitiationMethodValueStatic();
			// Consumer Account Information
			String cai = VietQRServiceIdUtil.getMerchantAccountInformationId() +
					getValueLength(generateCAI(dto.getCaiValue(), dto.getBankAccount())) +
					generateCAI(dto.getCaiValue(), dto.getBankAccount());
			// Transaction Currency
			String tc = VietQRServiceIdUtil.getTransactionCurrencyId() +
					getValueLength(VietQRServiceValueUtil.getTransactionCurrencyValue()) +
					VietQRServiceValueUtil.getTransactionCurrencyValue();
			// Transaction Amount
			String ta = VietQRServiceIdUtil.getTransactionAmountId() +
					getValueLength(dto.getAmount()) +
					dto.getAmount();
			// Country Code
			String cc = VietQRServiceIdUtil.getCountryCodeId() +
					getValueLength(VietQRServiceValueUtil.getCountryCodeValue()) +
					VietQRServiceValueUtil.getCountryCodeValue();
			// Additional Data Field Template
			String adft = "";
			if (!dto.getContent().isEmpty()) {
				adft = VietQRServiceIdUtil.getAdditionalDataFieldTemplateId() +
						getValueLength(getAdditionalDataFieldTemplateValue(dto.getContent())) +
						getAdditionalDataFieldTemplateValue(dto.getContent());
			} else {
				adft = VietQRServiceIdUtil.getAdditionalDataFieldTemplateId() +
						getValueLength(" ") +
						" ";
			}
			// CRC ID + CRC Length + CRC value (Cyclic Redundancy Check)
			String crcValue = "";
			if (dto.getContent().isEmpty()) {
				crcValue = generateCRC(pfi +
						poim +
						cai +
						tc +
						ta +
						cc +
						VietQRServiceIdUtil.getCrcId() +
						VietQRServiceValueUtil.getCrcLength());
				String crc = VietQRServiceIdUtil.getCrcId()
						+ VietQRServiceValueUtil.getCrcLength()
						+ crcValue;
				result = pfi + poim + cai + tc + ta + cc + crc;
			} else {
				crcValue = generateCRC(pfi +
						poim +
						cai +
						tc +
						ta +
						cc +
						adft +
						VietQRServiceIdUtil.getCrcId() +
						VietQRServiceValueUtil.getCrcLength());
				String crc = VietQRServiceIdUtil.getCrcId()
						+ VietQRServiceValueUtil.getCrcLength()
						+ crcValue;
				result = pfi + poim + cai + tc + ta + cc + adft + crc;
			}
		}
		return result;
	}

	private static String generateCAI(String caiValue, String bankAccount) {
		String result = "";
		String middleCAI = VietQRServiceIdUtil.getPayloadFormatIndicatorId()
				+ VietQRAdditionalDataUtil.getCustomerLabelId()
				+ caiValue
				+ VietQRServiceValueUtil.getPayloadFormatIndicatorValue()
				+ getValueLength(bankAccount)
				+ bankAccount;
		result = getGUID()
				+ VietQRServiceIdUtil.getPointOfInitiationMethodId()
				+ getValueLength(middleCAI)
				+ middleCAI
				+ VietQRServiceIdUtil.getTransferServciceCode()
				+ getValueLength(VietQRTransferServiceCode.getQuickTransferFromQrToBankAccount())
				+ VietQRTransferServiceCode.getQuickTransferFromQrToBankAccount();
		return result;
	}

	private static String getGUID() {
		String result = "";
		result = VietQRServiceIdUtil.getPayloadFormatIndicatorId()
				+ getValueLength(VietQRServiceAIDUtil.getAidNapas())
				+ VietQRServiceAIDUtil.getAidNapas();
		return result;
	}

	private static String getAdditionalDataFieldTemplateValue(String value) {
		String result = "";
		if (!value.isEmpty()) {
			result = VietQRAdditionalDataUtil.getPurposeOfTransactionId() + getValueLength(value) + value;
		}
		return result;
	}

	private static String getValueLength(String value) {
		String result = "00";
		if (!value.isEmpty()) {
			int length = value.length();
			if (length < 10) {
				result = "0" + Integer.toString(length);
			} else {
				result = Integer.toString(length);
			}
		}
		return result;
	}

	// Tạo mã CRC theo chuẩn CRC-16/CCITT-FALSE
	private static String generateCRC(String value) {
		int crc = 0xFFFF;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			crc ^= (c << 8);

			for (int j = 0; j < 8; j++) {
				if ((crc & 0x8000) != 0) {
					crc = (crc << 1) ^ 0x1021;
				} else {
					crc <<= 1;
				}
			}
		}

		crc &= 0xFFFF;
		return String.format("%04X", crc);
	}

	private static void drawQRCode(BitMatrix bitMatrix, Graphics2D graphics, int width, int height) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (bitMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
	}

	private static void drawImage(Graphics2D graphics, byte[] imageBytes, int xPos, int yPos, int imgWidth,
			int imgHeight) throws IOException {
		if (imageBytes != null && imageBytes.length != 0) {
			BufferedImage image = ImageUtil.byteArrayToBufferedImage(imageBytes);
			graphics.drawImage(image, xPos, yPos, imgWidth, imgHeight, null);
		}
	}

	// Create QR image
	public static byte[] generateVietQRImg(String value, byte[] header, byte[] centerIcon, byte[] bottonLeft) {
		byte[] result = null;
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

			BufferedImage bufferedImage = new BufferedImage(QR_CODE_WIDTH, QR_CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = bufferedImage.createGraphics();

			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, QR_CODE_WIDTH, QR_CODE_HEIGHT);
			graphics.setColor(Color.BLACK);

			drawQRCode(bitMatrix, graphics, QR_CODE_WIDTH, QR_CODE_HEIGHT);
			drawImage(graphics, centerIcon, (QR_CODE_WIDTH - CENTER_ICON_WIDTH) / 2,
					(QR_CODE_HEIGHT - CENTER_ICON_HEIGHT) / 2, CENTER_ICON_WIDTH, CENTER_ICON_HEIGHT);
			drawImage(graphics, header, (QR_CODE_WIDTH - HEADER_WIDTH) / 2, 140 - HEADER_HEIGHT, HEADER_WIDTH,
					HEADER_HEIGHT);
			drawImage(graphics, bottonLeft, 40, 460, BOTTOM_LEFT_WIDTH, BOTTOM_LEFT_HEIGHT);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

			result = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			result = new byte[0];
			//System.out.println("generateVietQRImg: ERROR: " + e.toString());
			logger.error("generateVietQRImg: ERROR: " + e.toString());
		}

		return result;
	}



//	public static String generateTransactionQRWithLogoBase64(VietQRGenerateDTO dto, byte[] logoBytes) throws Exception {
//		// Generate QR Code string
//		String qrCodeString = generateTransactionQR(dto);
//
//		// Create QR Code with specific size
//		QRCodeWriter qrCodeWriter = new QRCodeWriter();
//		Map<EncodeHintType, Object> hints = new HashMap<>();
//		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High level of error correction for logo support
//
//		// Encode the QR code with the given data
//		BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeString, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);
//
//		// Create a buffered image to draw the QR code on
//		BufferedImage qrImage = new BufferedImage(QR_CODE_WIDTH, QR_CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
//		Graphics2D graphics = qrImage.createGraphics();
//		graphics.setColor(Color.WHITE);
//		graphics.fillRect(0, 0, QR_CODE_WIDTH, QR_CODE_HEIGHT);
//		graphics.setColor(Color.BLACK);
//
//		// Draw the QR code pixels
//		for (int i = 0; i < QR_CODE_WIDTH; i++) {
//			for (int j = 0; j < QR_CODE_HEIGHT; j++) {
//				if (bitMatrix.get(i, j)) {
//					graphics.fillRect(i, j, 1, 1);
//				}
//			}
//		}
//
//		// Insert the logo with white circular background in the center of the QR code
//		if (logoBytes != null && logoBytes.length > 0) {
//			BufferedImage logoImage = ImageIO.read(new ByteArrayInputStream(logoBytes));
//
//			// Rescale the logo to make it smaller
//			int logoScaledWidth = QR_CODE_WIDTH / 8; // Adjust the size ratio
//			int logoScaledHeight = QR_CODE_HEIGHT / 8;
//
//			// Create a circular clipping path
//			int circleDiameter = logoScaledWidth + 40; // Slightly larger than the logo
//			int centerX = (QR_CODE_WIDTH - circleDiameter) / 2;
//			int centerY = (QR_CODE_HEIGHT - circleDiameter) / 2;
//
//			// Draw the white circle behind the logo
//			graphics.setColor(Color.WHITE);
//			graphics.fillOval(centerX, centerY, circleDiameter, circleDiameter); // Draw a circle, not a square
//
//			// Scale the logo
//			Image scaledLogo = logoImage.getScaledInstance(logoScaledWidth, logoScaledHeight, Image.SCALE_SMOOTH);
//
//			// Draw the logo inside the circle
//			int logoX = (QR_CODE_WIDTH - logoScaledWidth) / 2;
//			int logoY = (QR_CODE_HEIGHT - logoScaledHeight) / 2;
//			graphics.drawImage(scaledLogo, logoX, logoY, null);
//		}
//
//		// Dispose of the graphics context and flush
//		graphics.dispose();
//
//		// Convert the QR code with logo to a Base64 string
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		ImageIO.write(qrImage, "png", outputStream);
//		byte[] qrImageBytes = outputStream.toByteArray();
//
//		return Base64.getEncoder().encodeToString(qrImageBytes);
//	}


	public static String generateTransactionQRWithLogoBase64(VietQRGenerateDTO dto, byte[] logoBytes) throws Exception {
		// Generate QR Code string
		String qrCodeString = generateTransactionQR(dto);

		// Create QR Code with specific size
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High level of error correction for logo support

		// Encode the QR code with the given data
		BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeString, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);

		// Trim the QR code size to remove unnecessary white border
		bitMatrix = trimWhiteBorder(bitMatrix);

		int trimmedWidth = bitMatrix.getWidth();
		int trimmedHeight = bitMatrix.getHeight();

		// Add padding (white border) around the QR code
		int padding = 20; // Adjust padding size as needed
		int newWidth = trimmedWidth + 2 * padding;
		int newHeight = trimmedHeight + 2 * padding;

		// Create a buffered image to draw the QR code on with padding
		BufferedImage qrImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = qrImage.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, newWidth, newHeight);
		graphics.setColor(Color.BLACK);

		// Draw the QR code pixels in the center with padding
		for (int i = 0; i < trimmedWidth; i++) {
			for (int j = 0; j < trimmedHeight; j++) {
				if (bitMatrix.get(i, j)) {
					graphics.fillRect(i + padding, j + padding, 1, 1);
				}
			}
		}

		// Insert the logo with white circular background in the center of the QR code
		if (logoBytes != null && logoBytes.length > 0) {
			BufferedImage logoImage = ImageIO.read(new ByteArrayInputStream(logoBytes));

			// Rescale the logo to make it smaller
			int logoScaledWidth = trimmedWidth / 8; // Adjust the size ratio
			int logoScaledHeight = trimmedHeight / 8;

			// Create a circular clipping path
			int circleDiameter = logoScaledWidth + 40; // Slightly larger than the logo
			int centerX = (newWidth - circleDiameter) / 2;
			int centerY = (newHeight - circleDiameter) / 2;

			// Draw the white circle behind the logo
			graphics.setColor(Color.WHITE);
			graphics.fillOval(centerX, centerY, circleDiameter, circleDiameter); // Draw a circle, not a square

			// Scale the logo
			Image scaledLogo = logoImage.getScaledInstance(logoScaledWidth, logoScaledHeight, Image.SCALE_SMOOTH);

			// Draw the logo inside the circle
			int logoX = (newWidth - logoScaledWidth) / 2;
			int logoY = (newHeight - logoScaledHeight) / 2;
			graphics.drawImage(scaledLogo, logoX, logoY, null);
		}

		// Dispose of the graphics context and flush
		graphics.dispose();

		// Convert the QR code with logo to a Base64 string
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(qrImage, "png", outputStream);
		byte[] qrImageBytes = outputStream.toByteArray();

		return Base64.getEncoder().encodeToString(qrImageBytes);
	}

	private static BitMatrix trimWhiteBorder(BitMatrix matrix) {
		int left = matrix.getWidth(), top = matrix.getHeight(), right = 0, bottom = 0;
		for (int x = 0; x < matrix.getWidth(); x++) {
			for (int y = 0; y < matrix.getHeight(); y++) {
				if (matrix.get(x, y)) {
					if (x < left) left = x;
					if (x > right) right = x;
					if (y < top) top = y;
					if (y > bottom) bottom = y;
				}
			}
		}

		int newWidth = right - left + 1;
		int newHeight = bottom - top + 1;
		BitMatrix trimmedMatrix = new BitMatrix(newWidth, newHeight);
		for (int x = 0; x < newWidth; x++) {
			for (int y = 0; y < newHeight; y++) {
				if (matrix.get(x + left, y + top)) {
					trimmedMatrix.set(x, y);
				}
			}
		}
		return trimmedMatrix;
	}




}
