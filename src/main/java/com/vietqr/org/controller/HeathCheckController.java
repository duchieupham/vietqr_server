package com.vietqr.org.controller;

import com.vietqr.org.dto.ResponseMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/status")
public class HeathCheckController {
    @GetMapping
    public ResponseEntity<ResponseMessageDTO> heathCheckResponse() {
        ResponseMessageDTO result = new ResponseMessageDTO("SUCCESS", "");
        HttpStatus httpStatus = HttpStatus.OK;
        return new ResponseEntity<>(result, httpStatus);
    }
}
