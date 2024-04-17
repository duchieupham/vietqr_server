package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MerchantMemberRole")
public class MerchantMemberRoleEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "merchantMemberId")
    private String merchantMemberId;

    @Column(name = "transReceiveRoleIds")
    private String transReceiveRoleIds;

    @Column(name = "transRefundRoleIds")
    private String transRefundRoleIds;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMerchantMemberId(String merchantMemberId) {
        this.merchantMemberId = merchantMemberId;
    }

    public void setTransReceiveRoleIds(String transReceiveRoleIds) {
        this.transReceiveRoleIds = transReceiveRoleIds;
    }

    public void setTransRefundRoleIds(String transRefundRoleIds) {
        this.transRefundRoleIds = transRefundRoleIds;
    }

    public String getMerchantMemberId() {
        return merchantMemberId;
    }

    public String getTransReceiveRoleIds() {
        return transReceiveRoleIds;
    }

    public String getTransRefundRoleIds() {
        return transRefundRoleIds;
    }
}
