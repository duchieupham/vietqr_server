package com.vietqr.org.service;

import com.vietqr.org.dto.TransactionWalletAdminDTO;
import org.springframework.stereotype.Service;
import java.util.List;

import com.vietqr.org.dto.TransWalletListDTO;
import com.vietqr.org.dto.TransactionVNPTItemDTO;
import com.vietqr.org.dto.VNPTEpayTransCounterDTO;
import com.vietqr.org.entity.TransactionWalletEntity;

@Service
public interface TransactionWalletService {

        public int insertTransactionWallet(TransactionWalletEntity entity);

        public TransactionWalletEntity getTransactionWalletById(String id);

        public TransactionWalletEntity getTransactionWalletByBillNumber(String billNumber);

        public List<TransactionWalletEntity> getTransactionWalletsByUserId(String userId);

        public List<TransactionWalletEntity> getTransactionWallets();

        public void updateTransactionWalletStatus(int status, long timePaid, String id);

        public void updateTransactionWallet(int status, long timePaid, String amount, String phoneNoRC, String userId,
                        String otp,
                        int paymentType);

        public void updateTransactionWalletConfirm(long timeCreated, String amount, String userId, String otp,
                        int paymentType);

        public String checkExistedTransactionnWallet(String otp, String userId, int paymentType);

        public List<TransWalletListDTO> getTransactionWalletList(String userId, int offset);

        public List<TransWalletListDTO> getTransactionWalletListByStatus(String userId, int status, int offset);

        public List<TransactionVNPTItemDTO> getTransactionsVNPT(int offset);

        public List<TransactionVNPTItemDTO> getTransactionsVNPTFilter(int status, int offset);

        public VNPTEpayTransCounterDTO getVNPTEpayCounter();

        List<TransactionWalletAdminDTO> getTransactionWalletByBillNumberAndVNPTEpay(String value, String fromDate,
                                                                                    String toDate, int offset, int size);

        List<TransactionWalletAdminDTO> getTransactionWalletByBillNumber(String value, String fromDate,
                                                                         String toDate, int offset, int size);

        List<TransactionWalletAdminDTO> getTransactionWalletByBillNumberAndAnnualFee(String value,
                                                                                     String fromDate, String toDate,
                                                                                     int offset, int size);

        List<TransactionWalletAdminDTO> getTransactionWalletByPhoneNoAndVNPTEpay(String value, String fromDate,
                                                                                 String toDate, int offset, int size);

        List<TransactionWalletAdminDTO> getTransactionWalletByPhoneNoAndAnnualFee(String value, String fromDate,
                                                                                  String toDate, int offset, int size);

        List<TransactionWalletAdminDTO> getTransactionWalletByPhoneNo(String value, String fromDate,
                                                                      String toDate, int offset, int size);

        int countTransactionWalletByBillNumberAndVNPTEpay(String value, String fromDate, String toDate);

        int countTransactionWalletByBillNumberAndAnnualFee(String value, String fromDate, String toDate);

        int countTransactionWalletByBillNumber(String value, String fromDate, String toDate);

        int countTransactionWalletByPhoneNoAndVNPTEpay(String value, String fromDate, String toDate);

        int countTransactionWalletByPhoneNoAndAnnualFee(String value, String fromDate, String toDate);

        int countTransactionWalletByPhoneNo(String value, String fromDate, String toDate);

        List<TransactionWalletAdminDTO> getTransactionWalletVNPTEpay(String fromDate, String toDate, int offset, int size);

        int countTransactionWalletVNPTEpay(String fromDate, String toDate);

        List<TransactionWalletAdminDTO> getTransactionWalletAnnualFee(String fromDate, String toDate, int offset, int size);

        int countTransactionWalletAnnualFee(String fromDate, String toDate);

        List<TransactionWalletAdminDTO> getTransactionWallet(String fromDate, String toDate, int offset, int size);

        int countTransactionWallet(String fromDate, String toDate);
}
