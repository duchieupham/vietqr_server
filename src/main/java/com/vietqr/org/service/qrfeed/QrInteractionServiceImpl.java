package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IUserInteractionDTO;
import com.vietqr.org.dto.qrfeed.UserLikeDTO;
import com.vietqr.org.repository.QrInteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QrInteractionServiceImpl implements QrInteractionService{
    @Autowired
    private QrInteractionRepository qrInteractionRepository;
//    @Override
//    public void likeOrUnlikeQrWallet(String qrWalletId, String userId, int interactionType, long currentTime) {
//        int existingInteractions = qrInteractionRepository.countInteraction(qrWalletId, userId);
//
//        if (existingInteractions > 0) {
//            qrInteractionRepository.updateInteraction(qrWalletId, userId, interactionType, currentTime);
//        } else {
//            qrInteractionRepository.insertInteraction(UUID.randomUUID().toString(), qrWalletId, userId, interactionType, currentTime);
//        }
//    }

    @Override
    public List<UserLikeDTO> findLikersByQrWalletId(String qrWalletId, int offset, int size) {
        return qrInteractionRepository.findLikersByQrWalletId(qrWalletId, offset, size);
    }

    @Override
    public int countLikersByQrWalletId(String qrWalletId) {
        return qrInteractionRepository.countLikersByQrWalletId(qrWalletId);
    }

    @Override
    public void likeOrUnlikeQrWallet(String qrWalletId, String userId, int interactionType, long currentTime) {
        int existingInteractions = qrInteractionRepository.countInteraction(qrWalletId, userId);

        if (existingInteractions > 0) {
            qrInteractionRepository.updateInteraction(qrWalletId, userId, interactionType, currentTime);
        } else {
            qrInteractionRepository.insertInteraction(UUID.randomUUID().toString(), qrWalletId, userId, interactionType, currentTime);
        }

        qrInteractionRepository.findUserInteractionById(userId);
    }
}
