package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class VcardInputListDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<VCardInputDTO> list;

    public VcardInputListDTO() {
        super();
    }

    public VcardInputListDTO(List<VCardInputDTO> list) {
        this.list = list;
    }

    public List<VCardInputDTO> getList() {
        return list;
    }

    public void setList(List<VCardInputDTO> list) {
        this.list = list;
    }

}
