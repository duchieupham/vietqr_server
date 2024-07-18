package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.QrCommentDTO;
import com.vietqr.org.dto.qrfeed.QrCommentRequestDTO;
import com.vietqr.org.dto.qrfeed.UserCommentDTO;
import com.vietqr.org.entity.qrfeed.QrCommentEntity;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.repository.QrCommentRepository;
import com.vietqr.org.repository.QrWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QrCommentServiceImpl implements QrCommentService{
    @Autowired
    private QrCommentRepository qrCommentRepository;

    @Autowired
    private QrWalletRepository qrWalletRepository;


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
    public boolean deleteComment(String commentId, String userId) {
        QrCommentEntity comment = qrCommentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return false;
        }
        String qrWalletId = qrCommentRepository.findQrWalletIdByCommentId(commentId);
        QrWalletEntity qrWallet = qrWalletRepository.findById(qrWalletId).orElse(null);
        if (qrWallet == null) {
            return false;
        }
        if (comment.getUserId().equals(userId) || qrWallet.getUserId().equals(userId)) {
            qrCommentRepository.unlinkCommentFromQrWallet(commentId);
            qrCommentRepository.deleteComment(commentId);
            return true;
        }
        return false;
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
