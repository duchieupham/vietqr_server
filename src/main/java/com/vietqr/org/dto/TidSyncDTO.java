package com.vietqr.org.dto;

import java.util.List;

public class TidSyncDTO {
    private List<TidSynchronizeDTO> terminals;

    public TidSyncDTO() {
    }

    public TidSyncDTO(List<TidSynchronizeDTO> terminals) {
        this.terminals = terminals;
    }

    public List<TidSynchronizeDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TidSynchronizeDTO> terminals) {
        this.terminals = terminals;
    }
}
