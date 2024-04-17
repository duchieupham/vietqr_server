package com.vietqr.org.dto;

import java.util.List;

public class TerminalSyncResponseDTO {
    private int page;
    private int size;

    private List<TerminalTidResponseDTO> items;

    public TerminalSyncResponseDTO() {
    }

    public TerminalSyncResponseDTO(int page, int size, List<TerminalTidResponseDTO> items) {
        this.page = page;
        this.size = size;
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<TerminalTidResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<TerminalTidResponseDTO> items) {
        this.items = items;
    }
}
