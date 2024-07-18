package com.vietqr.org.entity.qrfeed;

//import com.vietqr.org.dto.qrfeed.QrWalletDTO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
//@SqlResultSetMapping(
//        name = "QrWalletDTOMapping",
//        classes = @ConstructorResult(
//                targetClass = QrWalletDTO.class,
//                columns = {
//                        @ColumnResult(name = "id", type = String.class),
//                        @ColumnResult(name = "title", type = String.class),
//                        @ColumnResult(name = "description", type = String.class),
//                        @ColumnResult(name = "value", type = String.class),
//                        @ColumnResult(name = "qrType", type = String.class),
//                        @ColumnResult(name = "timeCreated", type = Long.class),
//                        @ColumnResult(name = "userId", type = String.class),
//                        @ColumnResult(name = "likeCount", type = Integer.class),
//                        @ColumnResult(name = "commentCount", type = Integer.class),
//                        @ColumnResult(name = "hasLiked", type = Boolean.class),
//                        @ColumnResult(name = "data", type = String.class)
//                }
//        )
//)
//@NamedNativeQuery(
//        name = "QrWallet.findAllPublicQrWallets",
//        query = "SELECT w.id AS id, w.title AS title, w.description AS description, " +
//                "w.value AS value, w.qr_type AS qrType, w.time_created AS timeCreated, w.user_id AS userId, " +
//                "(SELECT COUNT(id) FROM qr_interaction i WHERE i.qr_wallet_id = w.id AND i.interaction_type = 1) AS likeCount, " +
//                "(SELECT COUNT(id) FROM qr_wallet_comment wc WHERE wc.qr_wallet_id = w.id) AS commentCount, " +
//                "CASE WHEN (SELECT COUNT(id) FROM qr_interaction i WHERE i.qr_wallet_id = w.id AND i.user_id = :userId AND i.interaction_type = 1) > 0 THEN TRUE ELSE FALSE END AS hasLiked, " +
//                "CASE " +
//                "WHEN w.qr_type = '0' THEN w.public_id " +
//                "WHEN w.qr_type = '1' THEN w.value " +
//                "WHEN w.qr_type = '2' THEN CONCAT(JSON_UNQUOTE(JSON_EXTRACT(w.qr_data, '$.fullName')), ' - ', JSON_UNQUOTE(JSON_EXTRACT(w.qr_data, '$.phoneNo'))) " +
//                "WHEN w.qr_type = '3' THEN CONCAT(JSON_UNQUOTE(JSON_EXTRACT(w.user_data, '$.bankCode')), ' - ', JSON_UNQUOTE(JSON_EXTRACT(w.user_data, '$.userBankName'))) " +
//                "ELSE NULL " +
//                "END AS data " +
//                "FROM qr_wallet w WHERE w.is_public = 1 " +
//                "ORDER BY w.time_created DESC " +
//                "LIMIT :offset, :size",
//        resultSetMapping = "QrWalletDTOMapping"
//)
@Table(name = "QrWallet")
public class QrWalletEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @Column(name = "value", columnDefinition = "LONGTEXT")
    private String value;
    @Column(name = "qrType")
    private int qrType;
    @Column(name = "qrData", columnDefinition = "LONGTEXT")
    private String qrData;
    @Column(name = "userData", columnDefinition = "JSON")
    private String userData;
    @Column(name = "isPublic")
    private int isPublic;
    @Column(name = "timeCreated")
    private long timeCreated;
    @Column(name = "userId")
    private String userId;
    @Column(name = "pin")
    private String pin;
    @Column(name = "publicId")
    private String publicId;
    @Column(name = "style")
    private int style;
    @Column(name = "theme")
    private int theme;

    @Column(name = "fileAttachmentId")
    private String fileAttachmentId;

    public QrWalletEntity() {
    }

    public QrWalletEntity(String id, String title, String description, String value, int qrType, String qrData, String userData, int isPublic, long timeCreated, String userId, String pin, String publicId, int style, int theme, String fileAttachmentId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.value = value;
        this.qrType = qrType;
        this.qrData = qrData;
        this.userData = userData;
        this.isPublic = isPublic;
        this.timeCreated = timeCreated;
        this.userId = userId;
        this.pin = pin;
        this.publicId = publicId;
        this.style = style;
        this.theme = theme;
        this.fileAttachmentId = fileAttachmentId;
    }

    public int getStyle() {
        return style;
    }

    public String getFileAttachmentId() {
        return fileAttachmentId;
    }

    public void setFileAttachmentId(String fileAttachmentId) {
        this.fileAttachmentId = fileAttachmentId;
    }

    public int getStyle(int style) {
        return this.style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getQrType() {
        return qrType;
    }

    public void setQrType(int qrType) {
        this.qrType = qrType;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
}
