package com.vietqr.org.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransByCusSyncDTO;
import com.vietqr.org.dto.TransStatisticByDateDTO;
import com.vietqr.org.dto.TransStatisticByMonthDTO;
import com.vietqr.org.dto.TransStatisticDTO;
import com.vietqr.org.dto.TransactionCheckStatusDTO;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Service
public interface TransactionReceiveService {

    public int insertTransactionReceive(TransactionReceiveEntity entity);

    public int insertAllTransactionReceive(List<TransactionReceiveEntity> entities);

    public void updateTransactionReceiveStatus(int status, String refId, String referenceNumber, long timePaid,
            String id);

    public List<TransactionRelatedDTO> getRelatedTransactionReceives(String businessId);

    public TransactionDetailDTO getTransactionById(String id);

    public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount, String transType);

    public List<TransactionReceiveEntity> getTransactionByBankId(String bankId);

    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId);

    public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId);

    public TransactionReceiveEntity getTransactionReceiveById(String id);

    public TransactionReceiveEntity getTransactionByOrderId(String orderId, String amount);

    public TransactionReceiveEntity findTransactionReceiveByFtCode(String ftCode);

    public TransactionReceiveEntity getTransactionReceiveByRefNumberAndOrderId(String referenceNumber, String orderId);

    public TransactionReceiveEntity getTransactionReceiveByRefNumber(String referenceNumber);

    public TransactionReceiveEntity getTransactionReceiveByOrderId(String orderId);

    public TransactionCheckStatusDTO getTransactionCheckStatus(String transactionId);

    public TransStatisticDTO getTransactionOverview(String bankId);

    public List<TransStatisticByDateDTO> getTransStatisticByDate(String bankId);

    public List<TransStatisticByMonthDTO> getTransStatisticByMonth(String bankId);

    public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset);

}
