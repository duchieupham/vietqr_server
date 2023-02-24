package com.vietqr.org.dto;

import java.io.Serializable;

public class FcmRequestDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	  private String title;
	    private String message;
	    private String topic;
	    private String token;

	    public FcmRequestDTO() {
	    }

	    public FcmRequestDTO(String title, String messageBody, String topicName) {
	        this.title = title;
	        this.message = messageBody;
	        this.topic = topicName;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

	    public String getTopic() {
	        return topic;
	    }

	    public void setTopic(String topic) {
	        this.topic = topic;
	    }

	    public String getToken() {
	        return token;
	    }

	    public void setToken(String token) {
	        this.token = token;
	    }

}
