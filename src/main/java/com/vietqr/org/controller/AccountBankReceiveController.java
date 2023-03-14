package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RestController;
import com.vietqr.org.dto.AccountBankReceiveDTO;
import com.vietqr.org.dto.AccountBankResponseDTO;
import com.vietqr.org.dto.BankAccountRemoveDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.AccountBankReceivePersonalDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.BankReceiveBranchEntity;
import com.vietqr.org.entity.BankReceivePersonalEntity;
import com.vietqr.org.service.AccountBankReceivePersonalService;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.BankReceiveBranchService;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.BranchMemberService;
import com.vietqr.org.service.BranchInformationService;

@RestController
@RequestMapping("/api")
public class AccountBankReceiveController {

	@Autowired
	AccountBankReceiveService accountBankService;

	@Autowired
	BankTypeService bankTypeService;

	@Autowired
	AccountBankReceivePersonalService bankReceivePersonalService;

	@Autowired
	BankReceiveBranchService bankReceiveBranchService;

	@Autowired
	BranchInformationService branchInformationService;

	@Autowired
	BranchMemberService branchMemberService;

	@PostMapping("account-bank")
	public ResponseEntity<ResponseMessageDTO> insertAccountBank(@Valid @RequestBody AccountBankReceiveDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID uuid = UUID.randomUUID();
			AccountBankReceiveEntity entity = new AccountBankReceiveEntity();
			entity.setId(uuid.toString());
			entity.setBankTypeId(dto.getBankTypeId());
			entity.setBankAccount(dto.getBankAccount());
			entity.setBankAccountName(dto.getUserBankName());
			entity.setType(dto.getType());
			accountBankService.insertAccountBank(entity);
			if (dto.getType() == 0) {
				// insert bank receive personal
				UUID uuidPersonal = UUID.randomUUID();
				BankReceivePersonalEntity personalEntity = new BankReceivePersonalEntity();
				personalEntity.setId(uuidPersonal.toString());
				personalEntity.setBankId(uuid.toString());
				personalEntity.setUserId(dto.getUserId());
				bankReceivePersonalService.insertAccountBankReceivePersonal(personalEntity);
			} else if (dto.getType() == 1) {
				// insert bank_receive_branch
				UUID uuidBankReceiveBranch = UUID.randomUUID();
				BankReceiveBranchEntity bankReceiveBranchEntity = new BankReceiveBranchEntity();
				bankReceiveBranchEntity.setId(uuidBankReceiveBranch.toString());
				bankReceiveBranchEntity.setBranchId(dto.getBranchId());
				bankReceiveBranchEntity.setBankId(uuid.toString());
				bankReceiveBranchService.insertBankReceiveBranch(bankReceiveBranchEntity);
			}
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at insertAccountBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("account-bank/{userId}")
	public ResponseEntity<List<AccountBankResponseDTO>> getAccountBanks(@PathVariable("userId") String userId) {
		List<AccountBankResponseDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			// get list personal bank
			//
			List<AccountBankReceivePersonalDTO> personalBanks = bankReceivePersonalService
					.getBankReceivePersonals(userId);
			if (personalBanks != null && !personalBanks.isEmpty()) {
				for (AccountBankReceivePersonalDTO personalBank : personalBanks) {
					AccountBankResponseDTO dto = new AccountBankResponseDTO();
					dto.setId(personalBank.getBankId());
					dto.setBankAccount(personalBank.getBankAccount());
					dto.setUserBankName(personalBank.getUserBankName());
					dto.setBankCode(personalBank.getBankCode());
					dto.setBankName(personalBank.getBankName());
					dto.setImgId(personalBank.getImgId());
					dto.setType(personalBank.getBankType());
					dto.setBranchId("");
					dto.setBusinessId("");
					dto.setBranchName("");
					dto.setBranchCode("");
					dto.setBusinessName("");
					dto.setBusinessCode("");
					result.add(dto);
				}
			}
			// get list business bank (with branch)
			List<String> branchIds = new ArrayList<>();
			// 1. check user is admin/manager of business or not.
			List<String> branchIdsByUserIdBusiness = branchInformationService.getBranchIdsByUserIdBusiness(userId);
			// 2. get list branch id of user
			// 3. get list business bank by userId
			List<String> branchIdsByUserIdBranch = branchMemberService.getBranchIdsByUserId(userId);
			// add all branchIds
			if (branchIdsByUserIdBusiness != null && !branchIdsByUserIdBusiness.isEmpty()) {
				branchIds.addAll(branchIdsByUserIdBusiness);
			}
			if (branchIdsByUserIdBranch != null && !branchIdsByUserIdBranch.isEmpty()) {
				branchIds.addAll(branchIdsByUserIdBranch);
			}
			List<AccountBankReceivePersonalDTO> businessBanks = new ArrayList<>();
			if (branchIds != null && !branchIds.isEmpty()) {
				for (String branchId : branchIds) {
					List<AccountBankReceivePersonalDTO> banks = bankReceiveBranchService
							.getBankReceiveBranchs(branchId);
					if (banks != null && !banks.isEmpty()) {
						for (AccountBankReceivePersonalDTO bank : banks) {
							businessBanks.add(bank);
						}
					}
				}
			}
			if (businessBanks != null && !businessBanks.isEmpty()) {
				for (AccountBankReceivePersonalDTO bank : businessBanks) {
					AccountBankResponseDTO dto = new AccountBankResponseDTO();
					dto.setId(bank.getBankId());
					dto.setBankAccount(bank.getBankAccount());
					dto.setUserBankName(bank.getUserBankName());
					dto.setBankCode(bank.getBankCode());
					dto.setBankName(bank.getBankName());
					dto.setImgId(bank.getImgId());
					dto.setType(bank.getBankType());
					dto.setBranchId(bank.getBranchId());
					dto.setBusinessId(bank.getBusinessId());
					dto.setBranchName(bank.getBranchName());
					dto.setBusinessName(bank.getBusinessName());
					dto.setBranchCode(bank.getBranchCode());
					dto.setBusinessCode(bank.getBusinessCode());
					result.add(dto);
				}
			}
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getAccountBanks: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@DeleteMapping("account-bank")
	public ResponseEntity<ResponseMessageDTO> deleteAccountBank(@Valid @RequestBody BankAccountRemoveDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			// if(dto.getRole() == 1) {
			// accountBankService.deleteAccountBank(dto.getBankId());
			// bankMemberService.deleteAllMemberByBankId(dto.getBankId());
			// }else {
			// bankMemberService.removeMemberFromBank(dto.getBankId(), dto.getUserId());
			// }

			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at deleteAccountBank: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	// @DeleteMapping("account-bank/remove")
	// public ResponseEntity<ResponseMessageDTO> removeMemberFromBank(@Valid
	// @RequestParam String bankId,
	// @Valid @RequestParam String userId) {
	// ResponseMessageDTO result = null;
	// HttpStatus httpStatus = null;
	// try {
	// // bankMemberService.removeMemberFromBank(bankId, userId);
	// result = new ResponseMessageDTO("SUCCESS", "");
	// httpStatus = HttpStatus.OK;
	// } catch (Exception e) {
	// System.out.println("Error at deleteAccountBank: " + e.toString());
	// result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
	// httpStatus = HttpStatus.BAD_REQUEST;
	// }
	// return new ResponseEntity<>(result, httpStatus);
	// }
}
