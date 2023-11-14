package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionReceiveNoteUpdateDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String note;
    private String id;

    public TransactionReceiveNoteUpdateDTO() {
        super();
    }

    public TransactionReceiveNoteUpdateDTO(String note, String id) {
        this.note = note;
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
