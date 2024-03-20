package com.vietqr.org.controller;

import com.vietqr.org.dto.DropListDTO;
import com.vietqr.org.dto.RoleMemberDTO;
import com.vietqr.org.service.MerchantMemberRoleService;
import com.vietqr.org.service.TransactionReceiveRoleService;
import com.vietqr.org.util.EnvironmentUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantMemberRoleController {

    private static final Logger logger = Logger.getLogger(MerchantMemberRoleController.class);

    @Autowired
    private MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    private TransactionReceiveRoleService transactionReceiveRoleService;

    @GetMapping("role-receive/{level}")
    public ResponseEntity<List<RoleMemberDTO>> getListRoleByType(@PathVariable int level) {
        List<RoleMemberDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<RoleMemberDTO> dtos = new ArrayList<>();
            List<String> roleIds = new ArrayList<>();
            switch (level) {
                // 0: merchant
                case 0:
                    roleIds.add(EnvironmentUtil.getRequestReceiveMerchantRoleId());
                    roleIds.add(EnvironmentUtil.getOnlyReadReceiveMerchantRoleId());
                    dtos = transactionReceiveRoleService.findRoleByIds(roleIds);
                    httpStatus = HttpStatus.OK;
                // 1: terminal
                case 1:
                    roleIds.add(EnvironmentUtil.getOnlyReadReceiveTerminalRoleId());
                    roleIds.add(EnvironmentUtil.getRequestReceiveTerminalRoleId());
                    dtos = transactionReceiveRoleService.findRoleByIds(roleIds);
                    httpStatus = HttpStatus.OK;
                    break;
                default:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
            result = dtos;
        } catch (Exception e) {
            logger.error("getListRoleByType: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
