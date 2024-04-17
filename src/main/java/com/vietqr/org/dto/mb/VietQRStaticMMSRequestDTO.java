package com.vietqr.org.dto.mb;

import java.io.Serializable;

public class VietQRStaticMMSRequestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String token;
    private String terminalId;
    private String content;

    public VietQRStaticMMSRequestDTO() {
        super();
    }

    public VietQRStaticMMSRequestDTO(String token, String terminalId, String content) {
        this.token = token;
        this.terminalId = terminalId;
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
