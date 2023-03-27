package com.vietqr.org.util;

import java.util.Random;
import java.util.UUID;

public class RandomCodeUtil {

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
}
