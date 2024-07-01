package com.vietqr.org.entity.qrfeed;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "FileAttachment")
public class FileAttachmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "fileName")
    private String fileName;

    @Lob
    @Column(name = "fileData", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(name = "fileType")
    private String fileType;

    @Column(name = "displayFileType")
    private String displayFileType;

    public FileAttachmentEntity() {
    }

    public FileAttachmentEntity(String id, String fileName, byte[] fileData, String fileType, String displayFileType) {
        this.id = id;
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileType = fileType;
        this.displayFileType = displayFileType;
    }

    public String getDisplayFileType() {
        return displayFileType;
    }

    public void setDisplayFileType(String displayFileType) {
        this.displayFileType = displayFileType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
