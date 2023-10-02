package com.vietqr.org.service;

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
}
