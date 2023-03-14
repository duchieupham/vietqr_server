package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionReceiveBranch")
public class TransactionReceiveBranchEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "transactionReceiveId")
    private String transactionReceiveId;

    @Column(name = "branchId")
    private String branchId;

    @Column(name = "businessId")
    private String businessId;

    public TransactionReceiveBranchEntity() {
        super();
    }

    public TransactionReceiveBranchEntity(String id, String transactionReceiveId, String branchId, String businessId) {
        this.id = id;
        this.transactionReceiveId = transactionReceiveId;
        this.branchId = branchId;
        this.businessId = businessId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

}
