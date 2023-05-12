package com.vietqr.org.util;

// import org.springframework.scheduling.annotation.Async;

import com.vietqr.org.dto.VietQRGenerateDTO;

public class VietQRUtil {

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
}
