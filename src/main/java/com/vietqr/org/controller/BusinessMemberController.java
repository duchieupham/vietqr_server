package com.vietqr.org.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.BusinessManagerInsertDTO;
// import com.vietqr.org.dto.MemberDeleteDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.BusinessMemberEntity;
import com.vietqr.org.service.BusinessMemberService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BusinessMemberController {

	@Autowired
	BusinessMemberService businessMemberService;

	//
	// @GetMapping("business-member/{id}")
	// public ResponseEntity<List<MemberDTO>>
	// getBusinessMembersByBankId(@PathVariable("id") String id) {
	// List<MemberDTO> result = new ArrayList<>();
	// HttpStatus httpStatus = null;
	// try {
	// result = businessMemberService.getBusinessMembersByBusinessId(id);
	// httpStatus = HttpStatus.OK;
	// } catch (Exception e) {
	// System.out.println("Error at getBusinessMembersByBankId: " + e.toString());
	// httpStatus = HttpStatus.BAD_REQUEST;
	// }
	// return new ResponseEntity<List<MemberDTO>>(result, httpStatus);
	// }

	// @DeleteMapping("business-member")
	// public ResponseEntity<ResponseMessageDTO> deleteMemberFromBusiness(@Valid
	// @RequestBody MemberDeleteDTO dto) {
	// ResponseMessageDTO result = null;
	// HttpStatus httpStatus = null;
	// try {
	// businessMemberService.deleteBusinessMemberByUserIdAndBankId(dto.getUserId(),
	// dto.getBusinessId());
	// result = new ResponseMessageDTO("SUCCESS", "");
	// httpStatus = HttpStatus.OK;
	// } catch (Exception e) {
	// result = new ResponseMessageDTO("FAILED", "Unexpected Error");
	// httpStatus = HttpStatus.BAD_REQUEST;
	// }
	// return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
	// }

	@PostMapping("business-member")
	public ResponseEntity<ResponseMessageDTO> insertBusinessMember(@Valid @RequestBody BusinessManagerInsertDTO dto) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			UUID uuid = UUID.randomUUID();
			BusinessMemberEntity entity = new BusinessMemberEntity();
			entity.setId(uuid.toString());
			entity.setBusinessId(dto.getBusinessId());
			entity.setRole(dto.getRole());
			entity.setUserId(dto.getUserId());
			businessMemberService.insertBusinessMember(entity);
			result = new ResponseMessageDTO("SUCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<ResponseMessageDTO>(result, httpStatus);
	}

}
