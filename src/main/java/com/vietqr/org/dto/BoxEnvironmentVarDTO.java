package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class BoxEnvironmentVarDTO {
    @NotBlank
    private String homePage;
    @NotBlank
    private String message1;
    @NotBlank
    private String message2;

    public BoxEnvironmentVarDTO() {
        homePage = "";
        message1 = "";
        message2 = "";
    }

    public BoxEnvironmentVarDTO(String homePage, String message1, String message2) {
        this.homePage = homePage;
        this.message1 = message1;
        this.message2 = message2;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }
}
