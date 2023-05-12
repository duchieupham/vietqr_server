package com.vietqr.org.controller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.vietqr.org.service.BranchInformationService;
import com.vietqr.org.service.BusinessMemberService;
import com.vietqr.org.dto.BusinessBranchChoiceDTO;
import com.vietqr.org.dto.BusinessChoiceDTO;
import com.vietqr.org.dto.BranchChoiceDTO;
import com.vietqr.org.dto.BranchChoiceReponseDTO;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BranchInformationController {

    @Autowired
    BusinessMemberService businessMemberService;

    @Autowired
    BranchInformationService branchInformationService;

    @GetMapping("branch-manage/{userId}")
    public ResponseEntity<List<BusinessBranchChoiceDTO>> getBusinessBranchChoice(
            @PathVariable("userId") String userId) {
        List<BusinessBranchChoiceDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<BusinessChoiceDTO> businessChoices = businessMemberService.getBusinessChoiceByUserId(userId);
            if (businessChoices != null && !businessChoices.isEmpty()) {
                for (BusinessChoiceDTO businessChoice : businessChoices) {
                    BusinessBranchChoiceDTO dto = new BusinessBranchChoiceDTO();
                    dto.setName(businessChoice.getName());
                    dto.setImage(businessChoice.getImgId());
                    dto.setCoverImage(businessChoice.getCoverImgId());
                    List<BranchChoiceReponseDTO> branchChoicesResponse = new ArrayList<>();
                    List<BranchChoiceDTO> branchChocies = branchInformationService
                            .getBranchsByBusinessId(businessChoice.getBusinessId());
                    if (branchChocies != null && !branchChocies.isEmpty()) {
                        for (BranchChoiceDTO branchChoice : branchChocies) {
                            BranchChoiceReponseDTO bankChoiceResponse = new BranchChoiceReponseDTO();
                            bankChoiceResponse.setBranchId(branchChoice.getBranchId());
                            bankChoiceResponse.setName(branchChoice.getName());
                            bankChoiceResponse.setAddress(branchChoice.getAddress());
                            branchChoicesResponse.add(bankChoiceResponse);
                        }
                    }
                    dto.setBranchs(branchChoicesResponse);
                    result.add(dto);
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<List<BusinessBranchChoiceDTO>>(result, httpStatus);
    }
}
