// package com.vietqr.org.controller;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;

// import javax.validation.Valid;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.vietqr.org.dto.AccountBankPaymentDTO;
// import com.vietqr.org.dto.AccountBankResponseDTO;
// import com.vietqr.org.dto.BankAccountRemoveDTO;
// import com.vietqr.org.dto.ResponseMessageDTO;
// import com.vietqr.org.entity.AccountBankPaymentEntity;
// import com.vietqr.org.service.AccountBankPaymentService;
// import com.vietqr.org.service.BankTypeService;

// @RestController
// @RequestMapping("/api")
// public class AccountBankPaymentController {

// @Autowired
// AccountBankPaymentService accountBankService;

// @Autowired
// BankTypeService bankTypeService;

// @PostMapping("account-bank-payment")
// public ResponseEntity<ResponseMessageDTO> insertAccountBank(@Valid
// @RequestBody AccountBankPaymentDTO dto) {
// ResponseMessageDTO result = null;
// HttpStatus httpStatus = null;
// try {
// String checkBankAccountExisted =
// accountBankService.checkExistedBank(dto.getBankAccount(),
// dto.getBankTypeId());
// if (checkBankAccountExisted == null || checkBankAccountExisted.isEmpty()) {
// UUID uuid = UUID.randomUUID();
// AccountBankPaymentEntity entity = new AccountBankPaymentEntity();
// entity.setId(uuid.toString());
// entity.setUserId(dto.getUserId());
// entity.setBankAccountName(dto.getUserBankName().toUpperCase());
// entity.setBankAccount(dto.getBankAccount());
// entity.setBankTypeId(dto.getBankTypeId());
// entity.setDateOpen(dto.getDateOpen());
// entity.setPhoneOTP(dto.getPhoneOtp());
// int check = accountBankService.insertAccountBank(entity);
// if (check == 1) {
// result = new ResponseMessageDTO("SUCESS", "");
// httpStatus = HttpStatus.OK;
// } else {
// result = new ResponseMessageDTO("FAILED", "E13");
// httpStatus = HttpStatus.BAD_REQUEST;
// }
// } else {
// result = new ResponseMessageDTO("FAILED", "E12");
// httpStatus = HttpStatus.BAD_REQUEST;
// }

// } catch (Exception e) {
// System.out.println("Error at insertAccountBank - BankAccountPayment: " +
// e.toString());
// result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
// httpStatus = HttpStatus.BAD_REQUEST;
// }
// return new ResponseEntity<>(result, httpStatus);
// }

// @GetMapping("account-bank-payment/{userId}")
// public ResponseEntity<List<AccountBankResponseDTO>>
// getAccountBanks(@PathVariable("userId") String userId) {
// List<AccountBankResponseDTO> result = new ArrayList<>();
// HttpStatus httpStatus = null;
// try {
// // List<AccountBankPaymentEntity> bankAccounts =
// // accountBankService.getAccountBanksByUserId(userId);
// // if(!bankAccounts.isEmpty()) {
// // for (AccountBankPaymentEntity accountBankEntity: bankAccounts) {
// // BankTypeEntity bankTypeEntity =
// // bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
// // AccountBankResponseDTO dto = new AccountBankResponseDTO();
// // dto.setId(accountBankEntity.getId());
// // dto.setUserId(accountBankEntity.getUserId());
// // dto.setBankAccount(accountBankEntity.getBankAccount());
// // dto.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
// // dto.setBankCode(bankTypeEntity.getBankCode());
// // dto.setBankName(bankTypeEntity.getBankName());
// // dto.setBankStatus(bankTypeEntity.getStatus());
// // dto.setImgId(bankTypeEntity.getImgId());
// // result.add(dto);
// // }
// // }
// httpStatus = HttpStatus.OK;
// } catch (Exception e) {
// System.out.println("Error at getAccountBanks: " + e.toString());
// httpStatus = HttpStatus.BAD_REQUEST;
// }
// return new ResponseEntity<>(result, httpStatus);
// }

// @DeleteMapping("account-bank-payment")
// public ResponseEntity<ResponseMessageDTO> deleteAccountBank(@Valid
// @RequestBody BankAccountRemoveDTO dto) {
// ResponseMessageDTO result = null;
// HttpStatus httpStatus = null;
// try {
// accountBankService.deleteAccountBank(dto.getBankId());
// result = new ResponseMessageDTO("SUCCESS", "");
// httpStatus = HttpStatus.OK;
// } catch (Exception e) {
// System.out.println("Error at deleteAccountBank: " + e.toString());
// result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
// httpStatus = HttpStatus.BAD_REQUEST;
// }
// return new ResponseEntity<>(result, httpStatus);
// }

// // @PostMapping("account-bank-payment/request-mb")
// // public ResponseEntity<Object> requestPaymentMb(@Valid @RequestParam String
// // bankPaymentId, @Valid @RequestParam Long amount){
// // Object result = null;
// // HttpStatus httpStatus = null;
// // try {
// // //insert into db
// // TransactionCreateEntity
// // //request payment
// // //response
// // }catch(Exception e) {
// // System.out.println("Error at requestPaymentMb: " + e.toString());
// // result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
// // httpStatus = HttpStatus.BAD_REQUEST;
// // }
// // return new ResponseEntity<Object>(result, httpStatus);
// //
// // }

// }
