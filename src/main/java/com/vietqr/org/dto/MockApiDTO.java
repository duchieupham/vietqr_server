package com.vietqr.org.dto;

import java.util.Map;

public class MockApiDTO {
    private String prefix;
    private String url;
    private String method;
    private Map<String, String> requestParams;
    private Object requestBody;
    private Object responseBody;
    private int responseStatus;
    private Boolean isPaging;

    public MockApiDTO() {
    }

    public MockApiDTO(String url, String method, Map<String,
            String> requestParams, Object requestBody, Object responseBody) {
        this.url = url;
        this.method = method;
        this.requestParams = requestParams;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getPaging() {
        return isPaging;
    }

    public void setPaging(Boolean paging) {
        isPaging = paging;
    }
}
