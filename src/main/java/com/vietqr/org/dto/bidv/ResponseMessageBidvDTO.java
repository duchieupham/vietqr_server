package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class ResponseMessageBidvDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String result_code;
    private String result_desc;

    public ResponseMessageBidvDTO() {
        super();
    }

    public ResponseMessageBidvDTO(String result_code, String result_desc) {
        this.result_code = result_code;
        this.result_desc = result_desc;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getResult_desc() {
        return result_desc;
    }

    public void setResult_desc(String result_desc) {
        this.result_desc = result_desc;
    }

}
