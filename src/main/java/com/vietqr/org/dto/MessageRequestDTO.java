package com.vietqr.org.dto;

public class MessageRequestDTO {
    private Recipient recipient;
    private Message message;

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public class Recipient {
        private String id; // ID of the user to whom message is to be sent

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public class Message {
        private String text; // Message text

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
