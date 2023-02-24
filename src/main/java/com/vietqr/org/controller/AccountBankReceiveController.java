package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankReceiveDTO;
import com.vietqr.org.dto.AccountBankResponseDTO;
import com.vietqr.org.dto.BankAccountRemoveDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankTypeService;

@RestController
@RequestMapping("/api")
public class AccountBankReceiveController {

	@Autowired
	AccountBankReceiveService accountBankService;

	@Autowired
	BankTypeService bankTypeService;


	@PostMapping("account-bank")
	public ResponseEntity<ResponseMessageDTO> insertAccountBank(@Valid @RequestBody AccountBankReceiveDTO dto){
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			String checkBankAccountExisted = accountBankService.checkExistedBank(dto.getBankAccount(), dto.getBankTypeId());
			if(checkBankAccountExisted == null || checkBankAccountExisted.isEmpty()) {
//				UUID uuid = UUID.randomUUID();
//				UUID bankMemberUUID = UUID.randomUUID();
//				AccountBankReceiveEntity entity = new AccountBankReceiveEntity();
//				entity.setId(uuid.toString());
//				entity.setBankAccountName(dto.getUserBankName().toUpperCase());
//				entity.setBankAccount(dto.getBankAccount());
//				entity.setBankTypeId(dto.getBankTypeId());
//				int check =	accountBankService.insertAccountBank(entity);
//				if(check == 1) {
//					BankMemberEntity bankMemberEntity = new BankMemberEntity();
//					bankMemberEntity.setId(bankMemberUUID.toString());
//					bankMemberEntity.setBankId(uuid.toString());
//					bankMemberEntity.setUserId(dto.getUserId());
//					bankMemberEntity.setRole(dto.getRole());
//					int checkInsertBankMember = bankMemberService.insertBankMember(bankMemberEntity);
//					if(checkInsertBankMember==1) {
//						result = new ResponseMessageDTO("SUCCESS", "");
//						httpStatus = HttpStatus.OK;
//					}else {
//						result = new ResponseMessageDTO("FAILED", "Cannot insert member with ADMIN role.");
//						httpStatus = HttpStatus.BAD_REQUEST;
//					}
//				}else {
//					result = new ResponseMessageDTO("FAILED", "Insert account bank failed.");
//					httpStatus = HttpStatus.BAD_REQUEST;
//				}
			} else {
				result = new ResponseMessageDTO("FAILED", "E06");
				httpStatus = HttpStatus.BAD_REQUEST;
			}

		}catch(Exception e) {
			System.out.println("Error at insertAccountBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}



	@GetMapping("account-bank/{userId}")
	public ResponseEntity<List<AccountBankResponseDTO>> getAccountBanks(@PathVariable("userId") String userId){
		List<AccountBankResponseDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
//			List<BankMemberEntity> bankAccounts = bankMemberService.getBankAccountByUserId(userId);
//			if(!bankAccounts.isEmpty()) {
//				for (BankMemberEntity entity: bankAccounts) {
//					AccountBankReceiveEntity accountBankEntity = accountBankService.getAccountBankById(entity.getBankId());
//					BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
//					AccountBankResponseDTO dto = new AccountBankResponseDTO();
//					dto.setId(accountBankEntity.getId());
//					dto.setBankAccount(accountBankEntity.getBankAccount());
//					dto.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
//					dto.setBankCode(bankTypeEntity.getBankCode());
//					dto.setBankName(bankTypeEntity.getBankName());
//					dto.setBankStatus(bankTypeEntity.getStatus());
//					dto.setImgId(bankTypeEntity.getImgId());
//					dto.setRole(entity.getRole());
//					result.add(dto);
//				}
//			}
			httpStatus = HttpStatus.OK;
		}catch(Exception e) {
			System.out.println("Error at getAccountBanks: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}


	@DeleteMapping("account-bank")
	public ResponseEntity<ResponseMessageDTO> deleteAccountBank(@Valid @RequestBody BankAccountRemoveDTO dto){
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
//			if(dto.getRole() == 1) {
//				accountBankService.deleteAccountBank(dto.getBankId());
//				bankMemberService.deleteAllMemberByBankId(dto.getBankId());
//			}else {
//				bankMemberService.removeMemberFromBank(dto.getBankId(), dto.getUserId());
//			}

			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		}catch(Exception e) {
			System.out.println("Error at deleteAccountBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@DeleteMapping("account-bank/remove")
	public ResponseEntity<ResponseMessageDTO> removeMemberFromBank(@Valid @RequestParam String bankId, @Valid @RequestParam String userId){
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
//			bankMemberService.removeMemberFromBank(bankId, userId);
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		}catch(Exception e) {
			System.out.println("Error at deleteAccountBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}
}
