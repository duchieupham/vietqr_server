package com.vietqr.org.dto;

import java.util.ArrayList;
import java.util.List;

public class DataFeeTransactionDTO {
    private BankReceiveFeePackageDTO customerDetails;
    private List<FeeTransactionInfoDTOs> transactions;

    public DataFeeTransactionDTO() {
        customerDetails = new BankReceiveFeePackageDTO();
        transactions = new ArrayList<>();
    }

    public DataFeeTransactionDTO(BankReceiveFeePackageDTO customerDetails,
                                 List<FeeTransactionInfoDTOs> transactions) {
        this.customerDetails = customerDetails;
        this.transactions = transactions;
    }

    public BankReceiveFeePackageDTO getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(BankReceiveFeePackageDTO customerDetails) {
        this.customerDetails = customerDetails;
    }

    public List<FeeTransactionInfoDTOs> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<FeeTransactionInfoDTOs> transactions) {
        this.transactions = transactions;
    }
}
