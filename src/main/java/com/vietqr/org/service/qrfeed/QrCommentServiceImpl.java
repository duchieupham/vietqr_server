package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.QrCommentDTO;
import com.vietqr.org.dto.qrfeed.QrCommentRequestDTO;
import com.vietqr.org.dto.qrfeed.UserCommentDTO;
import com.vietqr.org.repository.QrCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QrCommentServiceImpl implements QrCommentService{
    @Autowired
    private QrCommentRepository qrCommentRepository;


    @Override
    public void addComment(QrCommentRequestDTO request) {
        String commentId = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis() / 1000L; // Unix timestamp

        // Lấy thông tin userData từ bảng QR Wallet
        String userData = qrCommentRepository.findUserDataByUserId(request.getUserId());
        qrCommentRepository.insertComment(commentId, request.getMessage(), request.getUserId(), userData, currentTime);

        String walletCommentId = UUID.randomUUID().toString();
        qrCommentRepository.linkCommentToQrWallet(walletCommentId, request.getQrWalletId(), commentId);

    }

    @Override
    public void deleteComment(String commentId) {
        qrCommentRepository.unlinkCommentFromQrWallet(commentId);
        qrCommentRepository.deleteComment(commentId);
    }

    @Override
    public List<UserCommentDTO> findCommentersByQrWalletId(String qrWalletId, int offset, int size) {
        return qrCommentRepository.findCommentersByQrWalletId(qrWalletId, offset, size);
    }

    @Override
    public int countCommentersByQrWalletId(String qrWalletId) {
        return qrCommentRepository.countCommentersByQrWalletId(qrWalletId);
    }
}
