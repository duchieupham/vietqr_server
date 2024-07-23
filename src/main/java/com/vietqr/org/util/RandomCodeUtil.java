package com.vietqr.org.util;

import com.vietqr.org.controller.TerminalController;
import org.apache.log4j.Logger;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RandomCodeUtil {
	private static final Logger logger = Logger.getLogger(RandomCodeUtil.class);

	// type = 1: Business Information code: BU
	// type = 2: Branch Information code: BR
	// type = 3: Transaction Information code: TR
	public static String generateRandomCode(int type) {
		String result = "";
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		String prefix = "";
		if (type == 1) {
			prefix = "BU";
		} else if (type == 2) {
			prefix = "BR";
		} else if (type == 3) {
			prefix = "TR";
		}
		result = prefix + generatedString.toUpperCase();
		return result;
	}

	public static String generateRandomUUID() {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString().replaceAll("-", "").substring(0, 10);
		return randomUUIDString;
	}

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public static String generateRandomId(int length) {
		char[] randomChars = new char[length];
		for (int i = 0; i < randomChars.length; i++) {
			randomChars[i] = CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length()));
		}
		return new String(randomChars);
	}

	public static String getRandomBillId() {
		String result = "";
		try {
			result = EnvironmentUtil.getPrefixBidvBillIdCommon() + DateTimeUtil.getCurrentWeekYear() +
					StringUtil.convertToHexadecimal(DateTimeUtil.getMinusCurrentDate()) + RandomCodeUtil.generateRandomId(4);
		} catch (Exception e) {
			logger.error("getRandomBillId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
		}
		return result;
	}

	private static final String DIGITS = "0123456789";

	public static String generateOTP(int length) {
		Random random = new Random();
		StringBuilder code = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(DIGITS.length());
			code.append(DIGITS.charAt(index));
		}
		return code.toString();
	}

	// random color type Contact entity from 0-4
	public static int generateRandomColorType() {
		Random random = new Random();
		int randomNumber = random.nextInt(5); // Sinh số ngẫu nhiên từ 0 đến 4
		return randomNumber;
	}
}
