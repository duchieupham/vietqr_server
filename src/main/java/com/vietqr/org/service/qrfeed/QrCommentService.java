package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.QrCommentRequestDTO;
import com.vietqr.org.dto.qrfeed.UserCommentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrCommentService {

    void addComment(QrCommentRequestDTO request);

    boolean deleteComment(String commentId, String userId);

    List<UserCommentDTO> findCommentersByQrWalletId(String qrWalletId, int offset, int size);
    int countCommentersByQrWalletId(String qrWalletId);
}
