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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.BankTextFormEntity;
import com.vietqr.org.service.BankTextFormService;

@RestController
@RequestMapping("/api")
public class BankTextFormController {

	@Autowired
	BankTextFormService bankTextFormService;

	@PostMapping(value = "bank-text-form", produces = "application/json;charset=UTF-8")
	public ResponseEntity<ResponseMessageDTO> insertBankTextForm(@Valid @RequestParam String text, @Valid @RequestParam String bankId){
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID uuid = UUID.randomUUID();
			BankTextFormEntity entity = new BankTextFormEntity();
			entity.setId(uuid.toString());
			entity.setText(text);
			entity.setBankId(bankId);
			int check = bankTextFormService.insertBankTextForm(entity);
			if(check == 1) {
				result = new ResponseMessageDTO("SUCESS", "");
				httpStatus = HttpStatus.OK;
			}else {
				result = new ResponseMessageDTO("FAILED","E10");
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		}catch(Exception e) {
			System.out.println("Error at insertBankTextForm: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("bank-text-form/{bankId}")
	public ResponseEntity<List<BankTextFormEntity>> getBankTextForms(@PathVariable("bankId") String bankId){
		List<BankTextFormEntity> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			result = bankTextFormService.getBankTextFormsByBankId(bankId);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBankTextForms: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@DeleteMapping("bank-text-form")
	public ResponseEntity<ResponseMessageDTO> deleteBankTextForm(@Valid @RequestParam String id){
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			bankTextFormService.removeBankTextForm(id);
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		}catch(Exception e) {
			System.out.println("Error at deleteBankTextForm: " + e.toString());
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}
}
