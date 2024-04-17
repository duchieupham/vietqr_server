package com.vietqr.org.dto.example;

import com.google.gson.annotations.SerializedName;

public class JweObj {
    Recipients[] recipients;
    @SerializedName("protected")
    String _protected;
    String ciphertext;
    String iv;
    String tag;

    public JweObj(Recipients[] recipients, String _protected, String ciphertext, String iv, String tag) {
        this.recipients = recipients;
        this._protected = _protected;
        this.ciphertext = ciphertext;
        this.iv = iv;
        this.tag = tag;
    }

    public Recipients[] getRecipients() {
        return recipients;
    }

    public void setRecipients(Recipients[] recipients) {
        this.recipients = recipients;
    }

    public String get_protected() {
        return _protected;
    }

    public void set_protected(String _protected) {
        this._protected = _protected;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "JweObj{" +
                "recipients=" + recipients +
                ", _protected='" + _protected + '\'' +
                ", ciphertext='" + ciphertext + '\'' +
                ", iv='" + iv + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
