package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface VietQRService {
    ICaiBankTypeQR getCaiBankTypeById(String id);

    IAccountBankQR getAccountBankQR(String bankAccount, String bankTypeId);

    IAccountBankInfoQR getAccountBankQRByAccountAndId(String bankAccount, String bankTypeId);

    ITerminalBankReceiveQR getTerminalBankReceiveQR(String subRawCode);

    IAccountBankUserQR getAccountBankUserQRById(String bankId);
}
