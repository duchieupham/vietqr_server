package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class BussinessValidDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String businessId;
    private String image;
    private String coverImage;
    private String name;
    private List<BranchChoiceReponseDTO> branchs;

    public BussinessValidDTO() {
        super();
    }

    public BussinessValidDTO(String businessId, String image, String coverImage, String name,
            List<BranchChoiceReponseDTO> branchs) {
        this.businessId = businessId;
        this.image = image;
        this.coverImage = coverImage;
        this.name = name;
        this.branchs = branchs;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BranchChoiceReponseDTO> getBranchs() {
        return branchs;
    }

    public void setBranchs(List<BranchChoiceReponseDTO> branchs) {
        this.branchs = branchs;
    }

}
