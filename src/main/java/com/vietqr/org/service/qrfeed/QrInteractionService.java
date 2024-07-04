package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IUserInteractionDTO;
import com.vietqr.org.dto.qrfeed.UserLikeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrInteractionService {

    void likeOrUnlikeQrWallet(String qrWalletId, String userId, int interactionType, long currentTime);

    List<UserLikeDTO> findLikersByQrWalletId(String qrWalletId, int offset, int size);
    int countLikersByQrWalletId(String qrWalletId);


}
