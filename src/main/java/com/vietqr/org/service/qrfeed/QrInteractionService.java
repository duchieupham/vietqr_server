package com.vietqr.org.service.qrfeed;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrInteractionService {

    void likeOrUnlikeQrWallet(String qrWalletId, String userId, int interactionType, long currentTime);
    List<String> getUserNamesWhoLiked(String qrWalletId);
}
