package com.vietqr.org.entity.qrfeed;

import com.vietqr.org.dto.qrfeed.UserRoleDTOs;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "QrFolderUser.findUserRolesByFolderId",
                query = "SELECT DISTINCT qfu.user_id AS userId, qu.role AS role " +
                        "FROM qr_folder_user qfu " +
                        "INNER JOIN qr_user qu ON qfu.user_id = qu.user_id " +
                        "WHERE qfu.qr_folder_id = :folderId " +
                        "UNION " +
                        "SELECT DISTINCT qf.user_id AS userId, 'ADMIN' AS role " +
                        "FROM qr_folder qf " +
                        "WHERE qf.id = :folderId",
                resultSetMapping = "UserRoleMapping"
        )
})
@SqlResultSetMapping(
        name = "UserRoleMapping",
        classes = @ConstructorResult(
                targetClass = UserRoleDTOs.class,
                columns = {
                        @ColumnResult(name = "userId", type = String.class),
                        @ColumnResult(name = "role", type = String.class)
                }
        )
)
@Table(name = "QrFolderUser")
public class QrFolderUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "qrFolderId")
    private String qrFolderId;
    @Column(name = "userId")
    private String userId;

    public QrFolderUserEntity() {
    }

    public QrFolderUserEntity(String id, String qrFolderId, String userId) {
        this.id = id;
        this.qrFolderId = qrFolderId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQrFolderId() {
        return qrFolderId;
    }

    public void setQrFolderId(String qrFolderId) {
        this.qrFolderId = qrFolderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
