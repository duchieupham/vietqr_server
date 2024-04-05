package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BankReceiveOTP")
public class BankReceiveOTPEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "expiredDate")
    private long expiredDate;

    @Column(name = "otpToken")
    private String otpToken;

    @Column(name = "userId")
    private String userId;

    @Column(name = "bankId")
    private String bankId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
