package com.vietqr.org.dto;

import java.util.List;

public class TerminalShareResponseDTO {
    private String userId;
    private int totalTerminals;
    private List<TerminalResponseDTO> terminals;

    public TerminalShareResponseDTO() {
    }

    public TerminalShareResponseDTO(String userId, int totalTerminals, List<TerminalResponseDTO> terminals) {
        this.userId = userId;
        this.totalTerminals = totalTerminals;
        this.terminals = terminals;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalTerminals() {
        return totalTerminals;
    }

    public void setTotalTerminals(int totalTerminals) {
        this.totalTerminals = totalTerminals;
    }

    public List<TerminalResponseDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalResponseDTO> terminals) {
        this.terminals = terminals;
    }
}
