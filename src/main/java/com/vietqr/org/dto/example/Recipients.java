package com.vietqr.org.dto.example;

public class Recipients {
    private Header header;

    private String encrypted_key;

    public String getEncrypted_key() {
        return encrypted_key;
    }

    public void setEncrypted_key(String encrypted_key) {
        this.encrypted_key = encrypted_key;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "Recipients{" +
                "encrypted_key='" + encrypted_key + '\'' +
                ", header=" + header +
                '}';
    }
}
