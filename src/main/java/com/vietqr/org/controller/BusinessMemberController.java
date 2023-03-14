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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.MemberDeleteDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.service.BusinessMemberService;

@RestController
@RequestMapping("/api")
public class BusinessMemberController {

	@Autowired
	BusinessMemberService businessMemberService;

	@GetMapping("business-member/{id}")
	public ResponseEntity<List<MemberDTO>> getBusinessMembersByBankId(@PathVariable("id") String id) {
		List<MemberDTO> result = new ArrayList<>();
		HttpStatus httpStatus = null;
		try {
			result = businessMemberService.getBusinessMembersByBusinessId(id);
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			System.out.println("Error at getBusinessMembersByBankId: " + e.toString());
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<MemberDTO>>(result, httpStatus);
	}

	@DeleteMapping("business-member")
	public ResponseEntity<ResponseMessageDTO> deleteMemberFromBusiness(@Valid @RequestBody MemberDeleteDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			businessMemberService.deleteMemberFromBusiness(dto.getId());
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
	}

}
