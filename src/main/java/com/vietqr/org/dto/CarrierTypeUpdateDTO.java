package com.vietqr.org.dto;

import java.io.Serializable;

public class CarrierTypeUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String carrierTypeId;
    private String userId;

    public CarrierTypeUpdateDTO() {
        super();
    }

    public CarrierTypeUpdateDTO(String carrierTypeId, String userId) {
        this.carrierTypeId = carrierTypeId;
        this.userId = userId;
    }

    public String getCarrierTypeId() {
        return carrierTypeId;
    }

    public void setCarrierTypeId(String carrierTypeId) {
        this.carrierTypeId = carrierTypeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
