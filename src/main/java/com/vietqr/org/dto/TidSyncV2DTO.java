package com.vietqr.org.dto;

import java.util.List;

public class TidSyncV2DTO {
    private List<TidSynchronizeV2DTO> terminals;

    public TidSyncV2DTO() {
    }

    public TidSyncV2DTO(List<TidSynchronizeV2DTO> terminals) {
        this.terminals = terminals;
    }

    public List<TidSynchronizeV2DTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TidSynchronizeV2DTO> terminals) {
        this.terminals = terminals;
    }
}
