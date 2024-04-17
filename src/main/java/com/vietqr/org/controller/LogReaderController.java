// package com.vietqr.org.controller;

// import java.util.List;

// import org.apache.log4j.Logger;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.vietqr.org.dto.ResponseMessageDTO;
// import com.vietqr.org.util.LogReaderUtil;

// @RestController
// @CrossOrigin
// @RequestMapping("/api/admin")
// public class LogReaderController {
// private static final Logger logger =
// Logger.getLogger(LogReaderController.class);

// // @GetMapping("log-reader")
// // public ResponseEntity<Object> getLogByDate(@RequestParam(value = "date")
// // String date) {
// // Object result = null;
// // HttpStatus httpStatus = null;
// // try {
// // List<String> log = LogReaderUtil.readLogFile(date);
// // result = log;
// // httpStatus = HttpStatus.OK;
// // } catch (Exception e) {
// // logger.error("getLogByDate: ERROR: " + e.toString());
// // result = new ResponseMessageDTO("FAILED", "E05");
// // httpStatus = HttpStatus.BAD_REQUEST;
// // }
// // return new ResponseEntity<>(result, httpStatus);
// // }
// }
