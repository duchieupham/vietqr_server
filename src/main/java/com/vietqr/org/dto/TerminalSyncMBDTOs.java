package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TerminalSyncMBDTOs implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<TerminalSyncMBDTO> terminals;

    public TerminalSyncMBDTOs() {
        super();
    }

    public TerminalSyncMBDTOs(List<TerminalSyncMBDTO> terminals) {
        this.terminals = terminals;
    }

    public List<TerminalSyncMBDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalSyncMBDTO> terminals) {
        this.terminals = terminals;
    }

}