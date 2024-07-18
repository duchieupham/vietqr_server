package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

import com.vietqr.org.entity.ServiceFeeEntity;

public class ServiceFeeItemDTO implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private ServiceFeeEntity item;
    private List<ServiceFeeEntity> subItems;

    public ServiceFeeItemDTO() {
        super();
    }

    public ServiceFeeItemDTO(ServiceFeeEntity item, List<ServiceFeeEntity> subItems) {
        this.item = item;
        this.subItems = subItems;
    }

    public ServiceFeeEntity getItem() {
        return item;
    }

    public void setItem(ServiceFeeEntity item) {
        this.item = item;
    }

    public List<ServiceFeeEntity> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<ServiceFeeEntity> subItems) {
        this.subItems = subItems;
    }

}
