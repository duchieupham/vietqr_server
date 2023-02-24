package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.CaiBankDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.VietQRCreateDTO;
import com.vietqr.org.dto.VietQRCreateListDTO;
import com.vietqr.org.dto.VietQRDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.CaiBankEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.util.VietQRUtil;

@RestController
@RequestMapping("/api")
public class VietQRController {

	@Autowired
	CaiBankService caiBankService;

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	AccountBankReceiveService accountBankService;

	@PostMapping("qr/cai-bank")
	public ResponseEntity<ResponseMessageDTO> insertCaiBank(@Valid @RequestBody CaiBankDTO dto){
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
			UUID uuid = UUID.randomUUID();
			CaiBankEntity entity = new CaiBankEntity();
			entity.setId(uuid.toString());
			entity.setBankTypeId(bankTypeId);
			entity.setCaiValue(dto.getCaiValue());
			int check = caiBankService.insertCaiBank(entity);
			if(check == 1) {
				result = new ResponseMessageDTO("SUCCESS", "");
				httpStatus = HttpStatus.OK;
			}else {
				result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		}catch(Exception e) {
			System.out.println("Error at insertCaiBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
		}

	@PostMapping("qr/generate")
	public ResponseEntity<VietQRDTO> generateQR(@Valid @RequestBody VietQRCreateDTO dto){
		VietQRDTO result = null;
		HttpStatus httpStatus = null;
		try {
			//get bank information
			AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
			//get bank type information
			BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
			//get cai value
			String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
			//generate VietQRGenerateDTO
			VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
			vietQRGenerateDTO.setCaiValue(caiValue);
			vietQRGenerateDTO.setAmount(dto.getAmount());
			vietQRGenerateDTO.setContent(dto.getContent());
			vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
			String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
			//generate VietQRDTO
			VietQRDTO vietQRDTO = new VietQRDTO();
			vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
			vietQRDTO.setBankName(bankTypeEntity.getBankName());
			vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
			vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
			vietQRDTO.setAmount(dto.getAmount());
			vietQRDTO.setContent(dto.getContent());
			vietQRDTO.setQrCode(qr);
			result = vietQRDTO;
			httpStatus = HttpStatus.OK;
			//
		} catch(Exception e) {
			System.out.println("Error at insertCaiBank: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@PostMapping("qr/generate-list")
	public ResponseEntity<List<VietQRDTO>> generateQRList(@Valid @RequestBody VietQRCreateListDTO list){
		List<VietQRDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			if (!list.getDtos().isEmpty()) {
				for(VietQRCreateDTO dto: list.getDtos()) {
					//get bank information
					AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(dto.getBankId());
					//get bank type information
					BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
					//get cai value
					String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
					//generate VietQRGenerateDTO
					VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
					vietQRGenerateDTO.setCaiValue(caiValue);
					vietQRGenerateDTO.setAmount(dto.getAmount());
					vietQRGenerateDTO.setContent(dto.getContent());
					vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());			//generate QR
					String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
					//generate VietQRDTO
					VietQRDTO vietQRDTO = new VietQRDTO();
					vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
					vietQRDTO.setBankName(bankTypeEntity.getBankName());
					vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
					vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
					vietQRDTO.setAmount(dto.getAmount());
					vietQRDTO.setContent(dto.getContent());
					vietQRDTO.setQrCode(qr);
					vietQRDTO.setImgId(bankTypeEntity.getImgId());
					result.add(vietQRDTO);
					httpStatus = HttpStatus.OK;
				}
			}

		} catch(Exception e) {
			System.out.println("Error at insertCaiBank: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

}
