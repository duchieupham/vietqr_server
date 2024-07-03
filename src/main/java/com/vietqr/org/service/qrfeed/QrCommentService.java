package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.QrCommentDTO;
import com.vietqr.org.dto.qrfeed.QrCommentRequestDTO;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrCommentService {
    List<QrCommentDTO> findCommentsByQrWalletId(String qrWalletId);

    void addComment(QrCommentRequestDTO request);

    void deleteComment(String commentId);

    List<String> getUserNamesWhoCommented(String qrWalletId);
}
