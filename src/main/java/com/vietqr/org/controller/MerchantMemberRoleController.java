package com.vietqr.org.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.FormMerchantRoleDTO;
import com.vietqr.org.dto.IRawTransRoleDTO;
import com.vietqr.org.dto.RoleMemberDTO;
import com.vietqr.org.service.TransactionReceiveRoleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantMemberRoleController {

    private static final Logger logger = Logger.getLogger(MerchantMemberRoleController.class);

    @Autowired
    private TransactionReceiveRoleService transactionReceiveRoleService;

    @GetMapping("form-role")
    public ResponseEntity<List<FormMerchantRoleDTO>> getListRoleByType() {
        List<FormMerchantRoleDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<IRawTransRoleDTO> rawTransRoleDTOS = transactionReceiveRoleService.getAllRole();
            List<FormMerchantRoleDTO> roles = new ArrayList<>();
            if (rawTransRoleDTOS != null) {
                Map<Integer, List<IRawTransRoleDTO>> roleMaps = rawTransRoleDTOS.stream()
                        .collect(Collectors.groupingBy(IRawTransRoleDTO::getLevel));
                roles = roleMaps.entrySet().stream().map(entry -> {
                    List<RoleMemberDTO> roleSettingDTOS = entry.getValue().stream()
                            .map(role -> {
                                RoleMemberDTO roleSettingDTO = new RoleMemberDTO();
                                roleSettingDTO.setId(role.getId());
                                roleSettingDTO.setName(role.getName());
                                roleSettingDTO.setDescription(role.getDescription());
                                roleSettingDTO.setRole(role.getRole());
                                roleSettingDTO.setColor(role.getColor());
                                List<String> checkDot = new ArrayList<>();
                                try {
                                    checkDot = mapper.readValue(role.getCheckDot(), List.class);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                                roleSettingDTO.setCheckDot(checkDot);
                                roleSettingDTO.setCategory(role.getCategory());
                                return roleSettingDTO;
                            }).collect(Collectors.toList());
                    return new FormMerchantRoleDTO(entry.getKey(), roleSettingDTOS);
                }).collect(Collectors.toList());
            }
            result = roles;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getListRoleByType: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
